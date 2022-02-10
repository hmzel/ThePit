package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPerkCommand implements CommandExecutor {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1 || args.length > 3) {
            sender.sendMessage("§5Wrong args. \ntry: /setperk nerd 1 unset");
            return true;
        }

        if (args.length == 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("you cant run this command like this from console are you *trying* to cause errors? lol");
                return true;
            } else if (Perks.findByEnumName(args[1]) == null) {
                sender.sendMessage("§5Invalid command usage. \ntry /setperk 1 unset");
                return true;
            }

            switch (args[0]) {
                case "1":
                    sender.sendMessage("§aSuccessfully set your first perk slot to §e" + Perks.findByEnumName(args[1]).getName());
                    break;
                case "2":
                    sender.sendMessage("§aSuccessfully set your second perk slot to §e" + Perks.findByEnumName(args[1]).getName());
                    break;
                case "3":
                    sender.sendMessage("§aSuccessfully set your third perk slot to §e" + Perks.findByEnumName(args[1]).getName());
                    break;
                case "4":
                    sender.sendMessage("§aSuccessfully set your fourth perk slot to §e" + Perks.findByEnumName(args[1]).getName());
                    break;
                default:
                    sender.sendMessage("§5Invalid command usage. \ntry /setperk 1 unset");
                    return true;
            }
            Main.getInstance().getPlayerData((Player) sender).setPerkAtSlot(Integer.parseInt(args[0]), Perks.findByEnumName(args[1]));
        }

        if (args.length == 3) {
            if (!zl.playerCheck(Bukkit.getPlayer(args[0]))) {
                sender.sendMessage("§5Offline players are currently not supported.");
                return true;
            }  else if (Perks.findByEnumName(args[2]) == null) {
                sender.sendMessage("§5Invalid command usage. \ntry /setperk nerd 1 unset");
                return true;
            }

            Player p2 = Bukkit.getPlayer(args[0]);

            switch (args[1]) {
                case "1":
                    sender.sendMessage("§aSuccessfully set §7" + p2.getName() + "§a's first perk slot to §e" + Perks.findByEnumName(args[2]).getName());
                    break;
                case "2":
                    sender.sendMessage("§aSuccessfully set §7" + p2.getName() + "§a's second perk slot to §e" + Perks.findByEnumName(args[2]).getName());
                    break;
                case "3":
                    sender.sendMessage("§aSuccessfully set §7" + p2.getName() + "§a's third perk slot to §e" + Perks.findByEnumName(args[2]).getName());
                    break;
                case "4":
                    sender.sendMessage("§aSuccessfully set §7" + p2.getName() + "§a's fourth perk slot to §e" + Perks.findByEnumName(args[2]).getName());
                    break;
                default:
                    sender.sendMessage("§5Invalid command usage. \ntry /setperk nerd 1 unset");
                    return true;
            }
            Main.getInstance().getPlayerData(p2).setPerkAtSlot(Integer.parseInt(args[1]), Perks.findByEnumName(args[2]));
        }
        return true;
    }
}
