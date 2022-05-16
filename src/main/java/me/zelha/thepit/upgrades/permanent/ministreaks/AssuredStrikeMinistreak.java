package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.events.PitDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AssuredStrikeMinistreak extends Ministreak {

    private final Set<UUID> triggered = new HashSet<>();

    @Override
    public void onTrigger(Player player) {
        triggered.add(player.getUniqueId());
    }

    @Override
    public double getDamagerModifier(Player damager, PitDamageEvent event) {
        if (event.getArrow() != null) return 0;

        if (triggered.contains(damager.getUniqueId())) {
            triggered.remove(damager.getUniqueId());
            damager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 0, false, false, true));
            return 0.35;
        }

        return 0;
    }

    @Override
    public void onReset(Player player) {
        triggered.remove(player.getUniqueId());
    }
}
