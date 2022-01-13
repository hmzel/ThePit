package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.NPCs;
import me.zelha.thepit.zelenums.Worlds;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.bukkit.Material.*;


public class ItemsVillagerListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    private List<String> loreBuilder(Player p, Material material) {
        List<String> lore = new ArrayList<>();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        int cost = 0;

        if (material == DIAMOND_SWORD) {
            lore.add("§9+20% damage vs bountied");
        } else if (material == OBSIDIAN) {
            lore.add("§7Remains for 120 seconds.");
        } else if (material == GOLDEN_PICKAXE) {
            lore.add("§7Breaks a 5-high pillar of");
            lore.add("§7obsidian when 2-tapping it.");
        } else if (material == DIAMOND_CHESTPLATE || material == DIAMOND_BOOTS) {
            lore.add("§7Auto-equips on buy!");
        }

        lore.add("\n");
        lore.add("§7§oLost on death.");

        if (material == DIAMOND_SWORD) {
            lore.add("§7Cost: §6150g");
            cost = 150;
        } else if (material == OBSIDIAN) {
            lore.add("§7Cost: §640g");
            cost = 40;
        } else if (material == GOLDEN_PICKAXE || material == DIAMOND_CHESTPLATE) {
            lore.add("§7Cost: §6500g");
            cost = 500;
        } else if (material == DIAMOND_BOOTS) {
            lore.add("§7Cost: §6300g");
            cost = 300;
        }

        if ((pData.getGold() - cost) >= 0) {
            lore.add("§eClick to purchase!");
        } else {
            lore.add("§cNot enough gold!");
        }

        return lore;
    }

    private void openGUI(Player p) {
        Inventory itemsGUI = Bukkit.createInventory(p, 27, "Non-permanent items");

        itemsGUI.setItem(11, zl.itemBuilder(DIAMOND_SWORD, 1, "§eDiamond Sword", loreBuilder(p, DIAMOND_SWORD)));
        itemsGUI.setItem(12, zl.itemBuilder(OBSIDIAN, 8, "§eObsidian", loreBuilder(p, OBSIDIAN)));
        itemsGUI.setItem(13, zl.itemBuilder(GOLDEN_PICKAXE, 1, "§eGolden Pickaxe", loreBuilder(p, GOLDEN_PICKAXE)));
        itemsGUI.setItem(14, zl.itemBuilder(DIAMOND_CHESTPLATE, 1, "§eDiamond Chestplate", loreBuilder(p, DIAMOND_CHESTPLATE)));
        itemsGUI.setItem(15, zl.itemBuilder(DIAMOND_BOOTS, 1, "§eDiamond Boots", loreBuilder(p, DIAMOND_BOOTS)));

        p.openInventory(itemsGUI);
    }

    private void itemPurchase(Player p, ItemStack item, int cost) {
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if ((pData.getGold() - cost) >= 0) {
            HashMap<Integer, ItemStack> invFullCheck = p.getInventory().addItem(item);

            if (!invFullCheck.containsValue(item)) {
                pData.setGold(pData.getGold() - cost);
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
            } else {
                p.sendMessage("§cYour inventory is full!");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                p.closeInventory();
            }
        } else {
            p.sendMessage("§cNot enough gold!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            p.closeInventory();
        }
    }

    private void itemPurchase(Player p, ItemStack item, int cost, EquipmentSlot slot) {
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (!zl.itemCheck(p.getInventory().getItem(slot)) || p.getInventory().getItem(slot).getType() != item.getType()) {

            if ((pData.getGold() - cost) >= 0) {
                p.getInventory().setItem(slot, item);
                pData.setGold(pData.getGold() - cost);
                p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_DIAMOND, 1, 1);
            } else {
                p.sendMessage("§cNot enough gold!");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                p.closeInventory();
            }
        } else {
            itemPurchase(p, item, cost);
        }
    }

    private void itemPurchase(Player p, ItemStack item, int cost, int slot) {
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if ((pData.getGold() - cost) >= 0) {
            p.getInventory().setItem(slot, item);
            pData.setGold(pData.getGold() - cost);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
        } else {
            p.sendMessage("§cNot enough gold!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            p.closeInventory();
        }
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

            if (zl.noObstructions(Worlds.valueOfName(worldName), NPCs.ITEMS).contains(x, y, z)) {
                openGUI(p);
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent e) {//yes
        String worldName = e.getPlayer().getWorld().getName();
        double x = e.getRightClicked().getLocation().getX();
        double y = e.getRightClicked().getLocation().getY();
        double z = e.getRightClicked().getLocation().getZ();

        if (zl.noObstructions(Worlds.valueOfName(worldName), NPCs.ITEMS).contains(x, y, z)) {
            openGUI(e.getPlayer());
        }
    }

    @EventHandler
    public void onLeftClick(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damagerEntity = e.getDamager();

        if (zl.playerCheck(damagerEntity)) {
            Player damager = (Player) e.getDamager();
            String worldName = damager.getWorld().getName();
            double x = damaged.getLocation().getX();
            double y = damaged.getLocation().getY();
            double z = damaged.getLocation().getZ();

            if (zl.noObstructions(Worlds.valueOfName(worldName), NPCs.ITEMS).contains(x, y, z)) {
                openGUI(damager);
            }
        }
    }

    @EventHandler
    public void itemsGUIInteract(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getView().getTitle().equals("Non-permanent items") && e.getClickedInventory() != e.getView().getBottomInventory()) {
            e.setCancelled(true);

            if (e.getCurrentItem() != null) {
                switch (e.getCurrentItem().getType()) {
                    case DIAMOND_SWORD:
                        if (p.getInventory().contains(IRON_SWORD)) {
                            itemPurchase(p, zl.itemBuilder(DIAMOND_SWORD, 1), 150, p.getInventory().first(IRON_SWORD));
                            p.getInventory().remove(IRON_SWORD);
                        } else {
                            itemPurchase(p, zl.itemBuilder(DIAMOND_SWORD, 1), 150);
                        }
                        break;
                    case OBSIDIAN:
                        itemPurchase(p, zl.itemBuilder(OBSIDIAN, 8), 40);
                        break;
                    case GOLDEN_PICKAXE:
                        itemPurchase(p, zl.itemBuilder(GOLDEN_PICKAXE, 1, "§6Golden Pickaxe", Arrays.asList(
                                "§7Breaks a 5-high pillar of",
                                "§7Obsidian when 2-tapping it."
                        )), 500);
                        break;
                    case DIAMOND_CHESTPLATE:
                        itemPurchase(p, zl.itemBuilder(DIAMOND_CHESTPLATE, 1), 500, EquipmentSlot.CHEST);
                        break;
                    case DIAMOND_BOOTS:
                        itemPurchase(p, zl.itemBuilder(DIAMOND_BOOTS, 1), 300, EquipmentSlot.FEET);
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {

        if (e.getView().getTitle().equals("Non-permanent items") && e.getInventory() != e.getView().getBottomInventory()) {
        e.setCancelled(true);
        }
    }
}















