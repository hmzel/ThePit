package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Passives;
import me.zelha.thepit.zelenums.Perks;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class AssistListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final Map<UUID, List<Pair<UUID, Double>>> assistMap = new HashMap<>();

    public void addAssist(Player damaged, Player damager, double damage) {
        assistMap.get(damaged.getUniqueId()).add(Pair.of(damager.getUniqueId(), damage));
    }

    public Map<UUID, Double> getAssistMap(Player player) {
        Map<UUID, Double> map = new HashMap<>();

        for (Pair<UUID, Double> pair : assistMap.get(player.getUniqueId())) {
            if (map.containsKey(pair.getKey())) {
                map.put(pair.getKey(), map.get(pair.getKey()) + pair.getValue());
            } else {
                map.put(pair.getKey(), pair.getValue());
            }
        }

        return map;
    }

    public Player getLastDamager(Player player) {
        List<Pair<UUID, Double>> list = new ArrayList<>(assistMap.get(player.getUniqueId()));

        Collections.reverse(list);

        for (Pair<UUID, Double> pair : list) {
            if (Bukkit.getPlayer(pair.getKey()) != null && !pair.getKey().equals(player.getUniqueId())) {
                return Bukkit.getPlayer(pair.getKey());
            }
        }

        return null;
    }

    public int calculateAssistEXP(Player dead, Player assister) {
        double exp = 5;
        int maxEXP = 250;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData assisterData = Main.getInstance().getPlayerData(assister);

        //xp bump
        if (deadData.getStreak() > 5) exp += Math.min((int) Math.round(deadData.getStreak()), 25);
        if (deadData.getLevel() > assisterData.getLevel()) exp += (int) Math.round((deadData.getLevel() - assisterData.getLevel()) / 4.5);
        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) exp *= 0.91;
        //koth
        //2x event
        if (assisterData.getPassiveTier(Passives.XP_BOOST) > 0) exp *= 1 + (assisterData.getPassiveTier(Passives.XP_BOOST) / 10.0);
        //royalty
        exp *= getAssistMap(dead).get(assister.getUniqueId()) / dead.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        //pit day

        return (int) Math.min(Math.ceil(exp), maxEXP);
    }

    public double calculateAssistGold(Player dead, Player assister) {
        double gold = 10;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData assisterData = Main.getInstance().getPlayerData(assister);

        if (dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() > assister.getAttribute(Attribute.GENERIC_ARMOR).getValue()) {
            gold += Math.round((dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() - assister.getAttribute(Attribute.GENERIC_ARMOR).getValue()) / 5);
        }

        if (deadData.getStreak() > 5) gold += Math.min((int) Math.round(deadData.getStreak()), 30);
        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) gold *= 0.91;
        //koth
        //2x event
        if (assisterData.getPassiveTier(Passives.GOLD_BOOST) > 0) gold *= 1 + (assisterData.getPassiveTier(Passives.GOLD_BOOST) / 10.0);
        //renown gold boost
        gold *= getAssistMap(dead).get(assister.getUniqueId()) / dead.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        //celeb
        //pit day
        //conglomerate
        if (assisterData.hasPerkEquipped(Perks.SPAMMER)) gold += 2;
        if (assisterData.hasPerkEquipped(Perks.BOUNTY_HUNTER) && zl.itemCheck(assister.getInventory().getLeggings()) && assister.getInventory().getLeggings().getType() == Material.GOLDEN_LEGGINGS && deadData.getBounty() != 0) {
            gold += deadData.getBounty() * (getAssistMap(dead).get(assister.getUniqueId()) / dead.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        }

        return gold;
    }

    public void deathMethod(Player player) {
        Player killer;

        if (getLastDamager(player) != null) killer = getLastDamager(player); else return;

        for (UUID uuid : getAssistMap(player).keySet()) {
            Player p = Bukkit.getPlayer(uuid);

            if (p == null || p.getUniqueId().equals(killer.getUniqueId())) continue;

            PlayerData pData = Main.getInstance().getPlayerData(p);
            double gold = calculateAssistGold(player, p);
            int exp = calculateAssistEXP(player, p);

            pData.setGold(pData.getGold() + gold);
            pData.setExp(pData.getExp() - exp);
            p.spigot().sendMessage(new ComponentBuilder("§a§lASSIST! §7" + (int) (Double.parseDouble(BigDecimal.valueOf(getAssistMap(player).get(uuid) / player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()).setScale(2, RoundingMode.HALF_EVEN).toString()) * 100)
                    + "% on " + zl.getColorBracketAndLevel(player.getUniqueId().toString()) + " §7" + player.getName() + " §b+" + exp + "XP §6+"
                    + zl.getFancyGoldString(gold) + "g")
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§eClick to view kill recap!")))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/killrecap " + player.getUniqueId()))
                    .create());
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1.8F);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                assistMap.put(player.getUniqueId(), new ArrayList<>());
            }
        }.runTaskLater(Main.getInstance(), 1);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();
        Player damaged;
        Player damager;
        double damage;

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

        if (damaged.getUniqueId().equals(damager.getUniqueId())) return;

        if (damaged.getHealth() - e.getFinalDamage() > 0) damage = e.getFinalDamage(); else damage = damaged.getHealth();

        assistMap.get(damaged.getUniqueId()).add(Pair.of(damager.getUniqueId(), damage));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(EntityDamageEvent e) {
        Player damaged;

        if (zl.playerCheck(e.getEntity())) damaged = (Player) e.getEntity(); else return;
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) return;
        if (damaged.getHealth() - e.getFinalDamage() > 0) return;

        deathMethod(damaged);
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        if (!zl.playerCheck(e.getEntity())) return;

        Player p = (Player) e.getEntity();
        double heal = e.getAmount();

        for (Pair<UUID, Double> pair : new ArrayList<>(assistMap.get(p.getUniqueId()))) {
            double damage = pair.getValue();

            if (damage - heal > 0) {
                assistMap.get(p.getUniqueId()).set(assistMap.get(p.getUniqueId()).indexOf(pair), Pair.of(pair.getKey(), damage - heal));
                return;
            } else {
                assistMap.get(p.getUniqueId()).remove(pair);
                heal -= damage;
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        assistMap.put(e.getPlayer().getUniqueId(), new ArrayList<>());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent e) {
        if (Main.getInstance().getPlayerData(e.getPlayer()).getCombatLogged()) deathMethod(e.getPlayer());

        assistMap.remove(e.getPlayer().getUniqueId());
    }
}
