package me.zelha.thepit.upgrades.nonpermanent;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.NPCInteractEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.ZelLogic;
import me.zelha.thepit.zelenums.NPCs;
import me.zelha.thepit.zelenums.ShopItems;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Material.IRON_SWORD;


public class ItemsVillagerListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    @EventHandler
    public void onNPCInteract(NPCInteractEvent e) {
        if (e.getNPC() == NPCs.ITEMS) openGUI(e.getPlayer());
    }

    @EventHandler
    public void itemsGUIInteract(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("Non-permanent items")) return;
        if (e.getClickedInventory() == e.getView().getBottomInventory()) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;

        Player p = (Player) e.getWhoClicked();
        ShopItems item = ShopItems.findByMaterial(e.getCurrentItem().getType());
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
            inv.setItem(inv.first(IRON_SWORD), item.getBoughtItem());
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

    private void openGUI(Player p) {
        Inventory itemsGUI = Bukkit.createInventory(p, 27, "Non-permanent items");
        PlayerData pData = Main.getInstance().getPlayerData(p);
        int index = 11;
        String color;

        for (ShopItems item : ShopItems.values()) {
            List<String> lore = new ArrayList<>(item.getShopLore());

            lore.add("\n");
            lore.add("§7§oLost on death.");
            lore.add("§7Cost: §6" + item.getCost() + "g");

            if (pData.getGold() - item.getCost() >= 0) {
                color = "§e";
                lore.add("§eClick to purchase!");
            } else {
                lore.add("§cNot enough gold!");
                color = "§c";
            }

            itemsGUI.setItem(index, zl.itemBuilder(item.getMaterial(), item.getAmount(), color + item.getShopName(), lore));
            index++;
        }

        p.openInventory(itemsGUI);
    }
}















