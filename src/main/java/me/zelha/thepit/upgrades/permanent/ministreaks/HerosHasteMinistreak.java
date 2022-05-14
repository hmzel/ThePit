package me.zelha.thepit.upgrades.permanent.ministreaks;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HerosHasteMinistreak extends Ministreak {

    @Override
    public void onTrigger(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, false, true));
    }
}
