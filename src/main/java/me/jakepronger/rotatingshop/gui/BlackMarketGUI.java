package me.jakepronger.rotatingshop.gui;

import me.jakepronger.rotatingshop.utils.InvUtils;
import me.jakepronger.rotatingshop.utils.ItemUtils;
import me.jakepronger.rotatingshop.utils.Utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BlackMarketGUI {

    private static final String adminPanelName = Utils.getColorFromHex("#FF005D") + "ᴀ" + Utils.getColorFromHex("#F9006D") + "ᴅ" + Utils.getColorFromHex("#F2007D") + "ᴍ" + Utils.getColorFromHex("#EC008E") + "ɪ" + Utils.getColorFromHex("#E5009E") + "ɴ " + Utils.getColorFromHex("#D800BE") + "ᴘ" + Utils.getColorFromHex("#D200CE") + "ᴀ" + Utils.getColorFromHex("#CB00DF") + "ɴ" + Utils.getColorFromHex("#C500EF") + "ᴇ" + Utils.getColorFromHex("#BE00FF") + "ʟ";
    private static final String itemList = Utils.getColorFromHex("#FF005D") + "ɪ" + Utils.getColorFromHex("#F8006F") + "ᴛ" + Utils.getColorFromHex("#F10081") + "ᴇ" + Utils.getColorFromHex("#E90093") + "ᴍ" + Utils.getColorFromHex("#E200A5") + "ꜱ " + Utils.getColorFromHex("#D400C9") + "ʟ" + Utils.getColorFromHex("#CC00DB") + "ɪ" + Utils.getColorFromHex("#C500ED") + "ꜱ" + Utils.getColorFromHex("#BE00FF") + "ᴛ";
    private static final String shardBalance = Utils.getColorFromHex("#A900FF") + "ꜱ" + Utils.getColorFromHex("#9C0DFF") + "ʜ" + Utils.getColorFromHex("#8F1AFF") + "ᴀ" + Utils.getColorFromHex("#8228FF") + "ʀ" + Utils.getColorFromHex("#7535FF") + "ᴅ" + Utils.getColorFromHex("#6842FF") + "ꜱ " + Utils.getColorFromHex("#4E5DFF") + "ʙ" + Utils.getColorFromHex("#416AFF") + "ᴀ" + Utils.getColorFromHex("#3477FF") + "ʟ" + Utils.getColorFromHex("#2784FF") + "ᴀ" + Utils.getColorFromHex("#1A92FF") + "ɴ" + Utils.getColorFromHex("#0D9FFF") + "ᴄ" + Utils.getColorFromHex("#00ACFF") + "ᴇ";
    private static final String timeLimit = Utils.getColorFromHex("#FF0000") + "ᴛɪᴍᴇ ʟᴇꜰᴛ";

    public static HashMap<Player, Inventory> openInventories = new HashMap<>();

    public static void open(Player p) {

        //Inventory inv = InvUtils.getInventory("ʀᴏᴛᴀᴛɪɴɢ ꜱʜᴏᴘ", 3, Material.BLACK_STAINED_GLASS_PANE, Material.RED_STAINED_GLASS_PANE, " ", " ");

        Inventory inv = Utils.loadInventory("shop.gui", p);

        ItemStack writeableBook = ItemUtils.getItem(Material.WRITABLE_BOOK, timeLimit);

        ItemStack adminPanel = ItemUtils.getItem(Material.BARRIER, adminPanelName);
        ItemStack itemsList = ItemUtils.getItem(Material.BARRIER, itemList);

        ItemStack shards = ItemUtils.getItem(Material.PRISMARINE_SHARD, shardBalance);

        if (p.isOp()) {
            inv.setItem(18, adminPanel);
        } else inv.setItem(18, itemsList);

        inv.setItem(22, shards);

        inv.setItem(26, writeableBook);

        openInventories.put(p, inv);
        p.openInventory(inv);
    }

    public static Inventory getInventory() {
        return null;
    }

}
