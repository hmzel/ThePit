package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.entity.Player;

public class GoldStackMinistreak extends Ministreak {

    @Override
    public void onTrigger(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);

        if (pData.getGoldStack() < 4) pData.setGoldStack(pData.getGoldStack() + 0.1);
    }
}
