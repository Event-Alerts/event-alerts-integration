package gg.eventalerts.eventalertsintegration.config;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.libs.javautilities.MiscUtility;
import xyz.srnyx.annoyingapi.libs.javautilities.manipulation.Mapper;

import java.util.function.Function;


public enum HostFilter {
    SERVER(Material.CHEST, "EA", id -> MiscUtility.handleException(() -> new ObjectId(id)).isPresent()),
    USER(Material.PLAYER_HEAD, "Discord", id -> Mapper.toLong(id).isPresent());

    @NotNull public final Material material;
    @NotNull public final String idType;
    @NotNull public final Function<String, Boolean> idValidator;
    @NotNull public final String lower = name().toLowerCase();
    @NotNull public final String capitalized = StringUtils.capitalize(lower);

    HostFilter(@NotNull Material material, @NotNull String idType, @NotNull Function<String, Boolean> idValidator) {
        this.material = material;
        this.idType = idType;
        this.idValidator = idValidator;
    }
}
