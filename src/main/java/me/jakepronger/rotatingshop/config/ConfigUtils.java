package me.jakepronger.rotatingshop.config;

import me.jakepronger.rotatingshop.utils.Logger;

import me.jakepronger.rotatingshop.utils.Utils;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigUtils {

    private String reloadPerm;
    private boolean useReloadPerm;

    private String editorPerm;
    private boolean useEditorPerm;

    private String blackmarketPerm;
    private boolean useBlackMarketPerm;

    private String noPerm;

    private boolean logDebug;

    private int rotateMinutes;

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

        boolean configExists = new File(plugin.getDataFolder() + File.separator + "config.yml").exists();

        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        rotateMinutes = plugin.getConfig().getInt("shop.items.rotation", 360);

        if (!configExists)
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

    public boolean hasBlackMarketPerm(CommandSender s) {
        return !useBlackMarketPerm || s.hasPermission(blackmarketPerm);
    }

    public boolean hasReloadPerm(CommandSender s) {
        return !useReloadPerm || s.hasPermission(reloadPerm);
    }

    public boolean hasEditorPerm(CommandSender s) {
        return !useEditorPerm || s.hasPermission(editorPerm);
    }

    public String getNoPermMessage() {
        return Utils.format(noPerm);
    }

    public boolean isLogDebug() {
        return logDebug;
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

        logDebug = config.getBoolean("log.debug", false);

        if (reload)
            Logger.log("&aReloaded permissions.");
        else
            Logger.log("&aLoaded permissions.");
    }

}
