package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Material.*;

import java.util.Random;

public class DeathListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final Material[] lostOnDeathList = {
            DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS, DIAMOND_SWORD, DIAMOND_AXE,
            IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS,
            CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS
    };

    private boolean getThemPlayers(Player p, double x, double y, double z, double ax, double ay, double az) {
        return p.getWorld().getNearbyEntities(new Location(p.getWorld(), x, y, z), ax, ay, az).contains(p);
    }

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


    private void teleportBasedOnLocation(Player p, double baseY, double spawnY, double pSpawnX, double pSpawnZ, double npSpawnX, double npSpawnZ, double nSpawnX, double nSpawnZ, double pnSpawnX, double pnSpawnZ, double neg, double pos) {
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


    public void teleportToSpawnMethod(Player p) {
        double maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        p.setFireTicks(0);
        pData.setStreak(0);

        new BukkitRunnable() {
            @Override
            public void run() {
                p.setHealth(maxHealth);
            }
        }.runTaskLater(Main.getInstance(), 1);

        if (p.getWorld().getName().equals("Elementals")) {
            teleportBasedOnLocation(p, 82, 114, 11.5, 14.5, -9.5, 10.5, -8.5, -8.5, 12.5, -12.5, -7.5, 8.5);
        }

        if (p.getWorld().getName().equals("Corals")) {
            teleportBasedOnLocation(p, 82, 114, 11.5, 14.5, -9.5, 10.5, -8.5, -8.5, 12.5, -12.5, -7.5, 8.5);
        }

        if (p.getWorld().getName().equals("Seasons")) {
            teleportBasedOnLocation(p, 82, 114, 12.5, 13.5, -9.5, 10.5, -9.5, -9.5, 12.5, -12.5, -7.5, 8.5);
        }

        if (p.getWorld().getName().equals("Castle")) {
            teleportBasedOnLocation(p, 71, 95, 12.5, 12.5, -11.5, 12.5, -11.5, -11.5, 12.5, -11.5, -7.5, 8.5);
        }

        if (p.getWorld().getName().equals("Genesis")) {
            teleportBasedOnLocation(p, 43, 86, 17.5, 15.5, -14.5, 16.5, -15.5, -14.5, 15.5, -15.5, -8.5, 9.5);
        }
    }

    @EventHandler
    public void onPlayerDeath(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (zl.playerCheck(entity)) {
            if (Main.getInstance().getSpawnListener().spawnCheck(entity.getLocation())) {
                e.setCancelled(true);
                return;
            }

            Player p = (Player) e.getEntity();
            PlayerData pData = Main.getInstance().getPlayerData(p);
            PlayerInventory inv = p.getInventory();
            double finalDMG = e.getFinalDamage();
            double currentHP = p.getHealth();

            if (e.getCause() != DamageCause.FALL && (currentHP - finalDMG <= 0)) {
                e.setCancelled(true);

                for (ItemStack item : inv.getArmorContents()) {
                    String name = item.getType().name();

                    if (zl.itemCheck(item) && item.getItemMeta().getEnchants().isEmpty()) {
                        if ((name.contains("DIAMOND") || name.contains("IRON")) && new Random().nextInt(4) == 3) {
                            p.getWorld().dropItemNaturally(p.getLocation(), item);
                        }
                    }
                }

                for (Material material : lostOnDeathList) {
                    inv.remove(material);
                }

                switch (new Random().nextInt(3)) {
                    case 0:
                        if (!zl.itemCheck(inv.getChestplate())) inv.setChestplate(new ItemStack(IRON_CHESTPLATE, 1));
                        break;
                    case 1:
                        if (!zl.itemCheck(inv.getLeggings())) inv.setLeggings(new ItemStack(IRON_LEGGINGS, 1));
                        break;
                    case 2:
                        if (!zl.itemCheck(inv.getBoots())) inv.setBoots(new ItemStack(IRON_BOOTS, 1));
                        break;
                }

                if (!zl.itemCheck(inv.getChestplate())) inv.setChestplate(new ItemStack(CHAINMAIL_CHESTPLATE, 1));
                if (!zl.itemCheck(inv.getLeggings())) inv.setLeggings(new ItemStack(CHAINMAIL_LEGGINGS, 1));
                if (!zl.itemCheck(inv.getBoots())) inv.setBoots(new ItemStack(CHAINMAIL_BOOTS, 1));
                if (!inv.contains(BOW)) inv.addItem(new ItemStack(BOW, 1));
                if (!inv.contains(IRON_SWORD) && !pData.hasPerkEquipped(Perks.BARBARIAN)) inv.addItem(new ItemStack(IRON_SWORD, 1));
                //barbarian axe is given in PerkListenersAndUtils

                teleportToSpawnMethod(p);
            }
        }
    }
}

//perk-related death handling is in PerkListenersAndUtils






