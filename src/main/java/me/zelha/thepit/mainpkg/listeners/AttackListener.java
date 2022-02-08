package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.upgrades.permanent.perks.PerkListenersAndUtils;
import org.bukkit.entity.Arrow;
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
    private final PerkListenersAndUtils perkUtils = Main.getInstance().getPerkUtils();
    private final RunMethods methods = Main.getInstance().generateRunMethods();

    private double calculateAttackDamage(Player damaged, Player damager, double originalDamage) {
        double damageBoost = 1;
        double defenseBoost = 0;
        PlayerData damagedData = Main.getInstance().getPlayerData(damaged);

        if (damagedData.getPrestige() == 0) defenseBoost+= 0.15;
        damageBoost += perkUtils.getPerkDamageBoost(damager);

        return originalDamage * (damageBoost - defenseBoost);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();
        Player damaged;
        Player damager;

        if (spawnUtils.spawnCheck(damagedEntity.getLocation()) || spawnUtils.spawnCheck(damagerEntity.getLocation())) return;
        if (zl.playerCheck(damagedEntity)) damaged = (Player) damagedEntity; else return;

        if (damagerEntity instanceof Arrow && ((Arrow) damagerEntity).getShooter() instanceof Player) {
            damager = (Player) ((Arrow) damagerEntity).getShooter();
        } else if (zl.playerCheck(damagerEntity)) {
            damager = (Player) damagerEntity;
        } else {
            return;
        }

        e.setDamage(calculateAttackDamage(damaged, damager, e.getDamage()));

        if (methods.hasID(damaged.getUniqueId())) {
            methods.stop(damaged.getUniqueId());
        }

        if (methods.hasID(damager.getUniqueId())) {
            methods.stop(damager.getUniqueId());
        }

        new CombatTimerRunnable(damaged.getUniqueId()).runTaskTimer(Main.getInstance(), 0, 20);
        new CombatTimerRunnable(damager.getUniqueId()).runTaskTimer(Main.getInstance(), 0, 20);
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















