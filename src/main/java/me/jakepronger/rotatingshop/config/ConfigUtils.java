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

    private final JavaPlugin plugin;

    public ConfigUtils(JavaPlugin plugin) {
        this.plugin = plugin;

        loadConfig();
        loadPerms();
    }

    public void loadConfig() {
        loadConfig(false);
    }

    public void reloadConfig() {
        loadConfig(true);
    }

    private void loadConfig(boolean reload) {

        boolean newlyCreated = new File(plugin.getDataFolder() + File.separator + "config.yml").exists();

        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        if (newlyCreated)
            Logger.log("&aCreated config.");
        else {
            if (reload)
                Logger.log("&aReloaded config.");
            else
                Logger.log("&aLoaded config.");
        }
    }

    public void loadPerms() {
        loadPerms(false);
    }

    public void reloadPerms() {
        loadPerms(true);
    }

    private void loadPerms(boolean reload) {

        FileConfiguration config = plugin.getConfig();

        blackmarketPerm = config.getString("permissions.black-market.node", "rs.black-market");
        useBlackMarketPerm = config.getBoolean("permissions.black-market.require", true);

        reloadPerm = config.getString("permissions.reload.node", "rs.reload");
        useReloadPerm = config.getBoolean("permissions.reload.require", true);

        editorPerm = config.getString("permissions.editor.node", "rs.editor");
        useEditorPerm = config.getBoolean("permissions.editor.require", true);

        noPerm = config.getString("permissions.no-perm", "&cNo permissions!");

        if (reload)
            Logger.log("&aReloaded permissions.");
        else
            Logger.log("&aLoaded permissions.");
    }

}