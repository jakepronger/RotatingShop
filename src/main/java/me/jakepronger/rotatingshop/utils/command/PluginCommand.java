package me.jakepronger.rotatingshop.utils.command;

import me.jakepronger.rotatingshop.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public abstract class PluginCommand implements CommandExecutor {

    private final CommandInfo commandInfo;

    public PluginCommand() {
        commandInfo = getClass().getDeclaredAnnotation(CommandInfo.class);
        Objects.requireNonNull(commandInfo, "Commands must have CommandInfo annotations");
    }

    public CommandInfo getCommandInfo() {
        return commandInfo;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof ConsoleCommandSender) {

            if (commandInfo.requiresPlayer()) {
                sender.sendMessage(Utils.format("&cPlayer only command."));
                return true;
            }

            execute(sender, label, args);

        } else {
            execute(sender, label, args);
            execute((Player)sender, label, args);
        }

        return true;
    }

    public void execute(Player player, String label, String[] args) {}

    public void execute(CommandSender sender, String label, String[] args) {}

}
