package me.jakepronger.rotatingshop.gui;

import me.jakepronger.rotatingshop.config.DataUtils;
import me.jakepronger.rotatingshop.utils.InvUtils;
import me.jakepronger.rotatingshop.utils.Utils;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class BlackMarketItemsGUI {

    public static HashMap<Player, Inventory> openInventories = new HashMap<>();

    public static void open(Player p) {
        open(p, 1);
    }

    public static void open(Player p, int page) {

        DataUtils data = plugin.getDataUtils();

        Inventory inv = InvUtils.loadInventory("editor.gui", p);

        if (inv == null) {
            p.sendMessage(Utils.format("&cFailed to load inventory!"));
            return;
        }

        List<Integer> editorSlots = plugin.getConfigUtils().getEditorItemSlots();
        int editorSlotsAmount = editorSlots.size();

        // todo: verify max page number
        if (page < 1) {
            page = 1;
        }

        double maxValue = (double)data.getItemsAmount() / (double)editorSlotsAmount;
        if (maxValue % 1 != 0) {
            maxValue++;
        }

        int maxPage = (int)maxValue;

        if (page > maxPage && maxPage != 0)
            page = maxPage;

        Bukkit.broadcastMessage("opened editor at page: " + page);

        int firstSlotIndex = editorSlotsAmount*(page-1) + 1;
        Bukkit.broadcastMessage("editorSlotsAmount: " + editorSlotsAmount);
        Bukkit.broadcastMessage("firstSlotIndex: " + firstSlotIndex);

        NamespacedKey key = new NamespacedKey(plugin, "page");

        PersistentDataContainer dataContainer = p.getPersistentDataContainer();
        dataContainer.set(key, PersistentDataType.INTEGER, page);

        ArrayList<Map.Entry<ItemStack, Double>> items = data.getItems();

        int loopIndex = firstSlotIndex;

        for (int editorSlot : editorSlots) {

            if (loopIndex > items.size()) {
                break;
            }

            Map.Entry<ItemStack, Double> entry = items.get(loopIndex - 1);
            if (entry == null) {
                break;
            }

            // item formatting
            double price = entry.getValue();

            ItemStack item = entry.getKey();
            ItemMeta meta = item.getItemMeta();

            PersistentDataContainer pData = meta.getPersistentDataContainer();
            pData.set(new NamespacedKey(plugin, "price"), PersistentDataType.DOUBLE, price);
            pData.set(new NamespacedKey(plugin, "index"), PersistentDataType.INTEGER, loopIndex);

            Component pageComponent = Utils.stringToComponent(Utils.format("&7Page: &a" + page));
            Component priceComponent = Utils.stringToComponent(Utils.format("&7Price: &a" + price));
            Component indexComponent = Utils.stringToComponent(Utils.format("&7Index: &a" + loopIndex));

            List<Component> lore = new ArrayList<>();
            lore.add(pageComponent);
            lore.add(priceComponent);
            lore.add(indexComponent);

            meta.lore(lore);

            item.setItemMeta(meta);

            inv.setItem(editorSlot, item);

            loopIndex++;
        }

        openInventories.put(p, inv);
        p.openInventory(inv);
    }

}
