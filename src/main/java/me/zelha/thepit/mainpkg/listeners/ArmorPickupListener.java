package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
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

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {

        if (!zl.playerCheck(e.getEntity())) return;

        Player p = (Player) e.getEntity();
        PlayerInventory inv = p.getInventory();
        ItemStack item = e.getItem().getItemStack();
        Material type = item.getType();
        String name = type.name();


    }
}




















