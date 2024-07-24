package me.jakepronger.rotatingshop.listeners;

import me.jakepronger.rotatingshop.gui.BlackMarketGUI;
import me.jakepronger.rotatingshop.gui.BlackMarketItemsGUI;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class BlackMarketListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        // if clicked inventory is null, player's inventory or the inventory title doesn't match
        if (!BlackMarketGUI.openInventories.containsKey((Player)e.getWhoClicked())) {
            return;
        }

        Player p = (Player) e.getWhoClicked();

        e.setCancelled(true);

        if (e.getRawSlot() == 18) {
            // todo: this will trigger it to call the other close event?
            BlackMarketItemsGUI.open(p);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        BlackMarketGUI.openInventories.remove((Player)e.getPlayer());
    }

}
