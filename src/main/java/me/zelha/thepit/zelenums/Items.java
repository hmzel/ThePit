package me.zelha.thepit.zelenums;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum Items {
    DIAMOND_SWORD(Material.DIAMOND_SWORD, "Diamond Sword", Arrays.asList(
            "§9+20% damage vs bountied"
    )
            , 150
            , 1),
    OBSIDIAN(Material.OBSIDIAN, "Obsidian", Arrays.asList(
            "§7Remains for 120 seconds."
    )
            , 40
            , 8),
    GOLDEN_PICKAXE(Material.GOLDEN_PICKAXE, "Golden Pickaxe", Arrays.asList(
            "§7Breaks a 5-high pillar of",
            "§7obsidian when 2-tapping it."
    )
            , 500
            , 1),
    DIAMOND_CHESTPLATE(Material.DIAMOND_CHESTPLATE, "Diamond Chestplate", Arrays.asList(
            "§7Auto-equips on buy!"
    )
            , 500
            , 1),
    DIAMOND_BOOTS(Material.DIAMOND_BOOTS, "Diamond Boots", Arrays.asList(
            "§7Auto-equips on buy!"
    )
            , 300
            , 1);

    private final Material material;
    private final String name;
    private final List<String> lore;
    private final int cost;
    private final int amount;

    Items(Material material, String name, List<String> lore, int cost, int amount) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.cost = cost;
        this.amount = amount;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getCost() {
        return cost;
    }

    public int getAmount() {
        return amount;
    }
}
