package me.jakepronger.rotatingshop.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class ConfigUtils {

    public final String filePath;
    public final File file;

    private final FileConfiguration config;

    public ConfigUtils(String fileName) {

        filePath = plugin.getDataFolder() + File.separator + fileName;

        file = new File(filePath);

        if (!file.exists()) {
            try {
                if (!file.createNewFile())
                    throw new Exception("createNewFile() returned false for \"" + file.getName() + "\"");
                else
                    Logger.log("&aCreated file \"" + file.getName() + "\".");
            } catch (Exception e) {
                Logger.error("Error creating ConfigUtils file: " + e.getMessage());
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void save(FileConfiguration config) {

        if (file == null) {
            Logger.error("Failed to save unknown file because it's null.");
            return;
        }

        try {
            config.save(file);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

}
