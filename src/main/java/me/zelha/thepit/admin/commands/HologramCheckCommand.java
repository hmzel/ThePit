package me.zelha.thepit.admin.commands;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.zelenums.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HologramCheckCommand implements CommandExecutor {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    private void spawnIfAbsent(String name, Worlds world, Player player) {
        switch (world) {//unfinished. i cant be bothered to get every single coordinate for every single map rn
            case ELEMENTALS:
                World elementals = Bukkit.getWorld("Elementals");
                switch (name) {
                    case "§e§lUNLOCKED FEATURES":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, -6.5, 116.9, 11.6), player);
                        break;
                    case "§7Gain levels to unlock more":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, -6.5, 114.1, 11.6), player);
                        break;
                    case "§a§lUPGRADES":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, -1.5, 116.19, 12.5), player);
                        break;
                    case "§7Permanent":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, -1.5, 115.88, 12.5), player);
                        break;
                    case "§6§lITEMS":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, 2.5, 116.15, 12.5), player);
                        break;
                    case "§7Non-permanent":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, 2.5, 115.85, 12.5), player);
                        break;
                    case "§3§lSTATS":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, 11.5, 116.16, 5.5), player);
                        break;
                    case "§7View your stats":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, 11.5, 115.85, 5.5), player);
                        break;
                    case "§b§lTOP ACTIVE PLAYERS":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, 13.22, 119.35, 0.5), player);
                        break;
                    case "§7Pit Level":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, 13.22, 118.97, 0.5), player);
                        break;
                    case "§7All-time §ebest §7players!":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, 13.22, 114.38, 0.5), player);
                        break;
                    case "§7§oPlayers who logged in this week":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, 13.22, 114.0, 0.5), player);
                        break;
                    case "§a§lQUEST MASTER":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, 9.5, 116.16, -4.5), player);
                        break;
                    case "§7Quests & Contracts":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, 9.5, 115.85, -4.5), player);
                        break;
                    case "§e§lPRESTIGE":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, 0.5, 117.16, -11.35), player);
                        break;
                    case "§7Resets & Renown":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, 0.5, 116.85, -11.35), player);
                        break;
                    case "§2§lTHE KEEPER":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, -11.5, 116.16, -4.5), player);
                        break;
                    case "§7Back to lobby":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, -11.5, 115.85, -4.5), player);
                        break;
                    case "§d§lMYSTIC WELL":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, -12.5, 116.53, 0.5), player);
                        break;
                    case "§7Item Enchants":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, -12.5, 116.25, 0.5), player);
                        break;
                    case "§5§lENDER CHEST":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, -12.5, 115.28, 7.5), player);
                        break;
                    case "§7Store items forever":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, -12.5, 115, 7.5), player);
                        break;
                    case "§eThe Hypixel Pit":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, 0.5, 116, 0.5), player);
                        break;
                    case "§a§lJUMP! §c§lFIGHT!":
                        zl.spawnHologramIfAbsent(name, new Location(elementals, 0.5, 115.69, 0.5), player);
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

            spawnIfAbsent("§e§lUNLOCKED FEATURES", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§7Gain levels to unlock more", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§a§lUPGRADES", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§7Permanent", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§6§lITEMS", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§7Non-permanent", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§3§lSTATS", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§7View your stats", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§b§lTOP ACTIVE PLAYERS", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§7Pit Level", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§7All-time §ebest §7players!", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§7§oPlayers who logged in this week", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§a§lQUEST MASTER", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§7Quests & Contracts", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§e§lPRESTIGE", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§7Resets & Renown", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§2§lTHE KEEPER", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§7Back to lobby", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§d§lMYSTIC WELL", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§7Item Enchants", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§5§lENDER CHEST", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§7Store items forever", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§eThe Hypixel Pit", Worlds.valueOfName(worldname), p);
            spawnIfAbsent("§a§lJUMP! §c§lFIGHT!", Worlds.valueOfName(worldname), p);
        }
        return true;
    }
}



















