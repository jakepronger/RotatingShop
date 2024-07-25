package me.jakepronger.rotatingshop.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class DataUtils {

    public final String filePath;
    public final File file;

    private final FileConfiguration config;

    public DataUtils(String fileName) {

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

    public CompletableFuture<Boolean> setItemPrice(int index, double price) {
        return CompletableFuture.supplyAsync(() -> {

            FileConfiguration config = getConfig();

            String sectionPath = "data." + index;

            ConfigurationSection section = config.getConfigurationSection(sectionPath);
            if (section == null)
                return false;

            section.set("price", price);

            return true;
        });
    }

    public CompletableFuture<Boolean> setItemQuantity(int index, int quantity) {
        return CompletableFuture.supplyAsync(() -> {

            FileConfiguration config = getConfig();

            String sectionPath = "data." + index;

            ConfigurationSection section = config.getConfigurationSection(sectionPath);
            if (section == null)
                return false;

            ItemStack item = ItemSerializer.deserializeItemStack(section.getString("item"));
            item.setAmount(quantity);

            section.set("item", ItemSerializer.serializeItemStack(item));

            return true;
        });
    }

    public CompletableFuture<Boolean> removeItem(int index) {
        return CompletableFuture.supplyAsync(() -> {

            FileConfiguration config = getConfig();

            String sectionPath = "data." + index;

            ConfigurationSection section = config.getConfigurationSection(sectionPath);
            if (section == null)
                return false;

            config.set(sectionPath, null);

            return save(config);
        });
    }

    public CompletableFuture<Boolean> addItem(ItemStack item, int price) {

        FileConfiguration config = getConfig();
        int nextId = getNextIndex(config);

        return CompletableFuture.supplyAsync(() -> {

            ConfigurationSection section = config.createSection("data." + nextId);

            section.set("item", ItemSerializer.serializeItemStack(item));
            section.set("price", price);

            return save(config);
        });
    }

    private int getNextIndex(FileConfiguration config) {

        ConfigurationSection section = config.getConfigurationSection("data");
        if (section == null)
            return 0;

        return section.getKeys(false).size() + 1;
    }

    private FileConfiguration getConfig() {
        return config;
    }

    private boolean save(FileConfiguration config) {

        if (file == null) {
            Logger.error("Failed to save unknown file because it's null.");
            return false;
        }

        try {
            config.save(file);
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return false;
        }

        return true;
    }

}
