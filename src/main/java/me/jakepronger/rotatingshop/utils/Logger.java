package me.jakepronger.rotatingshop.utils;

import org.bukkit.Bukkit;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class Logger {

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + plugin.getName() + "] " + Utils.format(message));
    }

    public static void debug(String message) {
        if (plugin.getConfigUtils().logDebug)
            Bukkit.getConsoleSender().sendMessage(Utils.format("&e") + "[" + plugin.getName() + "] [DEBUG] " + Utils.format(message));
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage(Utils.format("&c") + "[" + plugin.getName() + "] [ERROR] " + Utils.format(message));
    }

}
