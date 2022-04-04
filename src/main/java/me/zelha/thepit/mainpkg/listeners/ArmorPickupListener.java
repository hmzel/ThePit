package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;
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

    private int determineWeight(Material type) {
        for (Material material : armorWeight0) if (material == type) return 0;
        for (Material material : armorWeight1) if (material == type) return 1;
        for (Material material : armorWeight2) if (material == type) return 2;

        return 13;
    }

    private void itemPlacementHandler(Player player, EquipmentSlot slot, Item item) {
        PlayerInventory inventory = player.getInventory();
        boolean doFakePickup = false;

        if (!zl.itemCheck(inventory.getItem(slot))) {
            inventory.setItem(slot, item.getItemStack());
            doFakePickup = true;
        } else if (determineWeight(inventory.getItem(slot).getType()) < determineWeight(item.getItemStack().getType())) {
            inventory.setItem(zl.firstEmptySlot(inventory), inventory.getItem(slot));
            inventory.setItem(slot, item.getItemStack());
            doFakePickup = true;
        } else if (!inventory.contains(item.getItemStack().getType())) {
            inventory.setItem(zl.firstEmptySlot(inventory), item.getItemStack());
            doFakePickup = true;
        }

        if (doFakePickup) {
            zl.fakePickup(player, item, 16);
            item.setPickupDelay(9999999);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!zl.playerCheck(e.getEntity())) return;
        if (determineWeight(e.getItem().getItemStack().getType()) == 13) return;

        Player p = (Player) e.getEntity();
        Item item = e.getItem();
        String name = item.getItemStack().getType().name();

        e.setCancelled(true);

        if (name.contains("HELMET")) {
            itemPlacementHandler(p, EquipmentSlot.HEAD, item);
        } else if (name.contains("CHESTPLATE")) {
            itemPlacementHandler(p, EquipmentSlot.CHEST, item);
        } else if (name.contains("LEGGINGS")) {
            itemPlacementHandler(p, EquipmentSlot.LEGS, item);
        } else if (name.contains("BOOTS")) {
            itemPlacementHandler(p, EquipmentSlot.FEET, item);
        }
    }
}




















