package me.zelha.thepit.upgrades.permanent.ministreaks;

import org.bukkit.entity.Player;

public class SpongesteveMinistreak extends Ministreak {

    @Override
    public void onTrigger(Player player) {
        player.setAbsorptionAmount(player.getAbsorptionAmount() + 30);
    }
}
