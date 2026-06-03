package gg.eventalerts.eventalertsintegration.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.file.PlayableSound;


public class PlayableSoundSerializer implements ObjectSerializer<PlayableSound> {
    @Override
    public boolean supports(@NotNull Class<?> type) {
        return PlayableSound.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull PlayableSound object, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.set("sound", object.sound);
        data.set("category", object.category);
        data.set("volume", object.volume);
        data.set("pitch", object.pitch);
    }

    @Override @NotNull
    public PlayableSound deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        return new PlayableSound(
                data.get("sound", Sound.class),
                data.get("category", SoundCategory.class),
                data.get("volume", float.class),
                data.get("pitch", float.class));
    }
}
