package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class DeathListener implements Listener {

    boolean getThemPlayers(Player p, double x, double y, double z, double ax, double ay, double az)
    {return p.getWorld().getNearbyEntities(new Location(p.getWorld(), x, y, z), ax, ay, az).contains(p);}

    ZelLogic zl = Main.getInstance().getZelLogic();

    private void randomMidSpawn(Player p, double y, double neg, double pos) {
        Random rng = new Random();
        int randomNumber = rng.nextInt(4) + 1;

        switch (randomNumber) {
            case 1:
                p.teleport(new Location(p.getWorld(), 0.5, y, neg, 0, 0));
                break;
            case 2:
                p.teleport(new Location(p.getWorld(), 0.5, y, pos, 180, 0));
                break;
            case 3:
                p.teleport(new Location(p.getWorld(), neg, y, 0.5, -90, 0));
                break;
            case 4:
                p.teleport(new Location(p.getWorld(), pos, y, 0.5, 90, 0));
                break;
        }
    }


    private void teleportToSpawnMethod(Player p, double baseY, double spawnY, double pSpawnX, double pSpawnZ, double npSpawnX, double npSpawnZ, double nSpawnX, double nSpawnZ, double pnSpawnX, double pnSpawnZ, double neg, double pos) {
        // p = positive, n = negative (coordinates) ^
        //dont blame me for the janky way this is set up its the builder's fault for being inconsistent with the maps

        if (getThemPlayers(p, 0.5, baseY, 0.5, 21.5, 200, 21.5)) {
            randomMidSpawn(p, spawnY, neg, pos);
        } else if (getThemPlayers(p, 64.5, baseY, 64.5, 64.5, 200, 64.5)) {
            p.teleport(new Location(p.getWorld(), pSpawnX, spawnY, pSpawnZ, -45, 0));
        } else if (getThemPlayers(p, -64.5, baseY, 64, 64.5, 200, 64.5)) {
            p.teleport(new Location(p.getWorld(), npSpawnX, spawnY, npSpawnZ, 45, 0));
        } else if (getThemPlayers(p, -64.5, baseY, -64.5, 64.5, 200, 64.5)) {
            p.teleport(new Location(p.getWorld(), nSpawnX, spawnY, nSpawnZ, 135, 0));
        } else if (getThemPlayers(p, 64.5, baseY, -64.5, 64.5, 200, 64.5)) {
            p.teleport(new Location(p.getWorld(), pnSpawnX, spawnY, pnSpawnZ, -135, 0));
        }
    }

    @EventHandler
    public void onPlayerDeath(EntityDamageEvent e) {
        Player p = (Player) e.getEntity();
        double finalDMG = e.getFinalDamage();
        double currentHP = p.getHealth();

        if (zl.playerCheck(p) && e.getCause() != DamageCause.FALL && (currentHP - finalDMG <= 0)) {
            double maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

            e.setCancelled(true);
            p.setFireTicks(0);

            new BukkitRunnable() {
                @Override
                public void run() {
                    p.setHealth(maxHealth);
                }
            }.runTaskLater(Main.getInstance(), 1);

            if (p.getWorld().getName().equals("Elementals")) {
                teleportToSpawnMethod(p, 82, 114, 11.5, 14.5, -9.5, 10.5, -8.5, -8.5, 12.5, -12.5, -7.5, 8.5);
            }

            if (p.getWorld().getName().equals("Corals")) {
                teleportToSpawnMethod(p, 82, 114, 11.5, 14.5, -9.5, 10.5, -8.5, -8.5, 12.5, -12.5, -7.5, 8.5);
            }

            if (p.getWorld().getName().equals("Seasons")) {
                teleportToSpawnMethod(p, 82, 114, 12.5, 13.5, -9.5, 10.5, -9.5, -9.5, 12.5, -12.5, -7.5, 8.5);
            }

            if (p.getWorld().getName().equals("Castle")) {
                teleportToSpawnMethod(p, 71, 95, 12.5, 12.5, -11.5, 12.5, -11.5, -11.5, 12.5, -11.5, -7.5, 8.5);
            }

            if (p.getWorld().getName().equals("Genesis")) {
                teleportToSpawnMethod(p, 43, 86, 17.5, 15.5, -14.5, 16.5, -15.5, -14.5, 15.5, -15.5, -8.5, 9.5);
            }
        }
    }
}
