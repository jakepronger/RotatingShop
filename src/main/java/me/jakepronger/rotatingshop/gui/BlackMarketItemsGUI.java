package me.jakepronger.rotatingshop.gui;

import me.jakepronger.rotatingshop.utils.InvUtils;
import me.jakepronger.rotatingshop.utils.ItemUtils;
import me.jakepronger.rotatingshop.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class BlackMarketItemsGUI {

    private static final String backArrowName = Utils.format("&#FF0000ʙᴀᴄᴋ");
    private static final String nextArrowName = Utils.format("&#08FB2Cɴᴇxᴛ");

    public static HashMap<Player, Inventory> openInventories = new HashMap<>();

    public static void open(Player p) {

        Inventory inv; /*= Bukkit.createInventory(null, 9*6, "ꜱʜᴏᴘ ɪᴛᴇᴍꜱ");*/
        inv = InvUtils.loadInventory("items-gui", p);

        ItemStack blackStainedGlass = ItemUtils.getItem(Material.BLACK_STAINED_GLASS_PANE, " ");

        ItemStack backArrow = ItemUtils.getItem(Material.ARROW, backArrowName);
        ItemStack nextArrow = ItemUtils.getItem(Material.ARROW, nextArrowName);

        plugin.dataFile.getItems().whenComplete((items, throwable) -> {

            for (int i = 0; true; i++) {

                if (i >= items.size()) {
                    break;
                }

                Map.Entry<ItemStack, Double> entry = items.get(i);

                double price = entry.getValue();

                ItemStack item = entry.getKey();
                ItemMeta meta = item.getItemMeta();

                meta.setDisplayName(Utils.format("&aprice: " + price));

                item.setItemMeta(meta);

                inv.setItem(i, item);
            }

            inv.setItem(45, backArrow);
            inv.setItem(53, nextArrow);

            inv.setItem(46, blackStainedGlass);
            inv.setItem(47, blackStainedGlass);
            inv.setItem(48, blackStainedGlass);
            inv.setItem(49, blackStainedGlass);
            inv.setItem(50, blackStainedGlass);
            inv.setItem(51, blackStainedGlass);
            inv.setItem(52, blackStainedGlass);

            Bukkit.getScheduler().runTask(plugin, () -> {
                openInventories.put(p, inv);
                p.openInventory(inv);
            });
        });

    }

}
