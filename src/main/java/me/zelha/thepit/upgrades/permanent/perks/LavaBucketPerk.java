package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Material.*;

public class LavaBucketPerk extends AbstractPerk implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final RunMethods runTracker = Main.getInstance().generateRunMethods();
    private final Map<UUID, Integer> lavaExistTimer = new HashMap<>();
    private final Map<UUID, Block> placedLava = new HashMap<>();
    private final Map<UUID, Material> previousLavaBlock = new HashMap<>();
    private final ItemStack lavaBucketItem = zl.itemBuilder(Material.LAVA_BUCKET, 1, null, Collections.singletonList("§7Perk item"));
    private final ItemStack emptyBucketItem = zl.itemBuilder(BUCKET, 1, null, Collections.singletonList("§7Perk item"));

    public LavaBucketPerk() {
        super(Perks.LAVA_BUCKET);
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void onReset(Player player, PlayerData playerData) {
        PlayerInventory inv = player.getInventory();

        if (playerData.hasPerkEquipped(Perks.LAVA_BUCKET)) {
            if (inv.contains(emptyBucketItem)) {
                inv.setItem(inv.first(emptyBucketItem), lavaBucketItem);
            } else if (!inv.contains(lavaBucketItem)) {
                inv.addItem(lavaBucketItem);
            }
        } else {
            removeAll(inv, emptyBucketItem);
            removeAll(inv, lavaBucketItem);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        Player p = e.getPlayer();
        Block block = e.getBlock();

        e.setCancelled(true);

        if (zl.spawnCheck(block.getLocation())) return;
        if (block.getType() == LAVA || block.getType() == WATER) return;

        if (e.getBucket() == Material.LAVA_BUCKET) {
            previousLavaBlock.put(p.getUniqueId(), e.getBlock().getType());
            placedLava.put(p.getUniqueId(), block);
            block.setType(LAVA);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!runTracker.hasID(p.getUniqueId())) runTracker.setID(p.getUniqueId(), getTaskId());

                    if (block.getType() != LAVA) {
                        lavaExistTimer.put(p.getUniqueId(), 0);
                        runTracker.stop(p.getUniqueId());
                    }

                    lavaExistTimer.putIfAbsent(p.getUniqueId(), 0);
                    lavaExistTimer.put(p.getUniqueId(), lavaExistTimer.get(p.getUniqueId()) + 1);

                    if (lavaExistTimer.get(p.getUniqueId()) == 240) {
                        lavaExistTimer.put(p.getUniqueId(), 0);
                        block.setType(previousLavaBlock.get(p.getUniqueId()));
                        previousLavaBlock.remove(p.getUniqueId());
                        lavaExistTimer.remove(p.getUniqueId());
                        placedLava.remove(p.getUniqueId());
                        runTracker.stop(p.getUniqueId());
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 1);

            p.getInventory().setItemInMainHand(emptyBucketItem);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {
        Player p = e.getPlayer();
        Block block = e.getBlock();

        if (e.getBucket() != BUCKET) return;

        if (block.getType() == LAVA && runTracker.getID(p.getUniqueId()) != null && placedLava.containsValue(block)) {
            block.setType(previousLavaBlock.get(p.getUniqueId()));
            previousLavaBlock.remove(p.getUniqueId());
            placedLava.remove(p.getUniqueId());
            runTracker.stop(p.getUniqueId());
            p.getInventory().setItemInMainHand(lavaBucketItem);
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onLavaDamage(EntityDamageEvent e) {
        if (zl.spawnCheck(e.getEntity().getLocation())) return;
        if (!zl.playerCheck(e.getEntity())) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.LAVA) return;

        Player damaged = (Player) e.getEntity();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        if (placedLava.containsKey(uuid)) placedLava.get(uuid).setType(previousLavaBlock.get(uuid));

        lavaExistTimer.remove(uuid);
        previousLavaBlock.remove(uuid);
        placedLava.remove(uuid);
    }
}














