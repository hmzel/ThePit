package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.DamageLog;
import me.zelha.thepit.mainpkg.data.KillRecap;
import me.zelha.thepit.utils.ZelLogic;
import me.zelha.thepit.zelenums.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BoundingBox;

import java.util.Arrays;

import static org.bukkit.Material.*;
import static org.bukkit.event.EventPriority.LOWEST;

public class GeneralListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final DeathListener deathUtils = Main.getInstance().getDeathUtils();
    private final Material[] undroppable = {
            IRON_SWORD, BOW
    };
    private final Material[] disappearOnDrop = {
        CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS
    };
    private final String[] inventoryNames = {
            "Non-permanent items", "Permanent upgrades", "Choose a perk", "Are you sure?", "Killstreaks", "Choose a killstreakĀ§2", "Choose a killstreakĀ§1"
    };

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        if (!zl.playerCheck(e.getEntity())) return;
        if (e.getItem().getItemStack().getType() != ARROW) return;

        PlayerInventory inv = ((Player) e.getEntity()).getInventory();
        ItemStack item = e.getItem().getItemStack();

        zl.fakePickup((Player) e.getEntity(), e.getItem(), 16);
        e.getItem().setPickupDelay(999999);
        e.setCancelled(true);

        for (ItemStack invItem : inv.getStorageContents()) {
            if (!zl.itemCheck(invItem)) continue;
            if (!invItem.isSimilar(item) || invItem.getAmount() == invItem.getMaxStackSize()) continue;

            if (invItem.getAmount() + item.getAmount() > item.getMaxStackSize()) {
                item.setAmount(item.getAmount() - (invItem.getMaxStackSize() - invItem.getAmount()));
                invItem.setAmount(invItem.getMaxStackSize());
                inv.setItem(zl.firstEmptySlot(inv), item);
            } else {
                invItem.setAmount(invItem.getAmount() + item.getAmount());
            }

            return;
        }

        inv.setItem(zl.firstEmptySlot(inv), item);
    }

    @EventHandler
    public void onArrowEntityPickup(PlayerPickupArrowEvent e) {
        PlayerInventory inv = e.getPlayer().getInventory();
        ItemStack item = e.getItem().getItemStack();

        zl.fakePickup(e.getPlayer(), e.getArrow(), 62);
        e.getArrow().setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        e.setCancelled(true);

        for (ItemStack invItem : inv.getStorageContents()) {
            if (!zl.itemCheck(invItem)) continue;
            if (!invItem.isSimilar(item) || invItem.getAmount() == invItem.getMaxStackSize()) continue;

            invItem.setAmount(invItem.getAmount() + item.getAmount());
            return;
        }

        inv.setItem(zl.firstEmptySlot(inv), item);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (Arrays.asList(disappearOnDrop).contains(e.getItemDrop().getItemStack().getType())) {
            e.getItemDrop().remove();
            return;
        }

        if (Arrays.asList(undroppable).contains(e.getItemDrop().getItemStack().getType())) e.setCancelled(true);
    }

    @EventHandler(priority = LOWEST)
    public void onVoid(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (p.getLocation().getY() > 64) return;

        KillRecap.addDamageLog(p, new DamageLog(p.getHealth(), "Fell out", false));
        p.damage(p.getHealth());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (p.getLocation().add(0, -1, 0).getBlock().getType() == BARRIER || !BoundingBox.of(new Location(p.getWorld(), 0, 0, 0), 228, 1000, 228).contains(p.getLocation().toVector())) {
            zl.teleportToSpawnMethod(p);
            p.sendMessage("Ā§cCongrats you went out of the map!");
        }
    }

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }

    @EventHandler
    public void onBlockChange(BlockSpreadEvent e) {
        if (e.getBlock().getType() == GRASS_BLOCK || e.getBlock().getType() == DIRT) e.setCancelled(true);
    }

    @EventHandler
    public void onFlow(BlockFromToEvent e) {
        if (e.getBlock().getType() == LAVA || e.getBlock().getType() == WATER) e.setCancelled(true);
    }

    @EventHandler(priority = LOWEST)
    public void onFire(EntityDamageEvent e) {
        if (!zl.playerCheck(e.getEntity())) return;

        if (e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            e.setCancelled(true);
            e.getEntity().setFireTicks(0);
        }
    }

    @EventHandler(priority = LOWEST)
    public void onMerchantInventory(InventoryOpenEvent e) {
        if (e.getInventory().getType() == InventoryType.MERCHANT) e.setCancelled(true);
    }

    @EventHandler
    public void onClickInCraftingGrid(InventoryClickEvent e) {
        if (e.getSlotType() != InventoryType.SlotType.CRAFTING) return;

        e.setCancelled(true);
        ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        for (Integer slot : e.getRawSlots()) {
            if (e.getView().getSlotType(slot) == InventoryType.SlotType.CRAFTING) {
                e.setCancelled(true);
                ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return;
            }
        }

        if (e.getInventory() == e.getView().getBottomInventory()) return;

        for (String name : inventoryNames) {
            if (e.getView().getTitle().equals(name)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (Worlds.findByName(p.getWorld().getName()) == null) {
            p.teleport(new Location(Bukkit.getWorld("elementals"), 0.5, 114, 9.5));
        }

        if (!Main.getInstance().getPlayerData(p).hasCombatLogged()) return;

        p.sendMessage("Ā§cĀ§lALERT! Ā§rĀ§cInventory/bounty reset for quitting mid-fight!");
        p.sendMessage("Ā§eĀ§lWARNING! Ā§rĀ§eThis action is logged for moderation.");
        Main.getInstance().getPlayerData(p).setCombatLogged(false);
    }

    @EventHandler(priority = LOWEST)
    public void onLeave(PlayerQuitEvent e) {
        zl.pitReset(e.getPlayer());
    }
}
















