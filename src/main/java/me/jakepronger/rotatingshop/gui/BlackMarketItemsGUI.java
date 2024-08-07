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

import java.util.HashMap;
import java.util.Map;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class BlackMarketItemsGUI {

    public static HashMap<Player, Inventory> openInventories = new HashMap<>();

    public static void open(Player p) {

        Inventory inv = InvUtils.loadInventory("items.gui", p);

        DataUtils data = plugin.getDataUtils();

        data.getItems().whenComplete((items, throwable) -> {

            for (int i = 0; true; i++) {

                if (i >= items.size()) {
                    break;
                }

                Map.Entry<ItemStack, Double> entry = items.get(i);

                double price = entry.getValue();

                ItemStack item = entry.getKey();
                ItemMeta meta = item.getItemMeta();

                PersistentDataContainer pData = meta.getPersistentDataContainer();
                pData.set(new NamespacedKey(plugin, "price"), PersistentDataType.DOUBLE, price);

                meta.setDisplayName(Utils.format("&aprice: " + price));

                item.setItemMeta(meta);

                inv.setItem(i, item);
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                openInventories.put(p, inv);
                p.openInventory(inv);
            });
        });

    }

}
