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
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.3"
}

paper(DependencyConfig(version = "1.18.2"))
setupAnnoyingAPI(
    javaSetupConfig = JavaSetupConfig(
        group = "gg.eventalerts",
        version = "1.1.0",
        description = "A plugin to integrate your Minecraft server with the Event Alerts ecosystem",
        javaVersion = JavaVersion.VERSION_21),
    annoyingAPIConfig = DependencyConfig(version = "5.2.0"))

// Runtime dependency versions
val javaWebSocketVersion: String = "1.6.0"
val bsonVersion: String = "5.7.0"
val jEmojiVersion: String = "1.7.6"

// Blossom (see java-templates module)
sourceSets.main { blossom.javaSources {
    property("java_websocket_version", javaWebSocketVersion)
    property("bson_version", bsonVersion)
    property("jemoji_version", jEmojiVersion)
} }

// Dependencies
repository(Repository.TRIUMPH_SNAPSHOTS, Repository.PLACEHOLDER_API)
dependencies {
    implementationRelocate("dev.triumphteam:triumph-gui-paper:4.0.0-SNAPSHOT", "dev.triumphteam")

    compileOnly("org.java-websocket:Java-WebSocket:$javaWebSocketVersion") { // Downloaded on runtime
        relocate("org.java_websocket")
    }
    compileOnly("org.mongodb:bson:$bsonVersion") { // Downloaded on runtime
        relocate("org.bson")
        relocate("org.checkerframework")
    }
    compileOnly("net.fellbaum:jemoji:$jEmojiVersion") { // Downloaded on runtime
        relocate("net.fellbaum")
    }

    compileOnly("me.clip:placeholderapi:2.12.2")
}

// Unknown relocations
val projectPackage = getPackage()
relocate("com.google.common", "$projectPackage.libs.google.common")
relocate("com.google.errorprone", "$projectPackage.libs.google.errorprone")
relocate("com.google.thirdparty", "$projectPackage.libs.google.thirdparty")
relocate("javax.annotation")
relocate("com.google.j2objc.annotations")
