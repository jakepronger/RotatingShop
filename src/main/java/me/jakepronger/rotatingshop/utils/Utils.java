package me.jakepronger.rotatingshop.utils;

import org.bukkit.ChatColor;

public class Utils {

    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /*@Deprecated
    public static String centerTitle(String title) {
        StringBuilder spacer = new StringBuilder();
        int spaces = 27 - ChatColor.stripColor(title).length();
        for (int i = 0; i < spaces; i++) {
            spacer.append(" ");
        }
        return spacer + title;
    }

    private static boolean isHexColor(String hex) {
        return hex.matches("^[a-fA-F0-9]{6}$");
    }

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

    public static net.md_5.bungee.api.ChatColor getColorFromHex(String hex) {
        return net.md_5.bungee.api.ChatColor.of(hex.toLowerCase());
    }

}
