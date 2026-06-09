@file:Suppress("AvoidDuplicateDependencies")
import xyz.srnyx.gradlegalaxy.data.config.DependencyConfig
import xyz.srnyx.gradlegalaxy.data.config.JavaSetupConfig
import xyz.srnyx.gradlegalaxy.enums.Repository
import xyz.srnyx.gradlegalaxy.enums.repository
import xyz.srnyx.gradlegalaxy.utility.*


plugins {
    java
    id("xyz.srnyx.gradle-galaxy") version "3.0.1"
    id("com.gradleup.shadow") version "9.4.2"
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
    annoyingAPIConfig = DependencyConfig(version = "90afed0"))

// Runtime dependency versions
val eventAlertsSdkVersion: String = "84b3cac"
val okaeriConfigsVersion: String = "6.1.0-beta.4"
val javaWebSocketVersion: String = "1.6.0"
val bsonVersion: String = "5.7.0"
val triumphGuiVersion: String = "4.0.0-SNAPSHOT"
val jEmojiVersion: String = "2.0.0"

// Blossom (see java-templates module)
sourceSets.main { blossom.javaSources {
    property("event_alerts_sdk_version", eventAlertsSdkVersion)
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
val okaeriConfigsYamlBukkit: String = "eu.okaeri:okaeri-configs-yaml-bukkit:$okaeriConfigsVersion"
val okaeriConfigsSerdesBukkit: String = "eu.okaeri:okaeri-configs-serdes-bukkit:$okaeriConfigsVersion"
val okaeriConfigsSerdesCommons: String = "eu.okaeri:okaeri-configs-serdes-commons:$okaeriConfigsVersion"
val okaeriConfigsValidatorOkaeri: String = "eu.okaeri:okaeri-configs-validator-okaeri:$okaeriConfigsVersion"
dependencies {
    // Downloaded on runtime
    compileOnly("gg.eventalerts.sdk:http:$eventAlertsSdkVersion")
    compileOnly("gg.eventalerts.sdk:websocket:$eventAlertsSdkVersion")
    compileOnly(okaeriConfigsYamlBukkit)
    compileOnly(okaeriConfigsSerdesBukkit)
    compileOnly(okaeriConfigsSerdesCommons)
    compileOnly(okaeriConfigsValidatorOkaeri)
    compileOnly("org.java-websocket:Java-WebSocket:1.6.0") {
        relocate("org.java_websocket")
    }
    compileOnly("org.mongodb:bson:5.7.0") {
        relocate("org.bson")
        relocate("org.checkerframework")
    }
    compileOnly("dev.triumphteam:triumph-gui-paper:4.0.0-SNAPSHOT") {
        relocate("dev.triumphteam")
    }
    compileOnly("net.fellbaum:jemoji:2.0.0") {
        relocate("net.fellbaum")
    }

    // Optional
    compileOnly("me.clip:placeholderapi:2.12.2")

    // Unit tests
    testImplementation("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    testImplementation("gg.eventalerts.sdk:core:$eventAlertsSdkVersion")
    testImplementation(okaeriConfigsYamlBukkit)
    testImplementation(okaeriConfigsSerdesBukkit)
    testImplementation(okaeriConfigsSerdesCommons)
    testImplementation(okaeriConfigsValidatorOkaeri)
    testImplementation(platform("org.junit:junit-bom:6.1.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Event Alerts SDK
relocate("gg.eventalerts.sdk")
// Okaeri Configs
relocate("eu.okaeri")

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
