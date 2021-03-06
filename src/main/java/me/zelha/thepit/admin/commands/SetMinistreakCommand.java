package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.utils.ZelLogic;
import me.zelha.thepit.zelenums.Ministreaks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.entity.Player;

public class SetMinistreakCommand implements CommandExecutor {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final String[] numberz = {"first", "second", "third"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1 || args.length > 3) {
            sender.sendMessage("§5Wrong args. \ntry: /setministreak nerd 1 unset");
            return true;
        }

        if (args.length == 2) {
            if (!(sender instanceof Player)) {
                return true;
            } else if (Ministreaks.findByEnumName(args[1]) == null || NumberUtils.toInt(args[0], 0) < 1 || NumberUtils.toInt(args[0], 0) > 3) {
                sender.sendMessage("§5Invalid command usage. \ntry /setministreak 1 unset");
                return true;
            }

            Main.getInstance().getPlayerData((Player) sender).setMinistreakAtSlot(Integer.parseInt(args[0]), Ministreaks.findByEnumName(args[1]));
            sender.sendMessage("§aSuccessfully set your " + numberz[Integer.parseInt(args[0]) - 1] + " ministreak slot to §e" + Ministreaks.findByEnumName(args[1]).getName());
        }

        if (args.length == 3) {
            if (!zl.playerCheck(Bukkit.getPlayer(args[0]))) {
                sender.sendMessage("§5Offline players are currently not supported.");
                return true;
            } else if (Ministreaks.findByEnumName(args[2]) == null || NumberUtils.toInt(args[1], 0) < 1 || NumberUtils.toInt(args[1], 0) > 3) {
                sender.sendMessage("§5Invalid command usage. \ntry /setministreak nerd 1 unset");
                return true;
            }

            Player p2 = Bukkit.getPlayer(args[0]);

            Main.getInstance().getPlayerData(p2).setMinistreakAtSlot(Integer.parseInt(args[1]), Ministreaks.findByEnumName(args[2]));
            sender.sendMessage("§aSuccessfully set §7" + p2.getName() + "§a's " + numberz[Integer.parseInt(args[1]) - 1] + " ministreak slot to §e" + Ministreaks.findByEnumName(args[2]).getName());
        }

        return true;
    }
}
