package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
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
            sender.sendMessage("§5Wrong args. try: /setperk nerd 1 unset");
            return true;
        }

        if (args.length == 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("you cant run this command like this from console are you *trying* to cause errors? lol");
                return true;
            } else if (Perks.findByEnumName(args[1]) == null) {
                sender.sendMessage("§5Invalid command usage. \n§5try /setperk 1 unset");
                return true;
            }

            Player p = (Player) sender;
            PlayerData pData = Main.getInstance().getPlayerData(p);

            switch (args[0]) {
                case "1":
                case "2":
                case "3":
                case "4":
                    pData.setPerkAtSlot(Integer.parseInt(args[0]), Perks.findByEnumName(args[1]));
                    break;
                default:
                    p.sendMessage("§5Invalid command usage. \n§5try /setperk 1 unset");
                    break;
            }
        }

        if (args.length == 3) {
            if (!zl.playerCheck(Bukkit.getPlayer(args[0]))) {
                sender.sendMessage("§5Offline players are currently not supported.");
                return true;
            }  else if (Perks.findByEnumName(args[2]) == null) {
                sender.sendMessage("§5Invalid command usage. \n§5try /setperk nerd 1 unset");
                return true;
            }

            Player p2 = Bukkit.getPlayer(args[0]);
            PlayerData pData2 = Main.getInstance().getPlayerData(p2);

            switch (args[1]) {
                case "1":
                case "2":
                case "3":
                case "4":
                    pData2.setPerkAtSlot(Integer.parseInt(args[1]), Perks.findByEnumName(args[2]));
                    break;
                default:
                    sender.sendMessage("§5Invalid command usage. \n§5try /setperk nerd 1 unset");
                    break;
            }
        }
        return true;
    }
}
