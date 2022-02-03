package me.zelha.thepit.mainpkg.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.upgrades.permanent.perks.PerkListenersAndUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RespawnCommand implements CommandExecutor {

    private final PerkListenersAndUtils perkUtils = Main.getInstance().getPerkUtils();
    private final List<UUID> cooldown = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            PlayerData pData = Main.getInstance().getPlayerData(p);

            if (!pData.getStatus().equals("fighting")) {

                if (!Main.getInstance().getSpawnListener().spawnCheck(p.getLocation())) {

                    if (!cooldown.contains(p.getUniqueId())) {
                        Main.getInstance().getDeathListener().teleportToSpawnMethod(p);
                        cooldown.add(p.getUniqueId());
                        perkUtils.perkReset(p);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                cooldown.remove(p.getUniqueId());
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










