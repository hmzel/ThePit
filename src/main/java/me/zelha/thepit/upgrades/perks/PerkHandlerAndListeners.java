package me.zelha.thepit.upgrades.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PerkHandlerAndListeners implements Listener {

    private PlayerData pData(Player player) {return Main.getInstance().getPlayerData(player);}
    private ZelLogic zl = Main.getInstance().getZelLogic();

    @EventHandler
    public void OnAttack(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();

        if (zl.playerCheck(damagedEntity) && zl.playerCheck(damagerEntity)) {
            Player damaged = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();
            double finalDMG = e.getFinalDamage();
            double currentHP = damaged.getHealth();

            //on attack

            //on kill
            if () {

            }
        }



    }
}










