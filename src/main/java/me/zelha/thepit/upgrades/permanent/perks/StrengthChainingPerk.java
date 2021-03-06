package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.RunTracker;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StrengthChainingPerk extends Perk {

    private final Map<UUID, Integer> strengthChaining = new HashMap<>();
    private final Map<UUID, Integer> strengthChainingTimer = new HashMap<>();
    private final RunTracker runTracker = new RunTracker();

    public StrengthChainingPerk() {
    }

    public Integer getLevel(Player p) {
        return strengthChaining.get(p.getUniqueId());
    }

    public Integer getTimer(Player p) {
        return strengthChainingTimer.get(p.getUniqueId());
    }

    @Override
    public double getDamageModifier(Player damager, Player damaged) {
        return (strengthChaining.get(damager.getUniqueId()) != null) ? 0.08 * strengthChaining.get(damager.getUniqueId()) : 0;
    }

    @Override
    public void onKill(Player killer, Player dead) {
        UUID killerUUID = killer.getUniqueId();

        if (strengthChaining.get(killerUUID) == null) {
            strengthChaining.put(killerUUID, 1);
        } else if (strengthChaining.get(killerUUID) != 5) {
            strengthChaining.put(killerUUID, strengthChaining.get(killerUUID) + 1);
        }

        if (runTracker.hasID(killerUUID)) runTracker.stop(killerUUID);

        new BukkitRunnable() {
            int timer = 7;

            @Override
            public void run() {
                if (!runTracker.hasID(killerUUID)) runTracker.setID(killerUUID, getTaskId());

                strengthChainingTimer.put(killerUUID, timer);

                if (timer <= 0) {
                    strengthChaining.remove(killerUUID);
                    strengthChainingTimer.remove(killerUUID);
                    cancel();
                }

                timer--;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    @Override
    public void onReset(Player player, PlayerData playerData) {
        strengthChaining.remove(player.getUniqueId());
        strengthChainingTimer.remove(player.getUniqueId());
    }
}
