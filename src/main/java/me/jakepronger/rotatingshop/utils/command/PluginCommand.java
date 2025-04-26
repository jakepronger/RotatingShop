package me.jakepronger.rotatingshop.utils.command;

import me.jakepronger.rotatingshop.RotatingShop;
import me.jakepronger.rotatingshop.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public abstract class PluginCommand implements CommandExecutor {

    // Get the global plugin instance
    protected RotatingShop plugin = RotatingShop.getInstance();

    private final CommandInfo commandInfo;

    public PluginCommand() {
        this.commandInfo = getClass().getDeclaredAnnotation(CommandInfo.class);
        Objects.requireNonNull(commandInfo, "Commands must have CommandInfo annotations");
    }

    public CommandInfo getCommandInfo() {
        return commandInfo;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command requires a player and sender is not a player
        if (commandInfo.requiresPlayer() && !(sender instanceof Player)) {
            sender.sendMessage(Utils.format("&cThis command requires a player."));
            return false;
        }

        // Handle the command execution
        if (sender instanceof ConsoleCommandSender) {
            handleConsoleCommand(sender, label, args);
        } else if (sender instanceof Player) {
            handlePlayerCommand((Player) sender, label, args);
        }

        return true;
    }

    // This method can be overridden in specific command classes
    protected void handlePlayerCommand(Player player, String label, String[] args) {
        execute(player, label, args);
    }

    // This method can be overridden in specific command classes
    protected void handleConsoleCommand(CommandSender sender, String label, String[] args) {
        execute(sender, label, args);
    }

    // Override this method for the actual logic in each command class
    protected abstract void execute(CommandSender sender, String label, String[] args);
}
