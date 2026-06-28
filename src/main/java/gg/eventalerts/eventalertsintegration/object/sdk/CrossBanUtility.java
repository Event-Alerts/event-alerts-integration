package gg.eventalerts.eventalertsintegration.object.sdk;

import gg.eventalerts.sdk.object.EACrossBan;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.DurationFormatter;


public class CrossBanUtility {
    @NotNull
    public static TextComponent getReasonExpires(@NotNull EACrossBan crossBan) {
        final TextComponent.Builder builder = Component.text();
        if (crossBan.reason != null) builder
                .append(Component.text("\n\nReason: ")
                        .color(NamedTextColor.YELLOW))
                .append(Component.text(crossBan.reason)
                        .color(NamedTextColor.GRAY));
        if (crossBan.expiration != null) builder
                .append(Component.text("\n\nExpires in: ")
                        .color(NamedTextColor.YELLOW))
                .append(Component.text(DurationFormatter.formatDuration(crossBan.expiration.getTime() - System.currentTimeMillis(), "d'd' H'h' m'm' s's'"))
                        .color(NamedTextColor.GRAY));
        return builder.build();
    }
}
