package gg.eventalerts.eventalertsintegration.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.commons.duration.DurationSpec;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.config.validator.annotation.DurationRange;
import gg.eventalerts.eventalertsintegration.config.validator.annotation.PatternCollection;
import gg.eventalerts.eventalertsintegration.socket.SocketEndpoint;
import org.bson.types.ObjectId;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.annoyingapi.file.PlayableSound;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.DurationFormatter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;


@Header("# --- WIKIS ---")
@Header("# 1: https://wiki.eventalerts.gg/EventAlertsIntegration/configuration")
@Header("# 2: https://github.com/srnyx/annoying-api/wiki/File-objects")
public class ConfigYml extends OkaeriConfig {
    /**
     * @param   plugin  Only {@code null} for unit tests
     */
    public ConfigYml(@Nullable EventAlertsIntegration plugin) {
        this.syncing = new Syncing(plugin);
        this.linking = new Linking(plugin);
        this.cross_ban = new CrossBan(plugin);
        this.event_messages = new EventMessages(plugin);
        this.advanced = new Advanced(plugin);
    }

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
    @NotNull public ApiKeys api_keys = new ApiKeys();

    @Comment
    @Comment
    @Comment("Settings related to syncing between Event Alerts' Discord server and the Minecraft server")
    @Comment("Requires API key(s) to be set up (see above)")
    @NotNull public Syncing syncing;

    @Comment
    @Comment
    @Comment("Settings related to Event Alerts' Minecraft-Discord linking system")
    @NotNull public Linking linking;

    @Comment
    @Comment
    @Comment("Settings related to Event Alerts' cross-banning feature")
    @NotNull public CrossBan cross_ban;

    @Comment
    @Comment
    @Comment("# Settings related to Event Alerts' event messages being broadcast in-game")
    @NotNull public EventMessages event_messages;

    @Comment
    @Comment
    @Comment("Advanced settings that you probably shouldn't touch...")
    @NotNull public Advanced advanced;

    public static class ApiKeys extends OkaeriConfig {

        @Comment("This will \"connect\" your Minecraft server to your Player account, effectively marking this Minecraft server as \"your Minecraft server\"")
        @Comment("Run the `/playerapikey` command in Event Alerts' Discord server to get your API key")
        @NotNull private String player = "PLAYER_API_KEY_HERE";

        @Comment
        @Comment("This will \"connect\" your Minecraft server to your Partner Server, effectively marking this Minecraft as \"your Partner Server's Minecraft server\"")
        @Comment("Run the `/server apikey` command in Event Alerts' Discord server to get your server's API key")
        @NotNull private String server = "SERVER_API_KEY_HERE";

        @Nullable
        public String getPlayer() {
            return player.startsWith("EA.Player.1.") ? player : null;
        }

        @Nullable
        public String getServer() {
            return server.startsWith("EA.PartnerServer.1.") ? server : null;
        }
    }

    public static class Syncing extends OkaeriConfig {
        public Syncing(@Nullable EventAlertsIntegration plugin) {
            this.discord_to_minecraft = new DiscordToMinecraft(plugin);
            this.minecraft_to_discord = new MinecraftToDiscord(plugin);
        }

        @Comment("Settings for Discord -> Minecraft syncing")
        @NotNull public DiscordToMinecraft discord_to_minecraft;

        @Comment
        @Comment("Settings for Minecraft -> Discord syncing")
        @NotNull public MinecraftToDiscord minecraft_to_discord;

        public static class DiscordToMinecraft extends OkaeriConfig {
            public DiscordToMinecraft(@Nullable EventAlertsIntegration plugin) {
                this.messages = new Messages(plugin);
            }

            @Comment("Settings for syncing Discord messages to Minecraft in-game chat")
            @NotNull public Messages messages;

            public static class Messages extends OkaeriConfig {
                @Nullable private transient final EventAlertsIntegration plugin;

                public Messages(@Nullable EventAlertsIntegration plugin) {
                    this.plugin = plugin;
                }

                @Comment("Whether to send messages to the Minecraft in-game chat from the event's Event Alerts thread")
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

                    if (plugin != null) plugin.webSockets.reconnect("Config updated", SocketEndpoint.EVENT_CHAT);
                }

                public void setFormat(@NotNull String newFormat) {
                    if (format.equals(newFormat)) return;
                    format = newFormat;
                    save();
                }
            }
        }

        public static class MinecraftToDiscord extends OkaeriConfig {
            @Nullable private transient final EventAlertsIntegration plugin;

            public MinecraftToDiscord(@Nullable EventAlertsIntegration plugin) {
                this.plugin = plugin;
            }

            @Comment("Whether to send join/quit messages in the event's Event Alerts thread in Discord")
            public boolean connections = true;

            public void setConnections(boolean newStatus) {
                if (connections == newStatus) return;

                connections = newStatus;
                save();

                if (plugin != null) plugin.webSockets.reconnect("Config updated", SocketEndpoint.PLAYER_CONNECTION);
            }
        }
    }

    public static class Linking extends OkaeriConfig {
        @Nullable private transient final EventAlertsIntegration plugin;

        public Linking(@Nullable EventAlertsIntegration plugin) {
            this.plugin = plugin;
        }

        @Comment("Whether to force players to be linked with Event Alerts to join/stay on the server")
        @Comment("To bypass the requirement, give the player the eventalerts.linking.bypass permission")
        public boolean require_link = false;

        @Comment
        @Comment("Whether to check link status when a player joins the server")
        public boolean check_on_join = true;

        @Comment
        @Comment("Whether to allow players to join the server when the linking check fails")
        public boolean allow_join_on_failure = false;

        public void setRequireLink(boolean newStatus) {
            if (require_link == newStatus) return;

            require_link = newStatus;
            save();

            if (plugin != null) plugin.webSockets.reconnect("Config updated", SocketEndpoint.LINK);
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

    public static class CrossBan extends OkaeriConfig {
        @Nullable private transient final EventAlertsIntegration plugin;

        public CrossBan(@Nullable EventAlertsIntegration plugin) {
            this.plugin = plugin;
        }

        @Comment("Whether to enable cross-ban checking")
        @Comment("Anyone with eventalerts.crossban.bypass will be exempt from cross-bans")
        public boolean enabled = true;

        @Comment
        @Comment("Whether to check cross-ban status when a player joins the server")
        public boolean check_on_join = true;

        @Comment
        @Comment("Whether to allow players to join the server when the cross-ban check fails")
        public boolean allow_join_on_failure = false;

        public void setEnabled(boolean newStatus) {
            if (enabled == newStatus) return;

            enabled = newStatus;
            save();

            if (plugin != null) plugin.webSockets.reconnect("Config updated", SocketEndpoint.CROSS_BAN);
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

    public static class EventMessages extends OkaeriConfig {
        @Nullable private transient final EventAlertsIntegration plugin;

        public EventMessages(@Nullable EventAlertsIntegration plugin) {
            this.plugin = plugin;
        }

        @Comment("Whether to enable event messages being broadcast in the server chat")
        public boolean enabled = false;

        @Comment
        @Comment("1.20.5+")
        @Comment("If an IP is detected in an event message, players will be able to click a button to join the event's server using transfer packets")
        public boolean detect_ips = true;

        @Comment
        @Comment("The sound to play when an event message is broadcasted")
        @NotNull public SoundYml sound = new SoundYml();

        @Comment
        @Comment("Types of events that shouldn't be broadcasted in the server chat")
        @Comment("Possible values: SKEPPY, POTENTIAL_FAMOUS, SIGHTING, FAMOUS, PARTNER, COMMUNITY")
        @NotNull public Set<EventType> ignored_types = new HashSet<>(Set.of(EventType.SIGHTING));

        @Comment
        @Comment("Ignore Partner events that mention any of these roles")
        @Comment("Possible values: BIG_MONEY, MONEY, FUN, HOUSING, CIVILIZATION")
        @NotNull public Set<PingRole> ignored_partner_roles = new HashSet<>(Set.of(PingRole.HOUSING, PingRole.CIVILIZATION));

        @Comment
        @Comment("Ignore Partner/Community events that are posted using any of these formats")
        @Comment("Possible values: CUSTOM, BUILT")
        @NotNull public Set<EventFormat> ignored_formats = new HashSet<>();

        @Comment
        @Comment("Only broadcast events from these specific hosts")
        @Comment("You can include both server EA IDs (found in footer of '/server get') and host IDs (Discord user IDs)")
        @PatternCollection("^(?:[0-9a-fA-F]{24}|\\d+)$")
        @NotNull public Set<String> host_filter = new HashSet<>();

        public boolean isInHostFilter(@NotNull ObjectId serverId) {
            return host_filter.contains(serverId.toString());
        }

        public boolean isInHostFilter(long hostId) {
            return host_filter.contains(Long.toString(hostId));
        }

        public void setEnabled(boolean newStatus) {
            if (enabled == newStatus) return;

            enabled = newStatus;
            save();

            if (plugin != null) plugin.webSockets.reconnect("Config updated", SocketEndpoint.EVENT_POSTED, SocketEndpoint.FAMOUS_EVENT_POSTED);
        }

        public void setDetectIps(boolean newStatus) {
            if (detect_ips == newStatus) return;

            detect_ips = newStatus;
            save();
        }

        public boolean toggleIgnoredType(@NotNull EventType type) {
            return toggleSetItem(ignored_types, type);
        }

        public boolean toggleIgnoredPartnerRole(@NotNull PingRole role) {
            return toggleSetItem(ignored_partner_roles, role);
        }

        public boolean toggleIgnoredFormat(@NotNull EventFormat format) {
            return toggleSetItem(ignored_formats, format);
        }

        public boolean addHostFilter(@NotNull String id) {
            if (!host_filter.add(id)) return false;
            save();
            return true;
        }

        public boolean removeHostFilter(@NotNull String id) {
            if (!host_filter.remove(id)) return false;
            save();
            return true;
        }

        private <T> boolean toggleSetItem(@NotNull Set<T> set, @NotNull T item) {
            final boolean newStatus = !set.remove(item);
            if (newStatus) set.add(item);

            save();
            return newStatus;
        }

        public static class SoundYml extends OkaeriConfig {

            @Comment("Whether to play a sound")
            public boolean enabled = true;

            @Comment
            @Comment("The sound to play (SEE WIKI #2)")
            @NotNull public PlayableSound sound = new PlayableSound(Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.AMBIENT);

            public void setEnabled(boolean newStatus) {
                if (enabled == newStatus) return;
                enabled = newStatus;
                save();
            }

            public void setSound(@NotNull Sound newSound) {
                if (sound.sound == newSound) return;
                sound = new PlayableSound(newSound, sound.category, sound.volume, sound.pitch);
                save();
            }

            public void setVolume(float newVolume) {
                if (Float.compare(sound.volume, newVolume) == 0) return;
                sound = new PlayableSound(sound.sound, sound.category, newVolume, sound.pitch);
                save();
            }

            public void setPitch(float newPitch) {
                if (Float.compare(sound.pitch, newPitch) == 0) return;
                sound = new PlayableSound(sound.sound, sound.category, sound.volume, newPitch);
                save();
            }

            public void setCategory(@NotNull SoundCategory newCategory) {
                if (sound.category == newCategory) return;
                sound = new PlayableSound(sound.sound, newCategory, sound.volume, sound.pitch);
                save();
            }
        }
    }

    public static class Advanced extends OkaeriConfig {
        @Nullable private transient final EventAlertsIntegration plugin;

        public Advanced(@Nullable EventAlertsIntegration plugin) {
            this.plugin = plugin;
        }

        @Comment("Whether to enable debug logging")
        public boolean debug = false;

        @Comment
        @Comment("Whether to enable using the testing API hosts")
        @Comment("Only the developer really needs to enable this")
        public boolean use_testing_api = false;

        @Comment
        @Comment("Settings for websocket connections")
        @NotNull public ConfigYml.Advanced.Websocket websocket = new Websocket();

        public void setUseTestingApi(boolean newStatus) {
            if (use_testing_api == newStatus) return;

            use_testing_api = newStatus;
            save();

            if (plugin != null) plugin.webSockets.reconnectAll("Testing API toggled");
        }

        public static class Websocket extends OkaeriConfig {

            @Comment("Whether to automatically reconnect to the websocket if it is disconnected")
            public boolean retry = true;

            @Comment
            @Comment("Duration until the websocket attempts to reconnect after being disconnected (min: 3 minutes)")
            @DurationSpec(fallbackUnit = ChronoUnit.MINUTES)
            @DurationRange(min = 3, minUnit = ChronoUnit.MINUTES)
            @NotNull public Duration retry_delay = Duration.ofMinutes(5);

            @Comment
            @Comment("Whether to log websocket connection messages")
            public boolean logs = false;

            @NotNull
            public static String formatRetryDelay(@NotNull Duration duration) {
                return DurationFormatter.formatDuration(duration.toMillis(), "H'h' m'm' s's'");
            }

            public void setRetry(boolean newStatus) {
                if (retry == newStatus) return;
                retry = newStatus;
                save();
            }

            public void setRetryDelay(@NotNull Duration newRetryDelay) {
                if (retry_delay.equals(newRetryDelay)) return;
                retry_delay = newRetryDelay;
                save();
            }

            public void setLogs(boolean newStatus) {
                if (logs == newStatus) return;
                logs = newStatus;
                save();
            }
        }
    }
}
