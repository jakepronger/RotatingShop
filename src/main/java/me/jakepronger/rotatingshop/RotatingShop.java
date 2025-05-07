package me.jakepronger.rotatingshop;

import me.jakepronger.rotatingshop.commands.BlackMarketCommand;
import me.jakepronger.rotatingshop.managers.ConfigManager;
import me.jakepronger.rotatingshop.hooks.PlayerPointsHook;
import me.jakepronger.rotatingshop.listeners.BlackMarketItemsListener;
import me.jakepronger.rotatingshop.listeners.BlackMarketListener;
import me.jakepronger.rotatingshop.managers.DataManager;
import me.jakepronger.rotatingshop.managers.InventoryManager;
import me.jakepronger.rotatingshop.utils.Logger;

import me.jakepronger.rotatingshop.managers.RotationManager;
import me.jakepronger.rotatingshop.managers.TimerManager;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RotatingShop extends JavaPlugin {

    private static RotatingShop instance;

    private ConfigManager configUtils;
    private DataManager dataUtils;
    private RotationManager rotationUtils;
    private TimerManager timerUtils;

    private InventoryManager inventoryManager;

    private PlayerPointsHook ppHook;

    @Override
    public void onEnable() {

        instance = this;

        configUtils = new ConfigManager(this);
        dataUtils = new DataManager("data.json");
        ppHook = new PlayerPointsHook(this);

        if (!ppHook.hook()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        rotationUtils = new RotationManager(dataUtils, configUtils);

        timerUtils = new TimerManager(this);
        timerUtils.startRotateTimer();
        timerUtils.startTimer();

        inventoryManager = new InventoryManager(instance);

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

    /**
     * Getter Methods
     */

    public static RotatingShop getInstance() {
        return instance; // Global access to the plugin instance
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public ConfigManager getConfigUtils() {
        return configUtils;
    }

    public DataManager getDataUtils() {
        return dataUtils;
    }

    public TimerManager getTimerUtils() {
        return timerUtils;
    }

    public RotationManager getRotationUtils() {
        return rotationUtils;
    }

    public PlayerPointsHook getPlayerPointsHook() {
        return ppHook;
    }

    /**
     * Register Methods
     */

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BlackMarketListener(instance), this);
        pm.registerEvents(new BlackMarketItemsListener(instance), this);
        pm.registerEvents(new InventoryManager(instance), this);
    }

    private void registerCommands() {
        PluginCommand blackmarket = getCommand("blackmarket");
        if (blackmarket != null)
            blackmarket.setExecutor(new BlackMarketCommand());
    }

    /**
     * Reload Method
     */

    public String reload() {

        long delay = System.currentTimeMillis();

        Logger.log("&cReloading...");
        Logger.log("&aClosed &f" + inventoryManager.closeInventories() + "&a inventories.");

        getConfigUtils().reloadConfig();
        getDataUtils().reloadConfig();

        PlayerPointsHook ppHook = getPlayerPointsHook();
        ppHook.unhook();
        if (!ppHook.hook()) {
            getServer().getPluginManager().disablePlugin(this);
            return "";
        }

        getTimerUtils().reloadTimer();
        getRotationUtils().reload();

        long duration = System.currentTimeMillis() - delay;

        return "&cReloaded in &f" + duration + "ms&c.";
    }

}