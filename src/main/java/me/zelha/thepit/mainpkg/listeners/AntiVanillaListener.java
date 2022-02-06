package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;

import static org.bukkit.Material.*;

public class AntiVanillaListener implements Listener {

    ZelLogic zl = Main.getInstance().getZelLogic();

    Material[] undroppable = {
            IRON_SWORD, BOW
    };

    @EventHandler
    public void onArrowItemPickup(EntityPickupItemEvent e) {

        if (!zl.playerCheck(e.getEntity())) return;
        if (e.getItem().getItemStack().getType() != ARROW) return;
        //yes. i did this just for arrows. because its in regular pit. why? only god knows.

        Player p = (Player) e.getEntity();
        PlayerInventory inv = p.getInventory();
        ItemStack item = e.getItem().getItemStack();

        zl.fakePickup(p, e.getItem(), 16);
        e.getItem().setPickupDelay(999999);
        e.setCancelled(true);

        for (ItemStack invItem : inv.getStorageContents()) {
            if (zl.itemCheck(invItem)) {
                if (invItem.isSimilar(item) && invItem.getAmount() != invItem.getMaxStackSize()) {
                    if (invItem.getAmount() + item.getAmount() > item.getMaxStackSize()) {
                        item.setAmount(item.getAmount() - (invItem.getMaxStackSize() - invItem.getAmount()));
                        invItem.setAmount(invItem.getMaxStackSize());
                        inv.setItem(zl.firstEmptySlot(inv), item);
                    } else {
                        invItem.setAmount(invItem.getAmount() + item.getAmount());
                    }
                    return;
                }
            }
        }

        inv.setItem(zl.firstEmptySlot(inv), item);
    }

    @EventHandler
    public void onArrowEntityPickup(PlayerPickupArrowEvent e) {

        if (!zl.playerCheck(e.getPlayer())) return;
        //why did mini do this. why am i doing this. why am i so stubborn. why

        Player p = e.getPlayer();
        PlayerInventory inv = p.getInventory();
        ItemStack item = e.getItem().getItemStack();

        zl.fakePickup(p, e.getArrow(), 62);
        e.getArrow().setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        e.setCancelled(true);

        for (ItemStack invItem : inv.getStorageContents()) {
            if (zl.itemCheck(invItem)) {
                if (invItem.isSimilar(item) && invItem.getAmount() != invItem.getMaxStackSize()) {
                    invItem.setAmount(invItem.getAmount() + item.getAmount());
                    return;
                }
            }
        }

        inv.setItem(zl.firstEmptySlot(inv), item);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {

        if (Arrays.asList(undroppable).contains(e.getItemDrop().getItemStack().getType())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }

    @EventHandler
    public void onBlockChange(BlockPhysicsEvent e) {
        if (e.getSourceBlock().getType() == GRASS_BLOCK) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFlow(BlockFromToEvent e) {
        if (e.getBlock().getType() == LAVA || e.getBlock().getType() == WATER) e.setCancelled(true);
    }
}
















