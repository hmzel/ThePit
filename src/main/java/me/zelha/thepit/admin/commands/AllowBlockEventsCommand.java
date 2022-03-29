package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AllowBlockEventsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        if (!Main.getInstance().blockPriviledges.contains(p)) {
            Main.getInstance().blockPriviledges.add(p);
            p.sendMessage("§5ok, since you asked so nicely");
        } else {
            Main.getInstance().blockPriviledges.remove(p);
            p.sendMessage("§5you no longer have block priviledges");
        }
        return true;
    }
}
