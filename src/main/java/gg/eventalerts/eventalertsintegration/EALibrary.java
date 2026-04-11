package gg.eventalerts.eventalertsintegration;

import org.jetbrains.annotations.NotNull;

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

    EALibrary(@NotNull Supplier<Library.Builder> librarySupplier, @NotNull Function<AnnoyingPlugin, Collection<Relocation>> relocations) {
        this.librarySupplier = librarySupplier;
        this.relocations = relocations;
    }

    @Override @NotNull
    public Supplier<Library.Builder> getLibrarySupplier() {
        return librarySupplier;
    }

    @Override @NotNull
    public Function<AnnoyingPlugin, Collection<Relocation>> getRelocations() {
        return relocations;
    }
}
