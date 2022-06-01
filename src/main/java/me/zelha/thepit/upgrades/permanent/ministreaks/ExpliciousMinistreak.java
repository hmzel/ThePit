package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.zelenums.Ministreaks;

public class ExpliciousMinistreak extends Ministreak {

    @Override
    public void addResourceModifiers(PitKillEvent e) {
        if (((int) Main.getInstance().getPlayerData(e.getKiller()).getStreak() + 1) % Ministreaks.EXPLICIOUS.getTrigger() != 0) return;

        e.addExp(12, "Explicious");
    }
}
