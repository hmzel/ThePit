package me.zelha.thepit.upgrades.permanent.ministreaks;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RAndRMinistreak extends Ministreak {

    @Override
    public void onTrigger(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1, false, false, true));
    }
}
