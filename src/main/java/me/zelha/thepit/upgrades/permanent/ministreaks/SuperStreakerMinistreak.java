package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Ministreaks;

public class SuperStreakerMinistreak extends Ministreak {//REMEMBER: needs to be added to killrecap

    @Override
    public void addResourceModifiers(PitKillEvent e) {
        PlayerData pData = Main.getInstance().getPlayerData(e.getKiller());

        if (pData.getStreak() < 1) return;

        if (((int) pData.getStreak() + 1) % Ministreaks.SUPER_STREAKER.getTrigger() == 0) {
            e.addExp(50, "Super Streaker");
        }

        double modifier = Math.min(((int) ((int) pData.getStreak() / 10.0) * 0.05) + 1, 1.5);

        if (modifier <= 1) return;

        e.addExpModifier(modifier, "Super Streaker");
    }
}
