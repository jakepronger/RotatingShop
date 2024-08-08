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

    private ConfigUtils configUtils;
    private DataUtils dataUtils;
    private TimerUtils timerUtils;

    private PlayerPointsHook ppHook;

    @Override
    public void onEnable() {

        plugin = this;

        configUtils = new ConfigUtils(plugin);
        dataUtils = new DataUtils("data.yml");

        ppHook = new PlayerPointsHook(plugin);
        if (!ppHook.hook()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        timerUtils = new TimerUtils(dataUtils);
        timerUtils.startRotateTimer();
        timerUtils.startTimer();

        registerEvents();
        Logger.log("&aRegistered events.");

        registerCommands();
        Logger.log("&aRegistered commands.");

        Logger.log("&aEnabled");
    }

    @Override
    public void onDisable() {

        ppHook.unhook();
        timerUtils.stopRotateTimer();
        timerUtils.stopTimer();

        timerUtils.updateUptime().whenComplete((result, throwable) -> {

            if (throwable != null) {
                Logger.error("Error updating uptime: " + throwable.getMessage());
            }

            Logger.log("&cDisabled");
        });
    }

    public ConfigUtils getConfigUtils() {
        return configUtils;
    }

    public DataUtils getDataUtils() {
        return dataUtils;
    }

    public PlayerPointsHook getPlayerPointsHook() {
        return ppHook;
    }

    public TimerUtils getTimerUtils() {
        return timerUtils;
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new BlackMarketListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new BlackMarketItemsListener(), plugin);
    }

    private void registerCommands() {
        getCommand("blackmarket").setExecutor(new BlackMarketCommand());
    }

}