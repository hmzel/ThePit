package me.zelha.thepit.zelenums;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.bukkit.Material.*;

public enum Perks {
    GOLDEN_HEADS("Golden Heads", PLAYER_HEAD, Arrays.asList(
            "§7Golden apples you earn turn into",
            "§6Golden Heads§7."
    )
            , 500
            , 10
            , 0),
    FISHING_ROD("Fishing Rod", Material.FISHING_ROD, Collections.singletonList(
            "§7Spawn with a fishing rod."
    )
            , 1000
            , 10
            , 0),
    LAVA_BUCKET("Lava Bucket", Material.LAVA_BUCKET, Collections.singletonList(
            "§7Spawn with a lava bucket."
    )
            , 1000
            , 10
            , 0),
    STRENGTH_CHAINING("Strength-Chaining", REDSTONE, Arrays.asList(
            "§c+8% damage §7for 7s stacking",
            "§7on kill."
    )
            , 2000
            , 20
            , 0),
    SAFETY_FIRST("Safety First", CHAINMAIL_HELMET, Collections.singletonList(
            "§7Spawn with a helmet."
    )
            , 3000
            , 30
            , 0),
    BARBARIAN("Barbarian", IRON_AXE, Arrays.asList(
            "§7Replaces your sword with a",
            "§7stronger axe."
    )
            , 3000
            , 30
            , 2),
    MINEMAN("Mineman", COBBLESTONE, Arrays.asList(
            "§7Spawn with §f24 cobblestone",
            "§7and a diamond pickaxe.",
            " ",
            "§7+§f3 blocks §7on kill."
    )
            , 3000
            , 20
            , 0),
    INSURANCE("Insurance", SADDLE, Arrays.asList(
            "§7If you die within §a2s §7of",
            "§7being damaged, §cfull heal §7and",
            "§7gain §9Resistance II §7(4s).",
            "§820s cooldown."
    )
            , 2000
            , 35
            , 0),
    TRICKLE_DOWN("Trickle-down", GOLD_INGOT, Arrays.asList(
            "§7Gold ingots reward §6+10g",
            "§7and heal §c2❤§7."
    )
            , 1000
            , 40
            , 0),
    LUCKY_DIAMOND("Lucky Diamond", DIAMOND, Arrays.asList(
            "§730% chance to upgrade dropped",
            "§7armor pieces from kills to",
            "§bdiamond§7.",
            " ",
            "§7Upgraded pieces warp to your",
            "§7inventory."
    )
            , 4000
            , 40
            , 0),
    SPAMMER("Spammer", BOW, Arrays.asList(
            "§7Get §f3 arrows §7on arrow hit.",
            " ",
            "§7Gain §63x base gold §7reward on",
            "§7targets you've shot an arrow in.",
            " ",
            "§7Earn §6+2g §7on assists."
    )
            , 4000
            , 40
            , 0),
    BOUNTY_HUNTER("Bounty Hunter", GOLDEN_LEGGINGS, Arrays.asList(
            "§6+4g §7on all kills.",
            "§7Earn bounty assist shares.",
            " ",
            "§c+1% damage§7/100g bounty on",
            "§7target."
    )
            , 2000
            , 50
            , 0),
    STREAKER("Streaker", WHEAT, Collections.singletonList(
            "§7Triple streak kill §bXP §7bonus."
    )
            , 8000
            , 50
            , 0),
    ASSISTANT_STREAKER("Assistant Streaker", SPRUCE_FENCE, Arrays.asList(
            "§7Assists count their",
            "§aparticipation §7towards",
            "§7killstreaks.",
            " ",
            "§7Earn §6+2g §7and §b+15% XP",
            "§7from kills and assists.",
            " ",
            "§7Gain §e+1 §7streak every §c4th",
            "§ckill§7."
    )
            , 8000
            , 50
            , 5),
    CO_OP_CAT("Co-op Cat", OCELOT_SPAWN_EGG, Arrays.asList(
            "§7Earn §b+50% XP §7and §6+50%g",
            "§7on all assists."
    )
            , 6000
            , 50
            , 6),
    CONGLOMERATE("Conglomerate", HAY_BLOCK, Arrays.asList(
            "§7Don't earn §bXP §7from kills.",
            "§7The §bXP §7you would earn is",
            "§7converted to §6gold §7at a §e20%",
            "§7ratio."
    )
            , 20000
            , 50
            , 8),
    GLADIATOR("Gladiator", BONE, Arrays.asList(
            "§7Receive §9-3% §7damage per",
            "§7nearby player.",
            " ",
            "§712 blocks range.",
            "§7Minimum 3, max 10 players."
    )
            , 4000
            , 60
            , 0),
    VAMPIRE("Vampire", FERMENTED_SPIDER_EYE, Arrays.asList(
            "§7Don't earn golden apples.",
            "§7Heal §c0.5❤ §7on hit.",
            "§7Tripled on arrow crit.",
            "§cRegen I §7(8s) on kill."
    )
            , 4000
            , 60
            , 0),
    RECON("Recon", ENDER_EYE, Arrays.asList(
            "§7Each fourth arrow shot at",
            "§7someone rewards §b+40 XP §7and",
            "§7deals §c+50% damage§7."
    )
            , 6000
            , 60
            , 7),
    OVERHEAL("Overheal", BREAD, Collections.singletonList(
            "§7Double healing item limits."
    )
            , 6000
            , 70
            , 1),
    RAMBO("Rambo", STICK, Arrays.asList(
            "§7Don't earn golden apples.",
            "§7Max health: §c8❤",
            "§7Refill all health on kill."
    )
            , 6000
            , 70
            , 3),
    OLYMPUS("Olympus", POTION, Arrays.asList(
            "§7Golden apples you earn turn into",
            "§bOlympus Potions§7.",
            " ",
            "§bOlympus Potion",
            "§9Speed I (0:24)",
            "§9Regeneration III (0:10)",
            "§9Resistance II (0:04)",
            "§bGain +27 XP!",
            "§7Can only hold 1"
    )
            , 6000
            , 70
            , 4),
    DIRTY("Dirty", PODZOL, Arrays.asList(
            "§7Gain §9Resistance II §7(4s) on",
            "§7kill."
    )
            , 8000
            , 80
            , 2),
    FIRST_STRIKE("First Strike", COOKED_CHICKEN, Arrays.asList(
            "§7First hit on a player deals",
            "§c+35% damage §7and grants",
            "§eSpeed I §7(5s)."
    )
            , 8000
            , 80
            , 5),
    SOUP("Soup", MUSHROOM_STEW, Arrays.asList(
            "§7Golden apples you earn turn into",
            "§aTasty Soup§7. You also earn",
            "§7soup on assists.",
            " ",
            "§aTasty Soup",
            "§9Speed I (0:07)",
            "§a1.5❤ Heal §7+ §61❤ Absorption",
            "§cNext melee hit +15% damage"
    )
            , 8000
            , 90
            , 7),
    MARATHON("Marathon", LEATHER_BOOTS, Arrays.asList(
            "§7Cannot wear boots.",
            "§7While you have speed:",
            "§8◾ §7Deal §c+18% §7damage",
            "§8◾ §7Receive §9-18% §7damage"
    )
            , 8000
            , 90
            , 6),
    THICK("Thick", APPLE, Collections.singletonList(
            "§7You have §c+2 Max ❤§7."
    )
            , 10000
            , 90
            , 11),
    KUNG_FU_KNOWLEDGE("Kung Fu Knowledge", BEEF, Arrays.asList(
            "§7No sword damage.",
            "§7Fists hit like a truck.",
            "§7Gain speed II (5s) every fourth",
            "§7strike on a player."
    )
            , 10000
            , 100
            , 9),
    UNSET("unset", DIAMOND_BLOCK, Collections.singletonList(
            "§7Select a perk to fill this slot."
    )
            , 0
            , 0
            , 13131313);

    private final String name;
    private final Material material;
    private final List<String> lore;
    private final int cost;
    private final int level;
    private final int prestige;

    Perks(String name, Material material, List<String> lore, int cost, int level, int prestige) {
        this.name = name;
        this.material = material;
        this.lore = lore;
        this.cost = cost;
        this.level = level;
        this.prestige = prestige;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getCost() {
        return cost;
    }

    public int getLevel() {
        return level;
    }

    public int getPrestige() {
        return prestige;
    }

    public static Perks findByName(String name) {
        Perks result = null;

        for (Perks perk : values()) {
            if (perk.getName().equals(name)) {
                result = perk;
                break;
            }
        }
        return result;
    }

    public static Perks findByEnumName(String name) {
        Perks result = null;

        for (Perks perk : values()) {
            if (perk.name().equalsIgnoreCase(name)) {
                result = perk;
                break;
            }
        }
        return result;
    }

    public static Perks findByMaterial(Material material) {
        for (Perks perk : values()) {
            if (perk.getMaterial() == material) return perk;
        }
        return null;
    }
}






















