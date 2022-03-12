package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.zelenums.Megastreaks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetMegastreakCommand implements CommandExecutor {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0 || args.length > 2) {
            sender.sendMessage("§5Wrong args. \ntry: /setmegastreak nerd overdrive");
            return true;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("you cant run this command like this from console are you *trying* to cause errors? lol");
                return true;
            } else if (Megastreaks.findByEnumName(args[0]) == null) {
                sender.sendMessage("§5Invalid command usage. \ntry /setmegastreak overdrive");
                return true;
            }

            sender.sendMessage("§aSuccessfully set your megastreak to §e" + Megastreaks.findByEnumName(args[0]).getName());
            Main.getInstance().getPlayerData((Player) sender).setMegastreak(Megastreaks.findByEnumName(args[0]));
        }

        if (args.length == 2) {
            if (!zl.playerCheck(Bukkit.getPlayer(args[0]))) {
                sender.sendMessage("§5Offline players are currently not supported.");
                return true;
            } else if (Megastreaks.findByEnumName(args[1]) == null) {
                sender.sendMessage("§5Invalid command usage. \ntry /setmegastreak nerd overdrive");
                return true;
            }

            Player p2 = Bukkit.getPlayer(args[0]);

            sender.sendMessage("§aSuccessfully set §7" + p2.getName() + "§a's megastreak to §e" + Megastreaks.findByEnumName(args[1]).getName());
            Main.getInstance().getPlayerData(p2).setMegastreak(Megastreaks.findByEnumName(args[1]));
        }

        return true;
    }
}
