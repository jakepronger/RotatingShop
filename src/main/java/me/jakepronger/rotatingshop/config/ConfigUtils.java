package me.jakepronger.rotatingshop.config;

import me.jakepronger.rotatingshop.utils.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigUtils {

    public String reloadPerm;
    public boolean useReloadPerm;

    public String editorPerm;
    public boolean useEditorPerm;

    public String blackmarketPerm;
    public boolean useBlackMarketPerm;

    public String noPerm;

    public boolean logDebug;

    public int rotateMinutes;

    private final JavaPlugin plugin;

    public ConfigUtils(JavaPlugin plugin) {

        this.plugin = plugin;

        loadConfig();
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

        FileConfiguration config = plugin.getConfig();

        blackmarketPerm = config.getString("permissions.black-market.node", "rs.black-market");
        useBlackMarketPerm = config.getBoolean("permissions.black-market.require", true);

        reloadPerm = config.getString("permissions.reload.node", "rs.reload");
        useReloadPerm = config.getBoolean("permissions.reload.require", true);

        editorPerm = config.getString("permissions.editor.node", "rs.editor");
        useEditorPerm = config.getBoolean("permissions.editor.require", true);

        noPerm = config.getString("permissions.no-perm", "&cNo permissions!");

        logDebug = config.getBoolean("log.debug", false);

        rotateMinutes = config.getInt("shop.items.rotation", 360);

        if (!configExists)
            Logger.log("&aCreated config.");
        else {
            if (reload)
                Logger.log("&aReloaded config.");
            else
                Logger.log("&aLoaded config.");
        }
    }

}
