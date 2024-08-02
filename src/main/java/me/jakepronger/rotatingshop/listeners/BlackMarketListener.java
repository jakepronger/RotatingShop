package me.jakepronger.rotatingshop.listeners;

import me.jakepronger.rotatingshop.gui.BlackMarketGUI;
import me.jakepronger.rotatingshop.gui.BlackMarketItemsGUI;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class BlackMarketListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        // if clicked inventory is null, player's inventory doesn't match
        if (e.getClickedInventory() == null
                || e.getClickedInventory() == e.getWhoClicked().getInventory())
            return;

        Player p = (Player) e.getWhoClicked();

        // if open inventory doesn't match
        if (!BlackMarketGUI.openInventories.containsKey(p)) {
            return;
        }

        e.setCancelled(true);

        // check if item has open flag
        ItemStack item = e.getClickedInventory().getItem(e.getRawSlot());

        if (item == null)
            return;

        PersistentDataContainer pData = item.getItemMeta().getPersistentDataContainer();
        String openValue = pData.get(new NamespacedKey(plugin, "open"), PersistentDataType.STRING);
        if (openValue != null
                && openValue.equalsIgnoreCase("items-gui")) {
            BlackMarketItemsGUI.open(p);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {

        Player p = (Player) e.getPlayer();

        if (BlackMarketGUI.openInventories.containsKey(p)
            && BlackMarketGUI.openInventories.get(p) == e.getInventory())
            BlackMarketGUI.openInventories.remove(p);
    }

}
