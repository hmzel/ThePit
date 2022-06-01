package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.ZelLogic;
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
                return true;
            } else if (Megastreaks.findByEnumName(args[0]) == null) {
                sender.sendMessage("§5Invalid command usage. \ntry /setmegastreak overdrive");
                return true;
            }

            PlayerData pData = Main.getInstance().getPlayerData((Player) sender);
            StringBuilder builder = new StringBuilder(pData.getMegastreak().getChatName());

            pData.setMegastreak(Megastreaks.findByEnumName(args[0]));
            sender.sendMessage("§aSuccessfully set your megastreak to §e" + Megastreaks.findByEnumName(args[0]).getName());

            if (pData.isMegaActive()) pData.setDummyStatus(builder.replace(2, builder.length(), pData.getMegastreak().getName()).toString());
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
            PlayerData pData = Main.getInstance().getPlayerData(p2);
            StringBuilder builder = new StringBuilder(pData.getMegastreak().getChatName());

            pData.setMegastreak(Megastreaks.findByEnumName(args[1]));
            sender.sendMessage("§aSuccessfully set §7" + p2.getName() + "§a's megastreak to §e" + Megastreaks.findByEnumName(args[1]).getName());

            if (pData.isMegaActive()) pData.setDummyStatus(builder.replace(2, builder.length(), pData.getMegastreak().getName()).toString());
        }
        return true;
    }
}
