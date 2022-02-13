package me.zelha.thepit.zelenums;

import me.zelha.thepit.Main;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ShopItems {
    DIAMOND_SWORD(Material.DIAMOND_SWORD, "Diamond Sword", Collections.singletonList(
            "§9+20% damage vs bountied"
    )
            , 150
            , 1
            , Main.getInstance().getZelLogic().itemBuilder(Material.DIAMOND_SWORD, 1)
            , null),
    OBSIDIAN(Material.OBSIDIAN, "Obsidian", Collections.singletonList(
            "§7Remains for 120 seconds."
    )
            , 40
            , 8
            , Main.getInstance().getZelLogic().itemBuilder(Material.OBSIDIAN, 8, null, null)
            , null),
    GOLDEN_PICKAXE(Material.GOLDEN_PICKAXE, "Golden Pickaxe", Arrays.asList(
            "§7Breaks a 5-high pillar of",
            "§7obsidian when 2-tapping it."
    )
            , 500
            , 1
            , Main.getInstance().getZelLogic().itemBuilder(Material.GOLDEN_PICKAXE, 1, "§6Golden Pickaxe", Arrays.asList(
            "§7Breaks a 5-high pillar of",
            "§7obsidian when 2-tapping it."
    ))
            , null),
    DIAMOND_CHESTPLATE(Material.DIAMOND_CHESTPLATE, "Diamond Chestplate", Collections.singletonList(
            "§7Auto-equips on buy!"
    )
            , 500
            , 1
            , Main.getInstance().getZelLogic().itemBuilder(Material.DIAMOND_CHESTPLATE, 1)
            , EquipmentSlot.CHEST),
    DIAMOND_BOOTS(Material.DIAMOND_BOOTS, "Diamond Boots", Collections.singletonList(
            "§7Auto-equips on buy!"
    )
            , 300
            , 1
            , Main.getInstance().getZelLogic().itemBuilder(Material.DIAMOND_BOOTS, 1)
            , EquipmentSlot.FEET);

    private final Material material;
    private final String shopName;
    private final List<String> shopLore;
    private final int cost;
    private final int amount;
    private final ItemStack boughtItem;
    private final EquipmentSlot slot;

    ShopItems(Material material, String shopName, List<String> shopLore, int cost, int amount, ItemStack boughtItem, @Nullable EquipmentSlot slot) {
        this.material = material;
        this.shopName = shopName;
        this.shopLore = shopLore;
        this.cost = cost;
        this.amount = amount;
        this.boughtItem = boughtItem;
        this.slot = slot;
    }

    public Material getMaterial() {
        return material;
    }

    public String getShopName() {
        return shopName;
    }

    public List<String> getShopLore() {
        return shopLore;
    }

    public int getCost() {
        return cost;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getBoughtItem() {
        return boughtItem;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public static ShopItems findByMaterial(Material material) {
        for (ShopItems item : ShopItems.values()) {
            if (item.getMaterial() == material) return item;
        }
        return null;
    }
}
