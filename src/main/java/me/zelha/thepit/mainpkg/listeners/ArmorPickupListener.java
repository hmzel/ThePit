package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static org.bukkit.Material.*;

public class ArmorPickupListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    private final Material[] armorWeight0 = {
            CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS,
            LEATHER_HELMET
    };
    private final Material[] armorWeight1 = {
            IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS
    };
    private final Material[] armorWeight2 = {
            DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS
    };

    private int determineWeight(ItemStack item) {
        Material type = item.getType();

        for (Material material : armorWeight0) {
            if (material == type) return 0;
        }

        for (Material material : armorWeight1) {
            if (material == type) return 1;
        }

        for (Material material : armorWeight2) {
            if (material == type) return 2;
        }
        return 13;
    }

    private void itemPlacementHandler(PlayerInventory inventory, EquipmentSlot slot, ItemStack item) {
        if (!zl.itemCheck(inventory.getItem(slot))) {
            inventory.setItem(slot, item);
        } else if (determineWeight(inventory.getItem(slot)) < determineWeight(item)) {
            inventory.setItem(zl.firstEmptySlot(inventory), inventory.getItem(slot));
            inventory.setItem(slot, item);
        } else if (!inventory.contains(item)) {
            inventory.setItem(zl.firstEmptySlot(inventory), item);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {

        if (!zl.playerCheck(e.getEntity())) return;
        if (determineWeight(e.getItem().getItemStack()) == 13) return;

        Player p = (Player) e.getEntity();
        PlayerInventory inv = p.getInventory();
        ItemStack item = e.getItem().getItemStack();

        zl.fakePickup(p, e.getItem(), 16);
        e.getItem().setPickupDelay(9999999);
        e.setCancelled(true);

        Material type = item.getType();
        String name = type.name();

        if (name.contains("HELMET")) {
            itemPlacementHandler(inv, EquipmentSlot.HEAD, item);
        } else if (name.contains("CHESTPLATE")) {
            itemPlacementHandler(inv, EquipmentSlot.CHEST, item);
        } else if (name.contains("LEGGINGS")) {
            itemPlacementHandler(inv, EquipmentSlot.LEGS, item);
        } else if (name.contains("BOOTS")) {
            itemPlacementHandler(inv, EquipmentSlot.FEET, item);
        }
    }
}




















