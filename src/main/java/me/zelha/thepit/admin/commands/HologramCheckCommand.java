package me.zelha.thepit.admin.commands;

import me.zelha.thepit.zelenums.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class HologramCheckCommand implements CommandExecutor {

    public void spawnHologramIfAbsent(String name, Location location, Player player) {
        List<Entity> entityList = location.getWorld().getEntities();

        for (Entity entity : entityList) {
            if (entity.getLocation().equals(location) && entity.getName().equals(name)) {
                player.sendMessage("§cHologram " + name + " §cis not absent.");
                return;
            }
        }

        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        hologram.setVisible(false);
        hologram.setBasePlate(false);
        hologram.setMarker(true);
        hologram.setCustomName(name);
        hologram.setCustomNameVisible(true);
        hologram.setInvulnerable(true);
        hologram.setSilent(true);
        hologram.setPersistent(true);
        hologram.setGravity(false);
        hologram.addScoreboardTag("z-entity");
        player.sendMessage("§aHologram " + name + " §asuccessfully spawned!");
    }

    private void spawnIfAbsent(String name, Worlds world, Player player) {
        switch (world) {//unfinished. i cant be bothered to get every single coordinate for every single map rn
            case ELEMENTALS:
                World elementals = Bukkit.getWorld("Elementals");
                switch (name) {
                    case "§e§lUNLOCKED FEATURES":
                        spawnHologramIfAbsent(name, new Location(elementals, -6.5, 116.9, 11.6), player);
                        break;
                    case "§7Gain levels to unlock more":
                        spawnHologramIfAbsent(name, new Location(elementals, -6.5, 114.1, 11.6), player);
                        break;
                    case "§a§lUPGRADES":
                        spawnHologramIfAbsent(name, new Location(elementals, -1.5, 116.19, 12.5), player);
                        break;
                    case "§7Permanent":
                        spawnHologramIfAbsent(name, new Location(elementals, -1.5, 115.88, 12.5), player);
                        break;
                    case "§6§lITEMS":
                        spawnHologramIfAbsent(name, new Location(elementals, 2.5, 116.15, 12.5), player);
                        break;
                    case "§7Non-permanent":
                        spawnHologramIfAbsent(name, new Location(elementals, 2.5, 115.85, 12.5), player);
                        break;
                    case "§3§lSTATS":
                        spawnHologramIfAbsent(name, new Location(elementals, 11.5, 116.16, 5.5), player);
                        break;
                    case "§7View your stats":
                        spawnHologramIfAbsent(name, new Location(elementals, 11.5, 115.85, 5.5), player);
                        break;
                    case "§b§lTOP ACTIVE PLAYERS":
                        spawnHologramIfAbsent(name, new Location(elementals, 13.22, 119.35, 0.5), player);
                        break;
                    case "§7Pit Level":
                        spawnHologramIfAbsent(name, new Location(elementals, 13.22, 118.97, 0.5), player);
                        break;
                    case "§7All-time §ebest §7players!":
                        spawnHologramIfAbsent(name, new Location(elementals, 13.22, 114.38, 0.5), player);
                        break;
                    case "§7§oPlayers who logged in this week":
                        spawnHologramIfAbsent(name, new Location(elementals, 13.22, 114.0, 0.5), player);
                        break;
                    case "§a§lQUEST MASTER":
                        spawnHologramIfAbsent(name, new Location(elementals, 9.5, 116.16, -4.5), player);
                        break;
                    case "§7Quests & Contracts":
                        spawnHologramIfAbsent(name, new Location(elementals, 9.5, 115.85, -4.5), player);
                        break;
                    case "§e§lPRESTIGE":
                        spawnHologramIfAbsent(name, new Location(elementals, 0.5, 117.16, -11.35), player);
                        break;
                    case "§7Resets & Renown":
                        spawnHologramIfAbsent(name, new Location(elementals, 0.5, 116.85, -11.35), player);
                        break;
                    case "§2§lTHE KEEPER":
                        spawnHologramIfAbsent(name, new Location(elementals, -11.5, 116.16, -4.5), player);
                        break;
                    case "§7Back to lobby":
                        spawnHologramIfAbsent(name, new Location(elementals, -11.5, 115.85, -4.5), player);
                        break;
                    case "§d§lMYSTIC WELL":
                        spawnHologramIfAbsent(name, new Location(elementals, -12.5, 116.53, 0.5), player);
                        break;
                    case "§7Item Enchants":
                        spawnHologramIfAbsent(name, new Location(elementals, -12.5, 116.25, 0.5), player);
                        break;
                    case "§5§lENDER CHEST":
                        spawnHologramIfAbsent(name, new Location(elementals, -12.5, 115.28, 7.5), player);
                        break;
                    case "§7Store items forever":
                        spawnHologramIfAbsent(name, new Location(elementals, -12.5, 115, 7.5), player);
                        break;
                    case "§eThe Hypixel Pit":
                        spawnHologramIfAbsent(name, new Location(elementals, 0.5, 116, 0.5), player);
                        break;
                    case "§a§lJUMP! §c§lFIGHT!":
                        spawnHologramIfAbsent(name, new Location(elementals, 0.5, 115.69, 0.5), player);
                        break;
                }
                break;
            default:
                player.sendMessage("§cWorld not supported!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String worldname = p.getWorld().getName();

            spawnIfAbsent("§e§lUNLOCKED FEATURES", Worlds.findByName(worldname), p);
            spawnIfAbsent("§7Gain levels to unlock more", Worlds.findByName(worldname), p);
            spawnIfAbsent("§a§lUPGRADES", Worlds.findByName(worldname), p);
            spawnIfAbsent("§7Permanent", Worlds.findByName(worldname), p);
            spawnIfAbsent("§6§lITEMS", Worlds.findByName(worldname), p);
            spawnIfAbsent("§7Non-permanent", Worlds.findByName(worldname), p);
            spawnIfAbsent("§3§lSTATS", Worlds.findByName(worldname), p);
            spawnIfAbsent("§7View your stats", Worlds.findByName(worldname), p);
            spawnIfAbsent("§b§lTOP ACTIVE PLAYERS", Worlds.findByName(worldname), p);
            spawnIfAbsent("§7Pit Level", Worlds.findByName(worldname), p);
            spawnIfAbsent("§7All-time §ebest §7players!", Worlds.findByName(worldname), p);
            spawnIfAbsent("§7§oPlayers who logged in this week", Worlds.findByName(worldname), p);
            spawnIfAbsent("§a§lQUEST MASTER", Worlds.findByName(worldname), p);
            spawnIfAbsent("§7Quests & Contracts", Worlds.findByName(worldname), p);
            spawnIfAbsent("§e§lPRESTIGE", Worlds.findByName(worldname), p);
            spawnIfAbsent("§7Resets & Renown", Worlds.findByName(worldname), p);
            spawnIfAbsent("§2§lTHE KEEPER", Worlds.findByName(worldname), p);
            spawnIfAbsent("§7Back to lobby", Worlds.findByName(worldname), p);
            spawnIfAbsent("§d§lMYSTIC WELL", Worlds.findByName(worldname), p);
            spawnIfAbsent("§7Item Enchants", Worlds.findByName(worldname), p);
            spawnIfAbsent("§5§lENDER CHEST", Worlds.findByName(worldname), p);
            spawnIfAbsent("§7Store items forever", Worlds.findByName(worldname), p);
            spawnIfAbsent("§eThe Hypixel Pit", Worlds.findByName(worldname), p);
            spawnIfAbsent("§a§lJUMP! §c§lFIGHT!", Worlds.findByName(worldname), p);
        }
        return true;
    }
}



















