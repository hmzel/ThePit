package me.zelha.thepit.upgrades.permanent.megastreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.events.TrueDamageEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Megastreaks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Material.BEDROCK;

public class HermitMegastreak extends Megastreak implements Listener {

    private final Map<UUID, Integer> bedrockGiveMap = new HashMap<>();

    public HermitMegastreak() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void onTrigger(Player player) {
        permanentEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false, true), true);
        player.getInventory().addItem(zl.itemBuilder(BEDROCK, 32));
        super.onTrigger(player);
    }

    @Override
    public void onEquip(Player player) {
        permanentEffect(player, new PotionEffect(PotionEffectType.SLOW, 100, 0, false, false, true), false);
    }

    @Override
    public double getDebuff(Player player, PitDamageEvent event) {
        return Math.max(0, (Main.getInstance().getPlayerData(player).getStreak() - 50) * 0.003);
    }

    @Override
    public double getEXPModifier(Player player) {
        return Math.max(1, 1 + Math.floor(((Math.min(Main.getInstance().getPlayerData(player).getStreak(), 200) - 50) / 10)) * 0.05);
    }

    @Override
    public double getGoldModifier(Player player) {
        return Math.max(1, 1 + Math.floor(((Math.min(Main.getInstance().getPlayerData(player).getStreak(), 200) - 50) / 10)) * 0.05);
    }

    @EventHandler
    public void onKill(PitKillEvent e) {
        Player p = e.getKiller();
        UUID uuid = p.getUniqueId();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (pData.getMegastreak() != Megastreaks.HERMIT) return;
        if (!pData.isMegaActive()) return;

        bedrockGiveMap.putIfAbsent(uuid, 0);
        bedrockGiveMap.put(uuid, bedrockGiveMap.get(uuid) + 1);

        if (bedrockGiveMap.get(uuid) >= 10) {
            p.getInventory().addItem(zl.itemBuilder(BEDROCK, 16));
            bedrockGiveMap.put(uuid, 0);
        }
    }

    @EventHandler
    public void onTrueDamage(TrueDamageEvent e) {
        PlayerData pData = Main.getInstance().getPlayerData(e.getDamaged());

        if (pData.getMegastreak() != Megastreaks.HERMIT) return;
        if (!pData.isMegaActive()) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        bedrockGiveMap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (Main.getInstance().getPlayerData(e.getPlayer()).getMegastreak() != Megastreaks.HERMIT) return;

        permanentEffect(e.getPlayer(), new PotionEffect(PotionEffectType.SLOW, 100, 0, false, false, true), false);
    }
}











