package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KillListener implements Listener {

    ZelLogic zl = Main.getInstance().getZelLogic();

    private boolean itemCheck(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() != Material.AIR;
    }

    public int calculateEXP(Player damaged, Player damager) {
        int exp = 5;
        int baseModifier = 0;
        double percentageModifier = 1;

        PlayerData damagedData = Main.getInstance().getStorage().getPlayerData(damaged.getUniqueId().toString());

        if (damagedData.getPrestige() == 0) {
            percentageModifier = percentageModifier - 0.09;
        }

        return Math.toIntExact(Math.round((exp + baseModifier) * percentageModifier));//this line looks janky
    }

    double calculateGold(Player damaged, Player damager) {
        int gold = 10;
        int baseModifier = 0;
        double percentageModifier = 1;

        PlayerData damagedData = Main.getInstance().getStorage().getPlayerData(damaged.getUniqueId().toString());

        if (itemCheck(damaged.getInventory().getHelmet())
           && damaged.getInventory().getHelmet().getType() == Material.DIAMOND_HELMET) {
            baseModifier++;
        }
        if (itemCheck(damaged.getInventory().getChestplate())
           && damaged.getInventory().getChestplate().getType() == Material.DIAMOND_CHESTPLATE) {
            baseModifier++;
        }
        if (itemCheck(damaged.getInventory().getLeggings())
           && damaged.getInventory().getLeggings().getType() == Material.DIAMOND_LEGGINGS) {
            baseModifier++;
        }
        if (itemCheck(damaged.getInventory().getBoots())
           && damaged.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS) {
            baseModifier++;
        }

        if (damagedData.getPrestige() == 0) {
            percentageModifier = percentageModifier - 0.09;
        }

        return (gold + baseModifier) * percentageModifier;
    }

    @EventHandler
    public void onPlayerDeath(EntityDamageByEntityEvent e) {
        Player damaged = (Player) e.getEntity();
        Player damager = (Player) e.getDamager();
        double finalDMG = e.getFinalDamage();
        double currentHP = damaged.getHealth();

        if (zl.playerCheck(damaged) && zl.playerCheck(damager) && e.getCause() != DamageCause.FALL && (currentHP - finalDMG) <= 0) {
            String uuid = damaged.getUniqueId().toString();
            PlayerData damagerData = Main.getInstance().getStorage().getPlayerData(damager.getUniqueId().toString());
            double calculatedGold = calculateGold(damaged, damager);

            damager.sendMessage("§a§lKILL! §7on " + zl.getColorBracketAndLevel(uuid) + " §7" + damaged.getName()
            + " §b+" + calculateEXP(damaged, damager) + "§bXP §6+" + zl.getFancyGoldString(calculatedGold) + "§6g");

            damagerData.setExp(damagerData.getExp() - calculateEXP(damaged, damager));
            damagerData.setGold(damagerData.getGold() + calculatedGold);
        }
    }
}
















