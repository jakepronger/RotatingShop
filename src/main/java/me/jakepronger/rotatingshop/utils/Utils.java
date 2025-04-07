package me.jakepronger.rotatingshop.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static boolean canFit(Player p, ItemStack item) {
        return false;
    }

    public static String format(String message) {

        // Regular expression to match &# followed by 6 hex digits
        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            // Extract the hex code
            String hex = matcher.group(1);
            // Get the color from hex
            String replacement = getColorFromHex("#" + hex).toString();
            // Replace the hex code with the color
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }

    public static Component stringToComponent(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    public static net.md_5.bungee.api.ChatColor getColorFromHex(String hex) {
        return net.md_5.bungee.api.ChatColor.of(hex.toLowerCase());
    }

}
