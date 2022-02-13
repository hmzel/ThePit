package me.zelha.thepit.upgrades.nonpermanent;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.listeners.BlockListener;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Material.*;

public class GoldenPickaxeListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    private final Map<Player, Integer> hitCount = new HashMap<>();

    private boolean isPlacedBlock(Block block) {
        return BlockListener.placedBlocks.contains(block);
    }

    private void stylishlyRemoveBlock(Block block) {
        block.setType(AIR);
        block.getWorld().spawnParticle(Particle.CLOUD, block.getLocation(), 5, 0.5, 0.5, 0.5, 0);
        BlockListener.placedBlocks.remove(block);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (!zl.blockCheck(e.getClickedBlock())) return;
        if (e.getClickedBlock().getType() != OBSIDIAN) return;
        if (!isPlacedBlock(e.getClickedBlock())) return;
        if (!zl.itemCheck(e.getItem())) return;
        if (e.getItem().getType() != GOLDEN_PICKAXE) return;

        Player p = e.getPlayer();
        Block clicked = e.getClickedBlock();

        hitCount.putIfAbsent(p, 0);
        hitCount.put(p, hitCount.get(p) + 1);

        if (hitCount.get(p) >= 2) {
            World world = clicked.getWorld();
            double x = clicked.getX();
            double y = clicked.getY();
            double z = clicked.getZ();

            hitCount.put(p, 0);
            stylishlyRemoveBlock(clicked);

            for (int i = 1; i < 5; i++) {
                Block extra = world.getBlockAt(new Location(world, x, y + i, z));

                if (zl.blockCheck(extra) && isPlacedBlock(extra) && extra.getType() == OBSIDIAN) {
                    stylishlyRemoveBlock(extra);
                }
            }
        }
    }
}





















