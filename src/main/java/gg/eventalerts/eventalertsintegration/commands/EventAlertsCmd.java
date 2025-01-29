package gg.eventalerts.eventalertsintegration.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.gui.config.ConfigMainGui;
import gg.eventalerts.eventalertsintegration.objects.CrossBan;
import gg.eventalerts.eventalertsintegration.objects.EAObject;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.command.AnnoyingCommand;
import xyz.srnyx.annoyingapi.command.AnnoyingSender;
import xyz.srnyx.annoyingapi.libs.javautilities.HttpUtility;
import xyz.srnyx.annoyingapi.libs.javautilities.MiscUtility;
import xyz.srnyx.annoyingapi.message.AnnoyingMessage;

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

        // linking check
        if (sender.argEquals(0, "linking") && sender.argEquals(1, "check")) {
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
                        for (final JsonElement player : players) MiscUtility.handleException(player::getAsJsonObject)
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
            if (cmdSender.hasPermission("eventalerts.linking.check")) result.add("linking");
            if (cmdSender.hasPermission("eventalerts.crossban.check")) result.add("crossban");
            return result;
        }

        if (length == 2) {
            if (sender.argEquals(0, "linking")) {
                if (cmdSender.hasPermission("eventalerts.linking.check")) result.add("check");
                return result;
            }

            if (sender.argEquals(0, "crossban")) {
                if (cmdSender.hasPermission("eventalerts.crossban.check")) result.add("check");
                return result;
            }
        }

        return result;
    }
}
