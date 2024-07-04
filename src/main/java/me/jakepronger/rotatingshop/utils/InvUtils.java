package me.jakepronger.rotatingshop.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InvUtils {

    /**
     * Creates a bukkit inventory
     * Ref blank text: " "
     */
    public static Inventory getInventory(String title, int rows) {
        return Bukkit.createInventory(null, 9*rows, title);
    }

    /**
     * Create two material cross patterned inventory
     * Ref blank text: " "
     */
    public static Inventory getInventory(String title, int rows, Material type1, Material type2, String type1Name, String type2Name) {
        Inventory inv = getInventory(title, rows);

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

}
