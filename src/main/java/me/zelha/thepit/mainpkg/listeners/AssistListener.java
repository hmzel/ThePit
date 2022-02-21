package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Passives;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AssistListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final Map<UUID, Map<UUID, Double>> assistMap = new HashMap<>();//this seems cursed i love it

    public int calculateAssistEXP(Player dead, Player assister) {
        double exp = 5;
        int maxEXP = 250;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData killerData = Main.getInstance().getPlayerData(assister);

        //xp bump
        if (deadData.getStreak() > 5) exp += Math.min((int) Math.round(deadData.getStreak()), 25);
        if (deadData.getLevel() > killerData.getLevel()) exp += (int) Math.round((deadData.getLevel() - killerData.getLevel()) / 4.5);
        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) exp *= 0.09;
        //2x event
        if (killerData.getPassiveTier(Passives.XP_BOOST) > 0) exp *= 1 + (killerData.getPassiveTier(Passives.XP_BOOST) / 10.0);
        //royalty
        exp *= assistMap.get(dead.getUniqueId()).get(assister.getUniqueId()) / dead.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        return (int) Math.min(Math.ceil(exp), maxEXP);
    }

    public double calculateAssistGold(Player dead, Player assister) {
        int gold = 10;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData killerData = Main.getInstance().getPlayerData(assister);

        if (dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() > assister.getAttribute(Attribute.GENERIC_ARMOR).getValue()) {
            gold += Math.round((dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() - assister.getAttribute(Attribute.GENERIC_ARMOR).getValue()) / 5);
        }

        if (deadData.getStreak() > 5) gold += Math.min((int) Math.round(deadData.getStreak()), 30);
        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) gold *= 0.09;
        //2x event
        if (killerData.getPassiveTier(Passives.GOLD_BOOST) > 0) gold *= 1 + (killerData.getPassiveTier(Passives.GOLD_BOOST) / 10.0);
        //renown gold boost
        gold *= assistMap.get(dead.getUniqueId()).get(assister.getUniqueId()) / dead.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        //celeb
        //conglomerate

        return gold;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAttackAndDeath(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();
        Player damaged;
        Player damager;

        if (zl.spawnCheck(damagedEntity.getLocation()) || zl.spawnCheck(damagerEntity.getLocation())) return;
        if (zl.playerCheck(damagedEntity)) damaged = (Player) damagedEntity; else return;
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) return;
        if (e.getFinalDamage() <= 0) return;

        if (damagerEntity instanceof Arrow && ((Arrow) damagerEntity).getShooter() instanceof Player && zl.playerCheck((Player) ((Arrow) damagerEntity).getShooter())) {
            damager = (Player) ((Arrow) damagerEntity).getShooter();
        } else if (zl.playerCheck(damagerEntity)) {
            damager = (Player) damagerEntity;
        } else {
            return;
        }

        Map<UUID, Double> currentAssists = assistMap.get(damaged.getUniqueId());

        if (damaged.getHealth() - e.getFinalDamage() > 0) {
            if (!currentAssists.containsKey(damager.getUniqueId())) {
                currentAssists.put(damager.getUniqueId(), e.getFinalDamage());
            } else {
                currentAssists.put(damager.getUniqueId(), currentAssists.get(damager.getUniqueId()) + e.getFinalDamage());
            }

            assistMap.put(damaged.getUniqueId(), currentAssists);
            return;
        }

        //if damaged is killed
        for (UUID uuid : currentAssists.keySet()) {
            Player p = Bukkit.getPlayer(uuid);

            if (p == null || p.getUniqueId() == damager.getUniqueId()) continue;

            PlayerData pData = Main.getInstance().getPlayerData(p);
            double gold = calculateAssistGold(damaged, p);
            int exp = calculateAssistEXP(damaged, p);

            pData.setGold(pData.getGold() + gold);
            pData.setExp(pData.getExp() - exp);
            p.sendMessage("§a§lASSIST! §7" + (int) ((currentAssists.get(uuid) / damaged.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) * 100)
                    + "% on " + zl.getColorBracketAndLevel(damaged.getUniqueId().toString()) + " §7" + damaged.getName() + " §b+" + exp + "XP §6+"
                    + zl.getFancyGoldString(gold) + "g");
        }

        currentAssists.clear();
        assistMap.put(damaged.getUniqueId(), currentAssists);
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        if (!zl.playerCheck(e.getEntity())) return;

        Player p = (Player) e.getEntity();
        double heal = e.getAmount();
        Map<UUID, Double> currentAssists = assistMap.get(p.getUniqueId());

        for (UUID uuid : currentAssists.keySet()) {
            double damage = currentAssists.get(uuid);

            if (damage - heal > 0) {
                currentAssists.put(uuid, damage - heal);
                break;
            } else {
                currentAssists.remove(uuid);
                heal -= damage;
            }
        }

        assistMap.put(p.getUniqueId(), currentAssists);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        assistMap.put(e.getPlayer().getUniqueId(), new HashMap<>());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent e) {
        assistMap.remove(e.getPlayer().getUniqueId());
    }
}
