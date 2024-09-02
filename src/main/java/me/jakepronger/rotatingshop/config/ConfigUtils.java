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

    private final List<Integer> shopItemSlots;
    private final List<Integer> editorItemSlots;

    private FileConfiguration config;

    public ConfigUtils(JavaPlugin plugin) {

        this.plugin = plugin;

        this.shopItemSlots = new ArrayList<>();
        this.editorItemSlots = new ArrayList<>();

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
        return config.getInt("shop.items.rotation", 360) / 60;
    }

    public List<Integer> getShopItemSlots() {
        return shopItemSlots;
    }

    private void loadShopItemSlots() {

        shopItemSlots.clear();

        String[] slots = config.getString("shop.items.slots", "12,13,14").split(",");

        for (String slot : slots) {
            int slotValue;
            try {
                slotValue = Integer.parseInt(slot);
            } catch (Exception e) {
                Logger.error("Error parsing int: " + e.getMessage());
                continue;
            }

            shopItemSlots.add(slotValue);
        }

    }

    public List<Integer> getEditorItemSlots() {
        return editorItemSlots;
    }

    private void loadEditorItemSlots() {

        editorItemSlots.clear();

        String[] slots = config.getString("editor.items.slots", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44").split(",");

        for (String slot : slots) {
            int slotValue;
            try {
                slotValue = Integer.parseInt(slot);
            } catch (Exception e) {
                Logger.error("Error parsing int: " + e.getMessage());
                continue;
            }

            editorItemSlots.add(slotValue);
        }

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

        loadShopItemSlots();
        loadEditorItemSlots();

        if (isReload)
            Logger.log("&aReloaded plugin config.");
        else if (!fileExists)
            Logger.log("&aCreated plugin config.");
        else
            Logger.log("&aLoaded plugin config.");
    }

}
