package me.zelha.thepit.upgrades.nonpermanent;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.listeners.BlockListener;
import me.zelha.thepit.utils.ZelLogic;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Material.*;

public class GoldenPickaxeListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    private final Map<UUID, Integer> hitCount = new HashMap<>();

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!zl.blockCheck(e.getClickedBlock())) return;
        if (e.getClickedBlock().getType() != OBSIDIAN) return;
        if (!BlockListener.placedBlocks.contains(e.getClickedBlock())) return;
        if (!zl.itemCheck(e.getItem())) return;
        if (e.getItem().getType() != GOLDEN_PICKAXE) return;

        UUID uuid = e.getPlayer().getUniqueId();
        Block clicked = e.getClickedBlock();
        Player p = e.getPlayer();

        hitCount.putIfAbsent(uuid, 0);
        hitCount.put(uuid, hitCount.get(uuid) + 1);
        p.playSound(e.getPlayer().getLocation(), Sound.BLOCK_STONE_HIT, 1F, 1.5F);

        if (hitCount.get(uuid) >= 2) {
            World world = clicked.getWorld();
            double x = clicked.getX();
            double y = clicked.getY();
            double z = clicked.getZ();

            hitCount.put(uuid, 0);

            new BukkitRunnable() {

                private int i = 0;

                @Override
                public void run() {
                    Block block = world.getBlockAt(new Location(world, x, y + i, z));

                    if (zl.blockCheck(block) && BlockListener.placedBlocks.contains(block) && block.getType() == OBSIDIAN) {
                        block.setType(AIR);
                        block.getWorld().spawnParticle(Particle.CLOUD, block.getLocation(), 5, 0.5, 0.5, 0.5, 0);
                        BlockListener.placedBlocks.remove(block);
                        p.playSound(p.getLocation(), Sound.BLOCK_STONE_HIT, 1F, 2F);
                    } else {
                        cancel();
                    }

                    i++;

                    if (i == 5) cancel();
                }
            }.runTaskTimer(Main.getInstance(), 3, 3);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        hitCount.remove(e.getPlayer().getUniqueId());
    }
}





















