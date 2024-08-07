package me.jakepronger.rotatingshop.gui;

import me.jakepronger.rotatingshop.utils.InvUtils;

import me.jakepronger.rotatingshop.utils.Logger;
import me.jakepronger.rotatingshop.utils.Utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class BlackMarketGUI {

    public static HashMap<Player, Inventory> openInventories = new HashMap<>();

    public static void open(Player p) {

        Inventory inv = InvUtils.loadInventory("shop.gui", p);

        if (inv == null) {
            p.sendMessage(Utils.format("&cFailed to load inventory!"));
            Logger.error("");
            return;
        }

        openInventories.put(p, inv);
        p.openInventory(inv);
    }

    public static Inventory getInventory() {
        return null;
    }

}
