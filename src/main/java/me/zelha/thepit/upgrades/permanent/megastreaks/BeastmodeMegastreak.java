package me.zelha.thepit.upgrades.permanent.megastreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitKillEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

public class BeastmodeMegastreak extends Megastreak {

    @Override
    public void onTrigger(Player player) {
        if (zl.itemCheck(player.getInventory().getHelmet()) && zl.firstEmptySlot(player.getInventory()) == -1) return;

        zl.itemPlacementHandler(player, EquipmentSlot.HEAD, zl.itemBuilder(
                Material.DIAMOND_HELMET, 1, "§aBeastmode Helmet", Collections.singletonList("§7Special item"), true
        ));

        super.onTrigger(player);
    }

    @Override
    public double getDamagedModifier(Player damaged, PitDamageEvent event) {
        event.setDamage(event.getDamage() + Math.max(0, Math.floor((Main.getInstance().getPlayerData(damaged).getStreak() - 50) / 5) * 0.2));

        return 0;
    }

    @Override
    public double getDamagerModifier(Player damager, PitDamageEvent event) {
        return 0.25;
    }

    @Override
    public void addResourceModifiers(PitKillEvent event) {
        event.addExpModifier(1.5, "Beastmode");
        event.addGoldModifier(1.75, "Beastmode");
    }

    @Override
    public void onDeath(Player player) {
        if (zl.itemCheck(player.getInventory().getHelmet()) && zl.firstEmptySlot(player.getInventory()) == -1) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!zl.playerCheck(player)) return;

                zl.itemPlacementHandler(player, EquipmentSlot.HEAD, zl.itemBuilder(
                        Material.DIAMOND_HELMET, 1, "§aBeastmode Helmet", null, true
                ));
            }
        }.runTaskLater(Main.getInstance(), 1);
    }
}







