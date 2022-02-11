package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.zelenums.Worlds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

import static org.bukkit.Material.*;

public class DeathListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final Material[] lostOnDeathList = {
            DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS, DIAMOND_SWORD, DIAMOND_AXE,
            IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS,
            CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS
    };

    public void teleportToSpawnMethod(Player p) {
        Worlds world = Worlds.findByName(p.getWorld().getName());

        if (world == null) {
            p.sendMessage("§5World not supported.");
            return;
        }

        p.setFireTicks(0);
        Main.getInstance().getPlayerData(p).setStreak(0);

        new BukkitRunnable() {
            @Override
            public void run() {
                p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            }
        }.runTaskLater(Main.getInstance(), 1);

        double spawnY;

        if (world == Worlds.CASTLE) {
            spawnY = 95;
        } else if (world == Worlds.GENESIS) {
            spawnY = 86;
        } else {
            spawnY = 114;
        }

        if (p.getLocation().distance(new Location(p.getWorld(), 0, p.getLocation().getY(), 0)) < 9) {
            double spawnPerimeter;

            if (world != Worlds.GENESIS) spawnPerimeter = 8.5; else spawnPerimeter = 9.5;

            switch (new Random().nextInt(4)) {
                case 0:
                    p.teleport(new Location(p.getWorld(), 0.5, spawnY, -(spawnPerimeter - 1), 0, 0));
                    break;
                case 1:
                    p.teleport(new Location(p.getWorld(), 0.5, spawnY, spawnPerimeter, 180, 0));
                    break;
                case 2:
                    p.teleport(new Location(p.getWorld(), -(spawnPerimeter - 1), spawnY, 0.5, -90, 0));
                    break;
                case 3:
                    p.teleport(new Location(p.getWorld(), spawnPerimeter, spawnY, 0.5, 90, 0));
                    break;
            }
            return;
        }

        double[] southEastSpawn;
        double[] northEastSpawn;
        double[] northWestSpawn;
        double[] southWestSpawn;

        if (world == Worlds.CASTLE) {
            southEastSpawn = new double[] {12.5, 12.5};
            southWestSpawn = new double[] {-11.5, 12.5};
            northWestSpawn = new double[] {-11.5, -11.5};
            northEastSpawn = new double[] {12.5, -11.5};
        } else if (world == Worlds.GENESIS) {
            southEastSpawn = new double[] {17.5, 15.5};
            southWestSpawn = new double[] {-14.5, 16.5};
            northWestSpawn = new double[] {-15.5, -14.5};
            northEastSpawn = new double[] {15.5, -15.5};
        } else {
            southEastSpawn = new double[] {11.5, 14.5};
            southWestSpawn = new double[] {-9.5, 10.5};
            northWestSpawn = new double[] {-8.5, -8.5};
            northEastSpawn = new double[] {12.5, -12.5};
        }

        switch (new Random().nextInt(4)) {
            case 0:
                p.teleport(new Location(p.getWorld(), southEastSpawn[0], spawnY, southEastSpawn[1], -45, 0));
                break;
            case 1:
                p.teleport(new Location(p.getWorld(), southWestSpawn[0], spawnY, southWestSpawn[1], 45, 0));
                break;
            case 2:
                p.teleport(new Location(p.getWorld(), northWestSpawn[0], spawnY, northWestSpawn[1], 135, 0));
                break;
            case 3:
                p.teleport(new Location(p.getWorld(), northEastSpawn[0], spawnY, northEastSpawn[1], -135, 0));
                break;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (!zl.playerCheck(entity)) return;
        if (e.getCause() == DamageCause.FALL) return;

        if (zl.spawnCheck(entity.getLocation())) {
            e.setCancelled(true);
            return;
        }

        Player p = (Player) e.getEntity();
        PlayerInventory inv = p.getInventory();
        double finalDMG = e.getFinalDamage();
        double currentHP = p.getHealth();

        if (currentHP - finalDMG <= 0) {
            e.setCancelled(true);
            teleportToSpawnMethod(p);

            for (ItemStack item : inv.getArmorContents()) {
                if (zl.itemCheck(item) && item.getItemMeta() != null && item.getItemMeta().getEnchants().isEmpty()) {
                    String name = item.getType().name();

                    if ((name.contains("DIAMOND") || name.contains("IRON")) && new Random().nextInt(4) == 3) {
                        p.getWorld().dropItemNaturally(p.getLocation(), zl.itemBuilder(item.getType(), 1));
                    }
                }
            }

            for (Material material : lostOnDeathList) {
                inv.remove(material);
                if (zl.itemCheck(inv.getHelmet()) && inv.getHelmet().getType() == material) inv.setHelmet(new ItemStack(AIR));
                if (zl.itemCheck(inv.getChestplate()) && inv.getChestplate().getType() == material) inv.setChestplate(new ItemStack(AIR));
                if (zl.itemCheck(inv.getLeggings()) && inv.getLeggings().getType() == material) inv.setLeggings(new ItemStack(AIR));
                if (zl.itemCheck(inv.getBoots()) && inv.getBoots().getType() == material) inv.setBoots(new ItemStack(AIR));
            }

            switch (new Random().nextInt(3)) {
                case 0:
                    if (!zl.itemCheck(inv.getChestplate())) inv.setChestplate(zl.itemBuilder(IRON_CHESTPLATE, 1));
                    break;
                case 1:
                    if (!zl.itemCheck(inv.getLeggings())) inv.setLeggings(zl.itemBuilder(IRON_LEGGINGS, 1));
                    break;
                case 2:
                    if (!zl.itemCheck(inv.getBoots())) inv.setBoots(zl.itemBuilder(IRON_BOOTS, 1));
                    break;
            }

            if (!zl.itemCheck(inv.getChestplate())) inv.setChestplate(zl.itemBuilder(CHAINMAIL_CHESTPLATE, 1));
            if (!zl.itemCheck(inv.getLeggings())) inv.setLeggings(zl.itemBuilder(CHAINMAIL_LEGGINGS, 1));
            if (!zl.itemCheck(inv.getBoots())) inv.setBoots(zl.itemBuilder(CHAINMAIL_BOOTS, 1));
            //sword & bow & arrow are handled in PerkListenersAndUtils.perkReset for consistency's sake
        }
    }
}//perk-related death handling is in PerkListenersAndUtils






