package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.ExpChangeEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.ZelLogic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ExpChangeListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    @EventHandler
    public void expChangeListener(ExpChangeEvent e) {
        Player p = e.getPlayer();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (pData.getExp() <= 0 && pData.getLevel() < 120) {
            String previousLevel = zl.getColorBracketAndLevel(p);
            int newLevel = pData.getLevel() + 1;
            int exp = pData.getExp();

            while (exp <= 0) {
                if (newLevel != 121) {
                    pData.setLevel(newLevel);
                } else break;

                exp += zl.maxXPReq(p);
                newLevel++;
            }

            pData.setExp(exp);
            p.sendTitle("§b§lLEVEL UP!",
                    previousLevel + " ➟ " + zl.getColorBracketAndLevel(p),
                    10, 40, 10);
            p.sendMessage("§b§lPIT LEVEL UP! " + previousLevel + " ➟ " + zl.getColorBracketAndLevel(p));
        }

        if (pData.getLevel() >= 0) p.setLevel(pData.getLevel());

        float percentage = (zl.maxXPReq(p) - pData.getExp()) / (float) zl.maxXPReq(p);

        if (percentage <= 1 && percentage >= 0) {
            if (pData.getLevel() < 120) {
                p.setExp(percentage);
            } else if (pData.getLevel() >= 120) {
                p.setExp(1);
            }
        } else {
            if (percentage > 1.0) {
                p.setExp(1);
            } else if (percentage < 0.0) {
                p.setExp(0);
            }
        }
    }
}





