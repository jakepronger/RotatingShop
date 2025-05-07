package me.jakepronger.rotatingshop.gui;

import me.jakepronger.rotatingshop.RotatingShop;
import me.jakepronger.rotatingshop.managers.InventoryManager;
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

public class BlackMarketItemsGUI {

    private final RotatingShop plugin;

    private final InventoryManager invManager;

    public BlackMarketItemsGUI(InventoryManager invManager) {
        this.plugin = RotatingShop.getInstance();
        this.invManager = invManager;
    }

    public void open(Player p, int page) {

        //DataUtils data = plugin.getDataUtils();

        setPage(p, page);

        Inventory inv = InvUtils.loadInventory("editor.gui", p);

        if (inv == null) {
            p.sendMessage(Utils.format("&cFailed to load inventory!"));
            return;
        }

        //List<Integer> editorSlots = ;
        //int editorSlotsAmount = editorSlots.size();

        // todo: verify max page number
        int maxPage = getMaxPage();
        page = verifyPage(page, maxPage);

        Bukkit.broadcastMessage("opened editor at page: " + page);

        int firstSlotIndex = getFirstSlotIndex(page);
        //Bukkit.broadcastMessage("editorSlotsAmount: " + editorSlotsAmount);
        //Bukkit.broadcastMessage("firstSlotIndex: " + firstSlotIndex);

        ArrayList<Map.Entry<ItemStack, Double>> items = plugin.getDataUtils().getItems();

        int loopIndex = firstSlotIndex;

        for (int editorSlot : plugin.getConfigUtils().getEditorItemSlots()) {

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
            Bukkit.broadcastMessage("set item in slot: " + editorSlot);

            loopIndex++;
        }

        invManager.addPlayer(p, InventoryManager.InventoryType.Editor);
        p.openInventory(inv);
    }

    public Integer getPlayerViewingPage(Player p) {
        Integer currentPage = null;
        NamespacedKey key = new NamespacedKey(plugin, "page");
        PersistentDataContainer dataContainer = p.getPersistentDataContainer();
        if (dataContainer.has(key)) {
            currentPage = dataContainer.get(key, PersistentDataType.INTEGER);
        }
        return currentPage;
    }

    public int getMaxEditorPage() {
        double maxValue = (double)plugin.getDataUtils().getItemsAmount() / (double)plugin.getConfigUtils().getEditorItemSlots().size();
        if (maxValue % 1 != 0) {
            maxValue++;
        }
        return (int)maxValue;
    }

    private void setPage(Player p, int page) {
        NamespacedKey key = new NamespacedKey(plugin, "page");
        PersistentDataContainer dataContainer = p.getPersistentDataContainer();
        dataContainer.set(key, PersistentDataType.INTEGER, page);
    }

    private int getFirstSlotIndex(int page) {
        return plugin.getConfigUtils().getEditorItemSlots().size()*(page-1) + 1;
    }

    private int verifyPage(int page, int maxPage) {
        if (page < 1)
            page = 1;
        if (page > maxPage && maxPage != 0)
            page = maxPage;
        return page;
    }

}
