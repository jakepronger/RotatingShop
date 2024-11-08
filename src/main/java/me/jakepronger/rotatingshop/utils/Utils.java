package me.jakepronger.rotatingshop.utils;

import me.jakepronger.rotatingshop.config.ConfigUtils;
import me.jakepronger.rotatingshop.config.DataUtils;
import me.jakepronger.rotatingshop.hooks.PlayerPointsHook;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

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

    public static long reload() {

        long delay = System.currentTimeMillis();

        Logger.log("&aReloading...");
        Logger.log("&eClosed &f" + InvUtils.closeInventories() + "&e inventories.");

        ConfigUtils config = plugin.getConfigUtils();
        config.reloadConfig();

        DataUtils data = plugin.getDataUtils();
        data.reloadConfig();

        PlayerPointsHook ppHook = plugin.getPlayerPointsHook();
        ppHook.unhook();
        if (!ppHook.hook()) {
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return 0L;
        }

        TimerUtils timer = plugin.getTimerUtils();
        timer.reloadTimer();

        return System.currentTimeMillis() - delay;
    }

}
