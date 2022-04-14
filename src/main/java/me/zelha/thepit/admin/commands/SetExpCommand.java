package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.utils.ZelLogic;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetExpCommand implements CommandExecutor {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args.length > 2) {
            sender.sendMessage("§5Wrong args. \ntry: /setexp nerd 100");
            return true;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                return true;
            } else if (!NumberUtils.isNumber(args[0])) {
                sender.sendMessage("§5First argument must be a player or an integer");
                return true;
            }

            Main.getInstance().getPlayerData((Player) sender).setExp(NumberUtils.toInt(args[0], 0));
            sender.sendMessage("§aSuccessfully set your §bEXP §ato §b" + NumberUtils.toInt(args[0], 0) + "XP");
        }

        if (args.length == 2) {
            if (!zl.playerCheck(Bukkit.getPlayer(args[0]))) {
                sender.sendMessage("§5Offline players are currently not supported.");
                return true;
            } else if (!NumberUtils.isNumber(args[1])) {
                sender.sendMessage("§5Second argument must be an integer");
                return true;
            }

            Player p2 = Bukkit.getPlayer(args[0]);

            Main.getInstance().getPlayerData(p2).setExp(NumberUtils.toInt(args[1], 0));
            sender.sendMessage("§aSuccessfully set §7" + p2.getName() + "§a's §bEXP §ato §b" + NumberUtils.toInt(args[1], 0) + "XP");
        }
        return true;
    }
}
