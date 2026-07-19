import xyz.srnyx.gradlegalaxy.data.annoyingapi.Relocation
import xyz.srnyx.gradlegalaxy.data.annoyingapi.RuntimeLibrary
import xyz.srnyx.gradlegalaxy.data.config.DependencyConfig
import xyz.srnyx.gradlegalaxy.data.config.JavaSetupConfig
import xyz.srnyx.gradlegalaxy.data.config.annoyingapi.CustomRuntimeLibrariesConfig
import xyz.srnyx.gradlegalaxy.data.config.publishing.PublishingPlatformConfig
import xyz.srnyx.gradlegalaxy.enums.PluginPlatform
import xyz.srnyx.gradlegalaxy.enums.Repository
import xyz.srnyx.gradlegalaxy.enums.repository
import xyz.srnyx.gradlegalaxy.utility.*


plugins {
    java
    id("xyz.srnyx.gradle-galaxy") version "ac4875a"
    id("com.gradleup.shadow") version "9.6.0"
    id("me.modmuss50.mod-publish-plugin") version "4e494b3"
}

// Runtime libraries
val eventAlertsSdkVersion: String = "c149ccb"
val runtimeLibraries = listOf(
    RuntimeLibrary(
        name = "bson",
        repositories = listOf(Repository.MAVEN_CENTRAL.url),
        group = "org.mongodb",
        artifact = "bson",
        version = "5.7.0",
        relocations = listOf(
            Relocation("org.bson"),
            Relocation("org.checkerframework"))),
    RuntimeLibrary(
        name = "java_websocket",
        repositories = listOf(Repository.MAVEN_CENTRAL.url),
        group = "org.java-websocket",
        artifact = "Java-WebSocket",
        version = "1.6.0",
        relocations = listOf(Relocation("org.java_websocket"))),
    RuntimeLibrary(
        name = "event_alerts_sdk_core",
        repositories = listOf(
            Repository.SRNYX_RELEASES.url,
            Repository.SRNYX_SNAPSHOTS.url),
        group = "gg.eventalerts.sdk",
        artifact = "core",
        version = eventAlertsSdkVersion,
        relocations = listOf(Relocation("gg.eventalerts.sdk")),
        dependencies = listOf("bson")),
    RuntimeLibrary(
        name = "event_alerts_sdk_http",
        repositories = listOf(
            Repository.SRNYX_RELEASES.url,
            Repository.SRNYX_SNAPSHOTS.url),
        group = "gg.eventalerts.sdk",
        artifact = "http",
        version = eventAlertsSdkVersion,
        relocations = listOf(Relocation("gg.eventalerts.sdk")),
        dependencies = listOf("event_alerts_sdk_core")),
    RuntimeLibrary(
        name = "event_alerts_sdk_websocket",
        repositories = listOf(
            Repository.SRNYX_RELEASES.url,
            Repository.SRNYX_SNAPSHOTS.url),
        group = "gg.eventalerts.sdk",
        artifact = "websocket",
        version = eventAlertsSdkVersion,
        relocations = listOf(Relocation("gg.eventalerts.sdk")),
        dependencies = listOf(
            "event_alerts_sdk_core",
            "java_websocket")),
    RuntimeLibrary(
        name = "nova",
        repositories = listOf(Repository.TRIUMPH_SNAPSHOTS.url),
        group = "dev.triumphteam",
        artifact = "nova",
        version = "1.0.0-SNAPSHOT",
        relocations = listOf(Relocation("dev.triumphteam"))),
    RuntimeLibrary(
        name = "triumph_gui_core",
        repositories = listOf(Repository.TRIUMPH_SNAPSHOTS.url),
        group = "dev.triumphteam",
        artifact = "triumph-gui-core",
        version = "4.0.0-SNAPSHOT",
        relocations = listOf(Relocation("dev.triumphteam")),
        dependencies = listOf("nova")),
    RuntimeLibrary(
        name = "triumph_gui_paper",
        repositories = listOf(Repository.TRIUMPH_SNAPSHOTS.url),
        group = "dev.triumphteam",
        artifact = "triumph-gui-paper",
        version = "4.0.0-SNAPSHOT",
        relocations = listOf(Relocation("dev.triumphteam")),
        dependencies = listOf("triumph_gui_core")),
    RuntimeLibrary(
        name = "jemoji",
        repositories = listOf(Repository.MAVEN_CENTRAL.url),
        group = "net.fellbaum",
        artifact = "jemoji",
        version = "2.0.0",
        relocations = listOf(Relocation("net.fellbaum"))))

paper(DependencyConfig(version = "1.18.2"))
setupAnnoyingAPI(
    javaSetupConfig = JavaSetupConfig(
        group = "gg.eventalerts",
        description = "A plugin to integrate your Minecraft server with the Event Alerts ecosystem",
        javaVersion = JavaVersion.VERSION_21),
    annoyingAPIConfig = DependencyConfig(version = "ab08072"),
    customRuntimeLibrariesConfig = CustomRuntimeLibrariesConfig(runtimeLibraries),
    publishingPlatformConfig = PublishingPlatformConfig(
        platforms = mapOf(PluginPlatform.MODRINTH to "DmjI2XpF"),
        minecraftVersionStart = "1.18.2",
        loaders = listOf("paper", "purpur"),
        modrinthAction = { optional("placeholderapi") }))

// Repositories
repository(Repository.PLACEHOLDER_API)

// Dependencies
dependencies {
    // Optional
    compileOnly("me.clip:placeholderapi:2.12.2")
}

// Unknown relocations
val projectPackage = getPackage()
relocate("com.google.common", "$projectPackage.libs.google.common")
relocate("com.google.errorprone", "$projectPackage.libs.google.errorprone")
relocate("com.google.thirdparty", "$projectPackage.libs.google.thirdparty")
relocate("javax.annotation")
relocate("com.google.j2objc.annotations")

// Testing
setupMockBukkit(
    junitBomConfig = DependencyConfig(version = "6.1.0"),
    mockBukkitDependencyConfig = DependencyConfig(version = "3.9.0"))
