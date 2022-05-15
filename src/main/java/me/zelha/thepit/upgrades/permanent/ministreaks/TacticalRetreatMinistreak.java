package me.zelha.thepit.upgrades.permanent.ministreaks;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TacticalRetreatMinistreak extends Ministreak {

    @Override
    public void onTrigger(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 3, false, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 3, false, false, true));
    }
}
