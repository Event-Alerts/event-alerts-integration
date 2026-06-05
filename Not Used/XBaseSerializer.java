package gg.eventalerts.eventalertsintegration.config.serdes;

import com.cryptomorin.xseries.base.XBase;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class XBaseSerializer<T extends XBase<T, ?>> implements ObjectSerializer<T> {
    @NotNull private static final Map<Class<?>, Method> CACHED_METHODS = new HashMap<>();

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return XBase.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull T object, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.setValue(object.name());
    }

    @Override @Nullable
    public T deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        final String name = data.getValue(String.class);
        if (name == null) return null;

        final Class<?> type = generics.getType();
        Method method = CACHED_METHODS.get(type);
        try {
            // Method not cached, get and cache
            if (method == null) {
                method = type.getMethod("of", String.class);
                CACHED_METHODS.put(type, method);
            }

            // Invoke method
            return ((Optional<T>) method.invoke(null, name)).orElse(null);
        } catch (final IllegalArgumentException | ReflectiveOperationException e) {
            return null;
        }
    }
}
