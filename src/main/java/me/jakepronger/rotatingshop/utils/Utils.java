package me.jakepronger.rotatingshop.utils;

import org.bukkit.ChatColor;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class Utils {

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

    public static long reload() {

        long delay = System.currentTimeMillis();

        Logger.log("&aReloading...");
        Logger.log("&eClosed &f" + InvUtils.closeInventories() + "&e inventories.");

        boolean newlyCreated = new File(plugin.getDataFolder() + File.separator + "config.yml").exists();

        plugin.loadConfig();

        if (newlyCreated)
            Logger.log("&aCreated config.");
        else Logger.log("&aReloaded config.");

        plugin.loadPerms();
        Logger.log("&eReloaded permissions.");

        return System.currentTimeMillis() - delay;
    }

    public static net.md_5.bungee.api.ChatColor getColorFromHex(String hex) {
        return net.md_5.bungee.api.ChatColor.of(hex.toLowerCase());
    }

    /*
    @Deprecated
    public static String formatHexColors(String text) {
        StringBuilder formattedText = new StringBuilder();
        int index = 0;

        while (index < text.length()) {

            if (text.charAt(index) == '&' && text.charAt(index + 1) == '#') {
                String hexColor = text.substring(index + 1, index + 8);
                if (isHexColor(hexColor)) {
                    formattedText.append(getColorFromHex(hexColor));
                    index += 8; // Skip past the hex color code
                    continue;
                }
            }

            formattedText.append(text.charAt(index));
            index++;
        }

        return format(formattedText.toString());
    }*/

}
