package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.mainpkg.listeners.SpawnListener;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.zelha.thepit.zelenums.Perks.*;

public class PerkListenersAndUtils implements Listener {

    private PlayerData pData(Player player) {
        return Main.getInstance().getPlayerData(player);
    }

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final SpawnListener spawnUtils = Main.getInstance().getSpawnListener();

    private final Set<UUID> gheadCooldown = new HashSet<>();
    private final ItemStack goldenHeadItem = zl.headItemBuilder("PhantomTupac", 1, "§6Golden Head", Arrays.asList(
                "§9Speed I (0:08)",
                "§9Regeneration II (0:05)",
                "§63❤ absorption!",
                "§71 second between eats"
        ));
    private final ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, 1);

    /**
     * supposed to be called every time perk items should be reset <p>
     * ex: dying, selecting a perk, etc
     */
    public void perkReset(Player p) {
        Inventory inv = p.getInventory();

        removeAll(inv, gapple);

        for (ItemStack items : p.getInventory().getContents()) {
            if (zl.itemCheck(items) && items.getItemMeta().getDisplayName().equals("§6Golden Head")) {
                items.setType(Material.AIR);
            }
        }
    }

    private void removeAll(Inventory inventory, ItemStack item) {
        for (ItemStack items : inventory.getContents()) {
            if (zl.itemCheck(items) && items.isSimilar(item)) {
                items.setType(Material.AIR);
            }
        }
    }

    private boolean containsLessThan(int amount, ItemStack item, Inventory inv) {
        return !inv.containsAtLeast(item, amount) || !inv.contains(item);
    }//why in the seven hells does containsAtLeast return true if theres less than 1 in the inventory

    private void determineKillReward(Player p) {
        if (!pData(p).hasPerkEquipped(VAMPIRE) && !pData(p).hasPerkEquipped(RAMBO)) {
            if (pData(p).hasPerkEquipped(OLYMPUS)) {

            } else if (pData(p).hasPerkEquipped(GOLDEN_HEADS) && containsLessThan(2, goldenHeadItem, p.getInventory())) {
                p.getInventory().addItem(goldenHeadItem);
            } else if (containsLessThan(2, gapple, p.getInventory())) {
                p.getInventory().addItem(gapple);
            }
        }

        if (pData(p).hasPerkEquipped(VAMPIRE)) {

        }

        if (pData(p).hasPerkEquipped(RAMBO)) {

        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();

        if (spawnUtils.spawnCheck(damagedEntity.getLocation()) || spawnUtils.spawnCheck(damagerEntity.getLocation())) {
            return;
        }

        if (zl.playerCheck(damagedEntity) && zl.playerCheck(damagerEntity)) {
            Player damaged = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();
            double finalDMG = e.getFinalDamage();
            double damagedHP = damaged.getHealth();

            if (e.getCause() != DamageCause.FALL && (damagedHP - finalDMG) <= 0) {
                determineKillReward(damager);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (zl.playerCheck(entity)) {
            if (spawnUtils.spawnCheck(entity.getLocation())) {
                return;
            }

            Player p = (Player) e.getEntity();
            double finalDMG = e.getFinalDamage();
            double currentHP = p.getHealth();

            if (e.getCause() != DamageCause.FALL && (currentHP - finalDMG <= 0)) {
                perkReset(p);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if (zl.itemCheck(item) && item.getItemMeta().getDisplayName().equals("§6Golden Head") && !gheadCooldown.contains(p.getUniqueId())) {
            e.setCancelled(true);

            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                item.setType(Material.AIR);
            }

            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 1, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2, false, false));
            p.setAbsorptionAmount(6);
            gheadCooldown.add(p.getUniqueId());

            new BukkitRunnable() {
                @Override
                public void run() {
                    gheadCooldown.remove(p.getUniqueId());
                }
            }.runTaskLater(Main.getInstance(), 20);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        perkReset(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        gheadCooldown.remove(uuid);
    }
}










