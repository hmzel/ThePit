package me.zelha.thepit.eventcallers;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.NPCInteractEvent;
import me.zelha.thepit.utils.ZelLogic;
import me.zelha.thepit.zelenums.NPCs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.List;

public class NPCInteractEventCaller implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent e) {
        if (!zl.spawnCheck(e.getPlayer().getLocation())) return;

        NPCs npc = null;
        List<Entity> entities = e.getRightClicked().getNearbyEntities(1.5, 1.5, 1.5);

        entities.add(e.getRightClicked());

        for (Entity entity2 : entities) {
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
        List<Entity> entities = damaged.getNearbyEntities(1.5, 1.5, 1.5);

        entities.add(damaged);

        for (Entity entity2 : entities) {
            if (entity2.getScoreboardTags().contains("items")) npc = NPCs.ITEMS;
            if (entity2.getScoreboardTags().contains("upgrades")) npc = NPCs.UPGRADES;
            if (npc != null) break;
        }

        if (npc == null) return;

        Bukkit.getPluginManager().callEvent(new NPCInteractEvent(damager, npc));
    }
}








