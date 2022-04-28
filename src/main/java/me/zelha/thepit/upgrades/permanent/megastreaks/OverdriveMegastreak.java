package me.zelha.thepit.upgrades.permanent.megastreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class OverdriveMegastreak extends Megastreak {

    @Override
    public void onTrigger(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!Main.getInstance().getPlayerData(player).isMegaActive()) {
                    cancel();
                    return;
                }

                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0, false, false, true));
            }
        }.runTaskTimer(Main.getInstance(), 0, 80);

        super.onTrigger(player);
    }

    @Override
    public double getDebuff(Player player) {
        double damage = ((Main.getInstance().getPlayerData(player).getStreak() - 50) / 5) * 0.1;

        if (damage > 0D) zl.trueDamage(player, null, damage, "§cOverdrive");
        return 1;
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
