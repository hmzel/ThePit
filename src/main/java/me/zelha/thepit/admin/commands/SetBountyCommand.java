package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetBountyCommand implements CommandExecutor {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args.length > 3) {
            sender.sendMessage("§5Wrong args. try: /setbounty nerd 100 (add a true at the end for it to be broadcasted)");
            return true;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("you cant run this command like this from console are you *trying* to cause errors? lol");
                return true;
            } else if (!NumberUtils.isNumber(args[0])) {
                sender.sendMessage("§5Must be a number");
                return true;
            }

            Player p = (Player) sender;
            PlayerData pData = Main.getInstance().getPlayerData(p);
            pData.setBounty(Integer.parseInt(args[0]));
        }

        if (args.length >= 2) {
            if (!zl.playerCheck(Bukkit.getPlayer(args[0]))) {
                sender.sendMessage("§5Offline players are currently not supported.");
                return true;
            } else if (!NumberUtils.isNumber(args[1])) {
                sender.sendMessage("§5Must be a number");
                return true;
            }

            Player p2 = Bukkit.getPlayer(args[0]);
            PlayerData pData2 = Main.getInstance().getPlayerData(p2);
            pData2.setBounty(Integer.parseInt(args[1]));

            if (BooleanUtils.toBoolean(args[2])) {
                Bukkit.broadcastMessage("§6§lBOUNTY! §7of §6§l " + Integer.parseInt(args[1]) + "g §7placed on " + zl.getColorBracketAndLevel(p2.getUniqueId().toString())
                        + " §7" + p2.getName() + " by " + sender.getName());
            }
        }
        return true;
    }
}
