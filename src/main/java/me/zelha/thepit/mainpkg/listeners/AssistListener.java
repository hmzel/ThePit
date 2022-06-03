package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitAssistEvent;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.ZelLogic;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
        UUID damagerUUID = damager.getUniqueId();
        UUID damagedUUID = damaged.getUniqueId();

        assistMap.get(damaged.getUniqueId()).add(Pair.of(damagerUUID, damage));

        new BukkitRunnable() {
            @Override
            public void run() {
                assistMap.get(damagedUUID).remove(Pair.of(damagerUUID, damage));
            }
        }.runTaskLater(Main.getInstance(), 300);
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttack(PitDamageEvent e) {
        Player damaged = e.getDamaged();
        Player damager = e.getDamager();
        double damage;

        if (damaged.getHealth() - e.getDamage() > 0) damage = e.getDamage(); else damage = damaged.getHealth();

        addAssist(damaged, damager, damage);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PitKillEvent e) {
        Player dead = e.getDead();
        Player killer = e.getKiller();
        Map<UUID, Double> assists = new HashMap<>();

        for (Pair<UUID, Double> pair : assistMap.get(dead.getUniqueId())) {
            if (assists.containsKey(pair.getKey())) {
                assists.put(pair.getKey(), assists.get(pair.getKey()) + pair.getValue());
            } else {
                assists.put(pair.getKey(), pair.getValue());
            }
        }

        Map<UUID, Double> sortedAssists = assists.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        for (Map.Entry<UUID, Double> entry : sortedAssists.entrySet()) {
            Player p = Bukkit.getPlayer(entry.getKey());

            if (p == null || p.getUniqueId().equals(dead.getUniqueId()) || p.getUniqueId().equals(killer.getUniqueId())) continue;

            PitAssistEvent assistEvent = new PitAssistEvent(dead, p, entry.getValue() / getTotalDamage(dead));

            Bukkit.getPluginManager().callEvent(assistEvent);

            if (assistEvent.isCancelled()) continue;

            e.addAssistEvent(assistEvent);

            PlayerData pData = Main.getInstance().getPlayerData(p);
            double gold = assistEvent.calculateGold();
            int exp = assistEvent.calculateEXP();

            pData.setGold(pData.getGold() + gold);
            pData.setExp(pData.getExp() - exp);
            p.spigot().sendMessage(
                    new ComponentBuilder("§a§lASSIST! §7" + (int) Math.round(assistEvent.getPercentage() * 100)
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
