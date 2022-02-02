package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import net.minecraft.network.protocol.game.PacketPlayOutCollect;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Material.ARROW;
import static org.bukkit.Material.GRASS_BLOCK;

public class AntiVanillaListener implements Listener {

    ZelLogic zl = Main.getInstance().getZelLogic();

    @EventHandler
    public void onArrowItemPickup(EntityPickupItemEvent e) {

        if (!zl.playerCheck(e.getEntity())) return;
        if (e.getItem().getItemStack().getType() != ARROW) return;
        //yes. i did this just for arrows. because its in regular pit. why? only god knows.

        Player p = (Player) e.getEntity();
        CraftPlayer craftP = (CraftPlayer) p;
        PlayerInventory inv = p.getInventory();
        Item itemEntity = e.getItem();
        ItemStack item = itemEntity.getItemStack();

        for (Entity entity : p.getNearbyEntities(16, 16, 16)) {
            if (entity instanceof Player) {
                CraftPlayer craftPlayer = (CraftPlayer) entity;
                craftPlayer.getHandle().b.sendPacket(new PacketPlayOutCollect(itemEntity.getEntityId(), p.getEntityId(), item.getAmount()));
            }
        }

        craftP.getHandle().b.sendPacket(new PacketPlayOutCollect(itemEntity.getEntityId(), p.getEntityId(), item.getAmount()));

        e.getItem().setPickupDelay(999999);
        e.setCancelled(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                itemEntity.remove();
            }
        }.runTaskLater(Main.getInstance(), 10);

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
        CraftPlayer craftP = (CraftPlayer) p;
        PlayerInventory inv = p.getInventory();
        AbstractArrow arrowEntity = e.getArrow();
        ItemStack item = e.getItem().getItemStack();

        for (Entity entity : p.getNearbyEntities(62, 62, 62)) {
            if (entity instanceof Player) {
                CraftPlayer craftPlayer = (CraftPlayer) entity;
                craftPlayer.getHandle().b.sendPacket(new PacketPlayOutCollect(arrowEntity.getEntityId(), p.getEntityId(), 1));
            }
        }

        craftP.getHandle().b.sendPacket(new PacketPlayOutCollect(arrowEntity.getEntityId(), p.getEntityId(), 1));

        e.getArrow().setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        e.setCancelled(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                arrowEntity.remove();
            }
        }.runTaskLater(Main.getInstance(), 10);

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
    public void onHungerLoss(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }

    @EventHandler
    public void onGrassChange(BlockPhysicsEvent e) {

        if (e.getSourceBlock().getType() == GRASS_BLOCK) {
            e.setCancelled(true);
        }
    }
}
















