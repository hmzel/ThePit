package me.zelha.thepit.upgrades.nonpermanent;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.listeners.BlockListener;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Material.*;

public class GoldenPickaxeListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    private final Map<UUID, Integer> hitCount = new HashMap<>();

    private boolean isPlacedBlock(Block block) {
        return BlockListener.placedBlocks.contains(block);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (!zl.blockCheck(e.getClickedBlock())) return;
        if (e.getClickedBlock().getType() != OBSIDIAN) return;
        if (!isPlacedBlock(e.getClickedBlock())) return;
        if (!zl.itemCheck(e.getItem())) return;
        if (e.getItem().getType() != GOLDEN_PICKAXE) return;

        UUID uuid = e.getPlayer().getUniqueId();
        Block clicked = e.getClickedBlock();

        hitCount.putIfAbsent(uuid, 0);
        hitCount.put(uuid, hitCount.get(uuid) + 1);

        if (hitCount.get(uuid) >= 2) {
            World world = clicked.getWorld();
            double x = clicked.getX();
            double y = clicked.getY();
            double z = clicked.getZ();

            hitCount.put(uuid, 0);

            for (int i = 0; i < 5; i++) {
                Block block = world.getBlockAt(new Location(world, x, y + i, z));

                if (zl.blockCheck(block) && isPlacedBlock(block) && block.getType() == OBSIDIAN) {
                    block.setType(AIR);
                    block.getWorld().spawnParticle(Particle.CLOUD, block.getLocation(), 5, 0.5, 0.5, 0.5, 0);
                    BlockListener.placedBlocks.remove(block);
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        hitCount.remove(e.getPlayer().getUniqueId());
    }
}





















