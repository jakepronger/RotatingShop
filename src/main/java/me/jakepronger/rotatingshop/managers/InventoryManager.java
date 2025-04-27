package me.jakepronger.rotatingshop.managers;

import me.jakepronger.rotatingshop.RotatingShop;
import me.jakepronger.rotatingshop.gui.BlackMarketGUI;
import me.jakepronger.rotatingshop.gui.BlackMarketItemsGUI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.persistence.PersistentDataContainer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class InventoryManager implements Listener {

    public enum InventoryType {
        Menu,
        Editor
    }

    private final RotatingShop plugin;

    private final HashMap<UUID, InventoryType> openInventories;

    private final BlackMarketGUI bmGUI;
    private final BlackMarketItemsGUI bmItemsGUI;

    public InventoryManager(RotatingShop instance) {
        openInventories = new HashMap<>();
        bmGUI = new BlackMarketGUI(this);
        bmItemsGUI = new BlackMarketItemsGUI(this);
        plugin = instance; // TODO: change to metadata manager
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
     * HashMap methods
     */

    public boolean hasPlayer(Player p) {
        return openInventories.containsKey(p.getUniqueId());
    }

    public void addPlayer(Player p, InventoryType inventoryType) {
        openInventories.put(p.getUniqueId(), inventoryType);
    }

    public void removePlayer(Player p) {
        openInventories.remove(p.getUniqueId());
    }

    @Nullable
    public InventoryType getInventoryType(Player p) {
        return openInventories.getOrDefault(p.getUniqueId(), null);
    }

    /**
     * Event Listeners
     */

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {

        Bukkit.broadcastMessage("InventoryCloseEvent Debug 1.");

        Player p = e.getPlayer().getKiller();
        if (p == null) {
            Bukkit.broadcastMessage("InventoryCloseEvent e.getPlayer().getKiller() is null");
            return;
        }

        Bukkit.broadcastMessage("InventoryCloseEvent e.getPlayer().getKiller() not null");

        if (hasPlayer(p)) {

            InventoryType invType = getInventoryType(p);

            if (invType == InventoryType.Editor) {
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
