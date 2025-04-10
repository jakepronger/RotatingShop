package me.jakepronger.rotatingshop.utils;

import me.jakepronger.rotatingshop.gui.BlackMarketGUI;
import me.jakepronger.rotatingshop.gui.BlackMarketItemsGUI;

import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class InvUtils {

    /**
     * Creates a bukkit inventory
     * Ref blank text: " "
     */
    public static Inventory getInventory(String title, int rows) {
        return Bukkit.createInventory(null, 9*rows, title);
    }

    /**
     * Create two material cross patterned inventory
     * Ref blank text: " "
     */
    public static Inventory getInventory(String title, int rows, Material type1, Material type2, String type1Name, String type2Name) {
        Inventory inv = getInventory(title, rows);

        ItemStack item1 = ItemUtils.getItem(type1, type1Name);
        ItemStack item2 = ItemUtils.getItem(type2, type2Name);

        boolean useFirstItem = true;

        for (int i = 0; i < 9*rows; i++) {
            if (useFirstItem)
                inv.setItem(i, item1);
            else
                inv.setItem(i, item2);

            useFirstItem = !useFirstItem;
        }

        return inv;
    }

    public static ItemStack loadItem(ConfigurationSection cs, Player p) {

        ItemStack item;
        ItemMeta meta;
        PersistentDataContainer pData;

        Material type;
        try {
            type = Material.valueOf(cs.getString("type"));
        } catch (Exception e) {
            return null;
        }

        item = new ItemStack(type);
        meta = item.getItemMeta();
        pData = meta.getPersistentDataContainer();

        PlayerPointsAPI ppApi = plugin.getPlayerPointsHook().get();

        String itemName = cs.getString(".name");
        if (itemName != null) {
            String displayName = Utils.format("&f" + itemName);
            displayName
                    .replace("%balance%", String.valueOf(ppApi.look(p.getUniqueId())))
                    .replace("%formatted-balance%", ppApi.lookFormatted(p.getUniqueId())
                    .replace("%time-left%", String.valueOf(plugin.getTimerUtils().getMinutesLeft())));
            meta.setDisplayName(displayName);
        }

        boolean glowing = cs.getBoolean("glowing", false);
        if (glowing) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        List<String> loreList = cs.getStringList("lore");
        if (!loreList.isEmpty()) {
            loreList.replaceAll(lore -> Utils.format("&f" + lore));
            loreList.replaceAll(lore -> lore.replace("%balance%", String.valueOf(ppApi.look(p.getUniqueId()))));
            loreList.replaceAll(lore -> lore.replace("%formatted-balance%", ppApi.lookFormatted(p.getUniqueId())));
            loreList.replaceAll(lore -> lore.replace("%time-left%", String.valueOf(plugin.getTimerUtils().getMinutesLeft())));
            meta.setLore(loreList);
        }

        String openInventory = cs.getString("open");
        if (openInventory != null) {
            pData.set(new NamespacedKey(plugin, "open"), PersistentDataType.STRING, openInventory);
        }

        item.setItemMeta(meta);

        return item;
    }

    public static int closeInventories() {

        int bmGuiSize = BlackMarketGUI.openInventories.size();

        // close all open BlackMarketGUI inventories
        for (Map.Entry<Player, Inventory> p : BlackMarketGUI.openInventories.entrySet()) {
            p.getKey().closeInventory();
        }

        int bmItemsGuiSize = BlackMarketItemsGUI.openInventories.size();

        // close all open BlackMarketItemsGUI inventories
        for (Map.Entry<Player, Inventory> p : BlackMarketItemsGUI.openInventories.entrySet()) {
            p.getKey().closeInventory();
        }

        return bmGuiSize + bmItemsGuiSize;
    }

    public static List<Integer> getIntsSeparatedByCommas(String text) {

        List<Integer> list = new ArrayList<>();

        for (String value : text.split(",")) {
            try {
                list.add(Integer.parseInt(value));
            } catch (Exception e) {
                return null;
            }
        }

        if (list.isEmpty())
            return null;

        return list;
    }

    public static Inventory loadInventory(String configSection, Player p) {

        // Player p: for permission checks
        // path: shop.gui

        // todo: don't do this use already loaded config
        FileConfiguration config = plugin.getConfig();

        ConfigurationSection cs = config.getConfigurationSection(configSection);
        if (cs == null) {
            Logger.error("Could not find config section: " + configSection);
            return null;
        }

        // inventory properties

        int pageNumber = 1;

        Integer viewingPage = BlackMarketItemsGUI.getPlayerViewingPage(p);
        if (viewingPage != null)
            pageNumber = viewingPage;

        int rows = cs.getInt("rows", 3);
        String name = cs.getString("name", "")
                .replace("%page%", String.valueOf(pageNumber));

        Inventory inv;
        try {
            inv = Bukkit.createInventory(null, 9*rows, name);
        } catch (IllegalArgumentException e) {
            Logger.error("Failed to create inventory \"" + name + "\": " + e.getMessage());
            return null;
        }

        // inventory slots

        for (String key : cs.getKeys(false)) {

            ConfigurationSection itemSection = cs.getConfigurationSection(key);
            if (itemSection == null) {
                continue;
            }

            List<Integer> slots = getIntsSeparatedByCommas(key);
            if (slots == null) {
                continue;
            }

            ItemStack item = null;

            // check permission items
            // loop permission items
            for (int permId = 1; ; permId++) {

                ConfigurationSection permSection = itemSection.getConfigurationSection("permission_" + permId);

                // Add an exit condition within the loop if needed
                if (permSection == null) {
                    break; // Exit the loop when permId reaches MAX_VALUE
                }

                String perm = permSection.getString("node");
                if (perm == null
                        || !p.hasPermission(perm))
                    continue;

                item = loadItem(permSection, p);
                if (item != null) {
                    break;
                }
            }

            // do default item if perm item wasn't found
            if (item == null) {
                item = loadItem(itemSection, p);
                if (item == null) {
                    continue;
                }
            }

            // set items in inventory
            for (int slot : slots) {
                inv.setItem(slot, item);
            }
        }

        return inv;
    }

}
