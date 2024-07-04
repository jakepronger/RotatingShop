package me.jakepronger.rotatingshop.commands;

import me.jakepronger.rotatingshop.gui.BlackMarketGUI;
import me.jakepronger.rotatingshop.utils.command.CommandInfo;
import me.jakepronger.rotatingshop.utils.command.PluginCommand;

import org.bukkit.entity.Player;

@CommandInfo(name = "blackmarket", requiresPlayer = true)
public class BlackMarketCommand extends PluginCommand {

    @Override
    public void execute(Player player, String label, String[] args) {
        BlackMarketGUI.open(player);
    }

}
