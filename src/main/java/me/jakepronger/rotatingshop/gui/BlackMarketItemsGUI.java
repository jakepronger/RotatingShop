package me.jakepronger.rotatingshop.gui;

import me.jakepronger.rotatingshop.config.DataUtils;
import me.jakepronger.rotatingshop.utils.InvUtils;
import me.jakepronger.rotatingshop.utils.Logger;
import me.jakepronger.rotatingshop.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class BlackMarketItemsGUI {

    public static HashMap<Player, Inventory> openInventories = new HashMap<>();

    public static void open(Player p) {

        Inventory inv = InvUtils.loadInventory("items.gui", p);

        if (inv == null) {
            p.sendMessage(Utils.format("&cFailed to load inventory!"));
            Logger.error("");
            // todo: ^
            return;
        }

        DataUtils data = plugin.getDataUtils();

        data.getItems().whenComplete((items, throwable) -> {

            for (int i = 1; true; i++) {

                if (i > items.size()) {
                    break;
                }

                Map.Entry<ItemStack, Double> entry = items.get(i-1);
                if (entry == null) {
                    break;
                }

                double price = entry.getValue();

                ItemStack item = entry.getKey();
                ItemMeta meta = item.getItemMeta();

                PersistentDataContainer pData = meta.getPersistentDataContainer();
                pData.set(new NamespacedKey(plugin, "price"), PersistentDataType.DOUBLE, price);
                pData.set(new NamespacedKey(plugin, "index"), PersistentDataType.INTEGER, i);

                meta.setDisplayName(Utils.format("&a") + "price: " + price + " index: " + i);

                item.setItemMeta(meta);

                inv.setItem(i-1, item);
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                openInventories.put(p, inv);
                p.openInventory(inv);
            });
        });

    }

}
