package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Passives;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.Material.*;

public class BlockListener implements Listener {
    //note: block placement prevention in spawn is handled in SpawnListener

    public final static List<Block> placedBlocks = new ArrayList<>();
    private final Material[] placeable = {OBSIDIAN, COBBLESTONE, OAK_WOOD};

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!placedBlocks.contains(e.getBlock()) && !Main.getInstance().blockPriviledges.contains(e.getPlayer())) e.setCancelled(true);

        e.setDropItems(false);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Material blockType = e.getBlock().getType();
        Location blockLocation = e.getBlock().getLocation();
        PlayerData pData = Main.getInstance().getPlayerData(e.getPlayer());

        if (!Arrays.asList(placeable).contains(blockType) && !Main.getInstance().blockPriviledges.contains(e.getPlayer())) {
            e.setCancelled(true);
        }

        if (blockLocation.distance(new Location(blockLocation.getWorld(), 0, blockLocation.getY(), 0)) < 9
           && !Main.getInstance().blockPriviledges.contains(e.getPlayer())) {
            e.setCancelled(true);
            return;
        }

        if (blockType == OBSIDIAN) {
            new BlockGoPoof(e.getBlock(), e.getBlockReplacedState().getType()).runTaskLater(Main.getInstance(), (Math.round(2400 * (1 + (pData.getPassiveTier(Passives.BUILD_BATTLER) * 0.6)))));
            placedBlocks.add(e.getBlock());
        } else if (blockType == COBBLESTONE) {
            new BlockGoPoof(e.getBlock(), e.getBlockReplacedState().getType()).runTaskLater(Main.getInstance(), (Math.round(300 * (1 + (pData.getPassiveTier(Passives.BUILD_BATTLER) * 0.6)))));
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
            placedBlocks.remove(block);
            cancel();
        }
    }
}


















