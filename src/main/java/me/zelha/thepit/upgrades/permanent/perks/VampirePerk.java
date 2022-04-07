package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.zelenums.Perks;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VampirePerk extends Perk {

    public VampirePerk() {
        super(Perks.VAMPIRE);
    }

    @Override
    public void onAttack(Player damager, Player damaged, Entity damagerEntity) {
        if (damagerEntity instanceof Arrow && ((Arrow) damagerEntity).isCritical()){
            damager.setHealth(Math.min(damager.getHealth() + 3, damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        } else{
            damager.setHealth(Math.min(damager.getHealth() + 1, damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        }
    }

    @Override
    public void onKill(Player killer, Player dead) {
        killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 160, 0, false, false));
    }
}
