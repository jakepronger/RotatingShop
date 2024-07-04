package me.jakepronger.rotatingshop.utils;

import org.bukkit.Bukkit;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class Logger {

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + plugin.getName() + "] " + Utils.format(message));
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + plugin.getName() + "] " + Utils.format("&c" + message));
    }

}
