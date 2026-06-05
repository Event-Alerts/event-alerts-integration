@file:Suppress("AvoidDuplicateDependencies")
import xyz.srnyx.gradlegalaxy.data.config.DependencyConfig
import xyz.srnyx.gradlegalaxy.data.config.JavaSetupConfig
import xyz.srnyx.gradlegalaxy.enums.Repository
import xyz.srnyx.gradlegalaxy.enums.repository
import xyz.srnyx.gradlegalaxy.utility.*


plugins {
    java
    id("xyz.srnyx.gradle-galaxy") version "2.1.0"
    id("com.gradleup.shadow") version "8.3.9"
    id("net.kyori.blossom") version "2.2.0"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.4.1"
}

paper(DependencyConfig(version = "1.18.2"))
setupAnnoyingAPI(
    javaSetupConfig = JavaSetupConfig(
        group = "gg.eventalerts",
        version = "1.1.0",
        description = "A plugin to integrate your Minecraft server with the Event Alerts ecosystem",
        javaVersion = JavaVersion.VERSION_21),
    annoyingAPIConfig = DependencyConfig(version = "b69a3ab"))

// Runtime dependency versions
val okaeriConfigsVersion: String = "6.1.0-beta.4"
val javaWebSocketVersion: String = "1.6.0"
val bsonVersion: String = "5.7.0"
val triumphGuiVersion: String = "4.0.0-SNAPSHOT"
val jEmojiVersion: String = "2.0.0"

// Blossom (see java-templates module)
sourceSets.main { blossom.javaSources {
    property("okaeri_configs_version", okaeriConfigsVersion)
    property("java_websocket_version", javaWebSocketVersion)
    property("bson_version", bsonVersion)
    property("triumph_gui_version", triumphGuiVersion)
    property("jemoji_version", jEmojiVersion)
} }

// Repositories
repository("https://repo.okaeri.cloud/releases")
repository(Repository.TRIUMPH_SNAPSHOTS, Repository.PLACEHOLDER_API)

// Dependencies
dependencies {
    // Downloaded on runtime
    compileOnly("eu.okaeri:okaeri-configs-yaml-bukkit:$okaeriConfigsVersion") {
        relocate("eu.okaeri")
    }
    compileOnly("eu.okaeri:okaeri-configs-serdes-commons:$okaeriConfigsVersion")
    compileOnly("eu.okaeri:okaeri-configs-serdes-bukkit:$okaeriConfigsVersion")
    compileOnly("eu.okaeri:okaeri-configs-validator-okaeri:$okaeriConfigsVersion")
    compileOnly("org.java-websocket:Java-WebSocket:$javaWebSocketVersion") {
        relocate("org.java_websocket")
    }
    compileOnly("org.mongodb:bson:$bsonVersion") {
        relocate("org.bson")
        relocate("org.checkerframework")
    }
    compileOnly("dev.triumphteam:triumph-gui-paper:$triumphGuiVersion") {
        relocate("dev.triumphteam")
    }
    compileOnly("net.fellbaum:jemoji:$jEmojiVersion") {
        relocate("net.fellbaum")
    }

    // Optional
    compileOnly("me.clip:placeholderapi:2.12.2")

    // Unit tests
    testImplementation("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    testImplementation("org.mongodb:bson:$bsonVersion")
    testImplementation("eu.okaeri:okaeri-configs-core:$okaeriConfigsVersion")
    testImplementation("eu.okaeri:okaeri-configs-yaml-bukkit:$okaeriConfigsVersion")
    testImplementation("eu.okaeri:okaeri-configs-serdes-commons:$okaeriConfigsVersion")
    testImplementation("eu.okaeri:okaeri-configs-serdes-bukkit:$okaeriConfigsVersion")
    testImplementation("eu.okaeri:okaeri-configs-validator-okaeri:$okaeriConfigsVersion")
    testImplementation(platform("org.junit:junit-bom:6.1.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Unknown relocations
val projectPackage = getPackage()
relocate("com.google.common", "$projectPackage.libs.google.common")
relocate("com.google.errorprone", "$projectPackage.libs.google.errorprone")
relocate("com.google.thirdparty", "$projectPackage.libs.google.thirdparty")
relocate("javax.annotation")
relocate("com.google.j2objc.annotations")

tasks.test {
    useJUnitPlatform()
}
