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
    @NotNull public static final String PATH_SYNCING = "syncing";
    @NotNull public static final String PATH_LINKING = "linking";
    @NotNull public static final String PATH_CROSS_BAN = "cross-ban";
    @NotNull public static final String PATH_EVENT_MESSAGES = "event-messages";
    @NotNull public static final String PATH_ADVANCED = "advanced";

    @NotNull private final EventAlertsIntegration eaPlugin;

    @NotNull public final ApiKeys apiKeys;
    @NotNull public final ConfigYml.Syncing syncing;
    @NotNull public final Linking linking;
    @NotNull public final CrossBan crossBan;
    @NotNull public final EventMessages eventMessages;
    @NotNull public final Advanced advanced;

    public ConfigYml(@NotNull EventAlertsIntegration plugin) {
        super(plugin, "config.yml");
        eaPlugin = plugin;

        apiKeys = new ApiKeys();
        syncing = new Syncing();
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
            if (playerApiKey != null && !playerApiKey.startsWith("EA.Player.1.")) playerApiKey = null;
            if (serverApiKey != null && !serverApiKey.startsWith("EA.PartnerServer.1.")) serverApiKey = null;
        }
    }

    public class Syncing {
        @NotNull public static final String PATH_DISCORD_TO_MINECRAFT = PATH_SYNCING + ".discord-to-minecraft";
        @NotNull public static final String PATH_MINECRAFT_TO_DISCORD = PATH_SYNCING + ".minecraft-to-discord";

        @NotNull public final DiscordToMinecraft discordToMinecraft = new DiscordToMinecraft();
        @NotNull public final MinecraftToDiscord minecraftToDiscord = new MinecraftToDiscord();

        public class DiscordToMinecraft {
            @NotNull public static final String PATH_MESSAGES = PATH_DISCORD_TO_MINECRAFT + ".messages";

            @NotNull public final Messages messages = new Messages();

            public class Messages {
                @NotNull public static final String PATH_ENABLED = PATH_MESSAGES + ".enabled";
                @NotNull public static final String PATH_FORMAT = PATH_MESSAGES + ".format";

                public boolean enabled = getBoolean(PATH_ENABLED, true);
                @NotNull public String format = getString(PATH_FORMAT, "<dark_aqua>\uD83C\uDF89 [<event_title>] <aqua>[<author_name>] <content_stripped>");

                public void setEnabled(boolean newStatus) {
                    if (enabled == newStatus) return;

                    // Update config
                    enabled = newStatus;
                    setSave(PATH_ENABLED, newStatus);

                    // Reconnect websocket
                    eaPlugin.webSockets.reconnect("Config updated", SocketEndpoint.EVENT_CHAT);
                }

                public void setFormat(@NotNull String newFormat) {
                    if (format.equals(newFormat)) return;
                    format = newFormat;
                    setSave(PATH_FORMAT, newFormat);
                }
            }
        }

        public class MinecraftToDiscord {
            @NotNull public static final String PATH_CONNECTIONS = PATH_MINECRAFT_TO_DISCORD + ".connections";

            public boolean connections = getBoolean(PATH_CONNECTIONS, true);

            public void setConnections(boolean newStatus) {
                if (connections == newStatus) return;

                // Update config
                connections = newStatus;
                setSave(PATH_CONNECTIONS, newStatus);

                // Reconnect websocket
                eaPlugin.webSockets.reconnect("Config updated", SocketEndpoint.PLAYER_CONNECTION);
            }
        }
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
