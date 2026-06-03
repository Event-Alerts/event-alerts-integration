package gg.eventalerts.eventalertsintegration.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.Serdes;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.config.serdes.HostFilterSerializer;
import gg.eventalerts.eventalertsintegration.socket.SocketEndpoint;
import org.bson.types.ObjectId;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.checkerframework.common.value.qual.MatchesRegex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.annoyingapi.file.PlayableSound;

import java.time.Duration;
import java.util.Set;


@Header("# --- WIKI ---")
@Header("# https://wiki.eventalerts.gg/EventAlertsIntegration/configuration")
public class ConfigYml extends OkaeriConfig {

    public ConfigYml(@NotNull EventAlertsIntegration plugin) {
        this.syncing = new Syncing(plugin);
        this.linking = new Linking(plugin);
        this.cross_ban = new CrossBan(plugin);
        this.event_messages = new EventMessages(plugin);
        this.advanced = new Advanced(plugin);
    }

    @Comment("API keys for Event Alerts' APIs")
    @Comment("You can set one OR both!")
    @Comment("")
    @Comment("WARNING: Do not, under ANY circumstances, share these API keys with ANYONE, no matter WHAT they say!")
    @Comment("Event Alerts staff will NEVER ask for your API keys.")
    @Comment("We recommend keeping your server files private if you choose to put your API keys here.")
    @Comment("If you think your key was leaked, regenerate it IMMEDIATELY using the appropriate command in Event Alerts' Discord server!")
    @NotNull public final ApiKeys api_keys = new ApiKeys();

    @Comment("Settings related to syncing between Event Alerts' Discord server and the Minecraft server")
    @Comment("Requires API key(s) to be set up (see above)")
    @NotNull public final Syncing syncing;

    @Comment("Settings related to Event Alerts' Minecraft-Discord linking system")
    @NotNull public final Linking linking;

    @Comment("Settings related to Event Alerts' cross-banning feature")
    @NotNull public final CrossBan cross_ban;

    @Comment("# Settings related to Event Alerts' event messages being broadcast in-game")
    @NotNull public final EventMessages event_messages;

    @Comment("Advanced settings that you probably shouldn't touch...")
    @NotNull public final Advanced advanced;

    public static class ApiKeys extends OkaeriConfig {

        @MatchesRegex("EA\\.Player\\.1\\..+")
        @Comment("This will \"connect\" your Minecraft server to your Player account, effectively marking this Minecraft server as \"your Minecraft server\"")
        @Comment("Run the `/playerapikey` command in Event Alerts' Discord server to get your API key")
        @Nullable public String player = "PLAYER_API_KEY_HERE";

        @MatchesRegex("EA\\.PartnerServer\\.1\\..+")
        @Comment("This will \"connect\" your Minecraft server to your Partner Server, effectively marking this Minecraft as \"your Partner Server's Minecraft server\"")
        @Comment("Run the `/server apikey` command in Event Alerts' Discord server to get your server's API key")
        @Nullable public String server = "SERVER_API_KEY_HERE";
    }

    public static class Syncing extends OkaeriConfig {

        public Syncing(@NotNull EventAlertsIntegration plugin) {
            this.discord_to_minecraft = new DiscordToMinecraft(plugin);
            this.minecraft_to_discord = new MinecraftToDiscord(plugin);
        }

        @Comment("Settings for Discord -> Minecraft syncing")
        @NotNull public final DiscordToMinecraft discord_to_minecraft;

        @Comment("Settings for Minecraft -> Discord syncing")
        @NotNull public final MinecraftToDiscord minecraft_to_discord;

        public static class DiscordToMinecraft extends OkaeriConfig {

            public DiscordToMinecraft(@NotNull EventAlertsIntegration plugin) {
                this.messages = new Messages(plugin);
            }

            @Comment("Settings for syncing Discord messages to Minecraft in-game chat")
            @NotNull public final Messages messages;

            public static class Messages extends OkaeriConfig {

                @NotNull private transient final EventAlertsIntegration plugin;

                public Messages(@NotNull EventAlertsIntegration plugin) {
                    this.plugin = plugin;
                }

                @Comment("Whether to send messages to the Minecraft in-game chat from the event's Event Alerts thread")
                public boolean enabled = true;

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

                    // Update config
                    enabled = newStatus;
                    set("enabled", enabled);
                    save();

                    // Reconnect websocket
                    plugin.webSockets.reconnect("Config updated", SocketEndpoint.EVENT_CHAT);
                }

                public void setFormat(@NotNull String newFormat) {
                    if (format.equals(newFormat)) return;
                    format = newFormat;
                    set("format", newFormat);
                    save();
                }
            }
        }

        public static class MinecraftToDiscord extends OkaeriConfig {

            @NotNull private transient final EventAlertsIntegration plugin;

            public MinecraftToDiscord(@NotNull EventAlertsIntegration plugin) {
                this.plugin = plugin;
            }

            @Comment("Whether to send join/quit messages in the event's Event Alerts thread in Discord")
            public boolean connections = true;

            public void setConnections(boolean newStatus) {
                if (connections == newStatus) return;

                // Update config
                connections = newStatus;
                set("connections", connections);
                save();

                // Reconnect websocket
                plugin.webSockets.reconnect("Config updated", SocketEndpoint.PLAYER_CONNECTION);
            }
        }
    }

    public static class Linking extends OkaeriConfig {

        @NotNull private transient final EventAlertsIntegration plugin;

        public Linking(@NotNull EventAlertsIntegration plugin) {
            this.plugin = plugin;
        }

        @Comment("Whether to force players to be linked with Event Alerts to join/stay on the server")
        @Comment("To bypass the requirement, give the player the eventalerts.linking.bypass permission")
        public boolean require_link = false;

        @Comment("Whether to check link status when a player joins the server")
        public boolean check_on_join = true;

        @Comment("Whether to allow players to join the server when the linking check fails")
        public boolean allow_join_on_failure = false;

        public void setRequireLink(boolean newStatus) {
            if (require_link == newStatus) return;

            // Update config
            require_link = newStatus;
            set("require-link", require_link);
            save();

            // Reconnect websocket
            plugin.webSockets.reconnect("Config updated", SocketEndpoint.LINK);
        }
    }

    public static class CrossBan extends OkaeriConfig {

        @NotNull private transient final EventAlertsIntegration plugin;

        public CrossBan(@NotNull EventAlertsIntegration plugin) {
            this.plugin = plugin;
        }

        @Comment("Whether to enable cross-ban checking")
        @Comment("Anyone with eventalerts.crossban.bypass will be exempt from cross-bans")
        public boolean enabled = true;

        @Comment("Whether to check cross-ban status when a player joins the server")
        public boolean check_on_join = true;

        @Comment("Whether to allow players to join the server when the cross-ban check fails")
        public boolean allow_join_on_failure = false;

        public void setEnabled(boolean newStatus) {
            if (enabled == newStatus) return;

            // Update config
            enabled = newStatus;
            set("enabled", enabled);
            save();

            // Reconnect websocket
            plugin.webSockets.reconnect("Config updated", SocketEndpoint.CROSS_BAN);
        }
    }

    public static class EventMessages extends OkaeriConfig {

        @NotNull private transient final EventAlertsIntegration plugin;

        public EventMessages(@NotNull EventAlertsIntegration plugin) {
            this.plugin = plugin;
        }

        @Comment("Whether to enable event messages being broadcast in the server chat")
        public boolean enabled = false;

        @Comment("1.20.5+")
        @Comment("If an IP is detected in an event message, players will be able to click a button to join the event's server using transfer packets")
        public boolean detect_ips = true;

        @Comment("The sound to play when an event message is broadcasted")
        @NotNull public final ConfigYml.EventMessages.SoundYml sound = new SoundYml();

        @Comment("Types of events that shouldn't be broadcasted in the server chat")
        @Comment("Possible values: SKEPPY, POTENTIAL_FAMOUS, SIGHTING, FAMOUS, PARTNER, COMMUNITY")
        @NotNull public final Set<EventType> ignored_types = Set.of(EventType.SIGHTING);

        @Comment("Ignore Partner events that mention any of these roles")
        @Comment("Possible values: BIG_MONEY, MONEY, FUN, HOUSING, CIVILIZATION")
        @NotNull public final Set<PingRole> ignored_partner_roles = Set.of(PingRole.HOUSING, PingRole.CIVILIZATION);

        @Comment("Ignore Partner/Community events that are posted using any of these formats")
        @Comment("Possible values: CUSTOM, BUILT")
        @NotNull public final Set<EventFormat> ignored_formats = Set.of();

        @Serdes(serializer = HostFilterSerializer.class)
        @Comment("Only broadcast events from these specific hosts")
        @Comment("You can include both server EA IDs (found in footer of '/server get') and host IDs (Discord user IDs)")
        @NotNull public final Set<String> host_filter = Set.of();

        public boolean isInHostFilter(@NotNull ObjectId serverId) {
            return host_filter.contains(serverId.toString());
        }

        public boolean isInHostFilter(long hostId) {
            return host_filter.contains(Long.toString(hostId));
        }

        public void setEnabled(boolean newStatus) {
            if (enabled == newStatus) return;

            // Update config
            enabled = newStatus;
            set("enabled", enabled);
            save();

            // Reconnect websocket
            plugin.webSockets.reconnect("Config updated", SocketEndpoint.EVENT_POSTED, SocketEndpoint.FAMOUS_EVENT_POSTED);
        }

        public boolean toggleIgnoredType(@NotNull EventType type) {
            return toggleSetItem("ignored-types", ignored_types, type);
        }

        public boolean toggleIgnoredPartnerRole(@NotNull PingRole role) {
            return toggleSetItem("ignored-partner-roles", ignored_partner_roles, role);
        }

        private <T> boolean toggleSetItem(@NotNull String path, @NotNull Set<T> set, @NotNull T item) {
            final boolean newStatus = !set.remove(item);
            if (newStatus) set.add(item);

            // Save the updated set to the config
            set(path, set.stream()
                    .map(Object::toString)
                    .toList());
            save();
            return newStatus;
        }

        public static class SoundYml extends OkaeriConfig {

            @Comment("Whether to play a sound")
            public boolean enabled = true;

            @Comment("The sound to play")
            @NotNull public final PlayableSound sound = new PlayableSound(Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.AMBIENT);
        }
    }

    public static class Advanced extends OkaeriConfig {

        @NotNull private transient final EventAlertsIntegration plugin;

        public Advanced(@NotNull EventAlertsIntegration plugin) {
            this.plugin = plugin;
        }

        @Comment("Whether to enable debug logging")
        public final boolean debug = false;

        @Comment("Whether to enable using the testing API hosts")
        @Comment("Only the developer really needs to enable this")
        public boolean use_testing_api = false;

        @Comment("Settings for websocket connections")
        @NotNull public final ConfigYml.Advanced.Websocket websocket = new Websocket();

        public void setUseTestingApi(boolean newStatus) {
            if (use_testing_api == newStatus) return;

            // Update config
            use_testing_api = newStatus;
            set("use-testing-api", use_testing_api);
            save();

            // Reconnect websockets
            plugin.webSockets.reconnectAll("Testing API toggled");
        }

        public static class Websocket extends OkaeriConfig {

            @Comment("Whether to automatically reconnect to the websocket if it is disconnected")
            public boolean retry = true;

            @Comment("Duration until the websocket attempts to reconnect after being disconnected (min: 3 minutes)")
            @NotNull public Duration retry_delay = Duration.ofMinutes(5);

            @Comment("Whether to log websocket connection messages")
            public boolean logs = false;
        }
    }
}
