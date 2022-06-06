package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDeathEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.ZelLogic;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.Sound;
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
            BEDROCK, OBSIDIAN, GOLDEN_PICKAXE
    };

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

        Player damager = deadData.getLastDamager();

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

        new BukkitRunnable() {
            @Override
            public void run() {
                if (zl.playerCheck(dead)) zl.teleportToSpawnMethod(dead);
            }
        }.runTaskLater(Main.getInstance(), 1);
    }
}






