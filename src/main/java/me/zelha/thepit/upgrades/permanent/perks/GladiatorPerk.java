package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static me.zelha.thepit.zelenums.Perks.GLADIATOR;

public class GladiatorPerk extends Perk {

    public GladiatorPerk() {
        super(Perks.GLADIATOR);
    }

    @Override
    public double getDamageModifier(Player damager, Player damaged) {
        int nearbyPlayers = 0;

        if (!Main.getInstance().getPlayerData(damaged).hasPerkEquipped(GLADIATOR)) return 0;

        for (Entity entity : damaged.getNearbyEntities(12, 12, 12)) {
            if (zl.playerCheck(entity) && !entity.getUniqueId().equals(damaged.getUniqueId())) {
                nearbyPlayers++;
            }
        }

        if (nearbyPlayers >= 3) return Math.max(-(nearbyPlayers * 0.03), -0.3);

        return 0;
    }
}
