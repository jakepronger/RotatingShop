package me.jakepronger.rotatingshop.commands;

import me.jakepronger.rotatingshop.gui.BlackMarketGUI;
import me.jakepronger.rotatingshop.utils.Utils;
import me.jakepronger.rotatingshop.utils.command.CommandInfo;
import me.jakepronger.rotatingshop.utils.command.PluginCommand;

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
    // /blackmarket add <price> [quantity]

    @Override
    public void execute(CommandSender sender, String label, String[] args) {

        // console supported
        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.isOp()) {
            int amount = Utils.closeInventories();
            sender.sendMessage(Utils.format("&c") + "Closed all " + amount + " inventories.");
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Utils.format("&cPlayer only command."));
            return;
        }

        Player player = (Player) sender;

        if (args.length == 0)
            BlackMarketGUI.open(player);
        else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")
                    && player.isOp()) {
                //BlackMarketGUI.open(player); // todo: call reload
                int amount = Utils.closeInventories();
                player.sendMessage(Utils.format("&c") + "Closed all " + amount + "inventories.");
            } else if (args[0].equalsIgnoreCase("editor")
                && player.isOp()) {
                BlackMarketGUI.open(player); // todo: call editor
            } else if (args[0].equalsIgnoreCase("add")) {

            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {

            // parse price
            double price;
            try {
                price = Double.parseDouble(args[1]);
            } catch (Exception e) {
                player.sendMessage(Utils.format("&cInvalid price: ") + args[1]);
                return;
            }

            // get main hand item
            ItemStack item = player.getInventory().getItemInMainHand();

            // (async) store item data and price flags are price, (quantity stored in item)
        }

        else {
            if (player.hasPermission(plugin.editorPerm) && player.hasPermission(plugin.reloadPerm)) // todo: player has reload perms show reload usage
                player.sendMessage(Utils.format("&c") + "/" + label + " [editor/add/reload]");
            else if (player.hasPermission(plugin.editorPerm))
                player.sendMessage(Utils.format("&c") + "/" + label + " [editor/add]");
            else if (player.hasPermission(plugin.reloadPerm))
                player.sendMessage(Utils.format("&c") + "/" + label + " [reload]");
            else
                player.sendMessage(Utils.format("&c") + "/" + label); // usage
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 1)
            return List.of();

        final List<String> oneArgList = new ArrayList<>();
        final List<String> completions = new ArrayList<>();

        if (sender.hasPermission(plugin.reloadPerm))
            oneArgList.add("reload");
        if (sender.hasPermission(plugin.editorPerm)) {
            oneArgList.add("editor");
            oneArgList.add("add"); // todo: look into adding add function on inventory drag item event..?
        }

        StringUtil.copyPartialMatches(args[0], oneArgList, completions);
        Collections.sort(completions);

        return completions;
    }

}
