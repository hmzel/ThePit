package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.utils.ZelLogic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetStatusCommand implements CommandExecutor {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args.length > 2) {
            sender.sendMessage("§5Wrong args. \ntry: /setstatus nerd idling");
            return true;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) return true;

            Main.getInstance().getPlayerData((Player) sender).setStatus(args[0]);
            sender.sendMessage("§aSuccessfully set your Status to §r" + ChatColor.translateAlternateColorCodes('&', args[0]));
        }

        if (args.length == 2) {
            if (!zl.playerCheck(Bukkit.getPlayer(args[0]))) {
                sender.sendMessage("§5Offline players are currently not supported.");
                return true;
            }

            Player p2 = Bukkit.getPlayer(args[0]);

            Main.getInstance().getPlayerData(p2).setStatus(args[1]);
            sender.sendMessage("§aSuccessfully set §7" + p2.getName() + "§a's Status to §r" + ChatColor.translateAlternateColorCodes('&', args[1]));
        }
        return true;
    }
}
