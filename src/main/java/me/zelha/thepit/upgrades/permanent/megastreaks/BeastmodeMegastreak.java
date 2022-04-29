package me.zelha.thepit.upgrades.permanent.megastreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
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
    public double getDebuff(Player player, PitDamageEvent event) {
        event.setDamage(event.getDamage() + Math.max(0, ((Main.getInstance().getPlayerData(player).getStreak() - 50) / 5) * 0.1));

        return 0;
    }

    @Override
    public double getBuff(Player player) {
        return 0.25;
    }

    @Override
    public double getEXPModifier(Player player) {
        return 1.5;
    }

    @Override
    public double getGoldModifier(Player player) {
        return 1.75;
    }

    @Override
    public void onDeath(Player player) {
        if (zl.itemCheck(player.getInventory().getHelmet()) && zl.firstEmptySlot(player.getInventory()) == -1) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                zl.itemPlacementHandler(player, EquipmentSlot.HEAD, zl.itemBuilder(
                        Material.DIAMOND_HELMET, 1, "§aBeastmode Helmet", null, true
                ));
            }
        }.runTaskLater(Main.getInstance(), 1);
    }
}







