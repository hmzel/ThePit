package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Passives;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
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

    public static final List<Block> placedBlocks = new ArrayList<>();
    private final Material[] placeable = {OBSIDIAN, COBBLESTONE, OAK_WOOD};

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!placedBlocks.contains(e.getBlock()) && !Main.getInstance().blockPriviledges.contains(e.getPlayer())) e.setCancelled(true);

        e.setDropItems(false);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Material blockType = e.getBlock().getType();
        Location loc = e.getBlock().getLocation();
        Player p = e.getPlayer();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (!Arrays.asList(placeable).contains(blockType) && !Main.getInstance().blockPriviledges.contains(p)) {
            e.setCancelled(true);
        }

        if (loc.distance(new Location(loc.getWorld(), 0.5, loc.getY(), 0.5)) < 9 && !Main.getInstance().blockPriviledges.contains(p)) {
            e.setCancelled(true);
            return;
        }

        if (blockType == OBSIDIAN) {
            blockPoof(e.getBlock(), e.getBlockReplacedState().getType(), Math.round(2400 * (1 + (pData.getPassiveTier(Passives.BUILD_BATTLER) * 0.6))));
        } else if (blockType == COBBLESTONE) {
            blockPoof(e.getBlock(), e.getBlockReplacedState().getType(), Math.round(300 * (1 + (pData.getPassiveTier(Passives.BUILD_BATTLER) * 0.6))));
        }
    }

    private void blockPoof(Block block, Material previousBlock, long time) {
        new BukkitRunnable() {
            @Override
            public void run() {
                block.setType(previousBlock);
                placedBlocks.remove(block);
            }
        }.runTaskLater(Main.getInstance(), time);

        placedBlocks.add(block);
    }
}


















