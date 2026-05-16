package gg.eventalerts.eventalertsintegration.gui.config.eventmessages;

import dev.triumphteam.gui.element.GuiItem;
import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import dev.triumphteam.gui.paper.container.type.HopperContainerType;

import gg.eventalerts.eventalertsintegration.config.ConfigYml;
import gg.eventalerts.eventalertsintegration.config.HostFilter;
import gg.eventalerts.eventalertsintegration.gui.Heads;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class HostFilterGui extends EventMessagesGui {
    public HostFilterGui(@NotNull EventMessagesGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        return Gui.of(new HopperContainerType())
                .title(Component.text("Host Filter"))
                .statelessComponent(container -> container.setItem(0, guiItem(HostFilter.SERVER)))
                .statelessComponent(container -> container.setItem(1, guiItem(HostFilter.USER)))
                .statelessComponent(container -> container.setItem(4, backButton()));
    }

    @NotNull
    private GuiItem<Player, ItemStack> guiItem(@NotNull HostFilter hostFilter) {
        return ItemBuilder.from(hostFilter.material)
                .name(unitalicize(Component.text(hostFilter + "S", NamedTextColor.GOLD, TextDecoration.BOLD)))
                .lore(lore("Only broadcast events\nhosted by these " + hostFilter.lower + "s"))
                .asGuiItem((player, context) -> openHostFilterGui(hostFilter));
    }

    public void openHostFilterGui(@NotNull HostFilter hostFilter) {
        new HostFilterGui(this) {
            @Override @NotNull
            public PaperGuiBuilder getGui() {
                final PaperGuiBuilder builder = Gui.of(6);

                // Add buttons
                final Set<String> set = hostFilter.setGetter.apply(plugin.config);
                int i = 0;
                for (final String id : set) {
                    final int finalI = i;
                    builder.statelessComponent(container1 -> container1.setItem(finalI, ItemBuilder.from(hostFilter.material)
                            .name(unitalicize(Component.text(id, NamedTextColor.GOLD)))
                            .lore(lore("Click to remove this " + hostFilter.lower + "\nfrom the host filter"))
                            .asGuiItem((player1, context1) -> {
                                set.remove(id);
                                final List<String> combinedFilter = new ArrayList<>(plugin.config.eventMessages.hostFilterServers);
                                combinedFilter.addAll(plugin.config.eventMessages.hostFilterUsers);
                                plugin.config.setSave(ConfigYml.EventMessages.PATH_HOST_FILTER, combinedFilter);
                                playDingSound(false);
                                open(false);
                            })));
                    if (i == 51) break; // Limit to 52
                    i++;
                }

                return builder
                        .title(Component.text()
                                .append(Component.text("Host Filter: ").decorate(TextDecoration.BOLD))
                                .append(Component.text(hostFilter.capitalized + "s"))
                                .build())
                        .statelessComponent(container -> container.setItem(52, ItemBuilder.skull()
                                .texture(Heads.GREEN_PLUS)
                                .name(unitalicize(Component.text("+ Add " + hostFilter.capitalized, NamedTextColor.DARK_GREEN)))
                                .lore(lore("<green>Click to add a " + hostFilter.lower + "\n<green>to the host filter"))
                                .asGuiItem((player1, context1) -> { //TODO switch to anvil GUI when Triumph GUI updates
                                    // Send chat messages
                                    player1.sendMessage(Component.text()
                                            .append(Component.text()
                                                    .color(NamedTextColor.GREEN)
                                                    .append(Component.text("\nType the "))
                                                    .append(Component.text(hostFilter.idType + " ID", NamedTextColor.DARK_GREEN))
                                                    .append(Component.text(" of the " + hostFilter.lower + " you want to add to the host filter in chat!")))
                                            .append(CANCEL));

                                    // Add to map and close GUI
                                    plugin.guiInput.put(player1.getUniqueId(), hostFilter.name());
                                    playDingSound(true);
                                    context1.guiView().close();
                                })))
                        .statelessComponent(container -> container.setItem(53, backButton()));
            }
        }.open(true);
    }
}
