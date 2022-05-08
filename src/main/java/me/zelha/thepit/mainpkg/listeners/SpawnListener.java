package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.ZelLogic;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!zl.playerCheck(p)) {
                    cancel();
                    return;
                }

                PlayerData pData = Main.getInstance().getPlayerData(p);

                if (zl.spawnCheck(p.getLocation()) && pData.getStreak() != 0) {
                    pData.setStreak(0);
                    p.sendMessage("§c§lRESET! §7streak reset for standing in the spawn area!");

                    for (AttributeModifier modifier : p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers()) {
                        if (modifier.getName().equals("Uberstreak")) {
                            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(modifier);
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 100);
    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent e) {
        if (e.getEntity() instanceof Arrow && zl.spawnCheck(e.getLocation())) {
            e.setCancelled(true);

            if (e.getEntity().getShooter() instanceof Player) {
                ((Player) e.getEntity().getShooter()).playSound(e.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            }
        }
    }

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



































