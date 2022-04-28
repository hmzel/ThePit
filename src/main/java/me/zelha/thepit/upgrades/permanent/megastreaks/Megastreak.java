package me.zelha.thepit.upgrades.permanent.megastreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.ZelLogic;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Megastreak {

    protected final ZelLogic zl = Main.getInstance().getZelLogic();

    public void onTrigger(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);
        StringBuilder builder = new StringBuilder(pData.getMegastreak().getChatName());

        Bukkit.broadcastMessage(
                "§c§lMEGASTREAK! " + zl.getColorBracketAndLevel(player) + " §7" + player.getName() + " activated " +
                pData.getMegastreak().getChatName()
        );

        pData.setDummyStatus(builder.replace(2, builder.length(), pData.getMegastreak().getName()).toString());
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 100, 1);
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
