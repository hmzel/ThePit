package me.zelha.thepit.zelenums;

import me.zelha.thepit.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ShopItems {
    DIAMOND_SWORD(Material.DIAMOND_SWORD, "Diamond Sword", Collections.singletonList(
            "§9+20% damage vs bountied"
    )
            , 150
            , 1
            , Main.getInstance().getZelLogic().itemBuilder(Material.DIAMOND_SWORD, 1)),
    OBSIDIAN(Material.OBSIDIAN, "Obsidian", Collections.singletonList(
            "§7Remains for 120 seconds."
    )
            , 40
            , 8
            , Main.getInstance().getZelLogic().itemBuilder(Material.OBSIDIAN, 8, null, null)),
    GOLDEN_PICKAXE(Material.GOLDEN_PICKAXE, "Golden Pickaxe", Arrays.asList(
            "§7Breaks a 5-high pillar of",
            "§7obsidian when 2-tapping it."
    )
            , 500
            , 1
            , Main.getInstance().getZelLogic().itemBuilder(Material.GOLDEN_PICKAXE, 1, "§6Golden Pickaxe", Arrays.asList(
            "§7Breaks a 5-high pillar of",
            "§7obsidian when 2-tapping it."
    ))),
    DIAMOND_CHESTPLATE(Material.DIAMOND_CHESTPLATE, "Diamond Chestplate", Collections.singletonList(
            "§7Auto-equips on buy!"
    )
            , 500
            , 1
            , Main.getInstance().getZelLogic().itemBuilder(Material.DIAMOND_CHESTPLATE, 1)),
    DIAMOND_BOOTS(Material.DIAMOND_BOOTS, "Diamond Boots", Collections.singletonList(
            "§7Auto-equips on buy!"
    )
            , 300
            , 1
            , Main.getInstance().getZelLogic().itemBuilder(Material.DIAMOND_BOOTS, 1));

    private final Material material;
    private final String shopName;
    private final List<String> shopLore;
    private final int cost;
    private final int amount;
    private final ItemStack boughtItem;

    ShopItems(Material material, String shopName, List<String> shopLore, int cost, int amount, ItemStack boughtItem) {
        this.material = material;
        this.shopName = shopName;
        this.shopLore = shopLore;
        this.cost = cost;
        this.amount = amount;
        this.boughtItem = boughtItem;
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
}
