package me.jakepronger.rotatingshop;

import me.jakepronger.rotatingshop.commands.BlackMarketCommand;
import me.jakepronger.rotatingshop.listeners.BlackMarketListener;
import me.jakepronger.rotatingshop.utils.Logger;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RotatingShop extends JavaPlugin {

    public static RotatingShop plugin;

    private PlayerPointsAPI ppAPI = null;

    @Override
    public void onEnable() {

        plugin = this;

        if (!setupPlayerPoints()) {
            Logger.error("&cPlayerPoints not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else Logger.log("&aHooked into PlayerPoints");

        registerEvents();
        registerCommands();

        Logger.log("&aEnabled");
    }

    @Override
    public void onDisable() {
        Logger.log("&cDisabled");
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new BlackMarketListener(), plugin);
    }

    private void registerCommands() {
        getCommand("blackmarket").setExecutor(new BlackMarketCommand());
    }

    public PlayerPointsAPI getPlayerPoints() {
        return plugin.ppAPI;
    }

    private boolean setupPlayerPoints() {
        if (getServer().getPluginManager().getPlugin("PlayerPoints") == null) {
            return false;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            ppAPI = PlayerPoints.getInstance().getAPI();
        }
        return ppAPI != null;
    }

}