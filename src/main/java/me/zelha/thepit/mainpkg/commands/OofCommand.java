package me.zelha.thepit.mainpkg.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.DamageLog;
import me.zelha.thepit.mainpkg.data.KillRecap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OofCommand implements CommandExecutor {

    private final List<UUID> cooldown = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        if (Main.getInstance().getZelLogic().spawnCheck(p.getLocation())) {
            p.sendMessage("§c§lNOPE! §7Can't /oof in spawn!");
            return true;
        } else if (cooldown.contains(p.getUniqueId())) {
            p.sendMessage("§c§lCHILL OUT! §7You may only /oof every 10 seconds!");
            return true;
        }

        KillRecap.addDamageLog(p, new DamageLog(1000000, "oof"));
        p.damage(1000000);
        cooldown.add(p.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                cooldown.remove(p.getUniqueId());
            }
        }.runTaskLater(Main.getInstance(), 200);

        return true;
    }
}





















