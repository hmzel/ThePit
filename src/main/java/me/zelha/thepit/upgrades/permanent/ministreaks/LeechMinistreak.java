package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LeechMinistreak extends Ministreak {

    private final Set<UUID> triggered = new HashSet<>();

    @Override
    public void onTrigger(Player player) {
        triggered.add(player.getUniqueId());
    }

    @Override
    public double getDamagerModifier(Player damager, PitDamageEvent event) {
        if (triggered.contains(damager.getUniqueId())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    zl.addHealth(damager, 1 + ((event.getDamage() * event.getBoost()) * 0.2));
                }
            }.runTaskLater(Main.getInstance(), 1);

            triggered.remove(damager.getUniqueId());
        }

        return 1;
    }
}
