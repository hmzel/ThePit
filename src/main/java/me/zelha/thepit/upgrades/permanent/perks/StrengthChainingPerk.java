package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.zelha.thepit.zelenums.Perks.STRENGTH_CHAINING;

public class StrengthChainingPerk extends AbstractPerk implements Listener {

    private final Map<UUID, Integer> strengthChaining = new HashMap<>();
    private final Map<UUID, Integer> strengthChainingTimer = new HashMap<>();
    private final RunMethods runTracker = Main.getInstance().generateRunMethods();

    public StrengthChainingPerk() {
        super(STRENGTH_CHAINING);
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public Integer getStrengthChainingLevel(Player p) {
        return strengthChaining.get(p.getUniqueId());
    }

    public Integer getStrengthChainingTimer(Player p) {
        return strengthChainingTimer.get(p.getUniqueId());
    }

    @Override
    public double getDamageModifier(Player player) {
        return (strengthChaining.get(player.getUniqueId()) != null) ? 0.08 * strengthChaining.get(player.getUniqueId()) : 0;
    }

    @Override
    public void onKill(Player killer, Player dead) {
        PlayerData killerData = Main.getInstance().getPlayerData(killer);
        UUID killerUUID = killer.getUniqueId();

        if (!killerData.hasPerkEquipped(STRENGTH_CHAINING)) return;

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
