package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
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
        Main.getInstance().getZelLogic().pitReset(p);

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

    private void deathMethod(Player player, boolean combatLogged) {
        PlayerInventory inv = player.getInventory();

        if (!combatLogged) {
            for (ItemStack item : inv.getArmorContents()) {
                if (zl.itemCheck(item) && item.getItemMeta() != null && item.getItemMeta().getEnchants().isEmpty()) {
                    String name = item.getType().name();

                    if ((name.contains("DIAMOND") || name.contains("IRON")) && new Random().nextInt(4) == 3) {
                        player.getWorld().dropItemNaturally(player.getLocation(), zl.itemBuilder(item.getType(), 1));
                    }
                }
            }
        }

        for (Material material : lostOnDeathList) {
            inv.remove(material);

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (zl.itemCheck(inv.getItem(slot)) && inv.getItem(slot).getType() == material) inv.setItem(slot, new ItemStack(AIR));
            }
        }

        if (!combatLogged) teleportToSpawnMethod(player);

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

        if (combatLogged) {
            player.sendMessage("§c§lALERT! §r§cInventory/bounty reset for quitting mid-fight!");
            player.sendMessage("§e§lWARNING! §r§eThis action is logged for moderation.");
            return;
        }

        Player damager = Main.getInstance().getAssistUtils().getLastDamager(player);

        if (damager != null) {
            player.spigot().sendMessage(new ComponentBuilder("§c§lDEATH! §7by " + zl.getColorBracketAndLevel(damager.getUniqueId().toString()) + " §7" + damager.getName() + " §e§lVIEW RECAP")
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§eClick to view kill recap!")))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/killrecap " + player.getUniqueId()))
                    .create());
        } else {
            player.sendMessage("§c§lDEATH!");
        }

        player.sendTitle("§cYOU DIED", "", 0, 40, 20);
        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 0.4F, 1.8F);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(EntityDamageEvent e) {
        if (!zl.playerCheck(e.getEntity())) return;
        if (e.getCause() == DamageCause.FALL) return;

        Player p = (Player) e.getEntity();

        if (zl.spawnCheck(p.getLocation())) {
            e.setCancelled(true);
            return;
        }

        if (p.getHealth() - e.getFinalDamage() <= 0) {
            e.setCancelled(true);
            deathMethod(p, false);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (Main.getInstance().getPlayerData(e.getPlayer()).getCombatLogged()) deathMethod(e.getPlayer(), true);

        Main.getInstance().getPlayerData(e.getPlayer()).setCombatLogged(false);
    }
}






