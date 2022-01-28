package me.zelha.thepit.mainpkg.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import static org.bukkit.Material.GRASS_BLOCK;

public class AntiVanillaListener implements Listener {

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }

    @EventHandler
    public void onGrassChange(BlockPhysicsEvent e) {

        if (e.getSourceBlock().getType() == GRASS_BLOCK) {
            e.setCancelled(true);
        }
    }
}
