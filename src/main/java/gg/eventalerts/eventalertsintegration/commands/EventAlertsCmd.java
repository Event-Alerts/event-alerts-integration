package gg.eventalerts.eventalertsintegration.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.gui.config.ConfigMainGui;
import gg.eventalerts.eventalertsintegration.objects.CrossBan;
import gg.eventalerts.eventalertsintegration.objects.EAObject;
import gg.eventalerts.eventalertsintegration.reflection.org.bukkit.entity.RefPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.libs.javautilities.HttpUtility;
import xyz.srnyx.annoyingapi.libs.javautilities.MiscUtility;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.Mapper;
import xyz.srnyx.annoyingapi.message.AnnoyingMessage;
import xyz.srnyx.annoyingapi.utility.BukkitUtility;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;


public class EventAlertsCmd extends AnnoyingCommand {
    @NotNull private final EventAlertsIntegration plugin;

    public EventAlertsCmd(@NotNull EventAlertsIntegration plugin) {
        this.plugin = plugin;
    }

    @Override @NotNull
    public EventAlertsIntegration getAnnoyingPlugin() {
        return plugin;
    }

    @Override @NotNull
    public String getName() {
        return "eventalerts";
    }

    @Override
    public void onCommand(@NotNull AnnoyingSender sender) {
        // reload
        if (sender.argEquals(0, "reload")) {
            if (!sender.checkPermission("eventalerts.reload")) return;
            plugin.reloadPlugin();
            new AnnoyingMessage(plugin, "command.reload").send(sender);
            return;
        }

        // config
        if (sender.argEquals(0, "config")) {
            if (sender.checkPlayer() && sender.checkPermission("eventalerts.config")) new ConfigMainGui(plugin, sender.getPlayer()).open(true);
            return;
        }

        // linking
        if (sender.argEquals(0, "linking")) {
            // linking check
            if (sender.argEquals(0, "check")) {
                if (!sender.checkPermission("eventalerts.linking.check")) return;

                // Get online players + UUIDs
                final Collection<? extends Player> online = Bukkit.getOnlinePlayers();
                final String onlineString = online.stream()
                        .map(player -> player.getUniqueId().toString())
                        .collect(Collectors.joining(","));

                // Make API request
                HttpUtility.getJson(plugin.getUserAgent(), plugin.getApiHost() + "players?minecraft_uuid=" + onlineString)
                        .flatMap(json -> MiscUtility.handleException(json::getAsJsonObject))
                        .flatMap(json -> MiscUtility.handleException(() -> json.getAsJsonArray("players")))
                        .ifPresent(players -> {
                            // Get linked
                            final Set<UUID> linked = new HashSet<>();
                            for (final JsonElement player : players)
                                MiscUtility.handleException(player::getAsJsonObject)
                                        .flatMap(json -> MiscUtility.handleException(() -> json.getAsJsonObject("minecraft")))
                                        .flatMap(json -> MiscUtility.handleException(() -> json.get("uuid").getAsString()))
                                        .flatMap(uuidString -> MiscUtility.handleException(() -> UUID.fromString(uuidString)))
                                        .ifPresent(linked::add);

                            // Get unlinked
                            final Set<Player> unlinked = new HashSet<>(online);
                            unlinked.removeIf(player -> linked.contains(player.getUniqueId()));

                            // Kick unlinked players
                            final TextComponent reason = Component.text()
                                    .append(EventAlertsIntegration.GATE)
                                    .append(Component.text("All unlinked players have been kicked!\n\n", NamedTextColor.RED))
                                    .append(EventAlertsIntegration.LINKING_INSTRUCTIONS)
                                    .build();
                            unlinked.forEach(player -> player.kick(reason));

                            // Send message
                            new AnnoyingMessage(plugin, "command.linking.check.result")
                                    .replace("%linked%", linked.size())
                                    .replace("%unlinked%", unlinked.size())
                                    .replace("%total%", online.size())
                                    .send(sender);
                        });
                return;
            }

            if (sender.args.length < 3) {
                sender.invalidArguments();
                return;
            }

            // linking discord <UUID or username>
            if (sender.argEquals(1, "discord")) {
                if (!sender.checkPermission("eventalerts.linking.discord")) return;

                // Get argument
                final String argument = sender.getArgument(2);
                if (argument == null) {
                    sender.invalidArguments();
                    return;
                }

                // Get UUID
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(argument);
                } catch (final IllegalArgumentException e) {
                    final Optional<OfflinePlayer> player = BukkitUtility.getOfflinePlayer(argument);
                    if (player.isPresent()) uuid = player.get().getUniqueId();
                }
                if (uuid == null) {
                    sender.invalidArgumentByIndex(2);
                    return;
                }

                // Make API request
                HttpUtility.getJson(plugin.getUserAgent(), plugin.getApiHost() + "players/minecraft/uuid/" + uuid)
                        .flatMap(json -> MiscUtility.handleException(json::getAsJsonObject))
                        .flatMap(json -> MiscUtility.handleException(() -> json.getAsJsonObject("player")))
                        .ifPresentOrElse(
                                // Player found
                                player -> {
                                    // Get Discord
                                    final JsonObject discord = player.getAsJsonObject("discord");
                                    if (discord == null) {
                                        new AnnoyingMessage(plugin, "command.linking.discord.not-linked")
                                                .replace("%input%", argument)
                                                .send(sender);
                                        return;
                                    }

                                    // Get ID and username
                                    final String id = MiscUtility.handleException(() -> discord.get("id").getAsString()).orElse("&cUnknown");
                                    final String username = MiscUtility.handleException(() -> discord.get("username").getAsString()).orElse("&cUnknown");

                                    // Send message
                                    new AnnoyingMessage(plugin, "command.linking.discord.linked")
                                            .replace("%input%", argument)
                                            .replace("%id%", id)
                                            .replace("%username%", username)
                                            .send(sender);
                                },
                                // No player found
                                () -> new AnnoyingMessage(plugin, "command.linking.discord.not-linked")
                                        .replace("%input%", argument)
                                        .send(sender));
                return;
            }

            // linking minecraft <ID or username>
            if (sender.argEquals(1, "minecraft")) {
                if (!sender.checkPermission("eventalerts.linking.minecraft")) return;

                // Get argument
                final String argument = sender.getArgument(2);
                if (argument == null) {
                    sender.invalidArguments();
                    return;
                }

                // Get through ID
                final Long id = Mapper.toLong(argument).orElse(null);
                if (id != null) {
                    // Make API request
                    HttpUtility.getJson(plugin.getUserAgent(), plugin.getApiHost() + "players/discord/id/" + id)
                            .flatMap(json -> MiscUtility.handleException(json::getAsJsonObject))
                            .flatMap(json -> MiscUtility.handleException(() -> json.getAsJsonObject("player")))
                            .ifPresentOrElse(
                                    // Player found
                                    player -> {
                                        // Get Minecraft
                                        final JsonObject minecraft = player.getAsJsonObject("minecraft");
                                        if (minecraft == null) {
                                            new AnnoyingMessage(plugin, "command.linking.minecraft.not-linked")
                                                    .replace("%input%", argument)
                                                    .send(sender);
                                            return;
                                        }

                                        // Get UUID and username
                                        final String uuid = MiscUtility.handleException(() -> minecraft.get("uuid").getAsString()).orElse("&cUnknown");
                                        final String username = MiscUtility.handleException(() -> minecraft.get("username").getAsString()).orElse("&cUnknown");

                                        // Send message
                                        new AnnoyingMessage(plugin, "command.linking.minecraft.linked")
                                                .replace("%input%", argument)
                                                .replace("%uuid%", uuid)
                                                .replace("%username%", username)
                                                .send(sender);
                                    },
                                    // No player found
                                    () -> new AnnoyingMessage(plugin, "command.linking.minecraft.not-linked")
                                            .replace("%input%", argument)
                                            .send(sender));
                    return;
                }

                // Get through username: Make API request
                HttpUtility.getJson(plugin.getUserAgent(), plugin.getApiHost() + "players?discord_username=" + argument)
                        .flatMap(json -> MiscUtility.handleException(json::getAsJsonObject))
                        .flatMap(json -> MiscUtility.handleException(() -> json.getAsJsonArray("players")))
                        .ifPresentOrElse(
                                // Players found
                                players -> {
                                    // Build message
                                    final StringBuilder builder = new StringBuilder("<gold><b>Found " + players.size() + " players:</b>\n");
                                    for (final JsonElement element : players) MiscUtility.handleException(element::getAsJsonObject)
                                            .flatMap(json -> MiscUtility.handleException(() -> json.getAsJsonObject("minecraft")))
                                            .ifPresent(player -> {
                                                // Get UUID and username
                                                final String uuid = MiscUtility.handleException(() -> player.get("uuid").getAsString()).orElse("<red>Unknown</red>");
                                                final String username = MiscUtility.handleException(() -> player.get("username").getAsString()).orElse("<red>Unknown</red>");

                                                // Append to message
                                                builder.append("<gold>- <yellow>").append(username).append(" <i>(").append(uuid).append(")</i>\n");
                                            });
                                    builder.setLength(builder.length() - 1);

                                    // Send message
                                    sender.cmdSender.sendMessage(EventAlertsIntegration.MINI_MESSAGE.deserialize(builder.toString()));
                                },
                                // No players found
                                () -> new AnnoyingMessage(plugin, "command.linking.minecraft.not-linked")
                                        .replace("%input%", argument)
                                        .send(sender));
                return;
            }

            sender.invalidArguments();
            return;
        }

        // crossban check
        if (sender.argEquals(0, "crossban") && sender.argEquals(1, "check")) {
            if (!sender.checkPermission("eventalerts.crossban.check")) return;

            // Get online players
            final String onlineString = Bukkit.getOnlinePlayers().stream()
                    .map(player -> player.getUniqueId().toString())
                    .collect(Collectors.joining(","));

            // Make API request
            HttpUtility.getJson(plugin.getUserAgent(), plugin.getApiHost() + "cross_bans?uuid=" + onlineString)
                    .flatMap(json -> MiscUtility.handleException(json::getAsJsonObject))
                    .flatMap(json -> MiscUtility.handleException(() -> json.getAsJsonArray("cross_bans")))
                    .ifPresent(crossBans -> {
                        // Kick banned players
                        int banned = 0;
                        final TextComponent reason = Component.text()
                                .append(EventAlertsIntegration.GATE)
                                .append(Component.text("You have been cross-banned from all event servers!", NamedTextColor.RED))
                                .build();
                        for (final JsonElement jsonElement : crossBans) {
                            final JsonObject jsonObject = MiscUtility.handleException(jsonElement::getAsJsonObject).orElse(null);
                            if (jsonObject == null) continue;
                            final CrossBan ban = EAObject.newObject(plugin, CrossBan.class, jsonObject);
                            if (ban == null) continue;
                            final Player player = Bukkit.getPlayer(ban.uuid);
                            if (player == null) continue;
                            banned++;
                            player.kick(Component.text()
                                    .append(reason)
                                    .append(ban.getReasonExpires())
                                    .build());
                        }

                        // Send message
                        new AnnoyingMessage(plugin, "command.crossban.check.result")
                                .replace("%banned%", banned)
                                .send(sender);
                    });
            return;
        }

        // transfer <host> [port]
        if (sender.args.length >= 2 && sender.argEquals(0, "transfer")) {
            if (RefPlayer.TRANSFER == null) {
                new AnnoyingMessage(plugin, "command.transfer.disabled").send(sender);
                return;
            }
            if (!sender.checkPlayer()) return;

            // Get host and port
            final String host = sender.getArgument(1);
            final int port = sender.getArgumentOptional(2)
                    .flatMap(Mapper::toInt)
                    .orElse(25565);

            // Transfer
            try {
                new AnnoyingMessage(plugin, "command.transfer.transferring")
                        .replace("%server%", host + ":" + port)
                        .send(sender);
                RefPlayer.TRANSFER.invoke(sender.getPlayer(), host, port);
            } catch (final IllegalAccessException | InvocationTargetException e) {
                new AnnoyingMessage(plugin, "command.transfer.disabled").send(sender);
            }
            return;
        }

        sender.invalidArguments();
    }

    @Override @NotNull
    public Set<String> onTabComplete(@NotNull AnnoyingSender sender) {
        final CommandSender cmdSender = sender.cmdSender;
        final int length = sender.args.length;
        final Set<String> result = new HashSet<>();

        if (length == 1) {
            if (cmdSender.hasPermission("eventalerts.reload")) result.add("reload");
            if (cmdSender.hasPermission("eventalerts.config")) result.add("config");
            if (cmdSender.hasPermission("eventalerts.linking.check") || cmdSender.hasPermission("eventalerts.linking.discord") || cmdSender.hasPermission("eventalerts.linking.minecraft")) result.add("linking");
            if (cmdSender.hasPermission("eventalerts.crossban.check")) result.add("crossban");
            return result;
        }

        if (length == 2) {
            if (sender.argEquals(0, "linking")) {
                if (cmdSender.hasPermission("eventalerts.linking.check")) result.add("check");
                if (cmdSender.hasPermission("eventalerts.linking.discord")) result.add("discord");
                if (cmdSender.hasPermission("eventalerts.linking.minecraft")) result.add("minecraft");
                return result;
            }

            if (sender.argEquals(0, "crossban")) {
                if (cmdSender.hasPermission("eventalerts.crossban.check")) result.add("check");
                return result;
            }
        }

        if (length == 3 && sender.argEquals(0, "linking") && sender.argEquals(1, "discord", "minecraft")) {
            if (sender.argEquals(1, "discord") && cmdSender.hasPermission("eventalerts.linking.discord")) return BukkitUtility.getOnlinePlayerNames();
            if (sender.argEquals(1, "minecraft") && cmdSender.hasPermission("eventalerts.linking.minecraft")) return Set.of("<ID>", "<username>");
            return result;
        }

        return result;
    }
}
