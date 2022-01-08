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

public class RespawnCommand implements CommandExecutor {//taking a break

    private final List<Player> cooldown = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            PlayerData pData = Main.getInstance().getStorage().getPlayerData(p.getUniqueId().toString());

            if (!pData.getStatus().equals("fighting")) {
                String worldName = p.getWorld().getName();
                double x = p.getLocation().getX();
                double y = p.getLocation().getY();
                double z = p.getLocation().getZ();

                if (!Main.getInstance().spawnListener.spawnCheck(worldName, x, y, z)) {

                    if (!cooldown.contains(p)) {
                        Main.getInstance().deathListener.teleportToSpawnMethod(p);
                        cooldown.add(p);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                cooldown.remove(p);
                            }
                        }.runTaskLater(Main.getInstance(), 200);
                    } else {
                        p.sendMessage("§cYou may only /respawn every 10 seconds.");
                    }
                } else {
                    p.sendMessage("§cYou cannot /respawn here!");
                }
            } else {
                p.sendMessage("§c§lHOLD UP! §7Can't /respawn while fighting (§c" + pData.getCombatTimer()
                + "s §7left)");
            }
        }

        return true;
    }
}










