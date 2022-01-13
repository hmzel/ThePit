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

    private Location villagerLocation(String worldName, String type) {//oh this isnt using the enusm msdfkasbnlkfjsd
        //whatever ill deal with that later

        switch (worldName) {
            case "Elementals":
            case "Corals":
            case "Seasons":
                switch (type) {
                    case "items":
                        return new Location(Bukkit.getWorld(worldName), 2.5, 114, 12.5, -180.0F, 0.0F);
                    case "upgrades":
                        return new Location(Bukkit.getWorld(worldName), -1.5, 114, 12.5, -180.0F, 0.0F);

                }
                break;
            case "Castle":
                switch (type) {
                    case "items":
                        return new Location(Bukkit.getWorld(worldName), 2.5, 95, 12.5, -180.0F, 0.0F);
                    case "upgrades":
                        return new Location(Bukkit.getWorld(worldName), -1.5, 95, 12.5, -180.0F, 0.0F);
                }
                break;
            case "Genesis":
                switch (type) {
                    case "items":
                        return new Location(Bukkit.getWorld(worldName), 2.5, 86, 16.5, -180.0F, 0.0F);
                    case "upgrades":
                        return new Location(Bukkit.getWorld(worldName), -1.5, 86, 16.5, -180.0F, 0.0F);
                }
                break;
        }
        return null;
    }

    private void spawnVillager(Location location, String type) {
        if (type.equals("items")) {
            Villager itemsVillager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
            itemsVillager.setAI(false);
            itemsVillager.setProfession(Villager.Profession.WEAPONSMITH);
            itemsVillager.setPersistent(true);
            itemsVillager.setSilent(true);
            itemsVillager.addScoreboardTag("z-entity");
        }else if (type.equals("upgrades")) {
            Villager upgradesVillager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
            upgradesVillager.setAI(false);
            upgradesVillager.setProfession(Villager.Profession.NONE);
            upgradesVillager.setPersistent(true);
            upgradesVillager.setSilent(true);
            upgradesVillager.addScoreboardTag("z-entity");
        }
    }

    public void npcCheck() {

        new BukkitRunnable() {
            @Override
            public void run() {

                if (Bukkit.getWorld("Elementals") != null) {
                    if (!npcExists(villagerLocation("Elementals", "items"))) {
                        spawnVillager(villagerLocation("Elementals", "items"), "items");
                    }
                    if (!npcExists(villagerLocation("Elementals", "upgrades"))) {
                        spawnVillager(villagerLocation("Elementals", "upgrades"), "upgrades");
                    }
                }

                if (Bukkit.getWorld("Corals") != null) {
                    if (!npcExists(villagerLocation("Corals", "items"))) {
                        spawnVillager(villagerLocation("Corals", "items"), "items");
                    }
                    if (!npcExists(villagerLocation("Corals", "upgrades"))) {
                        spawnVillager(villagerLocation("Corals", "upgrades"), "upgrades");
                    }
                }

                if (Bukkit.getWorld("Seasons") != null) {
                    if (!npcExists(villagerLocation("Seasons", "items"))) {
                        spawnVillager(villagerLocation("Seasons", "items"), "items");
                    }
                    if (!npcExists(villagerLocation("Seasons", "upgrades"))) {
                        spawnVillager(villagerLocation("Seasons", "upgrades"), "upgrades");
                    }
                }

                if (Bukkit.getWorld("Castle") != null) {
                    if (!npcExists(villagerLocation("Castle", "items"))) {
                        spawnVillager(villagerLocation("Castle", "items"), "items");
                    }
                    if (!npcExists(villagerLocation("Castle", "upgrades"))) {
                        spawnVillager(villagerLocation("Castle", "upgrades"), "upgrades");
                    }
                }

                if (Bukkit.getWorld("Genesis") != null) {
                    if (!npcExists(villagerLocation("Genesis", "items"))) {
                        spawnVillager(villagerLocation("Genesis", "items"), "items");
                    }
                    if (!npcExists(villagerLocation("Genesis", "upgrades"))) {
                        spawnVillager(villagerLocation("Genesis", "upgrades"), "upgrades");
                    }
                }
            }
        }.runTaskLater(Main.getInstance(), 200);
    }
}












