package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.entity.Player;

public class SuperStreakerMinistreak extends Ministreak {//REMEMBER: needs to be added to killrecap

    @Override
    public void onTrigger(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);

        pData.setExp(pData.getExp() - 50);
    }

    @Override
    public double getEXPModifier(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);

        if (pData.getStreak() < 1) return 1;

        return Math.min(((int) ((int) pData.getStreak() / 10) * 0.05) + 1, 1.5);
    }
}
