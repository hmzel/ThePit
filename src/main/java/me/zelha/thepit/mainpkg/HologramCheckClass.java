package me.zelha.thepit.mainpkg;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class HologramCheckClass {//unfinished. i cant be bothered to get every single coordinate for every single map rn

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    private void spawnIfAbsent(String name, Location location) {
        if (!zl.hologramExists(name, location)) {
            zl.spawnHologram(name, location);
        }
    }

    public void hologramCheck() {

        new BukkitRunnable() {
            @Override
            public void run() {

                if (Bukkit.getWorld("Elementals") != null) {
                    World elementals = Bukkit.getWorld("Elementals");

                    spawnIfAbsent("§e§lUNLOCKED FEATURES", new Location(elementals, -6.5, 116.9, 11.6));
                    spawnIfAbsent("§7Gain levels to unlock more", new Location(elementals, -6.5, 114.1, 11.6));
                    spawnIfAbsent("§a§lUPGRADES", new Location(elementals, -1.5, 116.19, 12.5));
                    spawnIfAbsent("§7Permanent", new Location(elementals, -1.5, 115.88, 12.5));
                    spawnIfAbsent("§6§lITEMS", new Location(elementals, 2.5, 116.15, 12.5));
                    spawnIfAbsent("§7Non-permanent", new Location(elementals, 2.5, 115.85, 12.5));
                    spawnIfAbsent("§3§lSTATS", new Location(elementals, 11.5, 116.16, 5.5));
                    spawnIfAbsent("§7View your stats", new Location(elementals, 11.5, 115.85, 5.5));
                    spawnIfAbsent("§b§lTOP ACTIVE PLAYERS", new Location(elementals, 13.22, 119.35, 0.5));
                    spawnIfAbsent("§7Pit Level", new Location(elementals, 13.22, 118.97, 0.5));
                    spawnIfAbsent("§7All-time §ebest §7players!", new Location(elementals, 13.22, 114.38, 0.5));
                    spawnIfAbsent("§7§oPlayers who logged in this week", new Location(elementals, 13.22, 114.0, 0.5));
                    spawnIfAbsent("§a§lQUEST MASTER", new Location(elementals, 9.5, 116.16, -4.5));
                    spawnIfAbsent("§7Quests & Contracts", new Location(elementals, 9.5, 115.85, -4.5));
                    spawnIfAbsent("§e§lPRESTIGE", new Location(elementals, 0.5, 117.16, -11.35));
                    spawnIfAbsent("§7Resets & Renown", new Location(elementals, 0.5, 116.85, -11.35));
                    spawnIfAbsent("§2§lTHE KEEPER", new Location(elementals, -11.5, 116.16, -4.5));
                    spawnIfAbsent("§7Back to lobby", new Location(elementals, -11.5, 115.85, -4.5));
                    spawnIfAbsent("§d§lMYSTIC WELL", new Location(elementals, -12.5, 116.53, 0.5));
                    spawnIfAbsent("§7Item Enchants", new Location(elementals, -12.5, 116.25, 0.5));
                    spawnIfAbsent("§5§lENDER CHEST", new Location(elementals, -12.5, 115.28, 7.5));
                    spawnIfAbsent("§7Store items forever", new Location(elementals, -12.5, 115, 7.5));
                    spawnIfAbsent("§eThe Hypixel Pit", new Location(elementals, 0.5, 116, 0.5));
                    spawnIfAbsent("§a§lJUMP! §c§lFIGHT!", new Location(elementals, 0.5, 115.69, 0.5));
                }

                if (Bukkit.getWorld("Corals") != null) {
                    World corals = Bukkit.getWorld("Corals");

                    spawnIfAbsent("§e§lUNLOCKED FEATURES", new Location(corals, -6.0, 116.9, 11.5));
                    spawnIfAbsent("§7Gain levels to unlock more", new Location(corals, -6.0, 114.1, 11.5));
                }
            }//fuck this shite
        }.runTaskLater(Main.getInstance(), 3600);
    }
}



















