import xyz.srnyx.gradlegalaxy.enums.repository
import xyz.srnyx.gradlegalaxy.utility.*


plugins {
    java
    id("xyz.srnyx.gradle-galaxy") version "1.3.3"
    id("com.gradleup.shadow") version "8.3.6"
}

paper("1.16.5")
setupAnnoyingAPI("7d41870d27", "gg.eventalerts", "1.0.0", "A plugin to integrate your Minecraft server with the Event Alerts ecosystem", JavaVersion.VERSION_21)

repository("https://repo.triumphteam.dev/snapshots/")
dependencies {
    implementationRelocate(project, "dev.triumphteam:triumph-gui-paper:4.0.0-SNAPSHOT", "dev.triumphteam")
    compileOnly("org.mongodb", "bson", "5.3.0")
    compileOnly("net.fellbaum", "jemoji", "1.7.0")
}

val projectPackage = getPackage()
relocate("org.bson")
relocate("net.fellbaum")
relocate("com.google.common", "$projectPackage.libs.google.common")
relocate("com.google.errorprone", "$projectPackage.libs.google.errorprone")
relocate("com.google.thirdparty", "$projectPackage.libs.google.thirdparty")
relocate("javax.annotation")
relocate("org.checkerframework")