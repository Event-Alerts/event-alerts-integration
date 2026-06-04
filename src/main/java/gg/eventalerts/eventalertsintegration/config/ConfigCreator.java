package gg.eventalerts.eventalertsintegration.config;

import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import gg.eventalerts.eventalertsintegration.config.migration.C0001_Rename_hyphen_names_to_snake_case;
import gg.eventalerts.eventalertsintegration.config.migration.C0002_Migrate_sound_to_nested_structure;
import gg.eventalerts.eventalertsintegration.config.migration.C0003_Migrate_negative_retry_delay;
import gg.eventalerts.eventalertsintegration.config.migration.C0004_Migrate_websockets_to_websocket;
import gg.eventalerts.eventalertsintegration.config.serdes.PlayableSoundSerializer;
import gg.eventalerts.eventalertsintegration.config.validator.EAConfigValidator;
import org.jetbrains.annotations.NotNull;

import java.io.File;


public class ConfigCreator {
    @NotNull
    public static ConfigYml create(@NotNull EventAlertsIntegration plugin) {
        return (ConfigYml) new ConfigYml(plugin)
                .configure(opt -> {
                    opt.configurer(new YamlBukkitConfigurer(), new SerdesCommons(), new SerdesBukkit(), registry -> {
                        registry.register(new PlayableSoundSerializer());
                    });
                    opt.validator(new EAConfigValidator());
                    opt.bindFile(new File(plugin.getDataFolder(), "config.yml"));
                    opt.removeOrphans(true);
                })
                .load()
                .migrate(
                        //TODO add unit tests
                        new C0001_Rename_hyphen_names_to_snake_case(),
                        new C0002_Migrate_sound_to_nested_structure(),
                        new C0003_Migrate_negative_retry_delay(),
                        new C0004_Migrate_websockets_to_websocket())
                .saveDefaults();
    }
}
