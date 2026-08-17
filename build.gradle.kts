plugins {
    java
    id("xyz.srnyx.gradle-galaxy") version "f930f7e"
    id("com.gradleup.shadow") version "9.6.1"
    id("me.modmuss50.mod-publish-plugin") version "675051c"
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

group = "gg.eventalerts"
description = "A plugin to integrate your Minecraft server with the Event Alerts ecosystem"

galaxy {
    java {
        javaVersion = JavaVersion.VERSION_21
    }

    minecraft {
        paper("1.18.2")

        annoyingAPI("a94f930") {
            customRuntimeLibraries {
                library("event_alerts_sdk_core") {
                    repositories.addAll(SRNYX_RELEASES, SRNYX_SNAPSHOTS)
                    group = "gg.eventalerts.sdk"
                    artifact = "core"
                    version = "c149ccb"
                    relocate("gg.eventalerts.sdk")

                    dependency("bson") {
                        repositories.add(MAVEN_CENTRAL)
                        group = "org.mongodb"
                        artifact = "bson"
                        version = "5.7.0"
                        relocate("org.bson")
                        relocate("org.checkerframework")
                    }

                    library("event_alerts_sdk_http") {
                        artifact = "http"
                    }

                    library("event_alerts_sdk_websocket") {
                        artifact = "websocket"

                        dependency("java_websocket") {
                            repositories.add(MAVEN_CENTRAL)
                            group = "org.java-websocket"
                            artifact = "Java-WebSocket"
                            version = "1.6.0"
                            relocate("org.java_websocket")
                        }
                    }
                }
                library("nova") {
                    repositories.add(TRIUMPH_SNAPSHOTS)
                    group = "dev.triumphteam"
                    artifact = "nova"
                    version = "1.0.0-SNAPSHOT"
                    relocate("dev.triumphteam")

                    library("triumph_gui_core") {
                        artifact = "triumph-gui-core"
                        version = "4.0.0-SNAPSHOT"

                        library("triumph_gui_paper") {
                            artifact = "triumph-gui-paper"
                        }
                    }
                }
                library("jemoji") {
                    repositories.add(MAVEN_CENTRAL)
                    group = "net.fellbaum"
                    artifact = "jemoji"
                    version = "2.0.0"
                    relocate("net.fellbaum")
                }
            }
        }

        dependency {
            optional {
                repositories.add(PLACEHOLDER_API)
                parse("me.clip:placeholderapi:2.12.2")

                pluginYml = "PlaceholderAPI"
                modrinth = "placeholderapi"
            }
        }

        pluginYml {
            developerData(SRNYX)
            permissionPrefix = "eventalerts"

            command("eventalerts") {
                description = "Primary command for Event Alerts"
                aliases.add("ea")
            }

            permission("reload") {
                description = "Allows the player to use /eventalerts reload"
            }
            permission("config") {
                description = "Allows the player to use /eventalerts config"
            }
            permission("linking.check") {
                description = "Allows the player to use /eventalerts linking check"
            }
            permission("linking.bypass") {
                description = "Allows the player to bypass the linking requirement"
                default = FALSE
            }
            permission("linking.discord") {
                description = "Allows the player to use /eventalerts linking discord"
                default = TRUE
            }
            permission("linking.minecraft") {
                description = "Allows the player to use /eventalerts linking minecraft"
                default = TRUE
            }
            permission("crossban.check") {
                description = "Allows the player to use /eventalerts crossban check"
            }
            permission("crossban.bypass") {
                description = "Allows the player to bypass the cross-banning system"
                default = FALSE
            }
        }

        platformPublishing {
            modrinth("DmjI2XpF")
            github("Event-Alerts/event-alerts-integration")

            projectData("event-alerts-integration")
        }
    }

    testing {
        jUnit("6.1.0")
        mockBukkit("3.9.0")
    }
}
