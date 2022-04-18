package me.zelha.thepit.admin.commands;

import me.zelha.thepit.zelenums.NPCs;
import me.zelha.thepit.zelenums.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.List;

public class NPCCheckCommand implements CommandExecutor {//nope.

    private boolean npcAbsent(Location location) {
        List<Entity> entityList = location.getWorld().getEntities();

        for (Entity entity : entityList) {
            if (entity.getLocation().equals(location)) {
                return false;
            }
        }
        return true;
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
            itemsVillager.addScoreboardTag("items");
        } else if (type == NPCs.UPGRADES) {
            Villager upgradesVillager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
            upgradesVillager.setAI(false);
            upgradesVillager.setProfession(Villager.Profession.NONE);
            upgradesVillager.setPersistent(true);
            upgradesVillager.setSilent(true);
            upgradesVillager.addScoreboardTag("z-entity");
            upgradesVillager.addScoreboardTag("upgrades");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String worldName = p.getWorld().getName();

            if (npcAbsent(villagerLocation(Worlds.findByName(worldName), NPCs.ITEMS))) {
                spawnVillager(villagerLocation(Worlds.findByName(worldName), NPCs.ITEMS), NPCs.ITEMS);
                p.sendMessage("§aItems Villager successfully spawned!");
            } else {
                p.sendMessage("§cItems Villager is not absent.");
            }
            if (npcAbsent(villagerLocation(Worlds.findByName(worldName), NPCs.UPGRADES))) {
                spawnVillager(villagerLocation(Worlds.findByName(worldName), NPCs.UPGRADES), NPCs.UPGRADES);
                p.sendMessage("§aUpgrades Villager successfully spawned!");
            } else {
                p.sendMessage("§cUpgrades Villager is not absent.");
            }
        }
        return true;
    }
}












