package me.zelha.thepit.upgrades.permanent.megastreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.utils.ZelLogic;
import org.bukkit.entity.Player;

public class Megastreak {

    protected final ZelLogic zl = Main.getInstance().getZelLogic();

    public void onTrigger(Player player) {
    }

    public double getDebuff(Player player) {
        return 0;
    }

    public void onDeath(Player player) {
    }
}
