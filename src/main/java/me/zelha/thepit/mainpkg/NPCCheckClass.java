package me.zelha.thepit.mainpkg;

import me.zelha.thepit.Main;
import me.zelha.thepit.zelenums.Worlds;
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

    private Location villagerLocation(Worlds world, String type) {

        switch (world) {
            case ELEMENTALS:
            case CORALS:
            case SEASONS:
                switch (type) {
                    case "items":
                        return new Location(Bukkit.getWorld(world.getName()), 2.5, 114, 12.5, -180.0F, 0.0F);
                    case "upgrades":
                        return new Location(Bukkit.getWorld(world.getName()), -1.5, 114, 12.5, -180.0F, 0.0F);

                }
                break;
            case CASTLE:
                switch (type) {
                    case "items":
                        return new Location(Bukkit.getWorld(world.getName()), 2.5, 95, 12.5, -180.0F, 0.0F);
                    case "upgrades":
                        return new Location(Bukkit.getWorld(world.getName()), -1.5, 95, 12.5, -180.0F, 0.0F);
                }
                break;
            case GENESIS:
                switch (type) {
                    case "items":
                        return new Location(Bukkit.getWorld(world.getName()), 2.5, 86, 16.5, -180.0F, 0.0F);
                    case "upgrades":
                        return new Location(Bukkit.getWorld(world.getName()), -1.5, 86, 16.5, -180.0F, 0.0F);
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
                    if (!npcExists(villagerLocation(Worlds.ELEMENTALS, "items"))) {
                        spawnVillager(villagerLocation(Worlds.ELEMENTALS, "items"), "items");
                    }
                    if (!npcExists(villagerLocation(Worlds.ELEMENTALS, "upgrades"))) {
                        spawnVillager(villagerLocation(Worlds.ELEMENTALS, "upgrades"), "upgrades");
                    }
                }

                if (Bukkit.getWorld("Corals") != null) {
                    if (!npcExists(villagerLocation(Worlds.CORALS, "items"))) {
                        spawnVillager(villagerLocation(Worlds.CORALS, "items"), "items");
                    }
                    if (!npcExists(villagerLocation(Worlds.CORALS, "upgrades"))) {
                        spawnVillager(villagerLocation(Worlds.CORALS, "upgrades"), "upgrades");
                    }
                }

                if (Bukkit.getWorld("Seasons") != null) {
                    if (!npcExists(villagerLocation(Worlds.SEASONS, "items"))) {
                        spawnVillager(villagerLocation(Worlds.SEASONS, "items"), "items");
                    }
                    if (!npcExists(villagerLocation(Worlds.SEASONS, "upgrades"))) {
                        spawnVillager(villagerLocation(Worlds.SEASONS, "upgrades"), "upgrades");
                    }
                }

                if (Bukkit.getWorld("Castle") != null) {
                    if (!npcExists(villagerLocation(Worlds.CASTLE, "items"))) {
                        spawnVillager(villagerLocation(Worlds.CASTLE, "items"), "items");
                    }
                    if (!npcExists(villagerLocation(Worlds.CASTLE, "upgrades"))) {
                        spawnVillager(villagerLocation(Worlds.CASTLE, "upgrades"), "upgrades");
                    }
                }

                if (Bukkit.getWorld("Genesis") != null) {
                    if (!npcExists(villagerLocation(Worlds.GENESIS, "items"))) {
                        spawnVillager(villagerLocation(Worlds.GENESIS, "items"), "items");
                    }
                    if (!npcExists(villagerLocation(Worlds.GENESIS, "upgrades"))) {
                        spawnVillager(villagerLocation(Worlds.GENESIS, "upgrades"), "upgrades");
                    }
                }
            }
        }.runTaskLater(Main.getInstance(), 200);
    }
}












