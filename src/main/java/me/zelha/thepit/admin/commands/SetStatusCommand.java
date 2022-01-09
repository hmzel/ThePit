package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetStatusCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p2 = Bukkit.getPlayer(args[0]);

        if (args.length == 1) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                PlayerData pData = Main.getInstance().getPlayerData(p);
                pData.setStatus(args[0]);
            }
        } else if (args.length == 2 && p2 != null && p2.isValid()) {
            PlayerData pData2 = Main.getInstance().getPlayerData(p2);

            pData2.setPrestige(Integer.parseInt(args[1]));
        } else {
            sender.sendMessage("§5Wrong args");
        }

        return true;
    }
}
