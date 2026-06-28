package gg.eventalerts.eventalertsintegration.stats;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;


public class StatsCollector {
    @NotNull public final AtomicInteger joinButtonClicks = new AtomicInteger();
}
