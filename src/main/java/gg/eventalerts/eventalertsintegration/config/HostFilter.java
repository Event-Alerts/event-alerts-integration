package gg.eventalerts.eventalertsintegration.config;

import gg.eventalerts.eventalertsintegration.EALibrary;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;

import org.apache.commons.lang.StringUtils;

import org.bson.types.ObjectId;
import org.bukkit.Material;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.libs.javautilities.MiscUtility;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.Mapper;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;


public enum HostFilter {
    SERVER(Material.CHEST, "EA",
            config -> config.eventMessages.hostFilterServers,
            (plugin, id) -> {
        if (!plugin.libraryManager.isLoaded(EALibrary.BSON)) plugin.libraryManager.loadLibrary(EALibrary.BSON); // Install BSON
        return MiscUtility.handleException(() -> new ObjectId(id)).isPresent();
    }),
    USER(Material.PLAYER_HEAD, "Discord",
            config -> config.eventMessages.hostFilterUsers,
            (plugin, id) -> Mapper.toLong(id).isPresent());

    @NotNull public final Material material;
    @NotNull public final String idType;
    @NotNull public final Function<ConfigYml, Set<String>> setGetter;
    @NotNull public final BiFunction<EventAlertsIntegration, String, Boolean> idValidator;
    @NotNull public final String lower = name().toLowerCase();
    @NotNull public final String capitalized = StringUtils.capitalize(lower);

    HostFilter(@NotNull Material material, @NotNull String idType, @NotNull Function<ConfigYml, Set<String>> setGetter, @NotNull BiFunction<EventAlertsIntegration, String, Boolean> idValidator) {
        this.setGetter = setGetter;
        this.material = material;
        this.idType = idType;
        this.idValidator = idValidator;
    }
}
