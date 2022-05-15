package me.zelha.thepit.events;

import me.zelha.thepit.Main;
import me.zelha.thepit.utils.ZelLogic;
import me.zelha.thepit.zelenums.NPCs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class NPCInteractEventCaller implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent e) {
        if (zl.spawnCheck(e.getPlayer().getLocation())) callEvent(e.getPlayer(), e.getRightClicked());
    }

    @EventHandler
    public void onLeftClick(EntityDamageByEntityEvent e) {
        if (zl.playerCheck(e.getDamager()) && zl.spawnCheck(e.getDamager().getLocation())) {
            callEvent((Player) e.getDamager(), e.getEntity());
        }
    }

    private void callEvent(Player clicker, Entity clicked) {
        NPCs npc = null;
        List<Entity> entities = clicked.getNearbyEntities(1.5, 1.5, 1.5);

        entities.add(clicked);

        for (Entity entity : entities) {
            if (entity.getScoreboardTags().contains("items")) npc = NPCs.ITEMS;
            if (entity.getScoreboardTags().contains("upgrades")) npc = NPCs.UPGRADES;
            if (npc != null) break;
        }

        if (npc == null) return;

        NPCs finalNpc = npc;//why???

        new BukkitRunnable() {//kinda jank but idk how else to fix the ghost inventory
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(new NPCInteractEvent(clicker, finalNpc));
            }
        }.runTaskLater(Main.getInstance(), 1);
    }
}








