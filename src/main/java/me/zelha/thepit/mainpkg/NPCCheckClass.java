package me.zelha.thepit.mainpkg;

import me.zelha.thepit.Main;
import me.zelha.thepit.zelenums.NPCs;
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

    private Location villagerLocation(Worlds world, NPCs type) {

        switch (world) {
            case ELEMENTALS:
            case CORALS:
            case SEASONS:
                switch (type) {
                    case ITEMS:
                        return new Location(Bukkit.getWorld(world.getName()), 2.5, 114, 12.5, -180.0F, 0.0F);
                    case UPGRADES:
                        return new Location(Bukkit.getWorld(world.getName()), -1.5, 114, 12.5, -180.0F, 0.0F);

                }
                break;
            case CASTLE:
                switch (type) {
                    case ITEMS:
                        return new Location(Bukkit.getWorld(world.getName()), 2.5, 95, 12.5, -180.0F, 0.0F);
                    case UPGRADES:
                        return new Location(Bukkit.getWorld(world.getName()), -1.5, 95, 12.5, -180.0F, 0.0F);
                }
                break;
            case GENESIS:
                switch (type) {
                    case ITEMS:
                        return new Location(Bukkit.getWorld(world.getName()), 2.5, 86, 16.5, -180.0F, 0.0F);
                    case UPGRADES:
                        return new Location(Bukkit.getWorld(world.getName()), -1.5, 86, 16.5, -180.0F, 0.0F);
                }
                break;
        }
        return null;
    }

    private void spawnVillager(Location location, NPCs type) {
        if (type == NPCs.ITEMS) {
            Villager itemsVillager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
            itemsVillager.setAI(false);
            itemsVillager.setProfession(Villager.Profession.WEAPONSMITH);
            itemsVillager.setPersistent(true);
            itemsVillager.setSilent(true);
            itemsVillager.addScoreboardTag("z-entity");
        }else if (type == NPCs.UPGRADES) {
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
                    if (!npcExists(villagerLocation(Worlds.ELEMENTALS, NPCs.ITEMS))) {
                        spawnVillager(villagerLocation(Worlds.ELEMENTALS, NPCs.ITEMS), NPCs.ITEMS);
                    }
                    if (!npcExists(villagerLocation(Worlds.ELEMENTALS, NPCs.UPGRADES))) {
                        spawnVillager(villagerLocation(Worlds.ELEMENTALS, NPCs.UPGRADES), NPCs.UPGRADES);
                    }
                }

                if (Bukkit.getWorld("Corals") != null) {
                    if (!npcExists(villagerLocation(Worlds.CORALS, NPCs.ITEMS))) {
                        spawnVillager(villagerLocation(Worlds.CORALS, NPCs.ITEMS), NPCs.ITEMS);
                    }
                    if (!npcExists(villagerLocation(Worlds.CORALS, NPCs.UPGRADES))) {
                        spawnVillager(villagerLocation(Worlds.CORALS, NPCs.UPGRADES), NPCs.UPGRADES);
                    }
                }

                if (Bukkit.getWorld("Seasons") != null) {
                    if (!npcExists(villagerLocation(Worlds.SEASONS, NPCs.ITEMS))) {
                        spawnVillager(villagerLocation(Worlds.SEASONS, NPCs.ITEMS), NPCs.ITEMS);
                    }
                    if (!npcExists(villagerLocation(Worlds.SEASONS, NPCs.UPGRADES))) {
                        spawnVillager(villagerLocation(Worlds.SEASONS, NPCs.UPGRADES), NPCs.UPGRADES);
                    }
                }

                if (Bukkit.getWorld("Castle") != null) {
                    if (!npcExists(villagerLocation(Worlds.CASTLE, NPCs.ITEMS))) {
                        spawnVillager(villagerLocation(Worlds.CASTLE, NPCs.ITEMS), NPCs.ITEMS);
                    }
                    if (!npcExists(villagerLocation(Worlds.CASTLE, NPCs.UPGRADES))) {
                        spawnVillager(villagerLocation(Worlds.CASTLE, NPCs.UPGRADES), NPCs.UPGRADES);
                    }
                }

                if (Bukkit.getWorld("Genesis") != null) {
                    if (!npcExists(villagerLocation(Worlds.GENESIS, NPCs.ITEMS))) {
                        spawnVillager(villagerLocation(Worlds.GENESIS, NPCs.ITEMS), NPCs.ITEMS);
                    }
                    if (!npcExists(villagerLocation(Worlds.GENESIS, NPCs.UPGRADES))) {
                        spawnVillager(villagerLocation(Worlds.GENESIS, NPCs.UPGRADES), NPCs.UPGRADES);
                    }
                }
            }
        }.runTaskLater(Main.getInstance(), 3600);
    }
}












