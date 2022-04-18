package me.zelha.thepit.eventcallers;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.NPCInteractEvent;
import me.zelha.thepit.utils.ZelLogic;
import me.zelha.thepit.zelenums.NPCs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class NPCInteractEventCaller implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    @EventHandler
    public void onDirectRightClick(InventoryOpenEvent e) {
        if (e.getView().getTopInventory().getType() != InventoryType.MERCHANT) return;
        if (!zl.spawnCheck(e.getPlayer().getLocation())) return;

        Villager villager = (Villager) e.getInventory().getHolder();
        NPCs npc = null;

        if (villager.getScoreboardTags().contains("items")) npc = NPCs.ITEMS;
        if (villager.getScoreboardTags().contains("upgrades")) npc = NPCs.UPGRADES;

        e.setCancelled(true);

        if (npc == null) return;

        Bukkit.getPluginManager().callEvent(new NPCInteractEvent((Player) e.getPlayer(), npc));
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent e) {
        if (!zl.spawnCheck(e.getPlayer().getLocation())) return;

        NPCs npc = null;

        for (Entity entity2 : e.getRightClicked().getNearbyEntities(1.5, 1.5, 1.5)) {
            if (entity2.getScoreboardTags().contains("items")) npc = NPCs.ITEMS;
            if (entity2.getScoreboardTags().contains("upgrades")) npc = NPCs.UPGRADES;
            if (npc != null) break;
        }

        if (npc == null) return;

        Bukkit.getPluginManager().callEvent(new NPCInteractEvent(e.getPlayer(), npc));
    }

    @EventHandler
    public void onLeftClick(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damagerEntity = e.getDamager();

        if (!zl.playerCheck(damagerEntity)) return;

        Player damager = (Player) e.getDamager();
        NPCs npc = null;

        for (Entity entity2 : damaged.getNearbyEntities(1.5, 1.5, 1.5)) {
            if (entity2.getScoreboardTags().contains("items")) npc = NPCs.ITEMS;
            if (entity2.getScoreboardTags().contains("upgrades")) npc = NPCs.UPGRADES;
            if (npc != null) break;
        }

        if (npc == null) return;

        Bukkit.getPluginManager().callEvent(new NPCInteractEvent(damager, npc));
    }
}








