package me.jakepronger.rotatingshop;

import me.jakepronger.rotatingshop.commands.BlackMarketCommand;
import me.jakepronger.rotatingshop.listeners.BlackMarketItemsListener;
import me.jakepronger.rotatingshop.listeners.BlackMarketListener;
import me.jakepronger.rotatingshop.config.DataUtils;
import me.jakepronger.rotatingshop.utils.Logger;

import me.jakepronger.rotatingshop.utils.TimerUtils;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RotatingShop extends JavaPlugin {

    public static RotatingShop plugin;

    public String reloadPerm;
    public String editorPerm;
    public String noPerm;

    public DataUtils dataFile;

    public long START_TIME;

    private PlayerPointsAPI ppAPI;

    @Override
    public void onEnable() {

        plugin = this;

        loadConfig();
        Logger.log("&aLoaded config.");

        loadPerms();
        Logger.log("&aLoaded permissions.");

        START_TIME = System.currentTimeMillis();

        dataFile = new DataUtils("data.yml");

        if (!setupPlayerPoints()) {
            Logger.error("&cPlayerPoints not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else Logger.log("&aHooked into PlayerPoints!");

        registerEvents();
        registerCommands();

        TimerUtils.startTimer();

        Logger.log("&aEnabled");
    }

    @Override
    public void onDisable() {
        TimerUtils.updateServerStoppedTime();
        Logger.log("&cDisabled");
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new BlackMarketListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new BlackMarketItemsListener(), plugin);
    }

    private void registerCommands() {
        getCommand("blackmarket").setExecutor(new BlackMarketCommand());
    }

    public PlayerPointsAPI getPlayerPoints() {
        return plugin.ppAPI;
    }

    public void loadPerms() {
        reloadPerm = getConfig().getString("permissions.reload", "rotatingshop.reload");
        editorPerm = getConfig().getString("permissions.editor", "rotatingshop.editor");
        noPerm = getConfig().getString("permissions.no-permission.message", "&cNo permissions!");
    }

    public void loadConfig() {
        saveDefaultConfig();
        reloadConfig();
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