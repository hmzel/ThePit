package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.zelenums.Passives;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPassiveCommand implements CommandExecutor {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1 || args.length > 3) {
            sender.sendMessage("§5Wrong args. \ntry: /setpassive nerd xp_boost 1");
            return true;
        }

        if (args.length == 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("you cant run this command like this from console are you *trying* to cause errors? lol");
                return true;
            } else if (Passives.findByName(args[0]) == null || !NumberUtils.isNumber(args[1]) || Integer.parseInt(args[1]) != Double.parseDouble(args[1])) {
                sender.sendMessage("§5Invalid command usage. \ntry /setpassive xp_boost 1");
                return true;
            }

            Main.getInstance().getPlayerData((Player) sender).setPassiveTier(Passives.findByName(args[0]), Integer.parseInt(args[1]));
            sender.sendMessage("§aSuccessfully set your passive " + Passives.findByName(args[0]).getColorfulName() + " §ato " + Integer.parseInt(args[1]));
        }

        if (args.length == 3) {
            if (!zl.playerCheck(Bukkit.getPlayer(args[0]))) {
                sender.sendMessage("§5Offline players are currently not supported.");
                return true;
            } else if (Passives.findByName(args[1]) == null || !NumberUtils.isNumber(args[2]) || Integer.parseInt(args[2]) != Double.parseDouble(args[2])) {
                sender.sendMessage("§5Invalid command usage. \ntry /setpassive nerd xp_boost 1");
                return true;
            }

            Player p2 = Bukkit.getPlayer(args[0]);
            Main.getInstance().getPlayerData(p2).setPassiveTier(Passives.findByName(args[1]), Integer.parseInt(args[2]));
            sender.sendMessage("§aSuccessfully set §7" + p2.getName() + "§a's passive " + Passives.findByName(args[1]).getColorfulName() + " §ato " + Integer.parseInt(args[2]));
        }
        return true;
    }
}
























