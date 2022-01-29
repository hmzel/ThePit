package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.zelenums.Worlds;
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

import static me.zelha.thepit.zelenums.Worlds.*;

public class SpawnListener implements Listener {
    private final BoundingBox elementalsSpawn = BoundingBox.of(new Location(Bukkit.getWorld("Elementals"), 0.0, 110.0, 0.0), 25.0, 15.0, 25.0);
    private final BoundingBox coralsSpawn = BoundingBox.of(new Location(Bukkit.getWorld("Corals"), 0.0, 110.0, 0.0), 25.0, 15.0, 25.0);
    private final BoundingBox seasonsSpawn = BoundingBox.of(new Location(Bukkit.getWorld("Seasons"), 0.0, 110.0, 0.0), 25.0, 15.0, 25.0);
    private final BoundingBox castleSpawn = BoundingBox.of(new Location(Bukkit.getWorld("Castle"), 0.0, 105.0, 0.0), 25.0, 15.0, 25.0);
    private final BoundingBox genesisSpawn = BoundingBox.of(new Location(Bukkit.getWorld("Genesis"), 0.0, 90.0, 0.0), 25.0, 15.0, 25.0);

    public boolean spawnCheck(Location location) {
        Worlds world = Worlds.findByName(location.getWorld().getName());
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        if (world == ELEMENTALS && elementalsSpawn.contains(x, y, z)) {
            return true;
        }
        if (world == CORALS && coralsSpawn.contains(x, y, z)) {
            return true;
        }
        if (world == SEASONS && seasonsSpawn.contains(x, y, z)) {
            return true;
        }
        if (world == CASTLE && castleSpawn.contains(x, y, z)) {
            return true;
        }
        if (world == GENESIS && genesisSpawn.contains(x, y, z)) {
            return true;
        }

        return false;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (spawnCheck(e.getEntity().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (spawnCheck(e.getBlock().getLocation())) {
            if (!Main.getInstance().blockPriviledges.contains(e.getPlayer())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (spawnCheck(e.getBlock().getLocation())) {
            if (!Main.getInstance().blockPriviledges.contains(e.getPlayer())) {
                e.setCancelled(true);
            }
        }
    }
}



































