package me.zelha.thepit.mainpkg.runnables;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Passives;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;

public class ParticipationRunnable extends BukkitRunnable {

    private boolean noRedos = true;

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now();

        if (now.getMinute() % 10 == 0 && noRedos) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerData pData = Main.getInstance().getPlayerData(p);

                p.sendMessage("§a§lFREE XP! §7for participation §b+" + (int) ((10 * expMultiplier(pData)) * (1 + (pData.getPassiveTier(Passives.XP_BOOST) * 0.1))) + "XP");
                pData.setExp((int) (pData.getExp() - (10 * expMultiplier(pData)) * (1 + (pData.getPassiveTier(Passives.XP_BOOST) * 0.1))));
            }
            noRedos = false;
        }

        if (now.getMinute() % 10 != 0 && !noRedos) noRedos = true;
    }

    private double expMultiplier(PlayerData pData) {//it feels weird putting this below the @Override but its a rly big method
        switch (pData.getPrestige()) {
            case 0:
                return 1;
            case 1:
                return 1.1;
            case 2:
                return 1.2;
            case 3:
                return 1.3;
            case 4:
                return 1.4;
            case 5:
                return 1.5;
            case 6:
                return 1.75;
            case 7:
                return 2;
            case 8:
                return 2.5;
            case 9:
                return 3;
            case 10:
                return 4;
            case 11:
                return 5;
            case 12:
                return 6;
            case 13:
                return 7;
            case 14:
                return 8;
            case 15:
                return 9;
            case 16:
                return 10;
            case 17:
                return 12;
            case 18:
                return 14;
            case 19:
                return 16;
            case 20:
                return 18;
            case 21:
                return 20;
            case 22:
                return 24;
            case 23:
                return 28;
            case 24:
                return 32;
            case 25:
                return 36;
            case 26:
                return 40;
            case 27:
                return 45;
            case 28:
                return 50;
            case 29:
                return 75;
            case 30:
                return 100;
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
                return 101;
            case 36:
                return 150;
            case 37:
                return 250;
            case 38:
                return 400;
            case 39:
                return 650;
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
                return 1000;
            default:
                return 1313131313;//fun
        }
    }
}
