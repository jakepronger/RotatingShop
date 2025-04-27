package me.jakepronger.rotatingshop;

import me.jakepronger.rotatingshop.commands.BlackMarketCommand;
import me.jakepronger.rotatingshop.config.ConfigUtils;
import me.jakepronger.rotatingshop.hooks.PlayerPointsHook;
import me.jakepronger.rotatingshop.listeners.BlackMarketItemsListener;
import me.jakepronger.rotatingshop.listeners.BlackMarketListener;
import me.jakepronger.rotatingshop.config.DataUtils;
import me.jakepronger.rotatingshop.managers.InventoryManager;
import me.jakepronger.rotatingshop.utils.InvUtils;
import me.jakepronger.rotatingshop.utils.Logger;

import me.jakepronger.rotatingshop.utils.RotationUtils;
import me.jakepronger.rotatingshop.utils.TimerUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RotatingShop extends JavaPlugin {

    private static RotatingShop instance;

    private ConfigUtils configUtils;
    private DataUtils dataUtils;
    private RotationUtils rotationUtils;
    private TimerUtils timerUtils;

    private InventoryManager inventoryManager;

    private PlayerPointsHook ppHook;

    @Override
    public void onEnable() {

        instance = this;

        configUtils = new ConfigUtils(this);
        dataUtils = new DataUtils("data.json");
        ppHook = new PlayerPointsHook(this);

        if (!ppHook.hook()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        rotationUtils = new RotationUtils(dataUtils, configUtils);

        timerUtils = new TimerUtils(this);
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

    public static RotatingShop getInstance() {
        return instance; // Global access to the plugin instance
    }

    /**
     * Getter Methods
     */

    public InventoryManager getInventoryManager() {
        return inventoryManager;
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
        Logger.log("&aClosed &f" + InvUtils.closeInventories() + "&a inventories.");

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