package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class AttackListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final SpawnListener spawnUtils = Main.getInstance().getSpawnListener();
    private final RunMethods methods = Main.getInstance().generateRunMethods();

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();

        if (spawnUtils.spawnCheck(damagedEntity.getLocation()) || spawnUtils.spawnCheck(damagerEntity.getLocation())) {
            return;
        }

        if (zl.playerCheck(damagedEntity) && zl.playerCheck(damagerEntity)) {
            Player damaged = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();

            if (methods.hasID(damaged.getUniqueId())) {
                methods.stop(damaged.getUniqueId());
            }

            if (methods.hasID(damager.getUniqueId())) {
                methods.stop(damager.getUniqueId());
            }

            new CombatTimerRunnable(damaged.getUniqueId()).runTaskTimer(Main.getInstance(),0, 20);
            new CombatTimerRunnable(damager.getUniqueId()).runTaskTimer(Main.getInstance(),0, 20);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        if (methods.hasID(uuid)) {
            methods.stop(uuid);
        }
    }


    private class CombatTimerRunnable extends BukkitRunnable {

        private final UUID uuid;
        private int hideTimer;

        private CombatTimerRunnable(UUID uuid) {
            this.uuid = uuid;
            this.hideTimer = 1;
        }

        private int calculateTimer(PlayerData pData) {
            return 15 + (int) Math.floor(pData.getBounty() / 100);
        }

        @Override
        public void run() {
            PlayerData pData = Main.getInstance().getPlayerData(uuid);

            if (!methods.hasID(uuid)) {
                methods.setID(uuid, super.getTaskId());
            }

            if (pData.getStatus().equals("idling") || pData.getStatus().equals("bountied")) {
                pData.setCombatTimer(calculateTimer(pData));
                pData.setStatus("fighting");
            } else if (pData.getCombatTimer() > 1) {
                pData.setCombatTimer(pData.getCombatTimer() - 1);
            } else {
                pData.setCombatTimer(pData.getCombatTimer() - 1);

                if (pData.getBounty() != 0) {
                    pData.setStatus("bountied");
                } else {
                    pData.setStatus("idling");
                }

                cancel();
            }

            if (hideTimer < 10 && !pData.hideTimer()) {
                pData.setHideTimer(true);
            } else if (hideTimer > 10) {
                pData.setHideTimer(pData.getCombatTimer() == 0);
            }

            hideTimer++;
        }
    }
}















