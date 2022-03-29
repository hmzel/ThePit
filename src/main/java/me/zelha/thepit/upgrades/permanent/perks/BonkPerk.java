package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.zelha.thepit.zelenums.Perks.BONK;

public class BonkPerk extends Perk implements Listener {

    private final Map<UUID, Set<UUID>> bonkMap = new HashMap<>();

    public BonkPerk() {
        super(BONK);
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public boolean canBonk(Player damaged, Player damager) {
        return Main.getInstance().getPlayerData(damaged).hasPerkEquipped(BONK)
               && !bonkMap.get(damaged.getUniqueId()).contains(damager.getUniqueId());
    }

    @Override
    public void onAttacked(Player damager, Player damaged) {
        UUID damagerUUID = damager.getUniqueId();
        UUID damagedUUID = damaged.getUniqueId();

        for (Entity entity : damaged.getNearbyEntities(32, 32, 32)) {
            if (!zl.playerCheck(entity) || entity.getUniqueId().equals(damagedUUID)) continue;

            ((Player) entity).spawnParticle(Particle.EXPLOSION_LARGE, damaged.getLocation(), 1, 0, 0, 0, 0);
        }

        bonkMap.get(damagedUUID).add(damagerUUID);
        damaged.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 1, false, false, true));
        damaged.setInvulnerable(true);

        new BukkitRunnable() {
            int runs = 0;

            @Override
            public void run() {
                if (runs == 0) damaged.setInvulnerable(false);

                if (bonkMap.get(damagedUUID) == null || !bonkMap.get(damagedUUID).contains(damagerUUID)) {
                    cancel();
                    return;
                }

                if (runs == 30) {
                    if (bonkMap.get(damagedUUID) != null) bonkMap.get(damagedUUID).remove(damagerUUID);
                    cancel();
                }

                runs++;
            }
        }.runTaskTimer(Main.getInstance(), 10, 10);
    }

    @Override
    public void onReset(Player player, PlayerData playerData) {
        if (bonkMap.get(player.getUniqueId()) != null) bonkMap.get(player.getUniqueId()).clear();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        bonkMap.put(e.getPlayer().getUniqueId(), new HashSet<>());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerQuitEvent e) {
        bonkMap.remove(e.getPlayer().getUniqueId());
    }
}
