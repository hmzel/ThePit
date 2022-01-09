package me.zelha.thepit.mainpkg;

import me.zelha.thepit.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class NPCCheckClass {

    private boolean npcExists(Location location) {
        List<Entity> entityList = location.getWorld().getEntities();

        for (Entity entity : entityList) {
            if (entity.getLocation().equals(location)) {
                return true;
            }
        }
        return false;
    }

    private void spawnVillager(Location location, String type) {
        if (type.equals("items")) {
            Villager itemsVillager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
            itemsVillager.setAI(false);
            itemsVillager.setProfession(Villager.Profession.WEAPONSMITH);
            itemsVillager.setPersistent(true);
            itemsVillager.setSilent(true);
            itemsVillager.setRotation(-180.0F, 0.0F);
            itemsVillager.addScoreboardTag("z-entity");
        }
    }

    public void npcCheck() {

        new BukkitRunnable() {
            @Override
            public void run() {

                if (Bukkit.getWorld("Elementals") != null) {
                    World elementals = Bukkit.getWorld("Elementals");
                    Location itemsVillagerLocation = new Location(elementals, 2.5, 114, 12.5, -180.0F, 0.0F);

                    if (!npcExists(itemsVillagerLocation)) {
                        spawnVillager(itemsVillagerLocation, "items");
                    }
                }

                if (Bukkit.getWorld("Corals") != null) {
                    World elementals = Bukkit.getWorld("Corals");
                    Location itemsVillagerLocation = new Location(elementals, 2.5, 114, 12.5, -180.0F, 0.0F);

                    if (!npcExists(itemsVillagerLocation)) {
                        spawnVillager(itemsVillagerLocation, "items");
                    }
                }

                if (Bukkit.getWorld("Seasons") != null) {
                    World elementals = Bukkit.getWorld("Seasons");
                    Location itemsVillagerLocation = new Location(elementals, 2.5, 114, 12.5, -180.0F, 0.0F);

                    if (!npcExists(itemsVillagerLocation)) {
                        spawnVillager(itemsVillagerLocation, "items");
                    }
                }

                if (Bukkit.getWorld("Castle") != null) {
                    World elementals = Bukkit.getWorld("Castle");
                    Location itemsVillagerLocation = new Location(elementals, 2.5, 95, 12.5, -180.0F, 0.0F);

                    if (!npcExists(itemsVillagerLocation)) {
                        spawnVillager(itemsVillagerLocation, "items");
                    }
                }

                if (Bukkit.getWorld("Genesis") != null) {
                    World elementals = Bukkit.getWorld("Genesis");
                    Location itemsVillagerLocation = new Location(elementals, 2.5, 86, 16.5, -180.0F, 0.0F);

                    if (!npcExists(itemsVillagerLocation)) {
                        spawnVillager(itemsVillagerLocation, "items");
                    }
                }
            }
        }.runTaskLater(Main.getInstance(), 200);
    }
}












