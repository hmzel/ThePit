package me.zelha.thepit.mainpkg.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RespawnCommand implements CommandExecutor {

    private final List<UUID> cooldown = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (pData.getStatus().equals("fighting")) {
            p.sendMessage("§c§lHOLD UP! §7Can't /respawn while fighting (§c" + pData.getCombatTimer() + "s §7left)");
            return true;
        } else if (Main.getInstance().getZelLogic().spawnCheck(p.getLocation())) {
            p.sendMessage("§cYou cannot /respawn here!");
            return true;
        } else if (cooldown.contains(p.getUniqueId())) {
            p.sendMessage("§cYou may only /respawn every 10 seconds.");
            return true;
        }

        Main.getInstance().getDeathUtils().teleportToSpawnMethod(p);
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










