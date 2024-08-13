package me.jakepronger.rotatingshop.config;

import me.jakepronger.rotatingshop.utils.ItemSerializer;
import me.jakepronger.rotatingshop.utils.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class DataUtils {

    public final String filePath;
    public final File file;

    private FileConfiguration config;

    public DataUtils(String fileName) {

        filePath = plugin.getDataFolder() + File.separator + fileName;
        file = new File(filePath);

        loadConfig();
    }

    public boolean isTimerEnabled() {
        return config.getBoolean("time.uptime-updater.use", true);
    }

    public int getTimerMinutes() {
        return config.getInt("time.uptime-updater.minutes", 5);
    }

    public long getUptime() {
        return config.getLong("time.uptime", 0);
    }

    public int getItemsAmount() {

        int amount = 0;

        ConfigurationSection section = config.getConfigurationSection("items");
        if (section != null) {
            amount = section.getKeys(false).size();
        }

        return amount;
    }

    public CompletableFuture<Void> setRotationInts(List<Integer> intList) {
        return CompletableFuture.supplyAsync(() -> {

            List<String> list = new ArrayList<>();
            for (int i : intList) {
                list.add(String.valueOf(i));
            }

            String value = String.join(",", list);
            config.set("rotation", value);

            save(config);

            return null;
        });
    }

    public CompletableFuture<Void> setUptime(long uptime) {
        return CompletableFuture.runAsync(() -> {
            config.set("time.uptime", uptime);
            save(config);
        });
    }

    public CompletableFuture<Map.Entry<ItemStack, Double>> getItem(int index) {
        return CompletableFuture.supplyAsync(() -> {

            String sectionPath = "items." + index;
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

            ArrayList<Map.Entry<ItemStack, Double>> list = new ArrayList<>();

            for (int index = 0; true; index++) {
                String sectionPath = "items." + index;
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

            String sectionPath = "items." + index;

            ConfigurationSection section = config.getConfigurationSection(sectionPath);
            if (section == null)
                return false;

            section.set("price", price);

            return save(config);
        });
    }

    public CompletableFuture<Boolean> removeItem(int index) {
        return CompletableFuture.supplyAsync(() -> {

            String sectionPath = "items." + index;

            ConfigurationSection section = config.getConfigurationSection(sectionPath);
            if (section == null)
                return false;

            config.set(sectionPath, null);

            for (int loopId = index; true; loopId++) {
                ConfigurationSection loopSection = config.getConfigurationSection("items." + loopId);
                if (loopSection == null)
                    break;
                else {
                    loopSection.set("items." + (loopId - 1), loopId);
                }
            }

            // update current rotation items

            // todo: if any of rotation items are above index
            // todo: loop rotation items numbers
            // todo: if number is above index remove one from number (in current rotation)
            // todo: if number is index reupdate number in current rotation?

            return save(config);
        });
    }

    public CompletableFuture<Boolean> addItem(ItemStack item, double price) {

        int nextId = getNextIndex(config);

        return CompletableFuture.supplyAsync(() -> {

            ConfigurationSection section = config.getConfigurationSection("items");
            if (section == null) {
                section = config.createSection("items");
            }

            section = section.createSection(String.valueOf(nextId));

            section.set("item", ItemSerializer.serializeItemStack(item));
            section.set("price", price);

            return save(config);
        });
    }

    private int getNextIndex(FileConfiguration config) {

        ConfigurationSection section = config.getConfigurationSection("items");
        if (section == null)
            return 0;

        return section.getKeys(false).size();
    }

    public void reloadConfig() {
        loadConfig(true);
    }

    public void loadConfig() {
        loadConfig(false);
    }

    private void loadConfig(boolean isReload) {

        boolean fileExists = file.exists();

        if (!fileExists) {

            InputStream stream = plugin.getResource(file.getName());

            if (stream == null) {
                Logger.error("Resource not found: " + file.getName());
                return;
            }

            try {
                Files.copy(stream, new File(plugin.getDataFolder(), file.getName()).toPath());
            } catch (IOException e) {
                Logger.error("Error creating data.yml file: " + e.getMessage());
                return;
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        if (isReload)
            Logger.log("&aReloaded data.yml file.");
        else if (!fileExists)
            Logger.log("&aCreated data.yml file.");
        else
            Logger.log("&aLoaded data.yml file.");
    }

    private boolean save(FileConfiguration config) {

        if (file == null) {
            Logger.error("Failed to save unknown file because it's null.");
            return false;
        }

        try {
            config.save(file);
            Logger.debug("Saved data.yml file.");
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return false;
        }

        this.config = config;
        Logger.debug("Updated data.yml config.");

        return true;
    }

}
