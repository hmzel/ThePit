package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
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

import static org.bukkit.Material.AIR;

public class GoldenPickaxeListener implements Listener {

    ZelLogic zl = Main.getInstance().getZelLogic();

    private final Map<Player, Integer> hitCount = new HashMap<>();
    private boolean isPlacedBlock(Block block) {return AntiVanillaListener.placedBlocks.contains(block);}

    private void stylishlyRemoveBlock(Block block) {
        block.setType(AIR);
        block.getWorld().spawnParticle(Particle.CLOUD, block.getLocation(), 5, 0.5, 0.5, 0.5, 0);
        AntiVanillaListener.placedBlocks.remove(block);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (hitCount.containsKey(p) && isPlacedBlock(e.getClickedBlock())) {
                hitCount.put(p, hitCount.get(p) + 1);
            }else {
                hitCount.put(p, 0);
            }

            if (hitCount.get(p) >= 2) {
                hitCount.put(p, 0);

                if (zl.blockCheck(e.getClickedBlock()) && isPlacedBlock(e.getClickedBlock())) {
                    World world = e.getClickedBlock().getWorld();
                    double x = e.getClickedBlock().getX();
                    double y = e.getClickedBlock().getY();
                    double z = e.getClickedBlock().getZ();
                    stylishlyRemoveBlock(e.getClickedBlock());

                    for (int i = 1; i < 5; i++) {
                        Block extra = world.getBlockAt(new Location(world, x, y + i, z));

                        if (zl.blockCheck(extra) && isPlacedBlock(extra)) {
                            stylishlyRemoveBlock(extra);
                        }
                    }
                }
            }
        }
    }
}





















