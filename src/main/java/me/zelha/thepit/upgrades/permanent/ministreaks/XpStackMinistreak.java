package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.entity.Player;

public class XpStackMinistreak extends Ministreak {
    //hardcoded into BaseResourceListener because its active while the ministreak isnt equipped

    @Override
    public void onTrigger(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);

        if (pData.getXpStack() < 8) pData.setXpStack(zl.normalizeDouble(pData.getXpStack() + 0.05, 2));
    }
}
