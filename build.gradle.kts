import xyz.srnyx.gradlegalaxy.data.config.DependencyConfig
import xyz.srnyx.gradlegalaxy.data.config.JavaSetupConfig
import xyz.srnyx.gradlegalaxy.enums.Repository
import xyz.srnyx.gradlegalaxy.enums.repository
import xyz.srnyx.gradlegalaxy.utility.*


plugins {
    java
    id("xyz.srnyx.gradle-galaxy") version "2.1.0"
    id("com.gradleup.shadow") version "8.3.9"
}

paper(DependencyConfig(version = "1.18.2"))
setupAnnoyingAPI(
    javaSetupConfig = JavaSetupConfig(
        group = "gg.eventalerts",
        version = "1.1.0",
        description = "A plugin to integrate your Minecraft server with the Event Alerts ecosystem",
        javaVersion = JavaVersion.VERSION_21),
    annoyingAPIConfig = DependencyConfig(version = "5.2.0"))

repository(Repository.TRIUMPH_SNAPSHOTS)

dependencies {
    implementationRelocate("dev.triumphteam:triumph-gui-paper:4.0.0-SNAPSHOT", "dev.triumphteam")

    compileOnly("org.java-websocket:Java-WebSocket:1.6.0") { // Downloaded on runtime
        relocate("org.java_websocket")
    }
    compileOnly("org.mongodb:bson:5.6.3") { // Downloaded on runtime
        relocate("org.bson")
        relocate("org.checkerframework")
    }
    compileOnly("net.fellbaum:jemoji:1.7.5") { // Downloaded on runtime
        relocate("net.fellbaum")
    }
}

// Unknown relocations
val projectPackage = getPackage()
relocate("com.google.common", "$projectPackage.libs.google.common")
relocate("com.google.errorprone", "$projectPackage.libs.google.errorprone")
relocate("com.google.thirdparty", "$projectPackage.libs.google.thirdparty")
relocate("javax.annotation")
relocate("com.google.j2objc.annotations")
