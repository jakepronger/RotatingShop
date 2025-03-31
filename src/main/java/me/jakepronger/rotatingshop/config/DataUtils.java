package me.jakepronger.rotatingshop.config;

import com.google.gson.*;

import me.jakepronger.rotatingshop.utils.ItemSerializer;
import me.jakepronger.rotatingshop.utils.Logger;

import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class DataUtils {

    private final List<Integer> rotationInts;
    private final ArrayList<Map.Entry<ItemStack, Double>> items;

    private final File file;
    private JsonObject config;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public DataUtils(String fileName) {
        file = new File(plugin.getDataFolder(), fileName);

        rotationInts = new ArrayList<>();
        items = new ArrayList<>();

        loadConfig();
    }

    // Method to get uptime from the JSON config
    public long getUptime() {
        // Check if the "current-uptime" property exists and return its value, otherwise return 0
        return config.has("current-uptime") ? config.get("current-uptime").getAsLong() : 0;
    }

    // Method to set uptime in the JSON config
    public CompletableFuture<Void> setUptime(long uptime) {
        return CompletableFuture.runAsync(() -> {
            // Set the new uptime in the JSON config
            config.addProperty("current-uptime", uptime);

            // Save the updated config to the file
            save(config);  // Assuming this method saves the config correctly
        });
    }

    public int getItemsAmount() {
        return items.size();
    }

    public List<Integer> getRotationInts() {
        return rotationInts;
    }

    private void loadRotationInts() {
        JsonArray rotationArray = config.getAsJsonArray("rotation");

        if (rotationArray == null || rotationArray.isEmpty())
            return;

        for (JsonElement element : rotationArray) {
            try {
                int number = element.getAsInt();  // Directly get the integer from the JsonElement
                rotationInts.add(number);
            } catch (Exception e) {
                Logger.error("Error parsing int: " + e.getMessage());
            }
        }
    }

    public CompletableFuture<Void> setRotationInts(List<Integer> intList) {
        return CompletableFuture.supplyAsync(() -> {

            // Create a JsonArray to hold the rotation integers
            JsonArray rotationArray = new JsonArray();

            // Add each integer from the list into the JsonArray
            for (int i : intList) {
                rotationArray.add(i);
            }

            // Set the "rotation" property in the config to the JsonArray
            config.add("rotation", rotationArray);

            // Save the config
            save(config);

            // Update the local rotationInts list
            rotationInts.clear();
            rotationInts.addAll(intList);

            return null;
        });
    }

    public Map.Entry<ItemStack, Double> getItem(int index) {
        return items.get(index - 1);
    }

    public ArrayList<Map.Entry<ItemStack, Double>> getItems() {
        return items;
    }

    public void loadItems() {
        items.clear();  // Clear the current list of items

        // Retrieve the "items" array from the config (JSON)
        JsonArray itemsArray = config.has("items") ? config.getAsJsonArray("items") : new JsonArray();

        // Iterate over the items in the array
        for (JsonElement element : itemsArray) {
            JsonObject itemObject = element.getAsJsonObject();  // Convert element to JSON object

            if (itemObject.get("data") == null)
                Logger.log("debug: " + itemObject + " is returning null when getting 'data'");

            // Get the item and price from the JSON object
            String serializedItem = itemObject.get("data").getAsString();
            double price = itemObject.get("price").getAsDouble();

            // Deserialize the item stack
            ItemStack item = ItemSerializer.deserializeItemStack(serializedItem);

            // Add the item to the list as a map entry
            items.add(Map.entry(item, price));
        }
    }

    public CompletableFuture<Boolean> setItemPrice(int position, double price) {
        return CompletableFuture.supplyAsync(() -> {
            // Retrieve the "items" section from the config
            JsonArray itemsArray = config.has("items") ? config.getAsJsonArray("items") : new JsonArray();

            // Check if the position exists in the "items" array
            if (position < 0 || position >= itemsArray.size()) {
                return false;  // If the position is invalid, return false
            }

            // Get the item object at the specified position
            JsonObject itemData = itemsArray.get(position).getAsJsonObject();

            // Update the price of the item
            itemData.addProperty("price", price);

            // Update the "items" array with the modified data
            itemsArray.set(position, itemData);  // Update the item at the given position

            // Save the updated configuration
            config.add("items", itemsArray);

            // Save the updated configuration to the file
            return save(config);
        });
    }



    /**
     * Removes an item from data.json list and adjusts every other item's id down one
     * @param position
     * @return
     */
    public CompletableFuture<Boolean> removeAndShiftItem(int position) {
        return CompletableFuture.supplyAsync(() -> {
            // Retrieve the "items" section from the config
            JsonArray itemsArray = config.has("items") ? config.getAsJsonArray("items") : new JsonArray();

            // Check if the position exists in the "items" array
            if (position < 0 || position >= itemsArray.size()) {
                return false;  // If the position is invalid, return false
            }

            // Remove the item at the given position
            itemsArray.remove(position-1);

            items.remove(position-1);

            // Shift the remaining items down (if any)
            //for (int i = position; i < itemsArray.size(); i++) {
            //    JsonObject shiftedItem = itemsArray.get(i).getAsJsonObject();
            //    itemsArray.set(i, shiftedItem);  // Just reassigning to shift
            //}

            // Save the updated "items" array back to the config
            //config.add("items", itemsArray);

            // Save the updated configuration to the file
            return save(config);
        });
    }


    public CompletableFuture<Boolean> addItem(ItemStack item, double price) {
        int nextId = getNextIndex();

        return CompletableFuture.supplyAsync(() -> {
            // Retrieve the "items" section (array) from the config, or create a new one if it doesn't exist
            JsonArray itemsArray = config.has("items") ? config.getAsJsonArray("items") : new JsonArray();

            // Create a new JSON object to store the item data
            JsonObject itemData = new JsonObject();
            itemData.addProperty("data", ItemSerializer.serializeItemStack(item));  // Serializing the item
            itemData.addProperty("price", price);  // Adding the price

            // Add the new item data to the array
            itemsArray.add(itemData);

            // Update the "items" array in the config
            config.add("items", itemsArray);

            // Save the updated configuration to file
            boolean saveResult = save(config);

            // Optionally, add the item to a local collection for further use (if needed)
            items.add(Map.entry(item, price));  // Assuming 'items' is a List<Map.Entry<ItemStack, Double>>

            // Return the result of the save operation
            return saveResult;
        });
    }


    private int getNextIndex() {
        return getItemsAmount() + 1;
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
                Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                Logger.error("Error creating " + file.getName() + " file: " + e.getMessage());
                return;
            }
        }

        // Load JSON instead of YAML
        try (FileReader reader = new FileReader(file)) {
            config = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            Logger.error("Error loading " + file.getName() + ": " + e.getMessage());
            config = new JsonObject(); // Fallback to empty JSON
        }

        loadRotationInts();
        loadItems();

        if (isReload)
            Logger.log("&aReloaded " + file.getName() + " file.");
        else if (!fileExists)
            Logger.log("&aCreated " + file.getName() + " file.");
        else
            Logger.log("&aLoaded " + file.getName() + " file.");
    }

    private boolean save(JsonObject config) {
        if (file == null) {
            Logger.error("Failed to save unknown file because it's null.");
            return false;
        }

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(config, writer);  // Save JSON to file
            Logger.debug("Saved " + file.getName() + ".");
        } catch (Exception e) {
            Logger.error("Failed to save " + file.getName() + ": " + e.getMessage());
            return false;
        }

        this.config = config;
        Logger.debug("Updated " + file.getName() + ".");

        return true;
    }

}
