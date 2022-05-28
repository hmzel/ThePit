package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Passives;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BaseResourceListener implements Listener {

    @EventHandler
    public void onKill(PitKillEvent e) {
        PlayerData killerData = Main.getInstance().getPlayerData(e.getKiller());

        if (killerData.getStreak() <= (killerData.getPassiveTier(Passives.EL_GATO) - 1)) {
            e.addExp(5, "El Gato");
            e.addGold(5, "El Gato");
        }
    }
}
