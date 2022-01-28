package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class LevelUpListener implements Listener {

    private final RunMethods methods = Main.getInstance().generateRunMethods();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        new LevelUpRunnable(p).runTaskTimer(Main.getInstance(),0, 1);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (methods.hasID(p.getUniqueId())) {
            methods.stop(p.getUniqueId());
        }
    }


    private class LevelUpRunnable extends BukkitRunnable  {

        private final Player player;
        private final UUID uuid;

        private final ZelLogic zl = Main.getInstance().getZelLogic();

        private LevelUpRunnable(Player player) {
            this.player = player;
            this.uuid = player.getUniqueId();
        }

        @Override
        public void run() {
            PlayerData pData = Main.getInstance().getPlayerData(uuid);

            if (!methods.hasID(uuid)) {
                methods.setID(uuid, super.getTaskId());
            }

            if (pData.getExp() <= 0 && pData.getLevel() < 120) {
                String previousLevel = zl.getColorBracketAndLevel(uuid.toString());
                int level = pData.getLevel();

                pData.setLevel(level + 1);
                pData.setExp(zl.maxXPReq(uuid.toString()));

                if (zl.playerCheck(Bukkit.getPlayer(uuid))) {
                    Player p = Bukkit.getPlayer(uuid);

                    p.sendTitle("§b§lLEVEL UP!",
                            previousLevel + " ➟ " + zl.getColorBracketAndLevel(uuid.toString()),
                            10, 0, 20);

                    p.sendMessage("§b§lPIT LEVEL UP! " + previousLevel + " ➟ " + zl.getColorBracketAndLevel(uuid.toString()));
                }
            }

            player.setLevel(pData.getLevel());

            float percentage = zl.maxXPReq(uuid.toString()) - pData.getExp();

            if (percentage <= 1 && percentage >= 0) {
                if (player.getExp() != (percentage / zl.maxXPReq(uuid.toString())) && pData.getLevel() < 120) {
                    player.setExp(percentage / zl.maxXPReq(uuid.toString()));
                } else if (pData.getLevel() >= 120) {
                    player.setExp(1);
                }
            } else {
                if (percentage > 1.0) {
                    player.setExp(1);
                } else if (percentage < 0.0) {
                    player.setExp(0);
                }
            }
        }
    }
}





