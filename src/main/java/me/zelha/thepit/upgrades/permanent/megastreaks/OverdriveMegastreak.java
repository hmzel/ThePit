package me.zelha.thepit.upgrades.permanent.megastreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OverdriveMegastreak extends Megastreak {

    @Override
    public void onTrigger(Player player) {
        permanentEffect(player, new PotionEffect(PotionEffectType.SPEED, 100, 0, false, false, true), true);
        super.onTrigger(player);
    }

    @Override
    public double getDamagedModifier(Player damaged, PitDamageEvent event) {
        double damage = Math.floor((Main.getInstance().getPlayerData(damaged).getStreak() - 50) / 5) * 0.2;

        if (damage > 0D) zl.trueDamage(damaged, null, damage, "§cOverdrive");
        return 0;
    }

    @Override
    public double getEXPModifier(Player player) {
        return 2;
    }

    @Override
    public double getGoldModifier(Player player) {
        return 1.5;
    }

    @Override
    public void onDeath(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);

        pData.setExp(pData.getExp() - 4000);
    }
}
