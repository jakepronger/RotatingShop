package me.jakepronger.rotatingshop.config;

import me.jakepronger.rotatingshop.utils.Logger;
import me.jakepronger.rotatingshop.utils.Utils;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {

    private final JavaPlugin plugin;

    private final List<Integer> itemSlots;

    private FileConfiguration config;

    public ConfigUtils(JavaPlugin plugin) {

        this.plugin = plugin;
        this.itemSlots = new ArrayList<>();

        loadConfig();
    }

    public boolean hasReloadPerm(CommandSender s) {
        return !config.getBoolean("permissions.reload.require", true)
                || s.hasPermission(config.getString("permissions.reload.node", "rs.reload"));
    }

    public boolean hasEditorPerm(CommandSender s) {
        return !config.getBoolean("permissions.editor.require", true)
                || s.hasPermission(config.getString("permissions.editor.node", "rs.editor"));
    }

    public boolean hasBlackMarketPerm(CommandSender s) {
        return !config.getBoolean("permissions.black-market.require", true)
                || s.hasPermission(config.getString("permissions.black-market.node", "rs.black-market"));
    }

    public boolean isLogDebug() {
        return config.getBoolean("log.debug", false);
    }

    public String getNoPermMessage() {
        return Utils.format(config.getString("permissions.no-perm", "&cNo permissions!"));
    }

    public int getItemRotateMinutes() {
        return config.getInt("shop.items.rotation", 360);
    }

    public List<Integer> getItemSlots() {
        return itemSlots;
    }

    public void loadConfig() {
        loadConfig(false);
    }

    public void reloadConfig() {
        loadConfig(true);
    }

    private void loadConfig(boolean isReload) {

        boolean fileExists = new File(plugin.getDataFolder() + File.separator + "config.yml").exists();

        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        config = plugin.getConfig();

        loadItemSlots();

        if (isReload)
            Logger.log("&aReloaded plugin config.");
        else if (!fileExists)
            Logger.log("&aCreated plugin config.");
        else
            Logger.log("&aLoaded plugin config.");
    }

    private void loadItemSlots() {

        itemSlots.clear();

        String[] slots = config.getString("shop.items.slots", "12,13,14").split(",");

        for (String slot : slots) {
            int slotValue;
            try {
                slotValue = Integer.parseInt(slot);
            } catch (Exception e) {
                Logger.error("Error parsing int: " + e.getMessage());
                continue;
            }

            itemSlots.add(slotValue);
        }

    }

}
