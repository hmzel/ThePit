package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Passives;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Material.*;

public class AntiVanillaListener implements Listener {

    public final static List<Block> placedBlocks = new ArrayList<>();

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
        PlayerData pData = Main.getInstance().getPlayerData(e.getPlayer());

        if (blockType != OBSIDIAN && blockType != COBBLESTONE && blockType != OAK_WOOD) {

            if (!Main.getInstance().blockPriviledges.contains(e.getPlayer())) {
                e.setCancelled(true);
            }
        }

        if (blockType == OBSIDIAN) {
            new BlockGoPoof(e.getBlock(), e.getBlockReplacedState().getType(), (int) Math.round(120 * (1 + (pData.getPassiveTier(Passives.BUILD_BATTLER) * 0.6)))).runTaskTimer(Main.getInstance(), 0, 1);
            placedBlocks.add(e.getBlock());
        }
    }


    private class BlockGoPoof extends BukkitRunnable {

        private final Block block;
        private final Material previousBlock;
        private final int timer;
        private int countdown = 0;

        private BlockGoPoof(Block block, Material previousBlock, int timer) {
            this.block = block;
            this.previousBlock = previousBlock;
            this.timer = timer * 20;
        }

        @Override
        public void run() {

            if (countdown >= timer) {
                block.setType(previousBlock);
                block.getWorld().spawnParticle(Particle.CLOUD, block.getLocation(), 5, 0.5, 0.5, 0.5, 0);
                placedBlocks.remove(block);
                cancel();
            }
            countdown++;
        }
    }
}


















