package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.zelenums.Worlds;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.BoundingBox;

import static me.zelha.thepit.zelenums.Worlds.CASTLE;
import static me.zelha.thepit.zelenums.Worlds.GENESIS;

public class SpawnListener implements Listener {

    public boolean spawnCheck(Location location) {
        Worlds world = Worlds.findByName(location.getWorld().getName());
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        double spawnY;

        if (world == GENESIS) {
            spawnY = 90.0;
        } else if (world == CASTLE) {
            spawnY = 105.0;
        } else {
            spawnY = 110.0;
        }
        return BoundingBox.of(new Location(location.getWorld(), 0.0, spawnY, 0.0), 25.0, 15.0, 25.0).contains(x, y, z);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (spawnCheck(e.getEntity().getLocation())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (spawnCheck(e.getBlock().getLocation()) && !Main.getInstance().blockPriviledges.contains(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (spawnCheck(e.getBlock().getLocation()) && !Main.getInstance().blockPriviledges.contains(e.getPlayer())) e.setCancelled(true);
    }
}



































