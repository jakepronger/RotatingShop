package me.jakepronger.rotatingshop.commands;

import me.jakepronger.rotatingshop.config.ConfigUtils;
import me.jakepronger.rotatingshop.config.DataUtils;
import me.jakepronger.rotatingshop.gui.BlackMarketGUI;
import me.jakepronger.rotatingshop.gui.BlackMarketItemsGUI;
import me.jakepronger.rotatingshop.utils.Logger;
import me.jakepronger.rotatingshop.utils.Utils;
import me.jakepronger.rotatingshop.utils.command.CommandInfo;
import me.jakepronger.rotatingshop.utils.command.PluginCommand;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

@CommandInfo(name = "blackmarket", requiresPlayer = false)
public class BlackMarketCommand extends PluginCommand implements TabExecutor {

    // item in main hand
    // /bm add <price>

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        // console supported
        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.isOp()) {

            long delay = Utils.reload();
            String message = Utils.format("&aReloaded in &f" + delay + "ms&a.");

            Logger.log(message);

            if (sender instanceof Player p) {
                p.sendMessage(message);
            }

            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Utils.format("&cPlayer only command."));
            return;
        }

        Player p = (Player) sender;

        ConfigUtils config = plugin.getConfigUtils();

        if (args.length == 0) {

            if (!config.hasBlackMarketPerm(sender)) {
                p.sendMessage(config.getNoPermMessage());
                return;
            }

            BlackMarketGUI.open(p);

            return;

        } else if (args.length == 1) {

            if (args[0].equalsIgnoreCase("reload")) {

                if (!config.hasReloadPerm(p)) {
                    p.sendMessage(config.getNoPermMessage());
                    return;
                }

                long delay = Utils.reload();
                p.sendMessage(Utils.format("&aReloaded in &f" + delay + "ms&a."));

                return;

            } else if (args[0].equalsIgnoreCase("editor")) {

                if (!config.hasEditorPerm(sender)) {
                    p.sendMessage(config.getNoPermMessage());
                    return;
                }

                BlackMarketItemsGUI.open(p);

                return;

            } else if (args[0].equalsIgnoreCase("add")) {
                // usage
                sender.sendMessage(Utils.format("&c") + "/" + label + " add <price>");
                return;
            }

        } else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {

            if (!config.hasEditorPerm(sender)) {
                p.sendMessage(config.getNoPermMessage());
                return;
            }

            // parse price
            double price;
            try {
                price = Double.parseDouble(args[1]);
            } catch (Exception e) {
                p.sendMessage(Utils.format("&cInvalid price: ") + args[1]);
                return;
            }

            // get main hand item
            ItemStack item = p.getInventory().getItemInMainHand();

            if (item.getType() == Material.AIR) {
                p.sendMessage(Utils.format("&cNo item in main hand."));
                return;
            }

            DataUtils data = plugin.getDataUtils();

            // (async) store item data and price flags are price, (quantity stored in item)
            data.addItem(item, price).whenComplete((value, throwable) -> {
                sender.sendMessage(Utils.format("&aAdded item to data."));
            });

            return;
        }

        if (config.hasEditorPerm(p)
                && config.hasReloadPerm(p))
            p.sendMessage(Utils.format("&c") + "/" + label + " [editor/add/refresh/reload]");
        else if (config.hasEditorPerm(p))
            p.sendMessage(Utils.format("&c") + "/" + label + " [editor/add/refresh]");
        else if (config.hasReloadPerm(p))
            p.sendMessage(Utils.format("&c") + "/" + label + " [reload]");
        else
            p.sendMessage(Utils.format("&c") + "/" + label); // usage
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        final List<String> oneArgList = new ArrayList<>();
        final List<String> twoArgList = new ArrayList<>();

        final List<String> completions = new ArrayList<>();

        ConfigUtils config = plugin.getConfigUtils();

        if (args.length == 1) {

            if (config.hasReloadPerm(sender))
                oneArgList.add("reload");

            if (config.hasBlackMarketPerm(sender)) {
                oneArgList.add("editor");
                oneArgList.add("add"); // todo: look into adding add function on inventory drag item event..?
                oneArgList.add("refresh");
            }

            StringUtil.copyPartialMatches(args[0], oneArgList, completions);
            Collections.sort(completions);

            return completions;

        } else if (args.length == 2
                && args[0].equalsIgnoreCase("add")
                && config.hasEditorPerm(sender)) {

            // todo: look into tab guidance thing?
            twoArgList.add("1");

            StringUtil.copyPartialMatches(args[1], twoArgList, completions);
            Collections.sort(completions);

            return completions;
        }

        return List.of();
    }

}
