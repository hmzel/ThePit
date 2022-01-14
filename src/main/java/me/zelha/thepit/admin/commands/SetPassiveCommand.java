package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Passives;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPassiveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            PlayerData pData = Main.getInstance().getPlayerData(p);

            if (args.length == 2) {
                if (Passives.findByName(args[0]) != null && NumberUtils.isNumber(args[1]) && Integer.parseInt(args[1]) == Double.parseDouble(args[1])) {
                    pData.setPassiveTier(Passives.findByName(args[0]), Integer.parseInt(args[1]));
                    p.sendMessage("§aSuccessfully set your passive " + Passives.findByName(args[0]).getColorfulName() + " §ato " + Integer.parseInt(args[1]));
                } else {
                    p.sendMessage("§5Invalid command usage." +
                            "\n§5try /setpassive xp_boost 1");
                }
            } else if (args.length == 3) {
                if (Bukkit.getPlayer(args[0]) != null) {
                    Player p2 = Bukkit.getPlayer(args[0]);
                    PlayerData pData2 = Main.getInstance().getPlayerData(p2);

                    if (Passives.findByName(args[1]) != null && NumberUtils.isNumber(args[2]) && Integer.parseInt(args[2]) == Double.parseDouble(args[2])) {
                        pData2.setPassiveTier(Passives.findByName(args[1]), Integer.parseInt(args[2]));
                        p.sendMessage("§aSuccessfully set " + p2.getName() + "'s passive " + Passives.findByName(args[1]).getColorfulName() + " §ato " + Integer.parseInt(args[2]));
                    } else {
                        p.sendMessage("§5Invalid command usage." +
                                "\n§5try /setpassive name xp_boost 1");
                    }
                } else {
                    p.sendMessage("§5i haven't added support for offline players yet");
                }
            } else {
                p.sendMessage("§5Invalid command usage." +
                        "\n§5try /setpassive xp_boost 1");
            }
        }
        return true;
    }
}
























