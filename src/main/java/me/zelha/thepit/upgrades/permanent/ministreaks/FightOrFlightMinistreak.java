package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.utils.RunTracker;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FightOrFlightMinistreak extends Ministreak {

    private final RunTracker runTracker = new RunTracker();
    private final Set<UUID> triggered = new HashSet<>();

    @Override
    public void onTrigger(Player player) {
        if (player.getHealth() <= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 140, 0, false, false, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 140, 0, false, false, true));
            return;
        }

        if (runTracker.hasID(player.getUniqueId())) runTracker.stop(player.getUniqueId());

        triggered.add(player.getUniqueId());

        runTracker.setID(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                if (!zl.playerCheck(player)) return;

                triggered.remove(player.getUniqueId());
            }
        }.runTaskLater(Main.getInstance(), 140).getTaskId());
    }

    @Override
    public double getDamagerModifier(Player damager, PitDamageEvent event) {
        if (triggered.contains(damager.getUniqueId())) return 0.2;

        return 0;
    }

    @Override
    public void onReset(Player player) {
        if (runTracker.hasID(player.getUniqueId())) runTracker.stop(player.getUniqueId());

        triggered.remove(player.getUniqueId());
    }
}
