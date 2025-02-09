package gg.eventalerts.eventalertsintegration.config;

import gg.eventalerts.eventalertsintegration.EALibrary;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.socket.clients.CrossBanClient;
import gg.eventalerts.eventalertsintegration.socket.clients.EventPostedClient;
import gg.eventalerts.eventalertsintegration.socket.clients.FamousEventPostedClient;
import gg.eventalerts.eventalertsintegration.socket.clients.LinkClient;

import org.bson.types.ObjectId;

import org.bukkit.configuration.ConfigurationSection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.file.AnnoyingResource;
import xyz.srnyx.annoyingapi.libs.javautilities.MapGenerator;
import xyz.srnyx.annoyingapi.libs.javautilities.MiscUtility;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.Mapper;

import java.util.*;
import java.util.stream.Collectors;


public class ConfigYml extends AnnoyingResource {
    @NotNull public static final String PATH_LINKING = "linking";
    @NotNull public static final String PATH_CROSS_BAN = "cross-ban";
    @NotNull public static final String PATH_EVENT_MESSAGES = "event-messages";
    @NotNull public static final String PATH_ADVANCED = "advanced";

    @NotNull private final EventAlertsIntegration eaPlugin;

    @NotNull public final Linking linking;
    @NotNull public final CrossBan crossBan;
    @NotNull public final EventMessages eventMessages;
    @NotNull public final Advanced advanced;

    public ConfigYml(@NotNull EventAlertsIntegration plugin) {
        super(plugin, "config.yml");
        eaPlugin = plugin;
        linking = new Linking();
        crossBan = new CrossBan();
        eventMessages = new EventMessages();
        advanced = new Advanced();
    }

    public class Linking {
        @NotNull public static final String PATH_REQUIRE_LINK = PATH_LINKING + ".require-link";
        @NotNull public static final String PATH_CHECK_ON_JOIN = PATH_LINKING + ".check-on-join";
        @NotNull public static final String PATH_ALLOW_JOIN_ON_FAILURE = PATH_LINKING + ".allow-join-on-failure";

        public boolean requireLink = getBoolean(PATH_REQUIRE_LINK);
        public boolean checkOnJoin = getBoolean(PATH_CHECK_ON_JOIN, true);
        public boolean allowJoinOnFailure = getBoolean(PATH_ALLOW_JOIN_ON_FAILURE);

        public void setRequireLink(boolean newStatus) {
            if (requireLink == newStatus) return;

            // Update config
            requireLink = newStatus;
            setSave(PATH_REQUIRE_LINK, newStatus);

            // Connect/disconnect websocket
            if (newStatus) {
                eaPlugin.webSockets.connect(LinkClient.class);
            } else {
                eaPlugin.webSockets.close("Config updated", LinkClient.class);
            }
        }
    }

    public class CrossBan {
        @NotNull public static final String PATH_ENABLED = PATH_CROSS_BAN + ".enabled";
        @NotNull public static final String PATH_CHECK_ON_JOIN = PATH_CROSS_BAN + ".check-on-join";
        @NotNull public static final String PATH_ALLOW_JOIN_ON_FAILURE = PATH_CROSS_BAN + ".allow-join-on-failure";

        public boolean enabled = getBoolean(PATH_ENABLED, true);
        public boolean checkOnJoin = getBoolean(PATH_CHECK_ON_JOIN, true);
        public boolean allowJoinOnFailure = getBoolean(PATH_ALLOW_JOIN_ON_FAILURE);

        public void setEnabled(boolean newStatus) {
            if (enabled == newStatus) return;

            // Update config
            enabled = newStatus;
            setSave(PATH_ENABLED, newStatus);

            // Connect/disconnect websocket
            if (newStatus) {
                eaPlugin.webSockets.connect(CrossBanClient.class);
            } else {
                eaPlugin.webSockets.close("Config updated", CrossBanClient.class);
            }
        }
    }

    public class EventMessages {
        @NotNull public static final String PATH_ENABLED = PATH_EVENT_MESSAGES + ".enabled";
        @NotNull public static final String PATH_DETECT_IPS = PATH_EVENT_MESSAGES + ".detect-ips";
        @NotNull public static final String PATH_IGNORED_TYPES = PATH_EVENT_MESSAGES + ".ignored-types";
        @NotNull public static final String PATH_IGNORED_PARTNER_ROLES = PATH_EVENT_MESSAGES + ".ignored-partner-roles";
        @NotNull public static final String PATH_IGNORED_FORMATS = PATH_EVENT_MESSAGES + ".ignored-formats";
        @NotNull public static final String PATH_HOST_FILTER = PATH_EVENT_MESSAGES + ".host-filter";

        public boolean enabled = getBoolean(PATH_ENABLED, true);
        public boolean detectIps = getBoolean(PATH_DETECT_IPS);
        @NotNull public final Set<EventType> ignoredTypes = getEnumSet(EventType.class, PATH_IGNORED_TYPES);
        @NotNull public final Set<PartnerPingRole> ignoredPartnerRoles = getEnumSet(PartnerPingRole.class, PATH_IGNORED_PARTNER_ROLES);
        @NotNull public final Set<EventFormat> ignoredFormats = getEnumSet(EventFormat.class, PATH_IGNORED_FORMATS);
        @NotNull public final Set<String> hostFilterServers = new HashSet<>();
        @NotNull public final Set<String> hostFilterUsers = new HashSet<>();

        public EventMessages() {
            // Get host filter
            final List<String> hostFilter = getStringList(PATH_HOST_FILTER);
            for (final String filter : hostFilter) {
                // User
                final Optional<Long> userId = Mapper.toLong(filter);
                if (userId.isPresent()) {
                    hostFilterUsers.add(userId.get().toString());
                    continue;
                }

                // Install BSON
                if (!plugin.libraryManager.isLoaded(EALibrary.BSON)) plugin.libraryManager.loadLibrary(EALibrary.BSON);

                // Server
                if (MiscUtility.handleException(() -> new ObjectId(filter)).isPresent()) {
                    hostFilterServers.add(filter);
                    continue;
                }

                // Invalid
                AnnoyingPlugin.log(Level.WARNING, "Invalid host filter entry: " + filter);
            }
        }

        @NotNull
        private <T extends Enum<T>> Set<T> getEnumSet(@NotNull Class<T> enumClass, @NotNull String path) {
            return getStringList(path).stream()
                    .map(string -> EventAlertsIntegration.getEnum(enumClass, string))
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        public void setEnabled(boolean newStatus) {
            if (enabled == newStatus) return;

            // Update config
            enabled = newStatus;
            setSave(PATH_ENABLED, newStatus);

            // Connect/disconnect websocket
            if (newStatus) {
                eaPlugin.webSockets.connect(EventPostedClient.class, FamousEventPostedClient.class);
            } else {
                eaPlugin.webSockets.close("Config updated", EventPostedClient.class, FamousEventPostedClient.class);
            }
        }
    }

    public class Advanced {
        @NotNull public static final String PATH_USE_TESTING_API = PATH_ADVANCED + ".use-testing-api";
        @NotNull public static final String PATH_WEBSOCKETS = PATH_ADVANCED + ".websockets";
        @NotNull public static final String PATH_ID_MAPPINGS = PATH_ADVANCED + ".id-mappings";

        public boolean useTestingApi = getBoolean(PATH_USE_TESTING_API, false);
        @NotNull public final Websockets websockets = new Websockets();
        @NotNull public final Map<Long, String> idMappings = MapGenerator.HASH_MAP.mapOf(
                List.of(314853603695394817L, 365630764244664320L, 242385234992037888L, 604377897662414854L, 267734235224211467L, 381890968971902976L, 468890330763231270L, 1111741660892762142L, 1006349851241480242L, 1216096556713906288L, 1280002787446493256L, 1096205843113967669L),
                List.of("Skeppy", "Oiiink", "srnyx", "hailey", "bacca", "Rame", "hayech", "Server Events", "Famous Events", "Potential Skeppy Events", "Skeppy Sighting", "Random Pings"));

        public Advanced() {
            final ConfigurationSection section = getConfigurationSection(PATH_ID_MAPPINGS);
            if (section != null) for (final Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                Mapper.toLong(entry.getKey()).ifPresent(id -> idMappings.put(id, entry.getValue().toString()));
            }
        }

        public class Websockets {
            @NotNull public static final String PATH_LOGS = PATH_WEBSOCKETS + ".logs";
            @NotNull public static final String PATH_RETRY_DELAY = PATH_WEBSOCKETS + ".retry-delay";

            /**
             * minutes
             */
            @Nullable public Long retryDelay;
            public boolean logs = getBoolean(PATH_LOGS, false);

            public Websockets() {
                final String retryDelayString = getString(PATH_RETRY_DELAY);
                if (retryDelayString != null && retryDelayString.equals("-1")) {
                    retryDelay = null;
                    return;
                }
                retryDelay = Math.max(3, getLong(PATH_RETRY_DELAY, 5));
            }
        }
    }
}
