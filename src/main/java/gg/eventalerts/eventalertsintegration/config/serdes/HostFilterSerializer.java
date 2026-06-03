package gg.eventalerts.eventalertsintegration.config.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import gg.eventalerts.eventalertsintegration.config.HostFilter;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;


public class HostFilterSerializer implements ObjectSerializer<Set<String>> {
    @Override
    public boolean supports(@NotNull Class<?> type) {
        return Set.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull Set<String> object, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.setValueCollection(object, String.class);
    }

    @Override
    public Set<String> deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        final Set<String> filters = data.getValueAsSet(String.class);
        for (final String filter : new HashSet<>(filters)) {
            boolean valid = false;
            for (final HostFilter hostFilterEnum : HostFilter.values()) {
                if (hostFilterEnum.idValidator.apply(filter)) {
                    valid = true;
                    break;
                }
            }

            // Invalid
            if (valid) continue;
            filters.remove(filter);
            AnnoyingPlugin.log(Level.WARNING, "Invalid host filter entry: " + filter);
        }
        return filters;
    }
}
