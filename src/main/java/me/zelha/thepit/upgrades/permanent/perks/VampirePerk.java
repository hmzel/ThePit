package me.zelha.thepit.upgrades.permanent.perks;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VampirePerk extends Perk {

    public VampirePerk() {
    }

    @Override
    public void onAttack(Player damager, Player damaged, Arrow arrow) {
        if (arrow != null && arrow.isCritical()){
            zl.addHealth(damager, 3);
        } else {
            zl.addHealth(damager, 1);
        }
    }

    @Override
    public void onKill(Player killer, Player dead) {
        killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 160, 0, false, false));
    }
}
