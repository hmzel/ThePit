package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.entity.Player;

public class KhanateMinistreak extends Ministreak {

    @Override
    public void onTrigger(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);

        pData.setGold(pData.getGold() + 8);
    }

    @Override
    public double getDamagerModifier(Player damager, PitDamageEvent event) {
        PlayerData pData = Main.getInstance().getPlayerData(damager);

        if (Main.getInstance().getPlayerData(event.getDamaged()).getBounty() == 0) {
            return 0;
        }

        return Math.min((int) ((int) pData.getStreak() / 3D) * 0.04, 0.4);
    }
}
