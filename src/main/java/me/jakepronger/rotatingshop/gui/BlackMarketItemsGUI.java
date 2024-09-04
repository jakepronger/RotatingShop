package me.jakepronger.rotatingshop.gui;

import me.jakepronger.rotatingshop.config.DataUtils;
import me.jakepronger.rotatingshop.utils.InvUtils;
import me.jakepronger.rotatingshop.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if (page > maxPage)
            page = maxPage;

        Bukkit.broadcastMessage("opened editor at page: " + page);

        int firstSlotIndex = editorSlotsAmount*(page-1) + 1;
        Bukkit.broadcastMessage("editorSlotsAmount: " + editorSlotsAmount);
        Bukkit.broadcastMessage("page: " + page);
        Bukkit.broadcastMessage("firstSlotIndex: " + firstSlotIndex);

        NamespacedKey key = new NamespacedKey(plugin, "page");

        PersistentDataContainer dataContainer = p.getPersistentDataContainer();
        dataContainer.set(key, PersistentDataType.INTEGER, page);

        //Bukkit.broadcastMessage("debug 1");

        data.getItems().whenComplete((items, throwable) -> {

            //Bukkit.broadcastMessage("debug 2");

            int loopIndex = firstSlotIndex;

            for (int editorSlot : editorSlots) {

                //Bukkit.broadcastMessage("loop debug: " + loopIndex);

                if (loopIndex > items.size()) {
                    break;
                }

                //Bukkit.broadcastMessage("debug 3");

                Map.Entry<ItemStack, Double> entry = items.get(loopIndex);
                if (entry == null) {
                    break;
                }

                //Bukkit.broadcastMessage("debug 4");

                // item formatting
                double price = entry.getValue();

                //Bukkit.broadcastMessage("debug 5");

                ItemStack item = entry.getKey();
                ItemMeta meta = item.getItemMeta();

                //Bukkit.broadcastMessage("debug 6");

                PersistentDataContainer pData = meta.getPersistentDataContainer();
                pData.set(new NamespacedKey(plugin, "price"), PersistentDataType.DOUBLE, price);
                pData.set(new NamespacedKey(plugin, "index"), PersistentDataType.INTEGER, loopIndex);

                //Bukkit.broadcastMessage("debug 7");

                List<String> lore = new ArrayList<>();
                if (meta.getLore() != null)
                    lore = meta.getLore();

                lore.add(Utils.format("&8----------"));
                lore.add(Utils.format("&7Price: &a" + price));
                lore.add(Utils.format("&7Index: &a" + loopIndex));

                //Bukkit.broadcastMessage("debug 8");

                meta.setLore(lore);

                item.setItemMeta(meta);

                //Bukkit.broadcastMessage("debug 9");

                inv.setItem(editorSlot, item);

                //Bukkit.broadcastMessage("debug 10");

                loopIndex++;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                //Bukkit.broadcastMessage("debuga 11");
                openInventories.put(p, inv);
                p.openInventory(inv);
                //Bukkit.broadcastMessage("debuga 12");
            });
        });

    }

}
