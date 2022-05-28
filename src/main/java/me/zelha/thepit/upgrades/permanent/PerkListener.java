package me.zelha.thepit.upgrades.permanent;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.upgrades.permanent.perks.BonkPerk;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static me.zelha.thepit.zelenums.Perks.*;
import static org.bukkit.Material.GOLDEN_APPLE;

//trickle down is handled in GoldIngotListener because thats just way easier
//all resource-related stuff is handled in KillListener
//streaker is hardcoded into BaseResourceListener for efficiency
public class PerkListener implements Listener {

    @EventHandler
    public void onKill(PitKillEvent e) {
        Player killer = e.getKiller();
        PlayerData killerData = Main.getInstance().getPlayerData(killer);

        for (Perks perk : killerData.getEquippedPerks()) {
            if (perk.getMethods() != null) perk.getMethods().onKill(killer, e.getDead());
        }

        if (killerData.hasPerkEquipped(GOLDEN_HEADS)) return;
        if (killerData.hasPerkEquipped(VAMPIRE)) return;
        if (killerData.hasPerkEquipped(RAMBO)) return;
        if (killerData.hasPerkEquipped(OLYMPUS)) return;

        int count = 0;

        for (ItemStack invItem : killer.getInventory().all(GOLDEN_APPLE).values()) {
            count += invItem.getAmount();
        }

        if (count < 2) killer.getInventory().addItem(new ItemStack(GOLDEN_APPLE, 1));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(PitDamageEvent e) {
        Player damaged = e.getDamaged();
        Player damager = e.getDamager();

        if (((BonkPerk) BONK.getMethods()).canBonk(damaged, damager)) {
            BONK.getMethods().onAttacked(damager, damaged);
            e.setDamage(0);
            e.setCancelled(true);
            return;
        }

        for (Perks perk : Main.getInstance().getPlayerData(damaged).getEquippedPerks()) {
            if (perk.getMethods() != null && perk != BONK) perk.getMethods().onAttacked(damager, damaged);
        }

        for (Perks perk : Main.getInstance().getPlayerData(damager).getEquippedPerks()) {
            if (perk.getMethods() != null) {
                perk.getMethods().onAttack(damager, damaged, e.getArrow());
                e.setBoost(e.getBoost() + perk.getMethods().getDamageModifier(damager, damaged));
            }
        }
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










