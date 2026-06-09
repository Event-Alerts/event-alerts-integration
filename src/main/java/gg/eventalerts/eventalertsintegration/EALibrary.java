package gg.eventalerts.eventalertsintegration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.library.AnnoyingLibrary;
import xyz.srnyx.annoyingapi.libs.libby.Library;
import xyz.srnyx.annoyingapi.libs.libby.Repositories;
import xyz.srnyx.annoyingapi.libs.libby.relocation.Relocation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


public enum EALibrary implements AnnoyingLibrary {
    /**
     * {@code org.mongodb:bson}
     */
    BSON(
            () -> Library.builder()
                    .repository(Repositories.MAVEN_CENTRAL)
                    .groupId("org{}mongodb")
                    .artifactId("bson")
                    .version(BuildProperties.BSON_VERSION),
            plugin -> List.of(
                    plugin.getRelocation("org{}bson"),
                    plugin.getRelocation("org{}checkerframework"))), //TODO not sure if should include checkerframework here
    /**
     * {@code org.java-websocket:Java-WebSocket}
     */
    JAVA_WEBSOCKET(
            () -> Library.builder()
                    .repository(Repositories.MAVEN_CENTRAL)
                    .groupId("org{}java-websocket")
                    .artifactId("Java-WebSocket")
                    .version(BuildProperties.JAVA_WEBSOCKET_VERSION),
            plugin -> Collections.singleton(plugin.getRelocation("org{}java_websocket"))),
    /**
     * {@code gg.eventalerts.sdk:core}
     */
    EVENT_ALERTS_SDK_CORE(
            () -> Library.builder()
                    .repository("https://repo.srnyx.com/releases/")
                    .repository("https://repo.srnyx.com/snapshots/")
                    .groupId("gg{}eventalerts{}sdk")
                    .artifactId("core")
                    .version(BuildProperties.EVENT_ALERTS_SDK_VERSION),
            plugin -> Collections.singleton(plugin.getRelocation("gg{}eventalerts{}sdk")),
            Collections.singleton(BSON)),
    /**
     * {@code gg.eventalerts.sdk:http}
     */
    EVENT_ALERTS_SDK_HTTP(
            () -> Library.builder()
                    .repository("https://repo.srnyx.com/releases/")
                    .repository("https://repo.srnyx.com/snapshots/")
                    .groupId("gg{}eventalerts{}sdk")
                    .artifactId("http")
                    .version(BuildProperties.EVENT_ALERTS_SDK_VERSION),
            plugin -> Collections.singleton(plugin.getRelocation("gg{}eventalerts{}sdk")),
            Collections.singleton(EVENT_ALERTS_SDK_CORE)),
    /**
     * {@code gg.eventalerts.sdk:websocket}
     */
    EVENT_ALERTS_SDK_WEBSOCKET(
            () -> Library.builder()
                    .repository("https://repo.srnyx.com/releases/")
                    .repository("https://repo.srnyx.com/snapshots/")
                    .groupId("gg{}eventalerts{}sdk")
                    .artifactId("websocket")
                    .version(BuildProperties.EVENT_ALERTS_SDK_VERSION),
            plugin -> Collections.singleton(plugin.getRelocation("gg{}eventalerts{}sdk")),
            List.of(
                    EVENT_ALERTS_SDK_CORE,
                    JAVA_WEBSOCKET)),
    /**
     * {@code eu.okaeri:okaeri-configs-core}
     */
    OKAERI_CONFIGS_CORE(
            () -> Library.builder()
                    .repository("https://repo.okaeri.cloud/releases/")
                    .groupId("eu{}okaeri")
                    .artifactId("okaeri-configs-core")
                    .version(BuildProperties.OKAERI_CONFIGS_VERSION),
            plugin -> Collections.singleton(plugin.getRelocation("eu{}okaeri"))),
    /**
     * {@code eu.okaeri:okaeri-configs-yaml-bukkit}
     */
    OKAERI_CONFIGS_YAML_BUKKIT(
            () -> Library.builder()
                    .repository("https://repo.okaeri.cloud/releases/")
                    .groupId("eu{}okaeri")
                    .artifactId("okaeri-configs-yaml-bukkit")
                    .version(BuildProperties.OKAERI_CONFIGS_VERSION),
            plugin -> Collections.singleton(plugin.getRelocation("eu{}okaeri")),
            Collections.singleton(OKAERI_CONFIGS_CORE)),
    /**
     * {@code eu.okaeri:okaeri-configs-serdes-commons}
     */
    OKAERI_CONFIGS_SERDES_COMMONS(
            () -> Library.builder()
                    .repository("https://repo.okaeri.cloud/releases/")
                    .groupId("eu{}okaeri")
                    .artifactId("okaeri-configs-serdes-commons")
                    .version(BuildProperties.OKAERI_CONFIGS_VERSION),
            plugin -> Collections.singleton(plugin.getRelocation("eu{}okaeri")),
            Collections.singleton(OKAERI_CONFIGS_CORE)),
    /**
     * {@code eu.okaeri:okaeri-configs-serdes-bukkit}
     */
    OKAERI_CONFIGS_SERDES_BUKKIT(
            () -> Library.builder()
                    .repository("https://repo.okaeri.cloud/releases/")
                    .groupId("eu{}okaeri")
                    .artifactId("okaeri-configs-serdes-bukkit")
                    .version(BuildProperties.OKAERI_CONFIGS_VERSION),
            plugin -> Collections.singleton(plugin.getRelocation("eu{}okaeri")),
            Collections.singleton(OKAERI_CONFIGS_YAML_BUKKIT)),
    /**
     * {@code eu.okaeri:okaeri-validator}
     */
    OKAERI_VALIDATOR(
            () -> Library.builder()
                    .repository("https://repo.okaeri.cloud/releases/")
                    .groupId("eu{}okaeri")
                    .artifactId("okaeri-validator")
                    .version("2.0.5"),
            plugin -> Collections.singleton(plugin.getRelocation("eu{}okaeri")),
            Collections.singleton(OKAERI_CONFIGS_CORE)),
    /**
     * {@code eu.okaeri:okaeri-configs-validator-okaeri}
     */
    OKAERI_CONFIGS_VALIDATOR_OKAERI(
            () -> Library.builder()
                    .repository("https://repo.okaeri.cloud/releases/")
                    .groupId("eu{}okaeri")
                    .artifactId("okaeri-configs-validator-okaeri")
                    .version(BuildProperties.OKAERI_CONFIGS_VERSION),
            plugin -> Collections.singleton(plugin.getRelocation("eu{}okaeri")),
            Collections.singleton(OKAERI_VALIDATOR)),
    /**
     * {@code dev.triumphteam:nova}
     */
    NOVA(
            () -> Library.builder()
                    .repository("https://repo.triumphteam.dev/snapshots/")
                    .groupId("dev{}triumphteam")
                    .artifactId("nova")
                    .version("1.0.0-SNAPSHOT"),
            plugin -> Collections.singleton(plugin.getRelocation("dev{}triumphteam"))),
    /**
     * {@code dev.triumphteam:triumph-gui-core}
     */
    TRIUMPH_GUI_CORE(
            () -> Library.builder()
                    .repository("https://repo.triumphteam.dev/snapshots/")
                    .groupId("dev{}triumphteam")
                    .artifactId("triumph-gui-core")
                    .version(BuildProperties.TRIUMPH_GUI_VERSION),
            plugin -> Collections.singleton(plugin.getRelocation("dev{}triumphteam")),
            Collections.singleton(NOVA)),
    /**
     * {@code dev.triumphteam:triumph-gui-paper}
     */
    TRIUMPH_GUI_PAPER(
            () -> Library.builder()
                    .repository("https://repo.triumphteam.dev/snapshots/")
                    .groupId("dev{}triumphteam")
                    .artifactId("triumph-gui-paper")
                    .version(BuildProperties.TRIUMPH_GUI_VERSION),
            plugin -> Collections.singleton(plugin.getRelocation("dev{}triumphteam")),
            Collections.singleton(TRIUMPH_GUI_CORE)),
    /**
     * {@code net.fellbaum:jememoji}
     */
    JEMOJI(
            () -> Library.builder()
                    .repository(Repositories.MAVEN_CENTRAL)
                    .groupId("net{}fellbaum")
                    .artifactId("jemoji")
                    .version(BuildProperties.JEMOJI_VERSION),
            plugin -> Collections.singleton(plugin.getRelocation("net{}fellbaum")));

    @NotNull public final Supplier<Library.Builder> librarySupplier;
    @NotNull public final Function<AnnoyingPlugin, Collection<Relocation>> relocations;
    @Nullable public final Collection<AnnoyingLibrary> requiredLibraries;

    EALibrary(@NotNull Supplier<Library.Builder> librarySupplier, @NotNull Function<AnnoyingPlugin, Collection<Relocation>> relocations, @Nullable Collection<AnnoyingLibrary> requiredLibraries) {
        this.librarySupplier = librarySupplier;
        this.relocations = relocations;
        this.requiredLibraries = requiredLibraries;
    }

    EALibrary(@NotNull Supplier<Library.Builder> librarySupplier, @NotNull Function<AnnoyingPlugin, Collection<Relocation>> relocations) {
        this(librarySupplier, relocations, null);
    }

    @Override @NotNull
    public Supplier<Library.Builder> getLibrarySupplier() {
        return librarySupplier;
    }

    @Override @NotNull
    public Function<AnnoyingPlugin, Collection<Relocation>> getRelocations() {
        return relocations;
    }

    @Override @Nullable
    public Collection<AnnoyingLibrary> getRequiredLibraries() {
        return requiredLibraries;
    }
}
