package me.jakepronger.rotatingshop.managers;

import me.jakepronger.rotatingshop.RotatingShop;
import me.jakepronger.rotatingshop.gui.BlackMarketGUI;
import me.jakepronger.rotatingshop.gui.BlackMarketItemsGUI;
import me.jakepronger.rotatingshop.utils.ItemUtils;
import me.jakepronger.rotatingshop.utils.Logger;
import me.jakepronger.rotatingshop.utils.Utils;

import org.black_ixx.playerpoints.PlayerPointsAPI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InventoryManager implements Listener {

    public enum ShopView {
        Menu,
        Editor
    }

    private final RotatingShop plugin;
    private final TimerManager timer;

    private final HashMap<UUID, ShopView> openInventories;

    private final BlackMarketGUI bmGUI;
    private final BlackMarketItemsGUI bmItemsGUI;

    public InventoryManager(RotatingShop instance) {
        openInventories = new HashMap<>();
        bmGUI = new BlackMarketGUI(this);
        bmItemsGUI = new BlackMarketItemsGUI(this);
        plugin = instance; // TODO: change to metadata manager
        timer = plugin.getTimerManager();
    }

    /**
     * Create GUI methods
     * Ref blank text: " "
     */

    public Inventory create(String title, int rows) {
        if (title == null) {
            return Bukkit.createInventory(null, 9 * rows);
        } else if (title.equalsIgnoreCase("")) {
            return Bukkit.createInventory(null, 9 * rows, " ");
        } else {
            return Bukkit.createInventory(null, 9*rows, title);
        }
    }

    /**
     * Create two material background cross checkered inventory
     */
    public Inventory create(String title, int rows, Material type1, Material type2, String type1Name, String type2Name) {
        Inventory inv = create(title, rows);

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

    /**
     * Load inventory methods
     */

    public Inventory load(String configSection, Player p) {

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

        Integer viewingPage = bmItemsGUI.getPlayerViewingPage(p);
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

            List<Integer> slots = Utils.getIntsSeparatedByCommas(key);
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

    public ItemStack loadItem(ConfigurationSection cs, Player p) {

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
                            .replace("%time-left%", String.valueOf(timer.getMinutesLeft())));
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
            loreList.replaceAll(lore -> lore.replace("%time-left%", String.valueOf(timer.getMinutesLeft())));
            meta.setLore(loreList);
        }

        String openInventory = cs.getString("open");
        if (openInventory != null) {
            pData.set(new NamespacedKey(plugin, "open"), PersistentDataType.STRING, openInventory);
        }

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Player inventory methods
     */

    public int closeAll() {

        int openInvSize = openInventories.size();

        // close all open BlackMarketGUI inventories
        for (Map.Entry<UUID, ShopView> p : openInventories.entrySet()) {
            Player player = Bukkit.getPlayer(p.getKey());
            if (player != null) {
                player.closeInventory();
            } else {
                openInventories.remove(p.getKey());
            }
        }

        return openInvSize;
    }

    /**
     * Inventory getter methods
     */

    public BlackMarketGUI getBlackMarketGUI() {
        return bmGUI;
    }

    public BlackMarketItemsGUI getBlackMarketItemsGUI() {
        return bmItemsGUI;
    }

    /**
     * HashMap methods for tracking open inventories
     */

    public boolean hasPlayer(Player p) {
        return openInventories.containsKey(p.getUniqueId());
    }

    public void addPlayer(Player p, ShopView shopView) {
        openInventories.put(p.getUniqueId(), shopView);
    }

    public void removePlayer(Player p) {
        openInventories.remove(p.getUniqueId());
    }

    @Nullable
    public ShopView getShopView(Player p) {
        return openInventories.getOrDefault(p.getUniqueId(), null);
    }

    /**
     * Event Listeners
     * Remove player from openInventories if inventory closed.
     */

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {

        Player p = (Player) e.getPlayer();

        Bukkit.broadcastMessage("InventoryCloseEvent e.getPlayer().getKiller() not null");

        if (hasPlayer(p)) {

            ShopView shopView = getShopView(p);

            if (shopView == ShopView.Editor) {
                // TODO: should be managed in a metadata manager
                NamespacedKey key = new NamespacedKey(plugin, "page");

                PersistentDataContainer dataContainer = p.getPersistentDataContainer();
                if (dataContainer.has(key)) {
                    dataContainer.remove(key);
                }
            }

            Bukkit.broadcastMessage("InventoryCloseEvent list has player");
            removePlayer(p);
            Bukkit.broadcastMessage("InventoryCloseEvent removed player from list");
        } else {
            Bukkit.broadcastMessage("InventoryCloseEvent list doesn't have player");
        }
    }

}
