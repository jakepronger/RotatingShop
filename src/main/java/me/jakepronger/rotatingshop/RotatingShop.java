package me.jakepronger.rotatingshop;

import me.jakepronger.rotatingshop.commands.BlackMarketCommand;
import me.jakepronger.rotatingshop.config.ConfigUtils;
import me.jakepronger.rotatingshop.hooks.PlayerPointsHook;
import me.jakepronger.rotatingshop.listeners.BlackMarketItemsListener;
import me.jakepronger.rotatingshop.listeners.BlackMarketListener;
import me.jakepronger.rotatingshop.config.DataUtils;
import me.jakepronger.rotatingshop.utils.Logger;

import me.jakepronger.rotatingshop.utils.TimerUtils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RotatingShop extends JavaPlugin {

    public static RotatingShop plugin;

    public DataUtils dataFile;
    public TimerUtils timerUtils;

    public long START_TIME;

    private ConfigUtils config;

    private PlayerPointsHook ppHook;

    @Override
    public void onEnable() {

        plugin = this;

        config = new ConfigUtils(plugin);
        dataFile = new DataUtils("data.yml");

        ppHook = new PlayerPointsHook(plugin);
        if (!ppHook.hook()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // todo: integrate with timer
        START_TIME = System.currentTimeMillis();
        timerUtils = new TimerUtils(dataFile);
        timerUtils.startTimer();

        registerEvents();
        registerCommands();

        Logger.log("&aEnabled");
    }

    @Override
    public void onDisable() {
        ppHook.unhook();
        timerUtils.updateServerStoppedTime();
        Logger.log("&cDisabled");
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new BlackMarketListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new BlackMarketItemsListener(), plugin);
    }

    private void registerCommands() {
        getCommand("blackmarket").setExecutor(new BlackMarketCommand());
    }

}