package me.zelha.thepit.upgrades.permanent;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.zelha.thepit.zelenums.Perks.*;
import static org.bukkit.Material.*;

//trickle down is handled in GoldIngotListener because thats just way easier
//all resource-related stuff is handled in KillListener
public class PerkListenersAndUtils implements Listener {

    private PlayerData pData(Player player) {
        return Main.getInstance().getPlayerData(player);
    }

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final RunMethods runTracker2 = Main.getInstance().generateRunMethods();

    private final Map<UUID, Set<UUID>> bonkMap = new HashMap<>();
    private final Map<UUID, UUID> spammerShotIdentifier = new HashMap<>();

    private final ItemStack bountyHunterItem = zl.itemBuilder(GOLDEN_LEGGINGS, 1, null, Collections.singletonList("§7Perk item"), true);
    private final ItemStack gapple = new ItemStack(GOLDEN_APPLE, 1);

    /**
     * supposed to be called every time items should be reset <p>
     * ex: dying, selecting a perk, etc <p>
     * must be called *after* the perk slot is set, in conditions where that applies
     */
    public void perkReset(Player p) {
        PlayerInventory inv = p.getInventory();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        int arrowCount = 0;

        for (Perks perk : Perks.values()) {
            if (perk.getMethods() != null) perk.getMethods().onReset(p, pData);
        }

        pData.setStreak(0);

        for (ItemStack item : inv.all(ARROW).values()) arrowCount += item.getAmount();

        if (bonkMap.get(p.getUniqueId()) != null) bonkMap.get(p.getUniqueId()).clear();

        removeAll(inv, gapple);

        if (!inv.contains(IRON_SWORD) && !inv.contains(DIAMOND_SWORD) && !pData.hasPerkEquipped(BARBARIAN)) {
            inv.addItem(zl.itemBuilder(IRON_SWORD, 1));
        }

        if (!inv.contains(zl.itemBuilder(BOW, 1))) inv.addItem(zl.itemBuilder(BOW, 1));

        if (pData.hasPerkEquipped(BOUNTY_HUNTER)) {
            if (!inv.contains(bountyHunterItem)) {
                if (!zl.itemCheck(inv.getLeggings()) || inv.getLeggings().getType() == CHAINMAIL_LEGGINGS || inv.getLeggings().getType() == IRON_LEGGINGS) {
                    inv.setLeggings(bountyHunterItem);
                }
            }
        } else {
            removeAll(inv, bountyHunterItem);
            if (zl.itemCheck(inv.getLeggings()) && inv.getLeggings().equals(bountyHunterItem)) inv.setLeggings(zl.itemBuilder(CHAINMAIL_LEGGINGS, 1));
        }

        if (arrowCount < 32 && arrowCount != 0) {
            inv.addItem(new ItemStack(ARROW, 32 - arrowCount));
        } else if (arrowCount == 0 && !zl.itemCheck(inv.getItem(8))) {
            inv.setItem(8, new ItemStack(ARROW, 32));
        } else if (arrowCount == 0) {
            inv.addItem(new ItemStack(ARROW, 32));
        }
    }

    public double getPerkDamageBoost(Player damager, Player damaged) {
        double boost = 0;

        for (Perks perk : pData(damager).getEquippedPerks()) {
            if (perk.getMethods() != null) boost += perk.getMethods().getDamageModifier(damager);
        }

        if (pData(damager).hasPerkEquipped(Perks.BOUNTY_HUNTER) && zl.itemCheck(damager.getInventory().getLeggings())
           && damager.getInventory().getLeggings().getType() == Material.GOLDEN_LEGGINGS) {
            boost += Math.floor((double) pData(damaged).getBounty() / 100) / 100;
        }
        return boost;
    }

    public double getPerkDamageReduction(Player damaged) {
        double reduction = 0;

        reduction += getGladiatorDamageReduction(damaged);

        return reduction;
    }

    public double getGladiatorDamageReduction(Player player) {
        double reduction = 0;
        int nearbyPlayers = 0;

        if (pData(player).hasPerkEquipped(GLADIATOR)) {
            for (Entity entity : player.getNearbyEntities(12, 12, 12)) {
                if (zl.playerCheck(entity)) nearbyPlayers++;
            }

            if (nearbyPlayers >= 3 && nearbyPlayers <= 10) {
                reduction += nearbyPlayers * 0.03;
            } else if (nearbyPlayers > 10) {
                reduction += 0.3;
            }
        }
        return reduction;
    }

    public boolean hasBeenShotBySpammer(Player damager, Player damaged) {
        boolean bool = spammerShotIdentifier.containsKey(damager.getUniqueId())
                && spammerShotIdentifier.get(damager.getUniqueId()) == damaged.getUniqueId()
                && pData(damager).hasPerkEquipped(SPAMMER);

        if (bool) new BukkitRunnable() {
            @Override
            public void run() {
                spammerShotIdentifier.remove(damager.getUniqueId());
            }
        }.runTaskLater(Main.getInstance(), 1);

        return bool;
    }

    private void removeAll(PlayerInventory inventory, ItemStack item) {
        for (ItemStack items : inventory.all(item.getType()).values()) {
            if (items.isSimilar(item)) inventory.remove(items);
        }
    }

    private boolean containsLessThan(int amount, ItemStack item, Inventory inv) {
        int count = 0;

        if (item.getType() == PLAYER_HEAD) {
            for (ItemStack item2 : inv.all(PLAYER_HEAD).values()) {
                if (zl.itemCheck(item2) && item2.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                    count += item2.getAmount();
                }
            }
            return count < amount;
        }

        for (ItemStack invItem : inv.all(item.getType()).values()) {
            if (zl.itemCheck(invItem) && invItem.isSimilar(item)) {
                count += invItem.getAmount();
            }
        }
        return count < amount;
    }

    private void determineKillRewards(Player killer, Player dead) {
        PlayerInventory inv = killer.getInventory();
        boolean doHealingItem = true;

        if (pData(killer).hasPerkEquipped(VAMPIRE)) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 160, 0, false, false));
            doHealingItem = false;
        }

        if (pData(killer).hasPerkEquipped(RAMBO)) {

            doHealingItem = false;
        }

        if (doHealingItem && pData(killer).hasPerkEquipped(OLYMPUS)) {

            doHealingItem = false;
        }

        int count = 0;

        for (ItemStack invItem : inv.all(GOLDEN_APPLE).values()) {
            if (zl.itemCheck(invItem) && invItem.isSimilar(new ItemStack(GOLDEN_APPLE))) {
                count += invItem.getAmount();
            }
        }

        if (count < 2) inv.addItem(gapple);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAttackAndKill(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();
        Player damaged;
        Player damager;
        boolean damageCauseArrow = false;

        if (zl.spawnCheck(damagedEntity.getLocation()) || zl.spawnCheck(damagerEntity.getLocation())) return;
        if (zl.playerCheck(damagedEntity)) damaged = (Player) damagedEntity; else return;

        if (damagerEntity instanceof Arrow && ((Arrow) damagerEntity).getShooter() instanceof Player) {
            damager = (Player) ((Arrow) damagerEntity).getShooter();
            damageCauseArrow = true;
        } else if (zl.playerCheck(damagerEntity)) {
            damager = (Player) damagerEntity;
        } else {
            return;
        }

        if (damaged.equals(damager)) return;

        UUID damagedUUID = damaged.getUniqueId();
        UUID damagerUUID = damager.getUniqueId();
        double finalDMG = e.getFinalDamage();
        double damagedHP = damaged.getHealth();

        if (pData(damaged).hasPerkEquipped(BONK) && !bonkMap.get(damagedUUID).contains(damagerUUID)) {
            for (Entity entity : damaged.getNearbyEntities(32, 32, 32)) {
                if (!zl.playerCheck(entity) || entity.getUniqueId().equals(damagedUUID)) continue;

                ((Player) entity).spawnParticle(Particle.EXPLOSION_LARGE, damaged.getLocation(), 1, 0, 0, 0, 0);
            }

            bonkMap.get(damagedUUID).add(damagerUUID);
            damaged.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 1, false, false, true));
            e.setDamage(0);
            e.setCancelled(true);
            damaged.setInvulnerable(true);

            new BukkitRunnable() {
                int runs = 0;

                @Override
                public void run() {
                    if (runs == 0) damaged.setInvulnerable(false);

                    if (bonkMap.get(damagedUUID) == null || !bonkMap.get(damagedUUID).contains(damagerUUID)) {
                        cancel();
                        return;
                    }

                    if (runs == 30) {
                        if (bonkMap.get(damagedUUID) != null) bonkMap.get(damagedUUID).remove(damagerUUID);
                        cancel();
                    }

                    runs++;
                }
            }.runTaskTimer(Main.getInstance(), 10, 10);

            return;
        }

        if (pData(damager).hasPerkEquipped(VAMPIRE)) {
            if (damageCauseArrow && ((Arrow) damagerEntity).isCritical()) {
                damager.setHealth(Math.min(damager.getHealth() + 3, damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
            } else {
                damager.setHealth(Math.min(damager.getHealth() + 1, damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
            }
        }

        if (e.getCause() == DamageCause.PROJECTILE && pData(damager).hasPerkEquipped(SPAMMER) && damageCauseArrow) {
            damager.getInventory().addItem(new ItemStack(ARROW, 3));
            spammerShotIdentifier.put(damager.getUniqueId(), damaged.getUniqueId());
        }

        if (e.getCause() != DamageCause.FALL && (damagedHP - finalDMG) <= 0) {
            for (Perks perk : pData(damager).getEquippedPerks()) {
                if (perk.getMethods() != null) perk.getMethods().onKill(damager, damaged);
            }

            determineKillRewards(damager, damaged);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (!zl.playerCheck(entity)) return;
        if (zl.spawnCheck(entity.getLocation())) return;

        double finalDMG = e.getFinalDamage();
        double currentHP = ((Player) e.getEntity()).getHealth();

        if (e.getCause() != DamageCause.FALL && (currentHP - finalDMG <= 0)) perkReset((Player) e.getEntity());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDrop(PlayerDropItemEvent e) {
        ItemMeta meta = e.getItemDrop().getItemStack().getItemMeta();

        if (meta != null && meta.getLore() != null && meta.getLore().contains("§7Perk item")) {
            e.getPlayer().sendMessage("§c§lNOPE! §7You cannot drop this item!");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        bonkMap.put(e.getPlayer().getUniqueId(), new HashSet<>());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        spammerShotIdentifier.remove(uuid);
        bonkMap.remove(uuid);
        perkReset(e.getPlayer());
    }
}










