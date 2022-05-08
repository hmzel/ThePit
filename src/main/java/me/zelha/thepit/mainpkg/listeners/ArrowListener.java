package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArrowListener implements Listener {

    private final static Map<UUID, ItemStack> arrowItemMap = new HashMap<>();

    public static ItemStack getArrowItem(Arrow arrow) {
        return arrowItemMap.get(arrow.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onArrowShoot(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof Arrow)) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        arrowItemMap.put(e.getEntity().getUniqueId(), ((Player) e.getEntity().getShooter()).getInventory().getItemInMainHand());
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow)) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                arrowItemMap.remove(e.getEntity().getUniqueId());
            }
        }.runTaskLater(Main.getInstance(), 1);
    }
}












