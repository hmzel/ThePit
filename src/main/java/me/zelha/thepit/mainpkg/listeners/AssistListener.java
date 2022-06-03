package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitAssistEvent;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.events.ResourceManager;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.ZelLogic;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class AssistListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final Map<UUID, List<Pair<UUID, Double>>> assistMap = new HashMap<>();

    public void addAssist(Player damaged, Player damager, double damage) {
        assistMap.get(damaged.getUniqueId()).add(Pair.of(damager.getUniqueId(), damage));

        new BukkitRunnable() {
            @Override
            public void run() {
                assistMap.get(damaged.getUniqueId()).remove(Pair.of(damager.getUniqueId(), damage));
            }
        }.runTaskLater(Main.getInstance(), 300);
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

    public double getTotalDamage(Player player) {
        double damage = 0;

        for (Pair<UUID, Double> pair : new ArrayList<>(assistMap.get(player.getUniqueId()))) {
            if (Bukkit.getPlayer(pair.getKey()) != null && !pair.getKey().equals(player.getUniqueId())) {
                damage += pair.getValue();
            }
        }

        return damage;
    }

    public int calculateAssistEXP(Player dead, Player assister, ResourceManager resources) {
        double exp = 0;
        int maxEXP = 250;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData assisterData = Main.getInstance().getPlayerData(assister);

        for (Pair<String, Double> pair : resources.getExpAdditions()) exp += pair.getValue();

        //xp bump
//        if (deadData.getStreak() > 5) exp += Math.min((int) Math.round(deadData.getStreak()), 25);
//        if (deadData.getLevel() > assisterData.getLevel()) exp += (int) Math.round((deadData.getLevel() - assisterData.getLevel()) / 4.5);
//        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) exp *= 0.90;
        //koth
        //2x event
        if (assisterData.getPassiveTier(Passives.XP_BOOST) > 0) exp *= 1 + (assisterData.getPassiveTier(Passives.XP_BOOST) / 10.0);
        //royalty
//        exp *= getAssistMap(dead).get(assister.getUniqueId()) / getTotalDamage(dead);

        for (Pair<String, Double> pair : resources.getExpModifiers()) exp *= pair.getValue();

        //pit day

        return (int) Math.min(Math.ceil(exp), maxEXP);
    }

    public double calculateAssistGold(Player dead, Player assister, ResourceManager resources) {
        double gold = 0;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData assisterData = Main.getInstance().getPlayerData(assister);
        boolean baseGoldModifiersApplied = false;

        for (Pair<String, Double> pair : resources.getGoldAdditions()) {
            gold += pair.getValue();

            if (!baseGoldModifiersApplied) {
                for (Pair<String, Double> pair2 : resources.getBaseGoldModifiers()) gold *= pair2.getValue();

                baseGoldModifiersApplied = true;
            }
        }

        if (dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() > assister.getAttribute(Attribute.GENERIC_ARMOR).getValue()) {
            gold += Math.round((dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() - assister.getAttribute(Attribute.GENERIC_ARMOR).getValue()) / 5);
        }

//        if (deadData.getStreak() > 5) gold += Math.min((int) Math.round(deadData.getStreak()), 30);
//        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) gold *= 0.90;
        //koth
        //2x event
        if (assisterData.getPassiveTier(Passives.GOLD_BOOST) > 0) gold *= 1 + (assisterData.getPassiveTier(Passives.GOLD_BOOST) / 10.0);
        //renown gold boost
//        gold *= getAssistMap(dead).get(assister.getUniqueId()) / getTotalDamage(dead);

        for (Pair<String, Double> pair : resources.getGoldModifiers()) gold *= pair.getValue();

        if (gold > resources.getMaxGold()) gold = resources.getMaxGold();

        for (Pair<String, Double> pair : resources.getSecondaryGoldAdditions()) gold += pair.getValue();

        //celeb
        //pit day
        //conglomerate
        if (assisterData.hasPerkEquipped(Perks.SPAMMER)) gold += 2;
        if (assisterData.hasPerkEquipped(Perks.BOUNTY_HUNTER) && zl.itemCheck(assister.getInventory().getLeggings()) && assister.getInventory().getLeggings().getType() == Material.GOLDEN_LEGGINGS && deadData.getBounty() != 0) {
            gold += deadData.getBounty() * (getAssistMap(dead).get(assister.getUniqueId()) / getTotalDamage(dead));
        }

        return gold;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAttack(PitDamageEvent e) {
        Player damaged = e.getDamaged();
        Player damager = e.getDamager();
        double damage;

        if (damaged.getHealth() - e.getDamage() > 0) damage = e.getDamage(); else damage = damaged.getHealth();

        assistMap.get(damaged.getUniqueId()).add(Pair.of(damager.getUniqueId(), damage));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PitKillEvent e) {
        Player dead = e.getDead();
        Player killer = e.getKiller();

        Map<UUID, Double> sortedAssistsMap = getAssistMap(dead).entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        for (UUID uuid : sortedAssistsMap.keySet()) {
            Player p = Bukkit.getPlayer(uuid);

            if (p == null || p.getUniqueId().equals(dead.getUniqueId()) || p.getUniqueId().equals(killer.getUniqueId())) continue;

            PitAssistEvent assistEvent = new PitAssistEvent(dead, p, getAssistMap(dead).get(uuid) / getTotalDamage(dead));

            Bukkit.getPluginManager().callEvent(assistEvent);

            if (assistEvent.isCancelled()) continue;

            e.addAssistEvent(assistEvent);

            PlayerData pData = Main.getInstance().getPlayerData(p);
            double gold = calculateAssistGold(dead, p, assistEvent);
            int exp = calculateAssistEXP(dead, p, assistEvent);

            pData.setGold(pData.getGold() + gold);
            pData.setExp(pData.getExp() - exp);
            p.spigot().sendMessage(
                    new ComponentBuilder("§a§lASSIST! §7" + (int) Math.round((getAssistMap(dead).get(uuid) / getTotalDamage(dead)) * 100)
                    + "% on " + zl.getColorBracketAndLevel(dead) + " §7" + dead.getName() + " §b+" + exp + "XP §6+"
                    + zl.getFancyGoldString(gold) + "g")
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§eClick to view kill recap!")))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/killrecap " + dead.getUniqueId()))
                    .create()
            );
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1.8F);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (zl.playerCheck(dead)) assistMap.put(dead.getUniqueId(), new ArrayList<>());
            }
        }.runTaskLater(Main.getInstance(), 1);
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        assistMap.put(e.getPlayer().getUniqueId(), new ArrayList<>());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent e) {
        assistMap.remove(e.getPlayer().getUniqueId());
    }
}
