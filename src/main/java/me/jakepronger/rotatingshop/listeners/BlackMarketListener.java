package me.jakepronger.rotatingshop.listeners;

import me.jakepronger.rotatingshop.RotatingShop;
import me.jakepronger.rotatingshop.gui.BlackMarketItemsGUI;

import me.jakepronger.rotatingshop.managers.InventoryManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BlackMarketListener implements Listener {

    private RotatingShop plugin;

    private InventoryManager invManager;
    private BlackMarketItemsGUI bmItemsGUI;

    public BlackMarketListener(RotatingShop plugin) {
        this.invManager = plugin.getInventoryManager();
        this.bmItemsGUI = plugin.getBlackMarketItemsGUI();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        // if clicked inventory is null, player's inventory doesn't match
        if (e.getClickedInventory() == null
                || e.getClickedInventory() == e.getWhoClicked().getInventory())
            return;

        Player p = (Player) e.getWhoClicked();

        // if open inventory doesn't match
        if (invManager.getInventoryType(p) != InventoryManager.InventoryType.Menu) {
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
                && openValue.equalsIgnoreCase("editor.gui")) {
            bmItemsGUI.open(p, 1);
        }
    }

}
