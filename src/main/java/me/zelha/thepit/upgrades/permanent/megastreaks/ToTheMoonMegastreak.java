package me.zelha.thepit.upgrades.permanent.megastreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitDeathEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.mainpkg.listeners.KillListener;
import me.zelha.thepit.zelenums.Megastreaks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ToTheMoonMegastreak extends Megastreak implements Listener {

    private final KillListener killUtils = Main.getInstance().getKillUtils();
    private final Map<UUID, Integer> storedEXPMap = new HashMap<>();

    public ToTheMoonMegastreak() {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public Integer getStoredEXP(Player player) {
        return storedEXPMap.get(player.getUniqueId());
    }

    public double getPercentage(Player player) {
        return Math.min(1, ((Math.floor(Main.getInstance().getPlayerData(player).getStreak()) - 100) * 0.005));
    }

    @Override
    public double getDamagedModifier(Player damaged, PitDamageEvent event) {
        PlayerData pData = Main.getInstance().getPlayerData(damaged);

        if (pData.getStreak() >= 220) {
            zl.trueDamage(damaged, null, Math.floor((Math.floor(pData.getStreak()) - 200) / 20) * 0.2, "§bTo the Moon", false);
        }

        return Math.max(0, Math.floor((Math.floor(pData.getStreak()) - 100) / 20) * 0.1);
    }

    @Override
    public double getEXPModifier(Player player) {
        return 1.2;
    }

    @Override
    public void onDeath(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);
        String percentage = getPercentage(player) + "";

        if (getPercentage(player) >= 1) percentage = "1";

        pData.setExp(pData.getExp() - (int) Math.ceil(getStoredEXP(player) * getPercentage(player)));

        player.sendMessage(
                "§b§lTO THE MOON! §7Earned §b+" + zl.getFancyNumberString((int) Math.ceil(getStoredEXP(player) * getPercentage(player))) +
                " XP §7from megastreak (" + "§b" + percentage + "x §7multiplier)"
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(PitKillEvent e) {
        Player p = e.getKiller();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (!pData.isMegaActive()) return;
        if (pData.getMegastreak() != Megastreaks.TO_THE_MOON) return;

        storedEXPMap.putIfAbsent(p.getUniqueId(), 0);
        storedEXPMap.put(p.getUniqueId(), storedEXPMap.get(p.getUniqueId()) + killUtils.calculateEXP(e.getDead(), p, e));
    }

    @EventHandler
    public void onPlayerDeath(PitDeathEvent e) {
        storedEXPMap.remove(e.getDead().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        storedEXPMap.remove(e.getPlayer().getUniqueId());
    }
}
