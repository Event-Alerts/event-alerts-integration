package gg.eventalerts.eventalertsintegration.gui.config.eventmessages;

import dev.triumphteam.gui.element.GuiItem;
import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.gui.PaperGuiBuilder;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import gg.eventalerts.eventalertsintegration.config.HostFilter;
import gg.eventalerts.eventalertsintegration.gui.GuiInputType;
import gg.eventalerts.eventalertsintegration.gui.Heads;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class HostFilterGui extends EventMessagesGui {
    private static final int ADD_SLOT = 52;
    private static final int BACK_SLOT = 53;

    public HostFilterGui(@NotNull EventMessagesGui parent) {
        super(parent);
    }

    @Override @NotNull
    public PaperGuiBuilder getGui() {
        final List<String> ids = new ArrayList<>(plugin.config.event_messages.host_filter);
        ids.sort(Comparator
                .comparing((String id) -> HostFilter.fromId(id) == HostFilter.USER ? 1 : 0)
                .thenComparing(String::compareTo));

        final PaperGuiBuilder builder = Gui.of(6)
                .title(Component.text("Host Filter"));

        int i = 0;
        for (final String id : ids) {
            if (i >= ADD_SLOT) break;
            final int slot = i++;
            final HostFilter hostFilter = HostFilter.fromId(id);
            builder.statelessComponent(container -> container.setItem(slot, hostFilterItem(id, hostFilter)));
        }

        return builder
                .statelessComponent(container -> container.setItem(ADD_SLOT, ItemBuilder.skull()
                        .texture(Heads.GREEN_PLUS)
                        .name(unitalicize(Component.text("Add Entry", NamedTextColor.DARK_GREEN, TextDecoration.BOLD)))
                        .lore(lore("Add an EA server ID or Discord\nuser ID to the host filter"))
                        .asGuiItem((player, context) -> {
                            player.sendMessage(Component.text()
                                    .color(NamedTextColor.GREEN)
                                    .append(Component.text("\nType the EA server ID or Discord user ID to add to the host filter in chat!"))
                                    .append(CANCEL));

                            plugin.guiInput.put(player.getUniqueId(), GuiInputType.HOST_FILTER_ENTRY);
                            playDingSound(true);
                            context.guiView().close();
                        })))
                .statelessComponent(container -> container.setItem(BACK_SLOT, backButton()));
    }

    @NotNull
    private GuiItem<Player, ItemStack> hostFilterItem(@NotNull String id, @Nullable HostFilter hostFilter) {
        final Material material = hostFilter == null ? Material.BARRIER : hostFilter.material;
        final String title = hostFilter == null ? id : hostFilter.capitalized + ": " + id;
        final String description = hostFilter == null
                ? "This entry no longer matches a valid host filter type"
                : "Click to remove this " + hostFilter.lower + " from the host filter";

        return ItemBuilder.from(material)
                .name(unitalicize(Component.text(title, NamedTextColor.GOLD, TextDecoration.BOLD)))
                .lore(lore(description))
                .asGuiItem((player, context) -> {
                    plugin.config.event_messages.removeHostFilter(id);
                    playDingSound(false);
                    open(false);
                });
    }
}
