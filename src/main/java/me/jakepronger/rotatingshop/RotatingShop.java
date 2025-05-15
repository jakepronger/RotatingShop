package me.jakepronger.rotatingshop;

import me.jakepronger.rotatingshop.commands.BlackMarketCommand;
import me.jakepronger.rotatingshop.listeners.BlackMarketItemsListener;
import me.jakepronger.rotatingshop.listeners.BlackMarketListener;
import me.jakepronger.rotatingshop.managers.ConfigManager;
import me.jakepronger.rotatingshop.hooks.PlayerPointsHook;
import me.jakepronger.rotatingshop.managers.DataManager;
import me.jakepronger.rotatingshop.managers.InventoryManager;

import me.jakepronger.rotatingshop.managers.RotationManager;
import me.jakepronger.rotatingshop.managers.TimerManager;

import me.jakepronger.rotatingshop.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RotatingShop extends JavaPlugin {

    private static RotatingShop instance;

    private ConfigManager configManager;
    private DataManager dataManager;

    // todo: combine both?
    private RotationManager rotationManager;
    private TimerManager timerManager;

    private InventoryManager invManager;

    private PlayerPointsHook ppHook;

    @Override
    public void onEnable() {

        instance = this;

        configManager = new ConfigManager(this);
        dataManager = new DataManager(instance, "data.json");
        ppHook = new PlayerPointsHook(this);

        if (!ppHook.hook()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        rotationManager = new RotationManager(dataManager, configManager);

        timerManager = new TimerManager(this);
        timerManager.startRotateTimer();
        timerManager.startTimer();

        invManager = new InventoryManager(instance);

        registerEvents();
        Logger.debug("Registered events.");

        registerCommands();
        Logger.debug("Registered commands.");

        Logger.log("&aEnabled");
    }

    @Override
    public void onDisable() {

        ppHook.unhook();
        timerManager.stopRotateTimer();
        timerManager.stopTimer();

        timerManager.updateUptime().whenComplete((result, throwable) -> {

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
        return invManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public RotationManager getRotationManager() {
        return rotationManager;
    }

    public PlayerPointsHook getPlayerPointsHook() {
        return ppHook;
    }

    /**
     * Register Methods
     */

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(invManager, this);
        pm.registerEvents(new BlackMarketListener(instance), this);
        pm.registerEvents(new BlackMarketItemsListener(instance), this);
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
        Logger.log("&aClosed &f" + invManager.closeAll() + "&a inventories.");

        configManager.reloadConfig();
        dataManager.reloadConfig();

        PlayerPointsHook ppHook = getPlayerPointsHook();
        ppHook.unhook();
        if (!ppHook.hook()) {
            getServer().getPluginManager().disablePlugin(this);
            return "";
        }

        timerManager.reloadTimer();
        rotationManager.reload();

        long duration = System.currentTimeMillis() - delay;

        return "&cReloaded in &f" + duration + "ms&c.";
    }

}