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
    private final Map<UUID, Map<UUID, Double>> assistMap = new HashMap<>();

    public void addAssist(Player damaged, Player damager, double damage) {
        UUID damagerUUID = damager.getUniqueId();

        Map<UUID, Double> assists = assistMap.get(damaged.getUniqueId());

        assists.putIfAbsent(damagerUUID, 0.0);
        assists.put(damagerUUID, assists.get(damagerUUID) + damage);

        new BukkitRunnable() {
            @Override
            public void run() {
                assists.remove(damagerUUID);
            }
        }.runTaskLater(Main.getInstance(), 300);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttack(PitDamageEvent e) {
        Player damaged = e.getDamaged();
        Player damager = e.getDamager();
        double damage;

        if (damaged.getHealth() - e.getFinalDamage() > 0) damage = e.getFinalDamage(); else damage = damaged.getHealth();

        addAssist(damaged, damager, damage);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PitKillEvent e) {
        Player dead = e.getDead();
        Player killer = e.getKiller();
        double totalDamage = 0;
        Map<UUID, Double> sortedAssists = assistMap.get(dead.getUniqueId()).entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        for (Map.Entry<UUID, Double> entry : sortedAssists.entrySet()) {
            if (Bukkit.getPlayer(entry.getKey()) != null && !entry.getKey().equals(dead.getUniqueId())) {
                totalDamage += entry.getValue();
            }
        }

        for (Map.Entry<UUID, Double> entry : sortedAssists.entrySet()) {
            Player p = Bukkit.getPlayer(entry.getKey());

            if (p == null) continue;
            if (p.getUniqueId().equals(dead.getUniqueId())) continue;
            if (p.getUniqueId().equals(killer.getUniqueId())) continue;

            PitAssistEvent assistEvent = new PitAssistEvent(dead, p, entry.getValue() / totalDamage);

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

        assistMap.put(dead.getUniqueId(), new HashMap<>());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        assistMap.put(e.getPlayer().getUniqueId(), new HashMap<>());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent e) {
        assistMap.remove(e.getPlayer().getUniqueId());
    }
}
