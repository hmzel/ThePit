package me.zelha.thepit.zelenums;

import me.zelha.thepit.upgrades.permanent.perks.*;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.Material.*;

public enum Perks {
    GOLDEN_HEADS("Golden Heads", PLAYER_HEAD, 500, 10, 0, new GoldenHeadsPerk(),
            "§7Golden apples you earn turn into",
            "§6Golden Heads§7."
    ),
    FISHING_ROD("Fishing Rod", Material.FISHING_ROD, 1000, 10, 0, new FishingRodPerk(),
            "§7Spawn with a fishing rod."
    ),
    LAVA_BUCKET("Lava Bucket", Material.LAVA_BUCKET, 1000, 10, 0, new LavaBucketPerk(),
            "§7Spawn with a lava bucket."
    ),
    STRENGTH_CHAINING("Strength-Chaining", REDSTONE, 2000, 20, 0, new StrengthChainingPerk(),
            "§c+8% damage §7for 7s stacking",
            "§7on kill."
    ),
    SAFETY_FIRST("Safety First", CHAINMAIL_HELMET, 3000, 30, 0, new SafetyFirstPerk(),
            "§7Spawn with a helmet."
    ),
    BARBARIAN("Barbarian", IRON_AXE, 3000, 30, 2, null,
            "§7Replaces your sword with a",
            "§7stronger axe."
    ),
    MINEMAN("Mineman", COBBLESTONE, 3000, 20, 0, new MinemanPerk(),
            "§7Spawn with §f24 cobblestone",
            "§7and a diamond pickaxe.",
            " ",
            "§7+§f3 blocks §7on kill."
    ),
    BONK("Bonk!", ANVIL, 2000, 35, 0, new BonkPerk(),
            "§7The first hit you receive from a",
            "§7player is blocked and grants",
            "§9Resistance I §7(1s)."
    ),
    TRICKLE_DOWN("Trickle-down", GOLD_INGOT, 1000, 40, 0, null, //handled in GoldIngotListener
            "§7Gold ingots reward §6+10g",
            "§7and heal §c2❤§7."
    ),
    LUCKY_DIAMOND("Lucky Diamond", DIAMOND, 4000, 40, 0, new LuckyDiamondPerk(),
            "§730% chance to upgrade dropped",
            "§7armor pieces from kills to",
            "§bdiamond§7.",
            " ",
            "§7Upgraded pieces warp to your",
            "§7inventory."
    ),
    SPAMMER("Spammer", BOW, 4000, 40, 0, new SpammerPerk(),
            "§7Get §f3 arrows §7on arrow hit.",
            " ",
            "§7Gain §63x base gold §7reward on",
            "§7targets you've shot an arrow in.",
            " ",
            "§7Earn §6+2g §7on assists."
    ),
    BOUNTY_HUNTER("Bounty Hunter", GOLDEN_LEGGINGS, 2000, 50, 0, new BountyHunterPerk(),
            "§6+4g §7on all kills.",
            "§7Earn bounty assist shares.",
            " ",
            "§c+1% damage§7/100g bounty on",
            "§7target."
    ),
    STREAKER("Streaker", WHEAT, 8000, 50, 0, null, //handled in KillListener
            "§7Triple streak kill §bXP §7bonus."
    ),
    ASSISTANT_STREAKER("Assistant Streaker", SPRUCE_FENCE, 8000, 50, 5, null,
            "§7Assists count their",
            "§aparticipation §7towards",
            "§7killstreaks.",
            " ",
            "§7Earn §6+2g §7and §b+15% XP",
            "§7from kills and assists.",
            " ",
            "§7Gain §e+1 §7streak every §c4th",
            "§ckill§7."
    ),
    CO_OP_CAT("Co-op Cat", OCELOT_SPAWN_EGG, 6000, 50, 6, null,
            "§7Earn §b+50% XP §7and §6+50%g",
            "§7on all assists."
    ),
    CONGLOMERATE("Conglomerate", HAY_BLOCK, 20000, 50, 8, null,
            "§7Don't earn §bXP §7from kills.",
            "§7The §bXP §7you would earn is",
            "§7converted to §6gold §7at a §e20%",
            "§7ratio."
    ),
    GLADIATOR("Gladiator", BONE, 4000, 60, 0, new GladiatorPerk(),
            "§7Receive §9-3% §7damage per",
            "§7nearby player.",
            " ",
            "§712 blocks range.",
            "§7Minimum 3, max 10 players."
    ),
    VAMPIRE("Vampire", FERMENTED_SPIDER_EYE, 4000, 60, 0, new VampirePerk(),
            "§7Don't earn golden apples.",
            "§7Heal §c0.5❤ §7on hit.",
            "§7Tripled on arrow crit.",
            "§cRegen I §7(8s) on kill."
    ),
    RECON("Recon", ENDER_EYE, 6000, 60, 7, null,
            "§7Each fourth arrow shot at",
            "§7someone rewards §b+40 XP §7and",
            "§7deals §c+50% damage§7."
    ),
    OVERHEAL("Overheal", BREAD, 6000, 70, 1, null,
            "§7Double healing item limits."
    ),
    RAMBO("Rambo", STICK, 6000, 70, 3, null,
            "§7Don't earn golden apples.",
            "§7Max health: §c8❤",
            "§7Refill all health on kill."
    ),
    OLYMPUS("Olympus", POTION, 6000, 70, 4, null,
            "§7Golden apples you earn turn into",
            "§bOlympus Potions§7.",
            " ",
            "§bOlympus Potion",
            "§9Speed I (0:24)",
            "§9Regeneration III (0:10)",
            "§9Resistance II (0:04)",
            "§bGain +27 XP!",
            "§7Can only hold 1"
    ),
    DIRTY("Dirty", PODZOL, 8000, 80, 2, null,
            "§7Gain §9Resistance II §7(4s) on",
            "§7kill."
    ),
    FIRST_STRIKE("First Strike", COOKED_CHICKEN, 8000, 80, 5, null,
            "§7First hit on a player deals",
            "§c+35% damage §7and grants",
            "§eSpeed I §7(5s)."
    ),
    SOUP("Soup", MUSHROOM_STEW, 8000, 90, 7, null,
            "§7Golden apples you earn turn into",
            "§aTasty Soup§7. You also earn",
            "§7soup on assists.",
            " ",
            "§aTasty Soup",
            "§9Speed I (0:07)",
            "§a1.5❤ Heal §7+ §61❤ Absorption",
            "§cNext melee hit +15% damage"
    ),
    MARATHON("Marathon", LEATHER_BOOTS, 8000, 90, 6, null,
            "§7Cannot wear boots.",
            "§7While you have speed:",
            "§8◾ §7Deal §c+18% §7damage",
            "§8◾ §7Receive §9-18% §7damage"
    ),
    THICK("Thick", APPLE, 10000, 90, 11, null,
            "§7You have §c+2 Max ❤§7."
    ),
    KUNG_FU_KNOWLEDGE("Kung Fu Knowledge", BEEF, 10000, 100, 9, null,
            "§7No sword damage.",
            "§7Fists hit like a truck.",
            "§7Gain speed II (5s) every fourth",
            "§7strike on a player."
    ),
    UNSET("unset", DIAMOND_BLOCK, 0, 0, 13131313, null,
            "§7Select a perk to fill this slot."
    );

    private final String name;
    private final Material material;
    private final int cost;
    private final int level;
    private final int prestige;
    private final Perk methods;
    private final String[] lore;

    Perks(String name, Material material, int cost, int level, int prestige, Perk methods, String... lore) {
        this.name = name;
        this.material = material;
        this.cost = cost;
        this.level = level;
        this.prestige = prestige;
        this.methods = methods;
        this.lore = lore;
    }

    public static Perks findByEnumName(String name) {
        for (Perks perk : values()) {
            if (perk.name().equalsIgnoreCase(name)) return perk;
        }
        return null;
    }

    public static Perks findByMaterial(Material material) {
        for (Perks perk : values()) {
            if (perk.getMaterial() == material) return perk;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public List<String> getLore() {
        return Arrays.asList(lore);
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

    public Perk getMethods() {
        return methods;
    }
}






















