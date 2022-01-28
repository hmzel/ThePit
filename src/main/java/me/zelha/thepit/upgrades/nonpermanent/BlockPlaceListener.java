package me.zelha.thepit.upgrades.nonpermanent;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Passives;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static org.bukkit.Material.*;

public class BlockPlaceListener implements Listener {

    public final static List<Block> placedBlocks = new ArrayList<>();
    private final List<Material> placeable = new ArrayList<>();

    private List<Material> getPlaceableBlocks() {
        if (!placeable.contains(OBSIDIAN)) placeable.add(OBSIDIAN);
        if (!placeable.contains(COBBLESTONE)) placeable.add(COBBLESTONE);
        if (!placeable.contains(OAK_WOOD)) placeable.add(OAK_WOOD);
     return placeable;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Material blockType = e.getBlock().getType();

        if (!getPlaceableBlocks().contains(blockType)) {
            if (!Main.getInstance().blockPriviledges.contains(e.getPlayer()) || !placedBlocks.contains(e.getBlock())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Material blockType = e.getBlock().getType();
        PlayerData pData = Main.getInstance().getPlayerData(e.getPlayer());

        if (getPlaceableBlocks().contains(blockType)) {
            if (!Main.getInstance().blockPriviledges.contains(e.getPlayer())) {
                e.setCancelled(true);
            }
        }

        if (blockType == OBSIDIAN) {
            new BlockGoPoof(e.getBlock(), e.getBlockReplacedState().getType()).runTaskTimer(Main.getInstance(), 0, (Math.round(120 * (1 + (pData.getPassiveTier(Passives.BUILD_BATTLER) * 0.6)))) * 20);
            placedBlocks.add(e.getBlock());
        }
    }


    private class BlockGoPoof extends BukkitRunnable {

        private final Block block;
        private final Material previousBlock;

        private BlockGoPoof(Block block, Material previousBlock) {
            this.block = block;
            this.previousBlock = previousBlock;
        }

        @Override
        public void run() {

            block.setType(previousBlock);
            block.getWorld().spawnParticle(Particle.CLOUD, block.getLocation(), 5, 0.5, 0.5, 0.5, 0);
            placedBlocks.remove(block);

            cancel();
        }
    }
}


















