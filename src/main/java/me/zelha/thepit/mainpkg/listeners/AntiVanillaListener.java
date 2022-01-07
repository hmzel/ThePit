package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Material.*;

public class AntiVanillaListener implements Listener {

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }

    @EventHandler
    public void onGrassChange(BlockPhysicsEvent e) {

        if (e.getChangedType() == DIRT) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Material blockType = e.getBlock().getType();

        if (blockType != OBSIDIAN && blockType != COBBLESTONE && blockType != OAK_WOOD) {

            if (!Main.getInstance().blockPriviledges.contains(e.getPlayer())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Material blockType = e.getBlock().getType();

        if (blockType != OBSIDIAN && blockType != COBBLESTONE && blockType != OAK_WOOD) {

            if (!Main.getInstance().blockPriviledges.contains(e.getPlayer())) {
                e.setCancelled(true);
            }
        }

        if (blockType == OBSIDIAN) {
            new BlockGoPoof(e.getBlock(), 120).runTaskTimer(Main.getInstance(), 0, 1);
        }
    }
}


class BlockGoPoof extends BukkitRunnable {

    private final Block block;
    private final int timer;
    private final Material type;
    private int countdown = 0;

    public BlockGoPoof(Block block, int timer) {
        this.block = block;
        this.timer = timer * 20;
        this.type = block.getType();
    }

    @Override
    public void run() {

        if (block.getType() != type) {
            cancel();
        }

        if (countdown >= timer) {
            block.setType(AIR);
            block.getWorld().spawnParticle(Particle.CLOUD, block.getLocation(), 5, 0.5, 0.5, 0.5, 0);
        }
        countdown++;
    }
}


















