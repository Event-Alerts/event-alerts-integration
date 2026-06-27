package gg.eventalerts.eventalertsintegration.commands;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.gui.config.ConfigGui;
import gg.eventalerts.eventalertsintegration.object.sdk.CrossBanUtility;
import gg.eventalerts.eventalertsintegration.reflection.org.bukkit.entity.RefPlayer;
import gg.eventalerts.sdk.object.EACrossBan;
import gg.eventalerts.sdk.object.EAPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.Mapper;
import xyz.srnyx.annoyingapi.utility.BukkitUtility;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
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
            plugin.getMessages().get().command.reload.newMessage().send(sender);
            return;
        }

        // config
        if (sender.argEquals(0, "config")) {
            if (sender.checkPlayer() && sender.checkPermission("eventalerts.config")) new ConfigGui(plugin, sender.getPlayer()).open(true);
            return;
        }

        // linking
        if (sender.argEquals(0, "linking")) {
            // linking check
            if (sender.argEquals(1, "check")) {
                if (!sender.checkPermission("eventalerts.linking.check")) return;

                // Get online players + UUIDs
                final Collection<? extends Player> online = Bukkit.getOnlinePlayers();
                final String onlineString = online.stream()
                        .map(player -> player.getUniqueId().toString())
                        .collect(Collectors.joining(","));

                // Make API request
                plugin.http.players.retrievePage(Map.of("minecraft_uuid", onlineString)).queue(
                        page -> {
                            // Get linked
                            final Set<UUID> linked = new HashSet<>();
                            for (final EAPlayer player : page.items) {
                                if (player.minecraft != null && player.minecraft.uuid != null) linked.add(player.minecraft.uuid);
                            }

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
                            plugin.getMessages().get().command.linking.check.result.newMessage()
                                    .replace("%linked%", linked.size())
                                    .replace("%unlinked%", unlinked.size())
                                    .replace("%total%", online.size())
                                    .send(sender);
                        },
                        t -> {
                            // Failure
                            AnnoyingPlugin.log(Level.WARNING, "Failed to retrieve linked players!", t);
                            sender.cmdSender.sendMessage(Component.text("Failed to retrieve linked players! Check console for more details.", NamedTextColor.RED));
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
                plugin.http.players.retrieveOneByMinecraftUuid(uuid).queue(
                        player -> {
                            // Doesn't exist
                            if (player == null) {
                                plugin.getMessages().get().command.linking.discord.not_linked.newMessage()
                                        .replace("%input%", argument)
                                        .send(sender);
                                return;
                            }

                            // Not linked
                            if (player.discord == null) {
                                plugin.getMessages().get().command.linking.discord.not_linked.newMessage()
                                        .replace("%input%", argument)
                                        .send(sender);
                                return;
                            }

                            // Send message
                            plugin.getMessages().get().command.linking.discord.linked.newMessage()
                                    .replace("%input%", argument)
                                    .replace("%id%", Objects.requireNonNullElse(player.discord.id, "&cUnknown"))
                                    .replace("%username%", Objects.requireNonNullElse(player.discord.username, "&cUnknown"))
                                    .send(sender);
                        },
                        t -> {
                            // Failure
                            AnnoyingPlugin.log(Level.WARNING, "Failed to retrieve linked player!", t);
                            sender.cmdSender.sendMessage(Component.text("Failed to retrieve linked player! Check console for more details.", NamedTextColor.RED));
                        });
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
                    plugin.http.players.retrieveOneByDiscordId(id).queue(
                            player -> {
                                // Doesn't exist
                                if (player == null) {
                                    plugin.getMessages().get().command.linking.minecraft.not_linked.newMessage()
                                            .replace("%input%", argument)
                                            .send(sender);
                                    return;
                                }

                                // Not linked
                                if (player.minecraft == null) {
                                    plugin.getMessages().get().command.linking.minecraft.not_linked.newMessage()
                                            .replace("%input%", argument)
                                            .send(sender);
                                    return;
                                }

                                // Send message
                                plugin.getMessages().get().command.linking.minecraft.linked.newMessage()
                                        .replace("%input%", argument)
                                        .replace("%uuid%", Objects.requireNonNullElse(player.minecraft.uuid, "&cUUID Unknown"))
                                        .replace("%username%", Objects.requireNonNullElse(player.minecraft.username, "&cUsername Unknown"))
                                        .send(sender);
                            },
                            t -> {
                                // Failure
                                AnnoyingPlugin.log(Level.WARNING, "Failed to retrieve linked player!", t);
                                sender.cmdSender.sendMessage(Component.text("Failed to retrieve linked player! Check console for more details.", NamedTextColor.RED));
                            });
                    return;
                }

                // Get through username: Make API request
                plugin.http.players.retrievePage(Map.of("discord_username", argument)).queue(
                        page -> {
                            // No players found
                            if (page.count == 0) {
                                plugin.getMessages().get().command.linking.minecraft.not_linked.newMessage()
                                        .replace("%input%", argument)
                                        .send(sender);
                                return;
                            }

                            // Build message
                            final TextComponent.Builder builder = Component.text()
                                    .append(Component.text("Found " + page.count + " players:", NamedTextColor.GOLD, TextDecoration.BOLD));
                            for (final EAPlayer player : page.items) {
                                // Get UUID and username
                                final UUID uuid = player.minecraft != null ? player.minecraft.uuid : null;
                                final String username = player.minecraft != null ? player.minecraft.username : null;

                                // Get hover event
                                final HoverEvent<?> hover = uuid != null
                                        ? HoverEvent.showEntity(
                                                Key.key("minecraft", "player"),
                                                uuid,
                                                username != null ? Component.text(username, NamedTextColor.YELLOW) : null)
                                        : HoverEvent.showText(Component.text("UUID Unknown", NamedTextColor.RED));

                                // Get click event
                                final StringBuilder clickText = new StringBuilder();
                                if (username != null) {
                                    // USERNAME (UUID)
                                    clickText.append(username);
                                    if (uuid != null) clickText.append(" (").append(uuid).append(")");
                                } else {
                                    // UUID
                                    if (uuid != null) clickText.append(uuid);
                                }
                                final ClickEvent click = uuid != null ? ClickEvent.copyToClipboard(clickText.toString()) : null;

                                // Append to message
                                final Component usernameComponent = username != null
                                        ? Component.text(username, NamedTextColor.YELLOW)
                                        : Component.text("Username Unknown", NamedTextColor.RED);
                                builder
                                        .append(Component.newline())
                                        .append(Component.text("- ", NamedTextColor.GOLD))
                                        .append(usernameComponent
                                                .hoverEvent(hover)
                                                .clickEvent(click));
                            }

                            // Send message
                            sender.cmdSender.sendMessage(builder);
                        },
                        t -> {
                            // Failure
                            AnnoyingPlugin.log(Level.WARNING, "Failed to retrieve linked players!", t);
                            sender.cmdSender.sendMessage(Component.text("Failed to retrieve linked players! Check console for more details.", NamedTextColor.RED));
                        });
                return;
            }

            sender.invalidArguments();
            return;
        }

        // crossban check
        if (sender.argEquals(0, "crossban") && sender.argEquals(1, "check")) {
            if (!sender.checkPermission("eventalerts.crossban.check")) return;

            // Get online players
            final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            final String onlineString = players.stream()
                    .map(player -> player.getUniqueId().toString())
                    .collect(Collectors.joining(","));

            // Make API request
            plugin.http.crossBans.retrieveMany(players.size(), Map.of("minecraft_uuid", onlineString))
                    .onErrorReturnEmptyList()
                    .queue(crossBans -> plugin.runOnMainThread(() -> {
                        // Kick banned players
                        int banned = 0;
                        final TextComponent reason = Component.text()
                                .append(EventAlertsIntegration.GATE)
                                .append(Component.text("You have been cross-banned from all event servers!", NamedTextColor.RED))
                                .build();
                        for (final EACrossBan ban : crossBans) {
                            if (ban.minecraftUuid == null) continue;
                            final Player player = Bukkit.getPlayer(ban.minecraftUuid);
                            if (player == null) continue;
                            player.kick(Component.text()
                                    .append(reason)
                                    .append(CrossBanUtility.getReasonExpires(ban))
                                    .build());
                            banned++;
                        }

                        // Send message
                        plugin.getMessages().get().command.crossban.check.result.newMessage()
                                .replace("%banned%", banned)
                                .send(sender);
                    }));
            return;
        }

        // transfer <host> [port]
        if (sender.args.length >= 2 && sender.argEquals(0, "transfer")) {
            if (RefPlayer.TRANSFER == null) {
                plugin.getMessages().get().command.transfer.disabled.newMessage().send(sender);
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
                // Send message
                plugin.getMessages().get().command.transfer.transferring.newMessage()
                        .replace("%server%", host + ":" + port)
                        .send(sender);

                // Increase stats
                plugin.statsCollector.joinButtonClicks.incrementAndGet();

                // Transfer
                RefPlayer.TRANSFER.invoke(sender.getPlayer(), host, port);
            } catch (final IllegalAccessException | InvocationTargetException e) {
                plugin.getMessages().get().command.transfer.disabled.newMessage().send(sender);
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
