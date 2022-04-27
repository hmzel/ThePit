package me.zelha.thepit.upgrades.permanent;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitDeathEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.upgrades.permanent.megastreaks.Megastreak;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KillstreakListener implements Listener {

    @EventHandler
    public void onKill(PitKillEvent e) {
        Player p = e.getKiller();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        Megastreak megaMethods = pData.getMegastreak().getMethods();

        if (pData.getStreak() < pData.getMegastreak().getTrigger()) return;
        if (pData.isMegaActive()) return;
        if (megaMethods == null) return;

        megaMethods.onTrigger(p);
        pData.setMegaActive(true);
    }

    @EventHandler
    public void onDamage(PitDamageEvent e) {
        Player p = e.getDamager();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        Megastreak megaMethods = pData.getMegastreak().getMethods();

        if (pData.getStreak() < pData.getMegastreak().getTrigger()) return;
        if (!pData.isMegaActive()) return;
        if (megaMethods == null) return;

        e.setDamage(e.getDamage() + megaMethods.getDebuff(p));
    }

    @EventHandler
    public void onDeath(PitDeathEvent e) {
        Player p = e.getDead();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (!pData.isMegaActive()) return;
        if (pData.getMegastreak().getMethods() == null) return;

        pData.getMegastreak().getMethods().onDeath(p);
    }
}

























