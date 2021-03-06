package me.zelha.thepit.upgrades.permanent.megastreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HighlanderMegastreak extends Megastreak {

    @Override
    public void onTrigger(Player player) {
        permanentEffect(player, new PotionEffect(PotionEffectType.SPEED, 100, 0, false, false, true), true);
        super.onTrigger(player);
    }

    @Override
    public double getDamagedModifier(Player damaged, PitDamageEvent event) {
        Player damager = event.getDamager();

        if (!Main.getInstance().getPlayerData(damager).hasPerkEquipped(Perks.BOUNTY_HUNTER)) return 0;

        return Math.max(0, (Main.getInstance().getPlayerData(damaged).getStreak() - 50) * 0.003);
    }

    @Override
    public double getDamagerModifier(Player damager, PitDamageEvent event) {
        Player damaged = event.getDamaged();

        if (Main.getInstance().getPlayerData(damaged).getBounty() == 0) return 0;

        return 0.33;
    }

    @Override
    public void addResourceModifiers(PitKillEvent event) {
        event.addGoldModifier(2.1, "Highlander");
    }

    @Override
    public void onDeath(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);

        pData.setGold(pData.getGold() + pData.getBounty());
        player.sendMessage("§6§lHIGHLANDER! §7Earned §6+" + zl.getFancyNumberString(pData.getBounty()) + "g §7from megastreak!");
    }
}
