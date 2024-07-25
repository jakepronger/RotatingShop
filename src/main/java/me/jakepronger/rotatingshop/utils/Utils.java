package me.jakepronger.rotatingshop.utils;

import me.jakepronger.rotatingshop.gui.BlackMarketGUI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class Utils {

    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Inventory loadInventory(String configSection, Player p) {

        // Player p: for permission checks
        // path: shop.gui

        FileConfiguration config = plugin.getConfig();

        ConfigurationSection cs = config.getConfigurationSection(configSection);
        if (cs == null) {
            Logger.error("Could not find config section: " + configSection);
            return null;
        }

        int rows = cs.getInt("rows", 3);
        String name = cs.getString("name", "Rotating Shop");

        Inventory inv;
        try {
            inv = Bukkit.createInventory(null, 9*rows, name);
        } catch (IllegalArgumentException e) {
            Logger.error("Failed to create inventory: " + name);
            return null;
        }

        for (String key : cs.getKeys(false)) {

            ItemStack item;
            ItemMeta meta;

            Material type;
            try {
                type = Material.valueOf(key);
            } catch (Exception e) {
                continue;
            }

            item = new ItemStack(type);
            meta = item.getItemMeta();

            String itemName = cs.getString(key + ".name", "");
            meta.setDisplayName(Utils.format(itemName));

            boolean glowing = cs.getBoolean(key + ".glowing", false);
            if (glowing) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            item.setItemMeta(meta);

            // todo: support perms

            String[] slots = cs.getString(key + ".slots", "").split(",");
            for (String slot : slots) {

                int slotID;
                try {
                    slotID = Integer.parseInt(slot);
                } catch (Exception e) {
                    continue;
                }

                inv.setItem(slotID, item);
            }

        }

        return inv;
    }

    public static void reload() {
        closeInventories();
        plugin.loadConfig();
        plugin.loadPerms();
    }

    public static int closeInventories() {

        int size = BlackMarketGUI.openInventories.size();

        // close all open inventories
        for (Map.Entry<Player, Inventory> player : BlackMarketGUI.openInventories.entrySet()) {
            player.getKey().closeInventory();
        }

        BlackMarketGUI.openInventories.clear();

        return size;
    }

    public static net.md_5.bungee.api.ChatColor getColorFromHex(String hex) {
        return net.md_5.bungee.api.ChatColor.of(hex.toLowerCase());
    }

    /*
    @Deprecated
    public static String formatHexColors(String text) {
        StringBuilder formattedText = new StringBuilder();
        int index = 0;

        while (index < text.length()) {

            if (text.charAt(index) == '&' && text.charAt(index + 1) == '#') {
                String hexColor = text.substring(index + 1, index + 8);
                if (isHexColor(hexColor)) {
                    formattedText.append(getColorFromHex(hexColor));
                    index += 8; // Skip past the hex color code
                    continue;
                }
            }

            formattedText.append(text.charAt(index));
            index++;
        }

        return format(formattedText.toString());
    }*/

}
