package me.jakepronger.rotatingshop.gui;

import me.jakepronger.rotatingshop.managers.InventoryManager;

import me.jakepronger.rotatingshop.utils.Logger;
import me.jakepronger.rotatingshop.utils.Utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BlackMarketGUI {

    private final InventoryManager invManager;

    public BlackMarketGUI(InventoryManager invManager) {
        this.invManager = invManager;
    }

    public void open(Player p) {

        Inventory inv = invManager.load("shop.gui", p);

        if (inv == null) {
            p.sendMessage(Utils.format("&cFailed to load inventory!"));
            return;
        }

        p.openInventory(inv);

        invManager.addPlayer(p, InventoryManager.ShopView.Menu);
        Logger.log("ADDED PLAYER TO MENU");
    }

    public Inventory getInventory() {
        return null;
    }

}
