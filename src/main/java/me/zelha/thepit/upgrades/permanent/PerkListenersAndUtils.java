package me.zelha.thepit.upgrades.permanent;

import me.zelha.thepit.Main;
import me.zelha.thepit.utils.ZelLogic;
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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static me.zelha.thepit.zelenums.Perks.*;
import static org.bukkit.Material.GOLDEN_APPLE;

//trickle down is handled in GoldIngotListener because thats just way easier
//all resource-related stuff is handled in KillListener
public class PerkListenersAndUtils implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

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

        PlayerData damagerData = Main.getInstance().getPlayerData(damager);

        for (Perks perk : Main.getInstance().getPlayerData(damaged).getEquippedPerks()) {
            if (perk.getMethods() != null && perk != BONK) perk.getMethods().onAttacked(damager, damaged);
        }

        for (Perks perk : damagerData.getEquippedPerks()) {
            if (perk.getMethods() != null) perk.getMethods().onAttack(damager, damaged, damagerEntity);
        }

        if (damaged.getHealth() - e.getFinalDamage() > 0) return;

        for (Perks perk : damagerData.getEquippedPerks()) {
            if (perk.getMethods() != null) perk.getMethods().onKill(damager, damaged);
        }

        if (damagerData.hasPerkEquipped(GOLDEN_HEADS)) return;
        if (damagerData.hasPerkEquipped(VAMPIRE)) return;
        if (damagerData.hasPerkEquipped(RAMBO)) return;
        if (damagerData.hasPerkEquipped(OLYMPUS)) return;

        int count = 0;

        for (ItemStack invItem : damager.getInventory().all(GOLDEN_APPLE).values()) {
            if (zl.itemCheck(invItem) && invItem.isSimilar(new ItemStack(GOLDEN_APPLE))) {
                count += invItem.getAmount();
            }
        }

        if (count < 2) damager.getInventory().addItem(new ItemStack(GOLDEN_APPLE, 1));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDrop(PlayerDropItemEvent e) {
        ItemMeta meta = e.getItemDrop().getItemStack().getItemMeta();

        if (meta != null && meta.getLore() != null && meta.getLore().contains("§7Perk item")) {
            e.getPlayer().sendMessage("§c§lNOPE! §7You cannot drop this item!");
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPickup(EntityPickupItemEvent e) {
        ItemMeta meta = e.getItem().getItemStack().getItemMeta();

        if (meta != null && meta.getLore() != null && meta.getLore().contains("§7Perk item")) {
            e.setCancelled(true);
        }
    }
}










