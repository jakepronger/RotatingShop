package me.jakepronger.rotatingshop.utils;

import me.jakepronger.rotatingshop.RotatingShop;

import org.bukkit.Bukkit;

public class Logger {

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + RotatingShop.getInstance().getName() + "] " + Utils.format(message));
    }

    public static void debug(String message) {
        if (RotatingShop.getInstance().getConfigManager().isLogDebug())
            Bukkit.getConsoleSender().sendMessage(Utils.format("&e") + "[" + RotatingShop.getInstance().getName() + "] [DEBUG] " + Utils.format(message));
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage(Utils.format("&c") + "[" + RotatingShop.getInstance().getName() + "] [ERROR] " + Utils.format(message));
    }

}
