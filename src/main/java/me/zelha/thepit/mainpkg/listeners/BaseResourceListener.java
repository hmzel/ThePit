package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Passives;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static me.zelha.thepit.zelenums.Perks.STREAKER;

public class BaseResourceListener implements Listener {

    @EventHandler
    public void onKill(PitKillEvent e) {
        PlayerData killerData = Main.getInstance().getPlayerData(e.getKiller());
        double streakModifier = 0;

        if (killerData.getStreak() <= (killerData.getPassiveTier(Passives.EL_GATO) - 1)) {
            e.addExp(5, "El Gato");
            e.addGold(5, "El Gato");
        }

        if (killerData.getStreak() == 4) {
            streakModifier = 3;
        } else if (killerData.getStreak() >= 5 && killerData.getStreak() < 20) {
            streakModifier = 5;
        } else if (killerData.getStreak() < 100 && killerData.getStreak() >= 20) {
            streakModifier = Math.floor(killerData.getStreak() / 10.0D) * 3;
        } else if (killerData.getStreak() >= 100) {
            streakModifier = 30;
        }

        if (killerData.getStreak() >= 4 && killerData.hasPerkEquipped(STREAKER)) {
            e.addExp((int) (streakModifier * 3), "Killer on streak (Streaker Perk)");
        } else if (killerData.getStreak() >= 4) {
            e.addExp((int) streakModifier, "Killer on streak");
        }
    }

    //using two eventhandlers to mimic behavior in pit, i dont know why its actually in this order in regular pit but i like being accurate
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKill2(PitKillEvent e) {
        PlayerData killerData = Main.getInstance().getPlayerData(e.getKiller());
        PlayerData deadData = Main.getInstance().getPlayerData(e.getDead());

        if (deadData.getStreak() > 5) {
            e.addExp((int) Math.min(Math.round(deadData.getStreak()), 25), "Streak Shutdown");
            e.addGold(Math.min((int) Math.round(deadData.getStreak()), 30), "Streak Shutdown");
        }

        if (killerData.getStreak() <= 3 && (killerData.getLevel() <= 30 || killerData.getPrestige() == 0)) {
            e.addExp(4, "First 3 kills");
            e.addGold(4, "First 3 kills");
        }
    }
}

























