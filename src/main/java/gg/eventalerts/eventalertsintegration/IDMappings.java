package gg.eventalerts.eventalertsintegration;

import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.libs.javautilities.MapGenerator;

import java.util.List;
import java.util.Map;


public class IDMappings {
    @NotNull public static final Map<Long, String> ID_MAPPINGS = MapGenerator.HASH_MAP.mapOf(List.of(
            // Users
            242385234992037888L, "srnyx",
            365630764244664320L, "Oiiink",
            381890968971902976L, "Rame",
            533985117589471233L, "Reece",
            267734235224211467L, "bacca",
            604377897662414854L, "hailey",
            314853603695394817L, "Skeppy",
            468890330763231270L, "hayech",

            // Roles
            1406379715471610029L, "MC Server Pings",
            1111741660892762142L, "Official Events",
            1006349851241480242L, "Creator Events",
            1426711659740790915L, "Event-B Info",
            1216096556713906288L, "Potential Skeppy Events",
            1280002787446493256L, "Skeppy Sighting",
            1096205843113967669L, "Random Pings",
            970434201990070424L, "Partner Events",
            1498450271825825874L, "Big Money Events",
            970434305203511359L, "Money Events",
            970434303391576164L, "Fun Events",
            970434294893928498L, "Housing Events",
            1134932175821734119L, "Civilization Events",
            980950599946362900L, "Community Events"));
}
