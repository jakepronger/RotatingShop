package me.jakepronger.rotatingshop.gui;

import me.jakepronger.rotatingshop.utils.ItemUtils;
import me.jakepronger.rotatingshop.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BlackMarketItemsGUI {

    private static final String backArrowName = Utils.getColorFromHex("#FF0000") + "ʙᴀᴄᴋ";
    private static final String nextArrowName = Utils.getColorFromHex("#08FB2C") + "ɴᴇxᴛ";

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 9*6, "ꜱʜᴏᴘ ɪᴛᴇᴍꜱ");

        ItemStack blackStainedGlass = ItemUtils.getItem(Material.BLACK_STAINED_GLASS_PANE, " ");

        ItemStack backArrow = ItemUtils.getItem(Material.ARROW, backArrowName);
        ItemStack nextArrow = ItemUtils.getItem(Material.ARROW, nextArrowName);

        inv.setItem(45, backArrow);
        inv.setItem(53, nextArrow);

        inv.setItem(46, blackStainedGlass);
        inv.setItem(47, blackStainedGlass);
        inv.setItem(48, blackStainedGlass);
        inv.setItem(49, blackStainedGlass);
        inv.setItem(50, blackStainedGlass);
        inv.setItem(51, blackStainedGlass);
        inv.setItem(52, blackStainedGlass);

        p.openInventory(inv);
    }

}
