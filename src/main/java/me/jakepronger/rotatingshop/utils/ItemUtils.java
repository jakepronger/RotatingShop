package me.jakepronger.rotatingshop.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    public static ItemStack getItem(Material type) {
        return new ItemStack(type);
    }

    public static ItemStack getItem(Material type, String name) {
        return setName(new ItemStack(type), name);
    }

    public static ItemStack getItem(Material type, String name, ItemFlag... itemFlags) {
        return setFlags(getItem(type, name), itemFlags);
    }

    public static ItemStack getItem(Material type, String name, List<String> lore) {
        return setLore(getItem(type, name), lore);
    }

    public static ItemStack getItem(Material type, String name, List<String> lore, ItemFlag... itemFlags) {
        return setFlags(getItem(type, name, lore), itemFlags);
    }

    public static ItemStack setName(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.format(name));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setFlags(ItemStack item, ItemFlag... itemFlags) {
        ItemMeta meta = item.getItemMeta();

        for (ItemFlag itemFlag : meta.getItemFlags()) {
            meta.removeItemFlags(itemFlag);
        }

        meta.addItemFlags(itemFlags);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setLore(ItemStack item, List<String> lore) {
        ItemMeta meta = item.getItemMeta();

        List<String> formattedLore = new ArrayList<>();
        for (String line : lore) {
            formattedLore.add(Utils.format(line));
        }

        meta.setLore(formattedLore);
        item.setItemMeta(meta);
        return item;
    }

}
