package gg.eventalerts.eventalertsintegration.config;

import com.cryptomorin.xseries.XSound;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.Variable;
import eu.okaeri.configs.serdes.commons.duration.DurationSpec;
import eu.okaeri.validator.annotation.NotNull;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.config.key.PartnerServerKey;
import gg.eventalerts.eventalertsintegration.config.key.PlayerKey;
import gg.eventalerts.eventalertsintegration.socket.listeners.CrossBanListener;
import gg.eventalerts.eventalertsintegration.socket.listeners.EventChatListener;
import gg.eventalerts.eventalertsintegration.socket.listeners.EventPostedListener;
import gg.eventalerts.eventalertsintegration.socket.listeners.FamousEventPostedListener;
import gg.eventalerts.eventalertsintegration.socket.listeners.LinkListener;
import gg.eventalerts.sdk.http.EAHTTP;
import gg.eventalerts.sdk.object.EAEvent;
import gg.eventalerts.sdk.websocket.EAWebSocket;
import org.bson.types.ObjectId;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.file.PlayableSound;
import xyz.srnyx.annoyingapi.file.okaeri.RootConfig;
import xyz.srnyx.annoyingapi.file.okaeri.SubConfig;
import xyz.srnyx.annoyingapi.file.okaeri.validator.annotation.DurationRange;
import xyz.srnyx.annoyingapi.file.okaeri.validator.annotation.PatternCollection;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.DurationFormatter;
import xyz.srnyx.annoyingapi.stats.Stat;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;


@Header("# --- WIKIS ---")
@Header("# 1: https://wiki.eventalerts.gg/EventAlertsIntegration/configuration")
@Header("# 2: https://github.com/srnyx/annoying-api/wiki/File-objects")
public class ConfigYml extends RootConfig {
    @Comment
    @Comment
    @Comment
    @Comment("API keys for Event Alerts' APIs")
    @Comment("You can set one OR both!")
    @Comment(" ")
    @Comment("WARNING: Do not, under ANY circumstances, share these API keys with ANYONE, no matter WHAT they say!")
    @Comment("Event Alerts staff will NEVER ask for your API keys.")
    @Comment("We recommend keeping your server files private if you choose to put your API keys here.")
    @Comment("If you think your key was leaked, regenerate it IMMEDIATELY using the appropriate command in Event Alerts' Discord server!")
    @NotNull public ApiKeys api_keys = new ApiKeys(this);

    @Comment
    @Comment
    @Comment("Settings related to syncing between Event Alerts' Discord server and the Minecraft server")
    @Comment("Requires API key(s) to be set up (see above)")
    @NotNull public Syncing syncing = new Syncing(this);

    @Comment
    @Comment
    @Comment("Settings related to Event Alerts' Minecraft-Discord linking system")
    @NotNull public Linking linking = new Linking(this);

    @Comment
    @Comment
    @Comment("Settings related to Event Alerts' cross-banning feature")
    @NotNull public CrossBan cross_ban = new CrossBan(this);

    @Comment
    @Comment
    @Comment("# Settings related to Event Alerts' event messages being broadcast in-game")
    @NotNull public EventMessages event_messages = new EventMessages(this);

    @Comment
    @Comment
    @Comment("Advanced settings that you probably shouldn't touch...")
    @NotNull public Advanced advanced = new Advanced(this);


    @org.jetbrains.annotations.NotNull private transient final EventAlertsIntegration plugin;

    public ConfigYml(@org.jetbrains.annotations.NotNull EventAlertsIntegration plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
        plugin.setDebug(advanced.debug);
        loadSDK();
    }

    public void loadSDK() {
        if (plugin.options.pluginOptions.isMock) return;
        final String userAgent = plugin.getName() + "/" + plugin.getDescription().getVersion() + " (MC/" + AnnoyingPlugin.MINECRAFT_VERSION + ")";

        // Load HTTP
        plugin.http = new EAHTTP.Builder(userAgent)
                .url(advanced.use_testing_api ? "http://localhost:8080/api/v1/" : "https://eventalerts.gg/api/v1/")
                .playerKey(api_keys.player.key)
                .serverKey(api_keys.server.key)
                .build();

        // Load WebSocket
        if (plugin.webSocket != null) try {
            plugin.webSocket.closeBlocking();
        } catch (final InterruptedException e) {
            AnnoyingPlugin.log(Level.WARNING, "Failed to close WebSocket", e);
        }
        plugin.webSocket = new EAWebSocket.Builder(userAgent)
                .url(advanced.use_testing_api ? "ws://localhost:9090/api/v1/socket" : "wss://eventalerts.gg/api/v1/socket")
                .handler(
                        new CrossBanListener(plugin),
                        new EventChatListener(plugin),
                        new EventPostedListener(plugin),
                        new FamousEventPostedListener(plugin),
                        new LinkListener(plugin))
                .retry(advanced.websocket.retry)
                .retryDelay(advanced.websocket.retry_delay)
                .playerKey(api_keys.player.key)
                .serverKey(api_keys.server.key)
                .buildThenConnect();
    }

    public static class ApiKeys extends SubConfig<ConfigYml, ConfigYml> {
        public ApiKeys(@org.jetbrains.annotations.NotNull ConfigYml root) {
            super(root);
        }

        @Comment("This will \"connect\" your Minecraft server to your Player account, effectively marking this Minecraft server as \"your Minecraft server\"")
        @Comment("Run the `/playerapikey` command in Event Alerts' Discord server to get your API key")
        @Variable("EA_PLAYER_KEY") @Stat
        @NotNull public PlayerKey player = new PlayerKey();

        @Comment
        @Comment("This will \"connect\" your Minecraft server to your Partner Server, effectively marking this Minecraft as \"your Partner Server's Minecraft server\"")
        @Comment("Run the `/server apikey` command in Event Alerts' Discord server to get your server's API key")
        @Variable("EA_PARTNER_SERVER_KEY") @Stat
        @NotNull public PartnerServerKey server = new PartnerServerKey();
    }

    public static class Syncing extends SubConfig<ConfigYml, ConfigYml> {
        public Syncing(@org.jetbrains.annotations.NotNull ConfigYml root) {
            super(root);
        }

        @Comment("Settings for Discord -> Minecraft syncing")
        @NotNull public DiscordToMinecraft discord_to_minecraft = new DiscordToMinecraft(this);

        @Comment
        @Comment("Settings for Minecraft -> Discord syncing")
        @NotNull public MinecraftToDiscord minecraft_to_discord = new MinecraftToDiscord(this);

        public static class DiscordToMinecraft extends SubConfig<ConfigYml, Syncing> {
            public DiscordToMinecraft(@org.jetbrains.annotations.NotNull Syncing root) {
                super(root);
            }

            @Comment("Settings for syncing Discord messages to Minecraft in-game chat")
            @NotNull public Messages messages = new Messages(this);

            public static class Messages extends SubConfig<ConfigYml, DiscordToMinecraft> {
                public Messages(@org.jetbrains.annotations.NotNull DiscordToMinecraft root) {
                    super(root);
                }

                @Comment("Whether to send messages to the Minecraft in-game chat from the event's Event Alerts thread")
                @Stat
                public boolean enabled = true;

                @Comment
                @Comment("The format of Discord messages in the Minecraft in-game chat")
                @Comment("Available placeholders:")
                @Comment("  - event: <event_id>, <event_type>, <event_channel>, <event_message>, <event_control_panel>, <event_custom>, <event_created>, <event_title>, <event_host>, <event_description>, <event_roles>, <event_roles_named>, <event_server>, <event_media_name>, <event_ip>, <event_platform>, <event_version>, <event_prize>, <event_max_players>, <event_time>, <event_subscribers>")
                @Comment("  - channel: <channel_id>, <channel_name>")
                @Comment("  - author: <author_id>, <author_name>, <author_effectivename>")
                @Comment("  - message: <message_id>, <message_content_raw>, <message_content_display>, <message_content_stripped>")
                @Comment("  - other: <player_name>")
                @Comment("  - Any PlaceholderAPI placeholder (%placeholder%)")
                @NotNull public String format = "<dark_aqua>\uD83C\uDF89 [<event_title>] <aqua>[<author_name>] <message_content_stripped><message_attachments_pretty>";

                public void setEnabled(boolean newStatus) {
                    if (enabled == newStatus) return;
                    enabled = newStatus;
                    save();

                    getRoot().plugin.webSocket.updateSubscriptions();
                }

                public void setFormat(@org.jetbrains.annotations.NotNull String newFormat) {
                    if (format.equals(newFormat)) return;
                    format = newFormat;
                    save();
                }
            }
        }

        public static class MinecraftToDiscord extends SubConfig<ConfigYml, Syncing> {
            public MinecraftToDiscord(@org.jetbrains.annotations.NotNull Syncing root) {
                super(root);
            }

            @Comment("Whether to send join/quit messages in the event's Event Alerts thread in Discord")
            @Stat
            public boolean connections = true;

            public void setConnections(boolean newStatus) {
                if (connections == newStatus) return;
                connections = newStatus;
                save();

                getRoot().plugin.webSocket.updateSubscriptions();
            }
        }
    }

    public static class Linking extends SubConfig<ConfigYml, ConfigYml> {
        public Linking(@org.jetbrains.annotations.NotNull ConfigYml root) {
            super(root);
        }

        @Comment("Whether to force players to be linked with Event Alerts to join/stay on the server")
        @Comment("To bypass the requirement, give the player the eventalerts.linking.bypass permission")
        @Stat
        public boolean require_link = false;

        @Comment
        @Comment("Whether to check link status when a player joins the server")
        @Stat
        public boolean check_on_join = true;

        @Comment
        @Comment("Whether to allow players to join the server when the linking check fails")
        @Stat
        public boolean allow_join_on_failure = false;

        public void setRequireLink(boolean newStatus) {
            if (require_link == newStatus) return;
            require_link = newStatus;
            save();

            getRoot().plugin.webSocket.updateSubscriptions();
        }

        public void setCheckOnJoin(boolean newStatus) {
            if (check_on_join == newStatus) return;
            check_on_join = newStatus;
            save();
        }

        public void setAllowJoinOnFailure(boolean newStatus) {
            if (allow_join_on_failure == newStatus) return;
            allow_join_on_failure = newStatus;
            save();
        }
    }

    public static class CrossBan extends SubConfig<ConfigYml, ConfigYml> {
        public CrossBan(@org.jetbrains.annotations.NotNull ConfigYml root) {
            super(root);
        }

        @Comment("Whether to enable cross-ban checking")
        @Comment("Anyone with eventalerts.crossban.bypass will be exempt from cross-bans")
        @Stat
        public boolean enabled = true;

        @Comment
        @Comment("Whether to check cross-ban status when a player joins the server")
        @Stat
        public boolean check_on_join = true;

        @Comment
        @Comment("Whether to allow players to join the server when the cross-ban check fails")
        @Stat
        public boolean allow_join_on_failure = false;

        public void setEnabled(boolean newStatus) {
            if (enabled == newStatus) return;
            enabled = newStatus;
            save();

            getRoot().plugin.webSocket.updateSubscriptions();
        }

        public void setCheckOnJoin(boolean newStatus) {
            if (check_on_join == newStatus) return;
            check_on_join = newStatus;
            save();
        }

        public void setAllowJoinOnFailure(boolean newStatus) {
            if (allow_join_on_failure == newStatus) return;
            allow_join_on_failure = newStatus;
            save();
        }
    }

    public static class EventMessages extends SubConfig<ConfigYml, ConfigYml> {
        public EventMessages(@org.jetbrains.annotations.NotNull ConfigYml root) {
            super(root);
        }

        @Comment("Whether to enable event messages being broadcast in the server chat")
        @Stat
        public boolean enabled = false;

        @Comment
        @Comment("1.20.5+")
        @Comment("If an IP is detected in an event message, players will be able to click a button to join the event's server using transfer packets")
        @Stat
        public boolean detect_ips = true;

        @Comment
        @Comment("The sound to play when an event message is broadcasted")
        @NotNull public SoundYml sound = new SoundYml(this);

        @Comment
        @Comment("Types of events that shouldn't be broadcasted in the server chat")
        @Comment("Possible values: SKEPPY, POTENTIAL_FAMOUS, SIGHTING, FAMOUS, PARTNER, COMMUNITY")
        @Stat
        @NotNull public Set<EventType> ignored_types = new HashSet<>(Set.of(EventType.SIGHTING));

        @Comment
        @Comment("Ignore Partner events that mention any of these roles")
        @Comment("Possible values: BIG_MONEY, MONEY, FUN, HOUSING, CIVILIZATION")
        @Stat
        @NotNull public Set<EAEvent.PingRole> ignored_partner_roles = new HashSet<>(Set.of(EAEvent.PingRole.HOUSING, EAEvent.PingRole.CIVILIZATION));

        @Comment
        @Comment("Ignore Partner/Community events that are posted using any of these formats")
        @Comment("Possible values: CUSTOM, BUILT")
        @Stat
        @NotNull public Set<EventFormat> ignored_formats = new HashSet<>();

        @Comment
        @Comment("Only broadcast events from these specific hosts")
        @Comment("You can include both server EA IDs (found in footer of '/server get') and host IDs (Discord user IDs)")
        @PatternCollection("^(?:[0-9a-fA-F]{24}|\\d+)$") @Stat
        @NotNull public Set<String> host_filter = new HashSet<>();

        public boolean isInHostFilter(@org.jetbrains.annotations.NotNull ObjectId serverId) {
            return host_filter.isEmpty() || host_filter.contains(serverId.toString());
        }

        public boolean isInHostFilter(long hostId) {
            return host_filter.isEmpty() || host_filter.contains(Long.toString(hostId));
        }

        public void setEnabled(boolean newStatus) {
            if (enabled == newStatus) return;
            enabled = newStatus;
            save();

            getRoot().plugin.webSocket.updateSubscriptions();
        }

        public void setDetectIps(boolean newStatus) {
            if (detect_ips == newStatus) return;
            detect_ips = newStatus;
            save();
        }

        public boolean toggleIgnoredType(@org.jetbrains.annotations.NotNull EventType type) {
            return toggleSetItem(ignored_types, type);
        }

        public boolean toggleIgnoredPartnerRole(@org.jetbrains.annotations.NotNull EAEvent.PingRole role) {
            return toggleSetItem(ignored_partner_roles, role);
        }

        public boolean toggleIgnoredFormat(@org.jetbrains.annotations.NotNull EventFormat format) {
            return toggleSetItem(ignored_formats, format);
        }

        public boolean addHostFilter(@org.jetbrains.annotations.NotNull String id) {
            if (!host_filter.add(id)) return false;
            save();
            return true;
        }

        public void removeHostFilter(@org.jetbrains.annotations.NotNull String id) {
            if (host_filter.remove(id)) save();
        }

        private <T> boolean toggleSetItem(@org.jetbrains.annotations.NotNull Set<T> set, @org.jetbrains.annotations.NotNull T item) {
            final boolean newStatus = !set.remove(item);
            if (newStatus) set.add(item);
            save();
            return newStatus;
        }

        public static class SoundYml extends SubConfig<ConfigYml, EventMessages> {
            public SoundYml(@org.jetbrains.annotations.NotNull EventMessages root) {
                super(root);
            }

            @Comment("Whether to play a sound")
            @Stat
            public boolean enabled = true;

            @Comment
            @Comment("The sound to play (SEE WIKI #2)")
            @Stat
            @NotNull public PlayableSound sound = new PlayableSound(Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.AMBIENT);

            public void setEnabled(boolean newStatus) {
                if (enabled == newStatus) return;
                enabled = newStatus;
                save();
            }

            public void setSound(@NotNull XSound newSound) {
                if (sound.sound == newSound) return;
                sound.sound = newSound;
                save();
            }

            public void setVolume(float newVolume) {
                if (Float.compare(sound.volume, newVolume) == 0) return;
                sound.volume = newVolume;
                save();
            }

            public void setPitch(float newPitch) {
                if (Float.compare(sound.pitch, newPitch) == 0) return;
                sound.pitch = newPitch;
                save();
            }

            public void setCategory(@org.jetbrains.annotations.NotNull XSound.Category newCategory) {
                if (sound.category == newCategory) return;
                sound.category = newCategory;
                save();
            }
        }
    }

    public static class Advanced extends SubConfig<ConfigYml, ConfigYml> {
        public Advanced(@org.jetbrains.annotations.NotNull ConfigYml root) {
            super(root);
        }

        @Comment("Whether to enable debug logging")
        @Stat
        public boolean debug = false;

        @Comment
        @Comment("Whether to enable using the testing API hosts")
        @Comment("Only the developer really needs to enable this")
        @Stat
        public boolean use_testing_api = false;

        @Comment
        @Comment("Settings for websocket connections")
        @NotNull public Websocket websocket = new Websocket(this);

        public void setDebug(boolean newStatus) {
            if (debug == newStatus) return;
            debug = newStatus;
            save();

            getRoot().plugin.setDebug(newStatus);
        }

        public void setUseTestingApi(boolean newStatus) {
            if (use_testing_api == newStatus) return;
            use_testing_api = newStatus;
            save();

            getRoot().loadSDK();
        }

        public static class Websocket extends SubConfig<ConfigYml, Advanced> {
            public Websocket(@org.jetbrains.annotations.NotNull Advanced root) {
                super(root);
            }

            @org.jetbrains.annotations.NotNull public static final Duration RETRY_DELAY_MIN = Duration.ofMinutes(3); // Change in @DurationRange too
            @org.jetbrains.annotations.NotNull public static final Duration RETRY_DELAY_DEFAULT = Duration.ofMinutes(5);

            @Comment("Whether to automatically reconnect to the websocket if it is disconnected")
            @Stat
            public boolean retry = true;

            @Comment
            @Comment("Duration until the websocket attempts to reconnect after being disconnected (min: 3 minutes)")
            @DurationSpec(fallbackUnit = ChronoUnit.MINUTES) @DurationRange(min = 3, minUnit = ChronoUnit.MINUTES) @Stat
            @NotNull public Duration retry_delay = RETRY_DELAY_DEFAULT;

            @Comment
            @Comment("Whether to log websocket connection messages")
            @Stat
            public boolean logs = false;

            public void setRetry(boolean newStatus) {
                if (retry == newStatus) return;
                retry = newStatus;
                save();
            }

            public void setRetryDelay(@org.jetbrains.annotations.NotNull Duration newRetryDelay) {
                if (retry_delay.equals(newRetryDelay)) return;
                retry_delay = newRetryDelay;
                save();
            }

            public void setLogs(boolean newStatus) {
                if (logs == newStatus) return;
                logs = newStatus;
                save();
            }

            @org.jetbrains.annotations.NotNull
            public static String formatRetryDelay(@org.jetbrains.annotations.NotNull Duration duration) {
                return DurationFormatter.formatDuration(duration.toMillis(), "H'h' m'm' s's'");
            }
        }
    }
}
