import xyz.srnyx.gradlegalaxy.enums.repository
import xyz.srnyx.gradlegalaxy.utility.implementationRelocate
import xyz.srnyx.gradlegalaxy.utility.paper
import xyz.srnyx.gradlegalaxy.utility.relocate
import xyz.srnyx.gradlegalaxy.utility.setupAnnoyingAPI


plugins {
    java
    id("xyz.srnyx.gradle-galaxy") version "1.3.3"
    id("com.gradleup.shadow") version "8.3.5"
}

paper("1.16.5")
setupAnnoyingAPI("7d41870d27", "gg.eventalerts", "1.0.0", "A plugin to integrate your Minecraft server with the Event Alerts ecosystem", JavaVersion.VERSION_21)

repository("https://repo.triumphteam.dev/snapshots/")
dependencies {
    implementationRelocate(project, "dev.triumphteam:triumph-gui-paper:4.0.0-SNAPSHOT", "dev.triumphteam.gui")
    compileOnly("org.mongodb", "bson", "5.3.0")
    compileOnly("net.fellbaum", "jemoji", "1.6.0")
}

relocate("org.bson")
relocate("net.fellbaum")
