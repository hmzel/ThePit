package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.utils.ZelLogic;
import org.bukkit.entity.Player;

public class Ministreak {

    protected final ZelLogic zl = Main.getInstance().getZelLogic();

    public void onTrigger(Player player) {
    }

    public double getDamagedModifier(Player damaged, PitDamageEvent event) {
        return 0;
    }

    public double getDamagerModifier(Player damager, PitDamageEvent event) {
        return 0;
    }

    public double getEXPModifier(Player player) {
        return 1;
    }

    public double getGoldModifier(Player player) {
        return 1;
    }

    public void onReset(Player player) {
    }
}
