package me.zelha.thepit.upgrades.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
    private final Set<UUID> gheadCooldown = new HashSet<>();
    private final ItemStack goldenHeadItem = zl.headItemBuilder("PhantomTupac", 1, "§6Golden Head", Arrays.asList(
                "§9Speed I (0:08)",
                "§9Regeneration II (0:05)",
                "§63❤ absorption!",
                "§71 second between eats"
        ));
    private final ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, 1);

    public void perkSelectHandler(Player p) {
        Inventory inv = p.getInventory();

        removeAll(inv, goldenHeadItem);
        removeAll(inv, gapple);
    }

    private void removeAll(Inventory inventory, ItemStack item) {
        for (ItemStack items : inventory.getContents()) {
            if (zl.itemCheck(items) && items.isSimilar(item)) {
                items.setType(Material.AIR);
            }
        }
    }

    private void determineHealingItem(Player p) {
        ItemStack item;

        if (pData(p).hasPerkEquipped(GOLDEN_HEADS)) {
            item = goldenHeadItem;
        } else {
            item = gapple;
        }

        if (!p.getInventory().containsAtLeast(item, 2)) {
            p.getInventory().addItem(item);
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();

        if (zl.playerCheck(damagedEntity) && zl.playerCheck(damagerEntity)) {
            Player damaged = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();
            double finalDMG = e.getFinalDamage();
            double damagedHP = damaged.getHealth();

            if (e.getCause() != DamageCause.FALL && (damagedHP - finalDMG) <= 0) {
                //on kill
                if (!pData(damager).hasPerkEquipped(VAMPIRE) && !pData(damager).hasPerkEquipped(RAMBO)) {
                    determineHealingItem(damager);
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if (zl.itemCheck(item) && item.isSimilar(goldenHeadItem) && !gheadCooldown.contains(p.getUniqueId())) {
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

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        gheadCooldown.remove(uuid);
    }
}










