package me.zelha.thepit.upgrades.nonpermanent.villager;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.NPCs;
import me.zelha.thepit.zelenums.ShopItems;
import me.zelha.thepit.zelenums.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Material.DIAMOND_SWORD;
import static org.bukkit.Material.IRON_SWORD;


public class ItemsVillagerListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    private List<String> loreBuilder(Player p, ShopItems item) {
        PlayerData pData = Main.getInstance().getPlayerData(p);
        List<String> lore = new ArrayList<>(item.getShopLore());

        lore.add("\n");
        lore.add("§7§oLost on death.");
        lore.add("§7Cost: §6" + item.getCost() + "g");

        if (pData.getGold() - item.getCost() >= 0) lore.add("§eClick to purchase!"); else lore.add("§cNot enough gold!");

        return lore;
    }

    private void openGUI(Player p) {
        Inventory itemsGUI = Bukkit.createInventory(p, 27, "Non-permanent items");
        double gold = Main.getInstance().getPlayerData(p).getGold();
        int index = 11;
        String color;

        for (ShopItems item : ShopItems.values()) {
            if (gold - item.getCost() >= 0) color = "§e"; else color = "§c";

            itemsGUI.setItem(index, zl.itemBuilder(item.getMaterial(), item.getAmount(), color + item.getShopName(), loreBuilder(p, item)));
            index++;
        }

        p.openInventory(itemsGUI);
    }

    private void itemPurchase(Player p, ShopItems item) {
        PlayerData pData = Main.getInstance().getPlayerData(p);
        PlayerInventory inv = p.getInventory();

        if (pData.getGold() - item.getCost() < 0) {
            p.sendMessage("§cNot enough gold!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            p.closeInventory();
            return;
        } else if (zl.firstEmptySlot(inv) == -1) {
            p.sendMessage("§cYour inventory is full!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            p.closeInventory();
            return;
        }

        p.sendMessage("§a§lPURCHASE! §6" + item.getShopName());

        if (item == ShopItems.DIAMOND_SWORD && inv.contains(IRON_SWORD)) {
            inv.setItem(inv.first(IRON_SWORD), zl.itemBuilder(DIAMOND_SWORD, 1));
            inv.remove(IRON_SWORD);
            pData.setGold(pData.getGold() - item.getCost());
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
            return;
        }

        if (item.getSlot() != null) {
            ItemStack slotItem = inv.getItem(item.getSlot());
            String typeString = (zl.itemCheck(slotItem)) ? slotItem.getType().name() : "AIR";

            if (!zl.itemCheck(slotItem) || typeString.contains("IRON") || typeString.contains("CHAINMAIL")) {
                if (zl.itemCheck(slotItem)) inv.setItem(zl.firstEmptySlot(inv), slotItem);

                inv.setItem(item.getSlot(), item.getBoughtItem());
                pData.setGold(pData.getGold() - item.getCost());
                p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_DIAMOND, 1, 1);
                return;
            }
        }

        inv.addItem(item.getBoughtItem());
        pData.setGold(pData.getGold() - item.getCost());
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
    }

    @EventHandler
    public void onDirectRightClick(InventoryOpenEvent e) {
        if (e.getView().getTopInventory().getType() == InventoryType.MERCHANT) {
            Player p = (Player) e.getPlayer();
            Villager villager = (Villager) e.getInventory().getHolder();
            String worldName = e.getPlayer().getWorld().getName();
            double x = villager.getLocation().getX();
            double y = villager.getLocation().getY();
            double z = villager.getLocation().getZ();

            e.setCancelled(true);

            if (zl.noObstructions(Worlds.findByName(worldName), NPCs.ITEMS).contains(x, y, z)) openGUI(p);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent e) {
        String worldName = e.getPlayer().getWorld().getName();
        double x = e.getRightClicked().getLocation().getX();
        double y = e.getRightClicked().getLocation().getY();
        double z = e.getRightClicked().getLocation().getZ();

        if (zl.noObstructions(Worlds.findByName(worldName), NPCs.ITEMS).contains(x, y, z)) openGUI(e.getPlayer());
    }

    @EventHandler
    public void onLeftClick(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damagerEntity = e.getDamager();

        if (!zl.playerCheck(damagerEntity)) return;

        Player damager = (Player) e.getDamager();
        String worldName = damager.getWorld().getName();
        double x = damaged.getLocation().getX();
        double y = damaged.getLocation().getY();
        double z = damaged.getLocation().getZ();

        if (zl.noObstructions(Worlds.findByName(worldName), NPCs.ITEMS).contains(x, y, z)) openGUI(damager);
    }

    @EventHandler
    public void itemsGUIInteract(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("Non-permanent items")) return;
        if (e.getClickedInventory() == e.getView().getBottomInventory()) return;

        Player p = (Player) e.getWhoClicked();

        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;

        itemPurchase(p, ShopItems.findByMaterial(e.getCurrentItem().getType()));
    }
}















