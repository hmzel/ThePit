package me.zelha.thepit.mainpkg.runnables;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;

public class ParticipationRunnable extends BukkitRunnable {

    private int noRedos = 0;
    private int EXP = 10;

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now();

        if (now.getMinute() % 5 == 0 && noRedos == 0) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerData pData = Main.getInstance().getPlayerData(p);

                p.sendMessage("§a§lFREE XP! §7for participation §b+" + EXP + "XP");
                pData.setExp(pData.getExp() - EXP);
            }
            noRedos++;
        }

        if (now.getMinute() % 5 != 0 && noRedos != 0) noRedos = 0;
    }
}
