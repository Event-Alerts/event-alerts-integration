package gg.eventalerts.eventalertsintegration.config;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.socket.SocketEndpoint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.file.AnnoyingResource;
import xyz.srnyx.annoyingapi.file.PlayableSound;
import xyz.srnyx.annoyingapi.libs.javautilities.HttpUtility;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;


public class ConfigYml extends AnnoyingResource {
    @NotNull public static final String PATH_API_KEYS  = "api-keys";
    @NotNull public static final String PATH_DISCORD_MESSAGE_SYNCING = "discord-message-syncing";
    @NotNull public static final String PATH_LINKING = "linking";
    @NotNull public static final String PATH_CROSS_BAN = "cross-ban";
    @NotNull public static final String PATH_EVENT_MESSAGES = "event-messages";
    @NotNull public static final String PATH_ADVANCED = "advanced";

    @NotNull private final EventAlertsIntegration eaPlugin;

    @NotNull public final ApiKeys apiKeys;
    @NotNull public final DiscordMessageSyncing discordMessageSyncing;
    @NotNull public final Linking linking;
    @NotNull public final CrossBan crossBan;
    @NotNull public final EventMessages eventMessages;
    @NotNull public final Advanced advanced;

    public ConfigYml(@NotNull EventAlertsIntegration plugin) {
        super(plugin, "config.yml");
        eaPlugin = plugin;

        apiKeys = new ApiKeys();
        discordMessageSyncing = new DiscordMessageSyncing();
        linking = new Linking();
        crossBan = new CrossBan();
        eventMessages = new EventMessages();
        eventMessages.loadHostFilter();
        advanced = new Advanced();
    }

    private <T> boolean toggleSetItem(@NotNull String path, @NotNull Set<T> set, @NotNull T item) {
        final boolean newStatus;
        if (set.contains(item)) {
            set.remove(item);
            newStatus = false;
        } else {
            set.add(item);
            newStatus = true;
        }
        // Save the updated set to the config
        setSave(path, set.stream()
                .map(Object::toString)
                .toList());
        return newStatus;
    }

    public class ApiKeys {
        @NotNull public static final String PATH_PLAYER_API_KEY = PATH_API_KEYS + ".player";
        @NotNull public static final String PATH_SERVER_API_KEY = PATH_API_KEYS + ".server";

        @Nullable public String playerApiKey = getString(PATH_PLAYER_API_KEY);
        @Nullable public String serverApiKey = getString(PATH_SERVER_API_KEY);

        public ApiKeys() {
            if ("PLAYER_API_KEY_HERE".equals(playerApiKey)) playerApiKey = null;
            if ("SERVER_API_KEY_HERE".equals(serverApiKey)) serverApiKey = null;
        }
    }

    public class DiscordMessageSyncing {
        @NotNull public static final String PATH_ENABLED = PATH_DISCORD_MESSAGE_SYNCING + ".enabled";
        @NotNull public static final String DISCORDSRV_INTEGRATION = PATH_DISCORD_MESSAGE_SYNCING + ".discordsrv-integration";
        @NotNull public static final String PATH_FORMAT = PATH_DISCORD_MESSAGE_SYNCING + ".format";

        public boolean enabled = getBoolean(PATH_ENABLED, true);
        public boolean discordSRVIntegration = getBoolean(DISCORDSRV_INTEGRATION, true);
        @NotNull public String format = getString(PATH_FORMAT, "<aqua>[{channelName}] [{authorName}] {contentDisplay}");
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

            // Reconnect websocket
            eaPlugin.webSockets.reconnect("Config updated", SocketEndpoint.LINK);
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

            // Reconnect websocket
            eaPlugin.webSockets.reconnect("Config updated", SocketEndpoint.CROSS_BAN);
        }
    }

    public class EventMessages {
        @NotNull public static final String PATH_ENABLED = PATH_EVENT_MESSAGES + ".enabled";
        @NotNull public static final String PATH_DETECT_IPS = PATH_EVENT_MESSAGES + ".detect-ips";
        @NotNull public static final String PATH_SOUND = PATH_EVENT_MESSAGES + ".sound";
        @NotNull public static final String PATH_SOUND_ENABLED = PATH_SOUND + ".enabled";
        @NotNull public static final String PATH_IGNORED_TYPES = PATH_EVENT_MESSAGES + ".ignored-types";
        @NotNull public static final String PATH_IGNORED_PARTNER_ROLES = PATH_EVENT_MESSAGES + ".ignored-partner-roles";
        @NotNull public static final String PATH_IGNORED_FORMATS = PATH_EVENT_MESSAGES + ".ignored-formats";
        @NotNull public static final String PATH_HOST_FILTER = PATH_EVENT_MESSAGES + ".host-filter";

        public boolean enabled = getBoolean(PATH_ENABLED, true);
        public boolean detectIps = getBoolean(PATH_DETECT_IPS);
        public boolean soundEnabled = getBoolean(PATH_SOUND_ENABLED, true);
        @Nullable public PlayableSound sound = getPlayableSound(PATH_SOUND).orElse(null);
        @NotNull public final Set<EventType> ignoredTypes = getEnumSet(EventType.class, PATH_IGNORED_TYPES);
        @NotNull public final Set<PingRole> ignoredPartnerRoles = getEnumSet(PingRole.class, PATH_IGNORED_PARTNER_ROLES);
        @NotNull public final Set<EventFormat> ignoredFormats = getEnumSet(EventFormat.class, PATH_IGNORED_FORMATS);
        @NotNull public final Set<String> hostFilterServers = new HashSet<>();
        @NotNull public final Set<String> hostFilterUsers = new HashSet<>();

        private void loadHostFilter() {
            // Get host filter
            final List<String> hostFilter = getStringList(PATH_HOST_FILTER);
            for (final String filter : hostFilter) {
                boolean valid = false;
                for (final HostFilter hostFilterEnum : HostFilter.values()) {
                    if (hostFilterEnum.idValidator.apply(eaPlugin, filter)) {
                        valid = true;
                        hostFilterEnum.setGetter.apply(ConfigYml.this).add(filter);
                        break;
                    }
                }

                // Invalid
                if (!valid) AnnoyingPlugin.log(Level.WARNING, "Invalid host filter entry: " + filter);
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

            // Reconnect websocket
            eaPlugin.webSockets.reconnect("Config updated", SocketEndpoint.EVENT_POSTED, SocketEndpoint.FAMOUS_EVENT_POSTED);
        }

        public boolean toggleIgnoredType(@NotNull EventType type) {
            return toggleSetItem(PATH_IGNORED_TYPES, ignoredTypes, type);
        }

        public boolean toggleIgnoredPartnerRole(@NotNull PingRole role) {
            return toggleSetItem(PATH_IGNORED_PARTNER_ROLES, ignoredPartnerRoles, role);
        }
    }

    public class Advanced {
        @NotNull public static final String PATH_DEBUG = PATH_ADVANCED + ".debug";
        @NotNull public static final String PATH_USE_TESTING_API = PATH_ADVANCED + ".use-testing-api";
        @NotNull public static final String PATH_WEBSOCKETS = PATH_ADVANCED + ".websockets";

        public boolean debug = getBoolean(PATH_DEBUG, false);
        public boolean useTestingApi = getBoolean(PATH_USE_TESTING_API, false);
        @NotNull public final Websockets websockets = new Websockets();

        public Advanced() {
            if (!debug) return;
            AnnoyingPlugin.LOGGER.setLevel(Level.FINE);
            HttpUtility.DEBUG = true;
        }

        public void setUseTestingApi(boolean newStatus) {
            if (useTestingApi == newStatus) return;

            // Update config
            useTestingApi = newStatus;
            setSave(PATH_USE_TESTING_API, newStatus);

            // Reconnect websockets
            eaPlugin.webSockets.reconnectAll("Testing API toggled");
        }

        public class Websockets {
            @NotNull public static final String PATH_LOGS = PATH_WEBSOCKETS + ".logs";
            @NotNull public static final String PATH_RETRY_DELAY = PATH_WEBSOCKETS + ".retry-delay";

            /**
             * minutes
             */
            @Nullable public Integer retryDelay = null;
            public boolean logs = getBoolean(PATH_LOGS, false);

            public Websockets() {
                final String retryDelayString = getString(PATH_RETRY_DELAY);
                if (retryDelayString == null || !retryDelayString.equals("-1")) retryDelay = Math.max(3, getInt(PATH_RETRY_DELAY, 5));
            }
        }
    }
}
