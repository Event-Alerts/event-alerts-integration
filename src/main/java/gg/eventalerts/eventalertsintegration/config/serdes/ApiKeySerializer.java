package gg.eventalerts.eventalertsintegration.config.serdes;

import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import gg.eventalerts.eventalertsintegration.config.key.ApiKey;
import gg.eventalerts.eventalertsintegration.config.key.PartnerServerKey;
import gg.eventalerts.eventalertsintegration.config.key.PlayerKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class ApiKeySerializer implements ObjectSerializer<ApiKey> {
    @Override
    public boolean supports(@NotNull Class<?> type) {
        return ApiKey.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull ApiKey object, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.setValue(Objects.requireNonNullElse(object.key, "API_KEY_HERE"));
    }

    @Override @NotNull
    public ApiKey deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        final Class<?> type = generics.getType();

        // PlayerKey
        if (PlayerKey.class.isAssignableFrom(type)) return new PlayerKey(data.getValue(String.class));
        // PartnerServerKey
        if (PartnerServerKey.class.isAssignableFrom(type)) return new PartnerServerKey(data.getValue(String.class));

        // Shouldn't happen
        throw new OkaeriException("Unsupported ApiKey type: " + type.getName());
    }
}
