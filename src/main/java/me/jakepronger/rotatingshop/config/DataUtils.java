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

    private final List<Integer> rotationInts;

    private final File file;
    private FileConfiguration config;

    public DataUtils(String fileName) {

        String filePath = plugin.getDataFolder() + File.separator + fileName;
        file = new File(filePath);

        rotationInts = new ArrayList<>();

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

    public List<Integer> getRotationInts() {
        return rotationInts;
    }

    private void loadRotationInts() {

        String value = config.getString("rotation", "");
        if (value.isEmpty())
            return;

        for (String loopValue : value.split(",")) {

            int number;
            try {
                number = Integer.parseInt(loopValue);
            } catch (Exception e) {
                Logger.error("Error parsing int: " + e.getMessage());
                continue;
            }

            rotationInts.add(number);
        }
    }

    public CompletableFuture<Void> setRotationInts(List<Integer> intList) {
        return CompletableFuture.supplyAsync(() -> {

            StringBuilder builder = new StringBuilder();
            for (int i : intList) {
                if (builder.isEmpty())
                    builder.append(i);
                else builder.append(",").append(i);
            }

            config.set("rotation", builder.toString());

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

            for (int index = 1; true; index++) {
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

            ConfigurationSection itemSection = config.getConfigurationSection("items");
            if (itemSection == null)
                return false;

            ConfigurationSection indexSection = itemSection.getConfigurationSection(String.valueOf(index));
            if (indexSection == null)
                return false;

            config.set("items." + index, null);

            for (int loopId = index+1; true; loopId++) {

                ConfigurationSection loopSection = config.getConfigurationSection("items." + loopId);

                if (loopSection == null) {
                    itemSection.set(String.valueOf(loopId-1), null);
                    //Bukkit.broadcastMessage("last key '" + loopId + "' set null; break loop");
                    //Bukkit.broadcastMessage("loopSection null returning '" + "items." + loopId + "'");
                    break;
                } else {
                    itemSection.set(String.valueOf((loopId - 1)), loopSection);
                    //Bukkit.broadcastMessage("updated item '" + (loopId) + "' to use id " + (loopId-1));
                }

            }

            //Bukkit.broadcastMessage("debug 4");

            // update current rotation items

            // todo: if any of rotation items are above index

            // todo: loop rotation items numbers
            //for (int id : )
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
            return 1;

        int size = section.getKeys(false).size();
        if (size == 0)
            return 1;

        return size + 1;
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

        loadRotationInts();

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
