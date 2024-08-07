package me.jakepronger.rotatingshop.hooks;

import me.jakepronger.rotatingshop.utils.Logger;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerPointsHook {

    private final JavaPlugin plugin;

    private PlayerPointsAPI ppAPI;

    public PlayerPointsHook(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public PlayerPointsAPI get() {
        return ppAPI;
    }

    public void unhook() {
        ppAPI = null;
        Logger.log("&aUnhooked from PlayerPoints!");
    }

    public boolean hook() {

        if (plugin.getServer().getPluginManager().getPlugin("PlayerPoints") == null) {
            Logger.error("Failed to locate required plugin: PlayerPoints");
            return false;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            Logger.error("Failed to hook into PlayerPoints: Plugin is not enabled");
            return false;
        }

        try {
            ppAPI = PlayerPoints.getInstance().getAPI();
        } catch (Exception e) {
            Logger.error("Failed to hook into PlayerPoints: " + e.getMessage());
            return false;
        }

        Logger.log("&aHooked into PlayerPoints!");
        return true;
    }

}
