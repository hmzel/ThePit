package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.utils.RunTracker;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CounterStrikeMinistreak extends Ministreak {

    private final RunTracker runTracker = new RunTracker();
    private final Set<UUID> triggered = new HashSet<>();

    @Override
    public void onTrigger(Player player) {
        if (runTracker.hasID(player.getUniqueId())) runTracker.stop(player.getUniqueId());

        triggered.add(player.getUniqueId());

        runTracker.setID(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                if (!zl.playerCheck(player)) return;

                triggered.remove(player.getUniqueId());
            }
        }.runTaskLater(Main.getInstance(), 160).getTaskId());
    }

    @Override
    public double getDamagedModifier(Player damaged, PitDamageEvent event) {
        if (triggered.contains(damaged.getUniqueId())) event.addFinalDamageModifier(-2);

        return 0;
    }

    @Override
    public double getDamagerModifier(Player damager, PitDamageEvent event) {
        if (triggered.contains(damager.getUniqueId())) return 0.15;

        return 0;
    }

    @Override
    public void onReset(Player player) {
        if (runTracker.hasID(player.getUniqueId())) runTracker.stop(player.getUniqueId());

        triggered.remove(player.getUniqueId());
    }
}
