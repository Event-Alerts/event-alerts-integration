package gg.eventalerts.eventalertsintegration.reflection.org.bukkit.entity;

import org.bukkit.entity.Player;

import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.utility.ReflectionUtility;

import java.lang.reflect.Method;


public class RefPlayer {
    @Nullable public static final Method TRANSFER = ReflectionUtility.getMethod(1, 20, 5, Player.class, "transfer", String.class, int.class);
}
