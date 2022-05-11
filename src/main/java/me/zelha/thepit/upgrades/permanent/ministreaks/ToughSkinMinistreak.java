package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import org.bukkit.entity.Player;

public class ToughSkinMinistreak extends Ministreak {

    @Override
    public double getDamagedModifier(Player damaged, PitDamageEvent event) {
        return Math.max((int) ((int) Main.getInstance().getPlayerData(damaged).getStreak() / 5D) * -0.03, -0.24);
    }
}
