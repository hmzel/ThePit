package me.zelha.thepit.upgrades.permanent;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.upgrades.permanent.perks.BonkPerk;
import me.zelha.thepit.zelenums.Perks;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import static me.zelha.thepit.zelenums.Perks.*;
import static org.bukkit.Material.*;

//trickle down is handled in GoldIngotListener because thats just way easier
//all resource-related stuff is handled in KillListener
public class PerkListenersAndUtils implements Listener {

    private PlayerData pData(Player player) {
        return Main.getInstance().getPlayerData(player);
    }

    private final ZelLogic zl = Main.getInstance().getZelLogic();
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

        removeAll(inv, gapple);

        if (!inv.contains(IRON_SWORD) && !inv.contains(DIAMOND_SWORD) && !pData.hasPerkEquipped(BARBARIAN)) {
            inv.addItem(zl.itemBuilder(IRON_SWORD, 1));
        }

        if (!inv.contains(zl.itemBuilder(BOW, 1))) inv.addItem(zl.itemBuilder(BOW, 1));

        if (arrowCount < 32 && arrowCount != 0) {
            inv.addItem(new ItemStack(ARROW, 32 - arrowCount));
        } else if (arrowCount == 0 && !zl.itemCheck(inv.getItem(8))) {
            inv.setItem(8, new ItemStack(ARROW, 32));
        } else if (arrowCount == 0) {
            inv.addItem(new ItemStack(ARROW, 32));
        }
    }

    public double getPerkDamageModifiers(Player damager, Player damaged) {
        double boost = 0;

        for (Perks perk : pData(damager).getEquippedPerks()) {
            if (perk.getMethods() != null) boost += perk.getMethods().getDamageModifier(damager, damaged);
        }

        return boost;
    }

    public double getPerkDamageReduction(Player damaged) {
        return 0;
    }



    private void removeAll(PlayerInventory inventory, ItemStack item) {
        for (ItemStack items : inventory.all(item.getType()).values()) {
            if (items.isSimilar(item)) inventory.remove(items);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAttackAndKill(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();
        Player damaged;
        Player damager;

        if (e.getCause() == DamageCause.FALL) return;
        if (zl.spawnCheck(damagedEntity.getLocation()) || zl.spawnCheck(damagerEntity.getLocation())) return;
        if (zl.playerCheck(damagedEntity)) damaged = (Player) damagedEntity; else return;

        if (damagerEntity instanceof Arrow && ((Arrow) damagerEntity).getShooter() instanceof Player) {
            damager = (Player) ((Arrow) damagerEntity).getShooter();
        } else if (zl.playerCheck(damagerEntity)) {
            damager = (Player) damagerEntity;
        } else {
            return;
        }

        if (damaged.equals(damager)) return;

        if (((BonkPerk) BONK.getMethods()).canBonk(damaged, damager)) {
            BONK.getMethods().onAttacked(damager, damaged);
            e.setDamage(0);
            e.setCancelled(true);
            return;
        }

        for (Perks perk : pData(damaged).getEquippedPerks()) {
            if (perk.getMethods() != null && perk != BONK) perk.getMethods().onAttacked(damager, damaged);
        }

        for (Perks perk : pData(damager).getEquippedPerks()) {
            if (perk.getMethods() != null) perk.getMethods().onAttack(damager, damaged, damagerEntity);
        }

        if (damaged.getHealth() - e.getFinalDamage() <= 0) {
            for (Perks perk : pData(damager).getEquippedPerks()) {
                if (perk.getMethods() != null) perk.getMethods().onKill(damager, damaged);
            }

            if (pData(damager).hasPerkEquipped(GOLDEN_HEADS)) return;
            if (pData(damager).hasPerkEquipped(VAMPIRE)) return;
            if (pData(damager).hasPerkEquipped(RAMBO)) return;
            if (pData(damager).hasPerkEquipped(OLYMPUS)) return;

            int count = 0;

            for (ItemStack invItem : damager.getInventory().all(GOLDEN_APPLE).values()) {
                if (zl.itemCheck(invItem) && invItem.isSimilar(new ItemStack(GOLDEN_APPLE))) {
                    count += invItem.getAmount();
                }
            }

            if (count < 2) damager.getInventory().addItem(gapple);
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerQuitEvent e) {
        perkReset(e.getPlayer());
    }
}










