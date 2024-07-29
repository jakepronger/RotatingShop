package me.jakepronger.rotatingshop.config;

import me.jakepronger.rotatingshop.utils.ItemSerializer;
import me.jakepronger.rotatingshop.utils.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
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

    public CompletableFuture<Map.Entry<ItemStack, Double>> getItem(int index) {
        return CompletableFuture.supplyAsync(() -> {

            FileConfiguration config = getConfig();

            String sectionPath = "data." + index;
            ConfigurationSection section = config.getConfigurationSection(sectionPath);

            if (section == null) {
                return null;
            }

            double price = section.getDouble("price");
            ItemStack item = ItemSerializer.deserializeItemStack(section.getString("item"));

            return Map.entry(item, price);
        });
    }

    public CompletableFuture<ArrayList<Map.Entry<ItemStack, Double>>> getItems() {
        return CompletableFuture.supplyAsync(() -> {

            FileConfiguration config = getConfig();

            ArrayList<Map.Entry<ItemStack, Double>> list = new ArrayList<>();

            for (int index = 0; true; index++) {
                String sectionPath = "data." + index;
                ConfigurationSection section = config.getConfigurationSection(sectionPath);

                if (section == null) {
                    break;
                }

                double price = section.getDouble("price");
                ItemStack item = ItemSerializer.deserializeItemStack(section.getString("item"));

                list.add(Map.entry(item, price));
            }

            return list;
        });
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

    public CompletableFuture<Boolean> removeItem(int index) {
        return CompletableFuture.supplyAsync(() -> {

            FileConfiguration config = getConfig();

            String sectionPath = "data." + index;

            ConfigurationSection section = config.getConfigurationSection(sectionPath);
            if (section == null)
                return false;

            config.set(sectionPath, null);

            // todo: loop through all other items and adjust their order number

            return save(config);
        });
    }

    public CompletableFuture<Boolean> addItem(ItemStack item, double price) {

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

        return section.getKeys(false).size();
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
