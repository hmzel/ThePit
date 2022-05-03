package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDeathEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.ZelLogic;
import me.zelha.thepit.zelenums.Worlds;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

import static org.bukkit.Material.*;

public class DeathListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final Random rng = new Random();
    private final Material[] lostOnDeathList = {
            DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS, DIAMOND_SWORD, DIAMOND_AXE,
            IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS,
            CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS,
            BEDROCK
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
                if (zl.playerCheck(p)) p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PitDeathEvent e) {
        Player dead = e.getDead();
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerInventory inv = dead.getInventory();

        for (ItemStack item : inv.getArmorContents()) {
            if (zl.itemCheck(item) && item.getItemMeta() != null && item.getItemMeta().getEnchants().isEmpty()) {
                String name = item.getType().name();

                if ((name.contains("DIAMOND") || name.contains("IRON")) && rng.nextInt(4) == 3) {
                    dead.getWorld().dropItemNaturally(dead.getLocation(), zl.itemBuilder(item.getType(), 1));
                }
            }
        }

        for (Material material : lostOnDeathList) {
            inv.remove(material);

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (zl.itemCheck(inv.getItem(slot)) && inv.getItem(slot).getType() == material) inv.setItem(slot, new ItemStack(AIR));
            }
        }

        switch (rng.nextInt(3)) {
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

        Player damager = Main.getInstance().getAssistUtils().getLastDamager(dead);

        if (damager != null) {
            dead.spigot().sendMessage(
                    new ComponentBuilder("§c§lDEATH! §7by " + zl.getColorBracketAndLevel(damager) + " §7" + damager.getName() + " §e§lVIEW RECAP")
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§eClick to view kill recap!")))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/killrecap " + dead.getUniqueId()))
                    .create()
            );
        } else {
            dead.sendMessage("§c§lDEATH!");
        }

        dead.sendTitle("§cYOU DIED", "", 0, 40, 20);
        dead.playSound(dead.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 0.4F, 1.8F);
        deadData.setDummyStatus(null);
        deadData.setCombatTimer(0);

        if (deadData.getBounty() != 0) {
            deadData.setStatus("bountied");
        } else {
            deadData.setStatus("idling");
        }

        zl.pitReset(dead);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (zl.playerCheck(dead)) teleportToSpawnMethod(dead);
            }
        }.runTaskLater(Main.getInstance(), 1);
    }
}






