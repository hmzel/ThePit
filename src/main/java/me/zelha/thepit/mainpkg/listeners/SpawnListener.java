package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.BoundingBox;

public class SpawnListener implements Listener {
    private final BoundingBox elementalsSpawn = BoundingBox.of(new Location(Bukkit.getWorld("Elementals"), 0, 101, 0), 25, 25, 25);
    private final BoundingBox coralsSpawn = BoundingBox.of(new Location(Bukkit.getWorld("Corals"), 0, 101, 0), 25, 25, 25);
    private final BoundingBox seasonsSpawn = BoundingBox.of(new Location(Bukkit.getWorld("Seasons"), 0, 101, 0), 25, 25, 25);
    private final BoundingBox castleSpawn = BoundingBox.of(new Location(Bukkit.getWorld("Castle"), 0, 90, 0), 25, 25, 25);
    private final BoundingBox genesisSpawn = BoundingBox.of(new Location(Bukkit.getWorld("Genesis"), 0, 80, 0), 25, 25, 25);

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        double x = entity.getLocation().getX();
        double y = entity.getLocation().getY();
        double z = entity.getLocation().getZ();

        if (elementalsSpawn.contains(x, y, z) || coralsSpawn.contains(x, y, z) || seasonsSpawn.contains(x, y, z)
           || castleSpawn.contains(x, y, z) || genesisSpawn.contains(x, y, z)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        double x = block.getLocation().getX();
        double y = block.getLocation().getY();
        double z = block.getLocation().getZ();

        if (elementalsSpawn.contains(x, y, z) || coralsSpawn.contains(x, y, z) || seasonsSpawn.contains(x, y, z)
                || castleSpawn.contains(x, y, z) || genesisSpawn.contains(x, y, z)) {

            if (!Main.getInstance().blockPriviledges.contains(e.getPlayer())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        double x = block.getLocation().getX();
        double y = block.getLocation().getY();
        double z = block.getLocation().getZ();

        if (elementalsSpawn.contains(x, y, z) || coralsSpawn.contains(x, y, z) || seasonsSpawn.contains(x, y, z)
                || castleSpawn.contains(x, y, z) || genesisSpawn.contains(x, y, z)) {

            if (!Main.getInstance().blockPriviledges.contains(e.getPlayer())) {
                e.setCancelled(true);
            }
        }
    }
}



































