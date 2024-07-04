package me.jakepronger.rotatingshop.listeners;

import me.jakepronger.rotatingshop.gui.BlackMarketItemsGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BlackMarketListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        //PlayerPointsAPI api = plugin.getPlayerPoints();

        // if clicked inventory is null, player's inventory or the inventory title doesn't match
        if (e.getClickedInventory() == null
                || e.getClickedInventory() == e.getWhoClicked().getInventory()
                || !e.getView().getTitle().startsWith("ʀᴏᴛᴀᴛɪɴɢ ꜱʜᴏᴘ")) {
            return;
        }

        Player p = (Player) e.getWhoClicked();

        e.setCancelled(true);

        if (e.getRawSlot() == 18) {
            BlackMarketItemsGUI.open(p);
        }
    }

}
