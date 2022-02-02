package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import net.minecraft.network.protocol.game.PacketPlayOutCollect;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Material.ARROW;
import static org.bukkit.Material.GRASS_BLOCK;

public class AntiVanillaListener implements Listener {

    ZelLogic zl = Main.getInstance().getZelLogic();

    private int firstEmptySlot(PlayerInventory inv) {
        ItemStack[] invItems = inv.getStorageContents();

        for (int i = 9; i < 36; i++) {
            if (!zl.itemCheck(invItems[i])) return i;
        }

        for (int i = 0; i < 9; i++) {
            if (!zl.itemCheck(invItems[i])) return i;
        }
        return -1;
    }

    @EventHandler
    public void onDrop(EntityPickupItemEvent e) {

        if (!zl.playerCheck(e.getEntity())) return;
        if (e.getItem().getItemStack().getType() != ARROW) return;
        //yes. i did this just for arrows. because its in regular pit. why? only god knows.

        Player p = (Player) e.getEntity();
        PlayerInventory inv = p.getInventory();
        Item itemEntity = e.getItem();
        ItemStack item = itemEntity.getItemStack();

        for (Entity entity : p.getNearbyEntities(16, 16, 16)) {
            if (entity instanceof Player) {
                CraftPlayer craftPlayer = (CraftPlayer) entity;
                craftPlayer.getHandle().b.sendPacket(new PacketPlayOutCollect(itemEntity.getEntityId(), p.getEntityId(), item.getAmount()));
            }
        }

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
                        inv.setItem(firstEmptySlot(inv), item);
                    } else {
                        invItem.setAmount(invItem.getAmount() + item.getAmount());
                    }
                    return;
                }
            }
        }

        inv.setItem(firstEmptySlot(inv), item);
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
















