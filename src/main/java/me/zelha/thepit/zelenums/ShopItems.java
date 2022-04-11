package me.zelha.thepit.zelenums;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ShopItems {
    DIAMOND_SWORD(Material.DIAMOND_SWORD, "Diamond Sword", 150, 1, null,
            "§9+20% damage vs bountied"
    ),
    OBSIDIAN(Material.OBSIDIAN, "Obsidian", 40, 8, null,
            "§7Remains for 120 seconds."
    ),
    GOLDEN_PICKAXE(Material.GOLDEN_PICKAXE, "Golden Pickaxe", 500, 1, null,
            "§7Breaks a 5-high pillar of",
            "§7obsidian when 2-tapping it."
    ),
    DIAMOND_CHESTPLATE(Material.DIAMOND_CHESTPLATE, "Diamond Chestplate", 500, 1, EquipmentSlot.CHEST,
            "§7Auto-equips on buy!"
    ),
    DIAMOND_BOOTS(Material.DIAMOND_BOOTS, "Diamond Boots", 300, 1, EquipmentSlot.FEET,
            "§7Auto-equips on buy!"
    );

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final Material material;
    private final String shopName;
    private final int cost;
    private final int amount;
    private final EquipmentSlot slot;
    private final String[] shopLore;

    ShopItems(Material material, String shopName, int cost, int amount, @Nullable EquipmentSlot slot, String... shopLore) {
        this.material = material;
        this.shopName = shopName;
        this.cost = cost;
        this.amount = amount;
        this.slot = slot;
        this.shopLore = shopLore;
    }

    public Material getMaterial() {
        return material;
    }

    public String getShopName() {
        return shopName;
    }

    public List<String> getShopLore() {
        return new ArrayList<>(Arrays.asList(shopLore));
    }

    public int getCost() {
        return cost;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getBoughtItem() {
        switch (this) {
            case OBSIDIAN:
                return zl.itemBuilder(Material.DIAMOND_SWORD, 1);
            case DIAMOND_SWORD:
                return zl.itemBuilder(Material.OBSIDIAN, 8, null, null);
            case GOLDEN_PICKAXE:
                return zl.itemBuilder(Material.GOLDEN_PICKAXE, 1, "§6Golden Pickaxe", Arrays.asList(
                        "§7Breaks a 5-high pillar of",
                        "§7obsidian when 2-tapping it."
                ));
            case DIAMOND_CHESTPLATE:
                return zl.itemBuilder(Material.DIAMOND_CHESTPLATE, 1);
            case DIAMOND_BOOTS:
                return zl.itemBuilder(Material.DIAMOND_BOOTS, 1);
            default:
                return null;
        }
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
