package gg.eventalerts.eventalertsintegration.config.serdes;

import com.cryptomorin.xseries.XSound;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.annoyingapi.file.PlayableSound;


public class PlayableSoundSerializer implements ObjectSerializer<PlayableSound> {
    @Override
    public boolean supports(@NotNull Class<?> type) {
        return PlayableSound.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull PlayableSound object, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.set("sound", object.sound.name());
        data.set("category", object.category != null ? object.category.name() : null);
        data.set("volume", object.volume);
        data.set("pitch", object.pitch);
    }

    @Override @Nullable
    public PlayableSound deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        final XSound sound = data.get("sound", XSound.class);
        final SoundCategory category = data.get("category", SoundCategory.class);
        final Float volume = data.get("volume", Float.class);
        final Float pitch = data.get("pitch", Float.class);
        if (sound == null || category == null) return null;
        final Sound bukkitSound = sound.get();
        if (bukkitSound == null) return null;

        return new PlayableSound(bukkitSound, category, volume, pitch);
    }
}
