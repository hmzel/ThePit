package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class CombatListener implements Listener {

    ZelLogic zl = Main.getInstance().getZelLogic();
    RunMethods methods = Main.getInstance().getRunMethods();

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();

        if (zl.playerCheck(damagedEntity) && zl.playerCheck(damagerEntity)) {
            Player damaged = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();

            if (methods.hasID(damaged.getUniqueId())) {
                methods.stop(damaged.getUniqueId());
            }

            if (methods.hasID(damager.getUniqueId())) {
                methods.stop(damager.getUniqueId());
            }

            new CombatTimerRunnable(damaged.getUniqueId(), methods).runTaskTimer(Main.getInstance(),0, 20);
            new CombatTimerRunnable(damager.getUniqueId(), methods).runTaskTimer(Main.getInstance(),0, 20);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        if(methods.hasID(uuid)) {
            methods.stop(uuid);
        }
    }
}


class CombatTimerRunnable extends BukkitRunnable {

    private final UUID uuid;
    private final RunMethods methods;
    private int hideTimer;

    ZelLogic zl = Main.getInstance().getZelLogic();

    public CombatTimerRunnable(UUID uuid, RunMethods methods) {
        this.uuid = uuid;
        this.methods = methods;
        this.hideTimer = 1;
    }

    public int calculateTimer(PlayerData pData) {
        return 15;
    }

    @Override
    public void run() {
        PlayerData pData = Main.getInstance().getStorage().getPlayerData(uuid.toString());

        if (!methods.hasID(uuid)) {
            methods.setID(uuid, super.getTaskId());
        }

        if (pData.getStatus().equals("idling") || pData.getStatus().equals("bountied")) {
            pData.setCombatTimer(calculateTimer(pData));
            pData.setStatus("fighting");
        }else if (pData.getCombatTimer() > 1) {
            pData.setCombatTimer(pData.getCombatTimer() - 1);
        }else {
            pData.setCombatTimer(pData.getCombatTimer() - 1);

            if (pData.getBounty() != 0) {
                pData.setStatus("bountied");
            }else {
                pData.setStatus("idling");
            }

            cancel();
        }

        if (hideTimer < 10 && !pData.hideTimer()) {
            pData.setHideTimer(true);
        }else if (hideTimer > 10) {
            pData.setHideTimer(pData.getCombatTimer() == 0);
        }

        hideTimer++;
    }
}












