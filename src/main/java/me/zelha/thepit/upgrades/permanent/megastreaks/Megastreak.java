package me.zelha.thepit.upgrades.permanent.megastreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.utils.ZelLogic;
import org.bukkit.entity.Player;

public class Megastreak {

    protected final ZelLogic zl = Main.getInstance().getZelLogic();

    public void onTrigger(Player player) {
    }

    public double getDebuff(Player player) {
        return 1;
    }

    public double getBuff(Player player) {
        return 1;
    }

    public double getEXPModifier(Player player) {
        return 1;
    }

    public double getGoldModifier(Player player) {
        return 1;
    }

    public void onDeath(Player player) {
    }
}
