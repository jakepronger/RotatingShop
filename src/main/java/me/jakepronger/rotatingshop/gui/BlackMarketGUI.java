package me.jakepronger.rotatingshop.gui;

import me.jakepronger.rotatingshop.utils.Utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class BlackMarketGUI {

    private static final String adminPanelName = Utils.format("&#FF005Dᴀ&#F9006Dᴅ&#F2007Dᴍ&#EC008Eɪ&#E5009Eɴ &#D800BEᴘ&#D200CEᴀ&#CB00DFɴ&#C500EFᴇ&#BE00FFʟ");
    private static final String itemList = Utils.format("&#FF005Dɪ&#F8006Fᴛ&#F10081ᴇ&#E90093ᴍ&#E200A5ꜱ &#D400C9ʟ&#CC00DBɪ&#C500EDꜱ&#BE00FFᴛ");
    private static final String shardBalance = Utils.format("&#A900FFꜱ&#9C0DFFʜ&#8F1AFFᴀ&#8228FFʀ&#7535FFᴅ&#6842FFꜱ &#4E5DFFʙ&#416AFFᴀ&#3477FFʟ&#2784FFᴀ&#1A92FFɴ&#0D9FFFᴄ&#00ACFFᴇ");
    private static final String timeLimit = Utils.format("&#FF0000ᴛɪᴍᴇ ʟᴇꜰᴛ");

    public static HashMap<Player, Inventory> openInventories = new HashMap<>();

    public static void open(Player p) {

        Inventory inv = Utils.loadInventory("shop.gui", p);

        //ItemStack writeableBook = ItemUtils.getItem(Material.WRITABLE_BOOK, timeLimit);

        //ItemStack adminPanel = ItemUtils.getItem(Material.BARRIER, adminPanelName);
        //ItemStack itemsList = ItemUtils.getItem(Material.BARRIER, itemList);

        //ItemStack shards = ItemUtils.getItem(Material.PRISMARINE_SHARD, shardBalance);

        //if (p.isOp()) {
        //    inv.setItem(18, adminPanel);
        //} else inv.setItem(18, itemsList);

        //inv.setItem(22, shards);

        //inv.setItem(26, writeableBook);

        openInventories.put(p, inv);
        p.openInventory(inv);
    }

    public static Inventory getInventory() {
        return null;
    }

}
