package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitDeathEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.DeathHandler;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.RunTracker;
import me.zelha.thepit.utils.ZelLogic;
import me.zelha.thepit.zelenums.Passives;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static org.bukkit.event.EventPriority.HIGHEST;
import static org.bukkit.event.EventPriority.LOWEST;
import static org.bukkit.event.entity.EntityDamageEvent.DamageModifier.ARMOR;

public class AttackListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final PluginManager manager = Main.getInstance().getServer().getPluginManager();
    private final DeathHandler deathHandler = Main.getInstance().getDeathHandler();
    private final RunTracker runTracker = new RunTracker();
    private final RunTracker runTracker2 = new RunTracker();

    public void startCombatTimer(Player damaged, Player damager) {
        for (UUID uuid : new UUID[] {damaged.getUniqueId(), damager.getUniqueId()}) {
            if (runTracker.hasID(uuid)) runTracker.stop(uuid);

            new BukkitRunnable() {
                @Override
                public void run() {
                    PlayerData pData = Main.getInstance().getPlayerData(uuid);

                    if (pData == null) return;

                    if (!runTracker.hasID(uuid)) {
                        runTracker.setID(uuid, getTaskId());
                        pData.setCombatTimer(15 + (int) (Math.floor(pData.getBounty() / 1000D) * 10));
                        pData.setStatus("fighting");
                    }

                    pData.setCombatTimer(pData.getCombatTimer() - 1);

                    int current = pData.getCombatTimer();

                    if (current <= 0) {
                        pData.setStatus((pData.getBounty() != 0) ? "bountied" : "idling");
                        cancel();
                    }

                    pData.setHideTimer((pData.getBounty() == 0 && current >= 10) || current <= 0);
                }
            }.runTaskTimer(Main.getInstance(), 0, 20);
        }
    }

    @EventHandler(priority = HIGHEST, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();
        Player damaged;
        Player damager;
        double boost = 1;

        if (zl.spawnCheck(damagedEntity.getLocation()) || zl.spawnCheck(damagerEntity.getLocation())) return;
        if (zl.playerCheck(damagedEntity)) damaged = (Player) damagedEntity; else return;

        if (damagerEntity instanceof Arrow && ((Arrow) damagerEntity).getShooter() instanceof Player) {
            damager = (Player) ((Arrow) damagerEntity).getShooter();
        } else if (zl.playerCheck(damagerEntity)) {
            damager = (Player) damagerEntity;
        } else {
            return;
        }

        if (!zl.playerCheck(damager)) return;

        PlayerData damagedData = Main.getInstance().getPlayerData(damaged);
        PlayerData damagerData = Main.getInstance().getPlayerData(damager);

        if (damagedData.getPrestige() == 0) boost -= 0.15;
        if (damagedData.getPassiveTier(Passives.DAMAGE_REDUCTION) > 0) boost -= (damagedData.getPassiveTier(Passives.DAMAGE_REDUCTION) / 100.0);
        if (damagerData.getPrestige() == 0) boost += 0.15;

        if (zl.itemCheck(damager.getInventory().getItemInMainHand()) && damager.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD && damagedData.getBounty() != 0) {
            boost += 0.2;
        }

        if (damagerEntity instanceof Arrow) {
            if (damagerData.getPassiveTier(Passives.BOW_DAMAGE) > 0) boost += ((damagerData.getPassiveTier(Passives.BOW_DAMAGE) * 3) / 100.0);
        } else {
            if (damagerData.getPassiveTier(Passives.MELEE_DAMAGE) > 0) boost += (damagerData.getPassiveTier(Passives.MELEE_DAMAGE) / 100.0);
        }

        startCombatTimer(damaged, damager);

        PitDamageEvent damageEvent = new PitDamageEvent(e, boost);

        if (!damaged.getUniqueId().equals(damager.getUniqueId())) manager.callEvent(damageEvent);

        if (damageEvent.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        e.setDamage(damageEvent.getDamage() * damageEvent.getBoost());
        e.setDamage(ARMOR, e.getDamage(ARMOR) + damageEvent.getFinalDamageModifier());

        if (zl.playerCheck(Bukkit.getPlayer("hazelis"))) {
            Bukkit.getPlayer("hazelis").sendMessage(e.getFinalDamage() + "");
        }

        damagedData.setLastDamager(damager);

        if (runTracker2.hasID(damaged.getUniqueId())) runTracker2.stop(damaged.getUniqueId());

        runTracker2.setID(damaged.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                damagedData.setLastDamager(null);
            }
        }.runTaskLater(Main.getInstance(), 300).getTaskId());

        if (damaged.getHealth() - e.getFinalDamage() > 0) return;

        e.setCancelled(true);

        handleDeathEvents(damaged, damager, false);
    }

    @EventHandler(priority = HIGHEST, ignoreCancelled = true)
    public void onOtherDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
            e.setCancelled(true);
            return;
        }

        if (!zl.playerCheck(e.getEntity())) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) return;

        Player p = (Player) e.getEntity();

        if (p.getHealth() - e.getFinalDamage() > 0) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) return;

        e.setCancelled(true);

        handleDeathEvents(p, Main.getInstance().getPlayerData(p).getLastDamager(), false);
    }

    @EventHandler(priority = LOWEST)
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (runTracker.hasID(e.getPlayer().getUniqueId())) runTracker.stop(e.getPlayer().getUniqueId());
        if (pData.getStatus().equals("idling") || pData.getStatus().equals("bountied")) return;

        handleDeathEvents(p, Main.getInstance().getPlayerData(p).getLastDamager(), true);
    }

    private void handleDeathEvents(Player dead, Player killer, boolean disconnected) {
        PitDeathEvent deathEvent = new PitDeathEvent(dead, disconnected);
        PitKillEvent killEvent = new PitKillEvent(dead, killer, disconnected);

        if (disconnected) Main.getInstance().getPlayerData(dead).setCombatLogged(true);

        manager.callEvent(deathEvent);

        if (killer != null && !dead.getUniqueId().equals(killer.getUniqueId())) manager.callEvent(killEvent);

        if (!deathEvent.isCancelled() && !killEvent.isCancelled()) deathHandler.deathHandler(dead);
    }
}















