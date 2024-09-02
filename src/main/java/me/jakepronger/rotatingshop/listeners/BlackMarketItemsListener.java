package me.jakepronger.rotatingshop.listeners;

import me.jakepronger.rotatingshop.gui.BlackMarketGUI;
import me.jakepronger.rotatingshop.gui.BlackMarketItemsGUI;

import me.jakepronger.rotatingshop.utils.Utils;

import org.bukkit.Bukkit;
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

public class BlackMarketItemsListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        // if clicked inventory is null, player's inventory doesn't match
        if (e.getClickedInventory() == null
                || e.getClickedInventory() == e.getWhoClicked().getInventory())
            return;

        Player p = (Player) e.getWhoClicked();

        // if open inventory doesn't match
        if (!BlackMarketItemsGUI.openInventories.containsKey(p)) {
            return;
        }

        e.setCancelled(true);

        // check if item has open flag
        ItemStack item = e.getClickedInventory().getItem(e.getRawSlot());
        if (item == null)
            return;

        PersistentDataContainer pData = item.getItemMeta().getPersistentDataContainer();

        NamespacedKey priceKey = new NamespacedKey(plugin, "price");
        NamespacedKey indexKey = new NamespacedKey(plugin, "index");

        Double price;
        if (pData.has(priceKey)) {
            price = pData.get(priceKey, PersistentDataType.DOUBLE);
            Bukkit.broadcastMessage("got price: " + price);
        } else {
            price = null;
        }

        Integer index;
        if (pData.has(indexKey)) {
            index = pData.get(indexKey, PersistentDataType.INTEGER);
            Bukkit.broadcastMessage("got index: " + index);
        } else {
            index = null;
        }

        if (price != null && index != null) {

            p.sendMessage(Utils.format("&cPrice: &a" + price));
            p.closeInventory();
            p.sendMessage(Utils.format("&aPurchased!"));

            plugin.getDataUtils().removeItem(index).whenComplete((value, throwable) -> {
               p.sendMessage(Utils.format("&aremoved item at index " + index));
            });

            return;
        }

        String openValue = pData.get(new NamespacedKey(plugin, "open"), PersistentDataType.STRING);
        if (openValue != null) {
            if (openValue.equalsIgnoreCase("shop.gui")) {
                BlackMarketGUI.open(p);
            } else if (openValue.equalsIgnoreCase("next")) {
                // todo
            } else if (openValue.equalsIgnoreCase("back")) {
                // todo
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {

        Player p = (Player) e.getPlayer();

        if (BlackMarketItemsGUI.openInventories.containsKey(p)
            && BlackMarketItemsGUI.openInventories.get(p) == e.getInventory())
            BlackMarketItemsGUI.openInventories.remove(p);
    }

}
