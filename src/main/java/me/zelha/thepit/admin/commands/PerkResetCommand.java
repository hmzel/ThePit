package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PerkResetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Main.getInstance().getPerkUtils().perkReset((Player) sender);
        return true;
    }
}
