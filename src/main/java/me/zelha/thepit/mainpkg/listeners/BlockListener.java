package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Megastreaks;
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

    public static final List<Block> placedBlocks = new ArrayList<>();
    private final Material[] placeable = {OBSIDIAN, COBBLESTONE, OAK_WOOD, BEDROCK};

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setDropItems(false);

        if (placedBlocks.contains(e.getBlock())) return;
        if (Main.getInstance().blockPriviledges.contains(e.getPlayer())) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        Location loc = block.getLocation();
        PlayerData pData = Main.getInstance().getPlayerData(e.getPlayer());
        double time;

        if (Main.getInstance().blockPriviledges.contains(e.getPlayer())) return;

        if (!Arrays.asList(placeable).contains(block.getType())) {
            e.setCancelled(true);
        }

        if (loc.distance(new Location(loc.getWorld(), 0.5, loc.getY(), 0.5)) < 9) {
            e.setCancelled(true);
        }

        if (e.isCancelled()) return;
        if (block.getType() == OBSIDIAN) time = 2400; else time = 300;

        time *= (1 + (pData.getPassiveTier(Passives.BUILD_BATTLER) * 0.6));

        if (pData.getMegastreak() == Megastreaks.HERMIT) time *= 2;

        placedBlocks.add(block);

        new BukkitRunnable() {
            @Override
            public void run() {
                block.setType(e.getBlockReplacedState().getType());
                placedBlocks.remove(block);
            }
        }.runTaskLater(Main.getInstance(), Math.round(time));
    }
}


















