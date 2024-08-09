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

    private String reloadPerm;
    private boolean useReloadPerm;

    private String editorPerm;
    private boolean useEditorPerm;

    private String blackmarketPerm;
    private boolean useBlackMarketPerm;

    private boolean logDebug;

    private String noPerm;

    private final JavaPlugin plugin;

    private FileConfiguration config;

    public ConfigUtils(JavaPlugin plugin) {

        this.plugin = plugin;

        loadConfig();
    }

    public boolean hasReloadPerm(CommandSender s) {
        return !useReloadPerm || s.hasPermission(reloadPerm);
    }

    public boolean hasEditorPerm(CommandSender s) {
        return !useEditorPerm || s.hasPermission(editorPerm);
    }

    public boolean hasBlackMarketPerm(CommandSender s) {
        return !useBlackMarketPerm || s.hasPermission(blackmarketPerm);
    }

    public boolean isLogDebug() {
        return logDebug;
    }

    public String getNoPermMessage() {
        return Utils.format(noPerm);
    }

    public int getItemRotateMinutes() {
        return config.getInt("shop.items.rotation", 360);
    }

    public List<Integer> getItemSlots() {

        List<Integer> itemSlots = new ArrayList<>();

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

        return itemSlots;
    }

    public void loadConfig() {
        loadConfig(false);
    }

    public void reloadConfig() {
        loadConfig(true);
    }

    private void loadConfig(boolean reload) {

        boolean configExists = new File(plugin.getDataFolder() + File.separator + "config.yml").exists();

        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        config = plugin.getConfig();

        blackmarketPerm = config.getString("permissions.black-market.node", "rs.black-market");
        useBlackMarketPerm = config.getBoolean("permissions.black-market.require", true);

        reloadPerm = config.getString("permissions.reload.node", "rs.reload");
        useReloadPerm = config.getBoolean("permissions.reload.require", true);

        editorPerm = config.getString("permissions.editor.node", "rs.editor");
        useEditorPerm = config.getBoolean("permissions.editor.require", true);

        noPerm = config.getString("permissions.no-perm", "&cNo permissions!");

        logDebug = config.getBoolean("log.debug", false);

        if (!configExists)
            Logger.log("&aCreated plugin config.");
        else {
            if (reload)
                Logger.log("&aReloaded plugin config.");
            else
                Logger.log("&aLoaded plugin config.");
        }
    }

}
