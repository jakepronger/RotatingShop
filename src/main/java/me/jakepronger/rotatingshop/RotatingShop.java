package me.jakepronger.rotatingshop;

import me.jakepronger.rotatingshop.commands.BlackMarketCommand;
import me.jakepronger.rotatingshop.config.ConfigUtils;
import me.jakepronger.rotatingshop.hooks.PlayerPointsHook;
import me.jakepronger.rotatingshop.listeners.BlackMarketItemsListener;
import me.jakepronger.rotatingshop.listeners.BlackMarketListener;
import me.jakepronger.rotatingshop.config.DataUtils;
import me.jakepronger.rotatingshop.utils.InvUtils;
import me.jakepronger.rotatingshop.utils.Logger;

import me.jakepronger.rotatingshop.utils.RotationUtils;
import me.jakepronger.rotatingshop.utils.TimerUtils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RotatingShop extends JavaPlugin {

    public static RotatingShop plugin;

    private ConfigUtils configUtils;
    private DataUtils dataUtils;
    private RotationUtils rotationUtils;
    private TimerUtils timerUtils;

    private PlayerPointsHook ppHook;

    @Override
    public void onEnable() {

        plugin = this;

        configUtils = new ConfigUtils(plugin);
        dataUtils = new DataUtils("data.json");

        ppHook = new PlayerPointsHook(plugin);
        if (!ppHook.hook()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        rotationUtils = new RotationUtils(dataUtils, configUtils);

        timerUtils = new TimerUtils(plugin);
        timerUtils.startRotateTimer();
        timerUtils.startTimer();

        registerEvents();
        Logger.debug("Registered events.");

        registerCommands();
        Logger.debug("Registered commands.");

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

    public TimerUtils getTimerUtils() {
        return timerUtils;
    }

    public RotationUtils getRotationUtils() {
        return rotationUtils;
    }

    public PlayerPointsHook getPlayerPointsHook() {
        return ppHook;
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new BlackMarketListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new BlackMarketItemsListener(), plugin);
    }

    private void registerCommands() {
        getCommand("blackmarket").setExecutor(new BlackMarketCommand());
    }

    public String reload() {

        long delay = System.currentTimeMillis();

        Logger.log("&cReloading...");
        Logger.log("&aClosed &f" + InvUtils.closeInventories() + "&a inventories.");

        ConfigUtils config = plugin.getConfigUtils();
        config.reloadConfig();

        DataUtils data = plugin.getDataUtils();
        data.reloadConfig();

        PlayerPointsHook ppHook = plugin.getPlayerPointsHook();
        ppHook.unhook();
        if (!ppHook.hook()) {
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return "";
        }

        TimerUtils timer = plugin.getTimerUtils();
        timer.reloadTimer();

        plugin.getRotationUtils().reload();

        long duration = System.currentTimeMillis() - delay;
        String message = "&cReloaded in &f" + duration + "ms&c.";

        return message;
    }

}