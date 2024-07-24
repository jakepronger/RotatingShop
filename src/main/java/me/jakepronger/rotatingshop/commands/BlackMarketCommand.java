package me.jakepronger.rotatingshop.commands;

import me.jakepronger.rotatingshop.gui.BlackMarketGUI;
import me.jakepronger.rotatingshop.utils.Utils;
import me.jakepronger.rotatingshop.utils.command.CommandInfo;
import me.jakepronger.rotatingshop.utils.command.PluginCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

@CommandInfo(name = "blackmarket", requiresPlayer = true)
public class BlackMarketCommand extends PluginCommand implements TabExecutor {

    @Override
    public void execute(Player player, String label, String[] args) {

        if (args.length == 0)
            BlackMarketGUI.open(player);
        else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")
                    && player.isOp()) {
                BlackMarketGUI.open(player); // todo: call reload
            } else if (args[0].equalsIgnoreCase("editor")
                && player.isOp()) {
                BlackMarketGUI.open(player); // todo: call editor
            }
        }

        else {
            if (player.hasPermission(plugin.editorPerm) && player.hasPermission(plugin.reloadPerm)) // todo: player has reload perms show reload usage
                player.sendMessage(Utils.format("&c") + "/" + label + " [editor/reload]");
            else if (player.hasPermission(plugin.editorPerm))
                player.sendMessage(Utils.format("&c") + "/" + label + " [editor]");
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
        if (sender.hasPermission(plugin.editorPerm))
            oneArgList.add("editor");

        StringUtil.copyPartialMatches(args[0], oneArgList, completions);
        Collections.sort(completions);

        return completions;
    }

}
