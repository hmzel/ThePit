package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.*;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class ItemsVillagerListener implements Listener {

    ZelLogic zl = Main.getInstance().getZelLogic();

    private BoundingBox noObstructions(World world) {
        if (world.getName().equals("Elementals") || world.getName().equals("Corals") || world.getName().equals("Seasons")) {
            return BoundingBox.of(new Location(world, 2.5, 114, 12.5), 1, 2.5, 1);
        }
        if (world.getName().equals("Castle")) {
            return BoundingBox.of(new Location(world, 2.5, 95, 12.5), 1, 2.5, 1);
        }
        if (world.getName().equals("Genesis")) {
            return BoundingBox.of(new Location(world, 2.5, 86, 16.5), 1, 2.5, 1);
        }
        return new BoundingBox();
    }

    private List<String> loreBuilder(Player p, ItemStack item) {
        List<String> lore = new ArrayList<>();
        PlayerData pData = Main.getInstance().getStorage().getPlayerData(p.getUniqueId().toString());
        int cost = 0;

        if (item.getType() == Material.DIAMOND_SWORD) {
            lore.add("§9+20% damage vs bountied");
        }
        if (item.getType() == Material.OBSIDIAN) {
            lore.add("§7Remains for 120 seconds.");
        }
        if (item.getType() == Material.GOLDEN_PICKAXE) {
            lore.add("§7Breaks a 5-high pillar of");
            lore.add("§7obsidian when 2-tapping it.");
        }
        if (item.getType() == Material.DIAMOND_CHESTPLATE || item.getType() == Material.DIAMOND_BOOTS) {
            lore.add("§7Auto-equips on buy!");
        }

        lore.add("\n");
        lore.add("§7§oLost on death.");

        if (item.getType() == Material.DIAMOND_SWORD) {
            lore.add("§7Cost: §6150g");
            cost = 150;
        }
        if (item.getType() == Material.OBSIDIAN) {
            lore.add("§7Cost: §640g");
            cost = 40;
        }
        if (item.getType() == Material.GOLDEN_PICKAXE || item.getType() == Material.DIAMOND_CHESTPLATE) {
            lore.add("§7Cost: §6500g");
            cost = 500;
        }
        if (item.getType() == Material.DIAMOND_BOOTS) {
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

        ItemStack dSword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta dSwordMeta = dSword.getItemMeta();
        dSwordMeta.setDisplayName("§eDiamond Sword");
        dSwordMeta.setLore(loreBuilder(p, dSword));
        dSword.setItemMeta(dSwordMeta);

        ItemStack obby = new ItemStack(Material.OBSIDIAN, 8);
        ItemMeta obbyMeta = obby.getItemMeta();
        obbyMeta.setDisplayName("§eObsidian");
        obbyMeta.setLore(loreBuilder(p, obby));
        obby.setItemMeta(obbyMeta);

        ItemStack gPickaxe = new ItemStack(Material.GOLDEN_PICKAXE);
        ItemMeta gPickaxeMeta = gPickaxe.getItemMeta();
        gPickaxeMeta.setDisplayName("§eGolden Pickaxe");
        gPickaxeMeta.setLore(loreBuilder(p, gPickaxe));
        gPickaxe.setItemMeta(gPickaxeMeta);

        ItemStack dChest = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta dChestMeta = dChest.getItemMeta();
        dChestMeta.setDisplayName("§eDiamond Chestplate");
        dChestMeta.setLore(loreBuilder(p, dChest));
        dChest.setItemMeta(dChestMeta);

        ItemStack dBoot = new ItemStack(Material.DIAMOND_BOOTS);
        ItemMeta dBootMeta = dBoot.getItemMeta();
        dBootMeta.setDisplayName("§eDiamond Boots");
        dBootMeta.setLore(loreBuilder(p, dBoot));
        dBoot.setItemMeta(dBootMeta);

        itemsGUI.setItem(11, dSword);
        itemsGUI.setItem(12, obby);
        itemsGUI.setItem(13, gPickaxe);
        itemsGUI.setItem(14, dChest);
        itemsGUI.setItem(15, dBoot);

        p.openInventory(itemsGUI);
    }

    private void itemPurchase(Player p, ItemStack item, int cost) {
        PlayerData pData = Main.getInstance().getStorage().getPlayerData(p.getUniqueId().toString());

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
        PlayerData pData = Main.getInstance().getStorage().getPlayerData(p.getUniqueId().toString());

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
        PlayerData pData = Main.getInstance().getStorage().getPlayerData(p.getUniqueId().toString());

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
            World world = e.getPlayer().getWorld();
            double x = villager.getLocation().getX();
            double y = villager.getLocation().getY();
            double z = villager.getLocation().getZ();

            e.setCancelled(true);

            if (noObstructions(world).contains(x, y, z)) {
                openGUI(p);
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent e) {//yes
        World world = e.getPlayer().getWorld();
        double x = e.getRightClicked().getLocation().getX();
        double y = e.getRightClicked().getLocation().getY();
        double z = e.getRightClicked().getLocation().getZ();

        if (noObstructions(world).contains(x, y, z)) {
            openGUI(e.getPlayer());
        }
    }

    @EventHandler
    public void onLeftClick(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damagerEntity = e.getDamager();

        if (zl.playerCheck(damagerEntity)) {
            Player damager = (Player) e.getDamager();
            World world = damager.getWorld();
            double x = damaged.getLocation().getX();
            double y = damaged.getLocation().getY();
            double z = damaged.getLocation().getZ();

            if (noObstructions(world).contains(x, y, z)) {
                openGUI(damager);
            }
        }
    }

    @EventHandler
    public void itemsGUIInteract(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if(e.getView().getTitle().equals("Non-permanent items") && e.getClickedInventory() != e.getView().getBottomInventory()) {
            e.setCancelled(true);

            if(e.getCurrentItem() != null) {
                switch (e.getSlot()) {
                    case 11:
                        ItemStack dSword = new ItemStack(Material.DIAMOND_SWORD);
                        ItemMeta dSwordMeta = dSword.getItemMeta();
                        dSwordMeta.setUnbreakable(true);
                        dSword.setItemMeta(dSwordMeta);

                        if (p.getInventory().contains(Material.IRON_SWORD)) {
                            itemPurchase(p, dSword, 150, p.getInventory().first(Material.IRON_SWORD));
                            p.getInventory().remove(Material.IRON_SWORD);
                        } else {
                            itemPurchase(p, dSword, 150);
                        }
                        break;
                    case 12:
                        ItemStack obby = new ItemStack(Material.OBSIDIAN, 8);
                        ItemMeta obbyMeta = obby.getItemMeta();
                        obbyMeta.setUnbreakable(true);
                        obby.setItemMeta(obbyMeta);

                        itemPurchase(p, obby, 40);
                        break;
                    case 13:
                        ItemStack gPickaxe = new ItemStack(Material.GOLDEN_PICKAXE);
                        ItemMeta gPickaxeMeta = gPickaxe.getItemMeta();
                        gPickaxeMeta.setUnbreakable(true);
                        gPickaxeMeta.setDisplayName("§6Golden Pickaxe");
                        gPickaxeMeta.setLore(Arrays.asList(
                                "§7Breaks a 5-high pillar of",
                                "§7Obsidian when 2-tapping it."
                        ));
                        gPickaxe.setItemMeta(gPickaxeMeta);

                        itemPurchase(p, gPickaxe, 500);
                        break;
                    case 14:
                        ItemStack dChest = new ItemStack(Material.DIAMOND_CHESTPLATE);
                        ItemMeta dChestMeta = dChest.getItemMeta();
                        dChestMeta.setUnbreakable(true);
                        dChest.setItemMeta(dChestMeta);

                        itemPurchase(p, dChest, 500, EquipmentSlot.CHEST);
                        break;
                    case 15:
                        ItemStack dBoot = new ItemStack(Material.DIAMOND_BOOTS);
                        ItemMeta dBootMeta = dBoot.getItemMeta();
                        dBootMeta.setUnbreakable(true);
                        dBoot.setItemMeta(dBootMeta);

                        itemPurchase(p, dBoot, 300, EquipmentSlot.FEET);
                        break;
                }
            }
        }
    }
}















