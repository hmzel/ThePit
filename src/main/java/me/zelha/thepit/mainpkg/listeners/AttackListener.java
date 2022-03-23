package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.upgrades.permanent.PerkListenersAndUtils;
import me.zelha.thepit.zelenums.Passives;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.UUID;

public class AttackListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final PerkListenersAndUtils perkUtils = Main.getInstance().getPerkUtils();
    private final RunMethods runTracker = Main.getInstance().generateRunMethods();

    public void startCombatTimer(Player damaged, Player damager) {
        if (runTracker.hasID(damaged.getUniqueId())) runTracker.stop(damaged.getUniqueId());
        if (runTracker.hasID(damager.getUniqueId())) runTracker.stop(damager.getUniqueId());

        new CombatTimerRunnable(damaged.getUniqueId()).runTaskTimer(Main.getInstance(), 0, 20);
        new CombatTimerRunnable(damager.getUniqueId()).runTaskTimer(Main.getInstance(), 0, 20);
    }

    private double calculateMeleeDamage(Player damaged, Player damager, double originalDamage, @Nullable Arrow arrow) {
        double damageBoost = 1;
        double defenseBoost = 0;
        PlayerData damagedData = Main.getInstance().getPlayerData(damaged);
        PlayerData damagerData = Main.getInstance().getPlayerData(damager);

        if (damagedData.getPrestige() == 0) defenseBoost += 0.15;
        if (damagedData.getPassiveTier(Passives.DAMAGE_REDUCTION) > 0) defenseBoost += (damagedData.getPassiveTier(Passives.DAMAGE_REDUCTION) / 100.0);
        defenseBoost += perkUtils.getPerkDamageReduction(damaged);

        if (arrow != null) {
            if (damagerData.getPassiveTier(Passives.BOW_DAMAGE) > 0) damageBoost += ((damagerData.getPassiveTier(Passives.BOW_DAMAGE) * 3) / 100.0);

            return originalDamage * (damageBoost - defenseBoost);
        }

        if (damagerData.getPrestige() == 0) damageBoost += 0.15;
        if (zl.itemCheck(damager.getInventory().getItemInMainHand()) && damager.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD && damagedData.getBounty() != 0) {
            damageBoost += 0.2;
        }
        if (damagerData.getPassiveTier(Passives.MELEE_DAMAGE) > 0) damageBoost += (damagerData.getPassiveTier(Passives.MELEE_DAMAGE) / 100.0);
        damageBoost += perkUtils.getPerkDamageBoost(damager, damaged);

        return originalDamage * (damageBoost - defenseBoost);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();
        Player damaged;
        Player damager;

        if (zl.spawnCheck(damagedEntity.getLocation()) || zl.spawnCheck(damagerEntity.getLocation())) return;
        if (zl.playerCheck(damagedEntity)) damaged = (Player) damagedEntity; else return;

        if (damagerEntity instanceof Arrow && ((Arrow) damagerEntity).getShooter() instanceof Player) {
            damager = (Player) ((Arrow) damagerEntity).getShooter();
            e.setDamage(calculateMeleeDamage(damaged, damager, e.getDamage(), (Arrow) damagerEntity));
        } else if (zl.playerCheck(damagerEntity)) {
            damager = (Player) damagerEntity;
            e.setDamage(calculateMeleeDamage(damaged, damager, e.getDamage(), null));
        } else {
            return;
        }

        if (runTracker.hasID(damaged.getUniqueId())) runTracker.stop(damaged.getUniqueId());
        if (runTracker.hasID(damager.getUniqueId())) runTracker.stop(damager.getUniqueId());

        new CombatTimerRunnable(damaged.getUniqueId()).runTaskTimer(Main.getInstance(), 0, 20);
        new CombatTimerRunnable(damager.getUniqueId()).runTaskTimer(Main.getInstance(), 0, 20);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAttackActionbar(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();
        Player damaged;
        Player damager;

        if (zl.spawnCheck(damagedEntity.getLocation()) || zl.spawnCheck(damagerEntity.getLocation())) return;
        if (zl.playerCheck(damagedEntity)) damaged = (Player) damagedEntity; else return;

        if (damagerEntity instanceof Arrow && ((Arrow) damagerEntity).getShooter() instanceof Player) {
            damager = (Player) ((Arrow) damagerEntity).getShooter();
        } else if (zl.playerCheck(damagerEntity)) {
            damager = (Player) damagerEntity;
        } else {
            return;
        }

        String bar = "§7" + damaged.getName() + " ";

        if (damaged.getHealth() - e.getFinalDamage() <= 0) {
            damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(bar + "§a§lKILL!"));
            return;
        }

        StringBuilder barBuilder = new StringBuilder();
        StringBuilder barBuilder2 = new StringBuilder();

        int health = (int) damaged.getHealth() / 2;
        int healthAfterDmg = (int) Math.max((health - (e.getFinalDamage() / 2D)), 0);
        int maxHealth = (int) (damaged.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) / 2;

        for (int i = 0; i < maxHealth; i++) barBuilder.append("❤");

        if (damaged.getAbsorptionAmount() > 0) {
            int absorption = (int) ((damaged.getAbsorptionAmount() + e.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION)) / 2);

            for (int i = 0; i < (int) damaged.getAbsorptionAmount() / 2; i++) barBuilder2.append("❤");

            barBuilder2.replace(Math.max(absorption, 0), Math.max(absorption, 0), "§6");
            barBuilder2.replace(0, 0, "§e");
        }

        barBuilder.replace(health, health, "§0");
        barBuilder.replace(healthAfterDmg, healthAfterDmg, "§c");
        barBuilder.replace(0, 0, "§4");
        damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(bar + barBuilder + barBuilder2));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerQuitEvent e) {
        PlayerData pData = Main.getInstance().getPlayerData(e.getPlayer());

        if (!pData.getStatus().equals("bountied") && !pData.getStatus().equals("idling")) {
            pData.setCombatLogged(true);
        }

        if (runTracker.hasID(e.getPlayer().getUniqueId())) runTracker.stop(e.getPlayer().getUniqueId());
    }


    private class CombatTimerRunnable extends BukkitRunnable {

        private final UUID uuid;
        private int hideTimer;
        private boolean reset;

        private CombatTimerRunnable(UUID uuid) {
            this.uuid = uuid;
            this.hideTimer = 1;
            this.reset = true;
        }

        @Override
        public void run() {
            PlayerData pData = Main.getInstance().getPlayerData(uuid);

            if (!runTracker.hasID(uuid)) runTracker.setID(uuid, super.getTaskId());
            if (pData == null) return;

            if (reset) {
                pData.setCombatTimer(15 + (int) (Math.floor(pData.getBounty() / 1000D) * 10));
                pData.setStatus("fighting");
                reset = false;
            }

            if (pData.getCombatTimer() > 1) {
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

            if (pData.getBounty() != 0) {
                pData.setHideTimer(pData.getCombatTimer() == 0);
            } else if (hideTimer < 10 && !pData.hideTimer()) {
                pData.setHideTimer(true);
            } else if (hideTimer >= 10) {
                pData.setHideTimer(pData.getCombatTimer() == 0);
            }
            hideTimer++;
        }
    }
}















