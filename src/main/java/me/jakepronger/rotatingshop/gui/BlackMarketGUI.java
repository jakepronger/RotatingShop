package me.jakepronger.rotatingshop.gui;

import me.jakepronger.rotatingshop.managers.InventoryManager;
import me.jakepronger.rotatingshop.utils.InvUtils;

import me.jakepronger.rotatingshop.utils.Utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BlackMarketGUI {

    private final InventoryManager invManager;

    public BlackMarketGUI(InventoryManager invManager) {
        this.invManager = invManager;
    }

    public void open(Player p) {

        Inventory inv = InvUtils.loadInventory("shop.gui", p);

        if (inv == null) {
            p.sendMessage(Utils.format("&cFailed to load inventory!"));
            return;
        }

        invManager.addPlayer(p, InventoryManager.InventoryType.Menu);
        p.openInventory(inv);
    }

    public Inventory getInventory() {
        return null;
    }

}
