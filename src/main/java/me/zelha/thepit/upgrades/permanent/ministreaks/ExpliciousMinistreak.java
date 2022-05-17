package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.entity.Player;

public class ExpliciousMinistreak extends Ministreak {//REMEMBER: needs to be added to killrecap

    @Override
    public void onTrigger(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);

        pData.setExp(pData.getExp() - 12);
    }
}
