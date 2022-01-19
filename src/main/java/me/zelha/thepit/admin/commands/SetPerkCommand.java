package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPerkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            PlayerData pData = Main.getInstance().getPlayerData(p);

            if (args.length == 2) {
                if (Perks.findByEnumName(args[1]) != null) {
                    switch (args[0]) {
                        case "1":
                        case "2":
                        case "3":
                        case "4":
                            pData.setPerkAtSlot(Integer.parseInt(args[0]), Perks.findByEnumName(args[1]));
                            break;
                        default:
                            p.sendMessage("§5Invalid command usage." +
                                    "\n§5try /setperk 1 unset");
                            break;
                    }
                } else {
                    p.sendMessage("§5Invalid command usage." +
                            "\n§5try /setperk 1 unset");
                }
            } else if (args.length == 3) {
                if (Bukkit.getPlayer(args[0]) != null) {
                    Player p2 = Bukkit.getPlayer(args[0]);
                    PlayerData pData2 = Main.getInstance().getPlayerData(p2);

                    if (Perks.findByEnumName(args[2]) != null) {
                        switch (args[1]) {
                            case "1":
                            case "2":
                            case "3":
                            case "4":
                                pData2.setPerkAtSlot(Integer.parseInt(args[1]), Perks.findByEnumName(args[2]));
                                break;
                            default:
                                p.sendMessage("§5Invalid command usage." +
                                        "\n§5try /setperk name 1 unset");
                                break;
                        }
                    } else {
                        p.sendMessage("§5Invalid command usage." +
                                "\n§5try /setperk name 1 unset");
                    }
                } else {
                    p.sendMessage("§5i haven't added support for offline players yet");
                }
            } else {
                p.sendMessage("§5Invalid command usage." +
                        "\n§5try /setperk 1 unset");
            }
        }
        return true;
    }
}
