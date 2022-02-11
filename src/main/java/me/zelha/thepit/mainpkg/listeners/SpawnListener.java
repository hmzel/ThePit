package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class SpawnListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (zl.spawnCheck(e.getEntity().getLocation())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (zl.spawnCheck(e.getBlock().getLocation()) && !Main.getInstance().blockPriviledges.contains(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (zl.spawnCheck(e.getBlock().getLocation()) && !Main.getInstance().blockPriviledges.contains(e.getPlayer())) e.setCancelled(true);
    }
}



































