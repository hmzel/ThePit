package me.zelha.thepit.zelenums;

import me.zelha.thepit.upgrades.permanent.ministreaks.*;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

import static me.zelha.thepit.zelenums.StreakBundles.*;
import static org.bukkit.Material.*;

public enum Ministreaks {
    SECOND_GAPPLE(null, 3, "Second Gapple", GOLDEN_APPLE, 0, 0, 1500, new SecondGappleMinistreak(),
            "§7Every: §c3 kills",
            " ",
            "§7Gain §b5 XP§7, §6+5g §7and an",
            "§7extra golden apple."
    ),
    EXPLICIOUS(null, 3, "Explicious", LIGHT_BLUE_DYE, 0, 0, 3000, new ExpliciousMinistreak(),
            "§7Every: §c3 kills",
            " ",
            "§7Gain §b+12 XP§7."
    ),
    R_AND_R(BEASTMODE, 3, "R&R", GOLDEN_CARROT, 3, 40, 4000, new RAndRMinistreak(),
            "§7Every: §c3 kills",
            " ",
            "§7Gain §9Resistance I §7and",
            "§cRegen II §7for 3s."
    ),
    ARQUEBUSIER(null, 3, "Arquebusier", ARROW, 0, 50, 5000, new ArquebusierMinistreak(),
            "§7Every: §c3 kills",
            " ",
            "§7Gain:",
            "§7■ §6+7g",
            "§7■ §f16 arrows §7(max 128)",
            "§7■ §eSpeed §7for 10 seconds"
    ),
    KHANATE(HIGHLANDER, 3, "Khanate", GOLDEN_HELMET, 7, 60, 6000, new KhanateMinistreak(),
            "§7Every: §c3 kills",
            " ",
            "§7Earn §6+8g§7.",
            " ",
            "§7Stack §c+4% damage §7vs bountied players",
            "§7up to §c+40%§7."
    ),
    LEECH(MAGNUM_OPUS, 3, "Leech", CHICKEN_SPAWN_EGG, 10, 70, 6000, new LeechMinistreak(),
            "§7Every: §c3 kills",
            " ",
            "§7Next hit heals for §c0.5❤ §7+",
            "§c20% §7of its damage."
    ),
    TOUGH_SKIN(BEASTMODE, 5, "Tough Skin", LEATHER_CHESTPLATE, 3, 30, 3000, new ToughSkinMinistreak(),
            "§7Every: §c5 kills",
            " ",
            "§7Receive §9-3% §7damage.",
            "§7Stacks up to §9-24%§7."
    ),
    FIGHT_OR_FLIGHT(null, 5, "Fight or Flight", FIRE_CHARGE, 0, 50, 5000,  new FightOrFlightMinistreak(),
            "§7Every: §c5 kills",
            " ",
            "§7If below half §c❤§7:",
            "§7Gain §eSpeed I §7and §9Resistance I §7for 7 seconds.",
            " ",
            "§7Otherwise:",
            "§7Deal §c+20% §7damage for 7 seconds."
    ),
    PUNGENT(HERMIT, 5, "Pungent", FERMENTED_SPIDER_EYE, 4, 70, 9000, null,
            "§7Every: §c5 kills",
            " ",
            "§7Obtain a §cSmelly Bomb§7:",
            "§7Applies §9Slowness I §7to players",
            "§7within 3 blocks for 5 seconds."
    ),
    HEROS_HASTE(null, 5, "Hero's Haste", ENCHANTED_BOOK, 0, 100, 15000, null,
            "§7Every: §c5 kills",
            " ",
            "§7Gain §eSpeed II §7for 5 seconds."
    ),
    RUSH(HIGHLANDER, 5, "Rush", SUGAR, 7, 110, 25000, null,
            "§7Every: §c5 kills",
            " ",
            "§7Gain §e1.5% Speed§7.",
            "§7Max §e+15% Speed§7."
    ),
    FEAST(null, 7, "Feast", MUTTON, 0, 30, 4000, null,
            "§7Every: §c7 kills",
            " ",
            "§7Obtain a §6AAA-Rated Steak§7:",
            "§7■ §c+20% damage",
            "§7■ §eSpeed I",
            "§7■ §9Resistance I",
            "§7Insta-eat (0:10)"
    ),
    COUNTER_STRIKE(null, 7, "Counter-Strike", IRON_HORSE_ARMOR, 0, 40, 5000, null,
            "§7Every: §c7 kills",
            " ",
            "§7Deal §c+15% damage §7and block",
            "§91❤ §7per hit for 8s."
    ),
    GOLD_NANO_FACTORY(HIGHLANDER, 7, "Gold Nano-factory", GOLD_NUGGET, 7, 50, 6000, null,
            "§7Every: §c7 kills",
            " ",
            "§7Obtain a molecular assembler:",
            "§7Spawns §67 gold ingots§7.",
            "§7Grants §cRegen IV §7for 2 seconds."
    ),
    TACTICAL_RETREAT(BEASTMODE, 7, "Tactical Retreat", GRASS, 3, 50, 5000, null,
            "§7Every: §c7 kills",
            " ",
            "§7Gain §cRegeneration IV §7and",
            "§cWeakness IV §7for 5 seconds."
    ),
    GLASS_PICKAXE(HERMIT, 7, "Glass Pickaxe", DIAMOND_PICKAXE, 4, 60, 6000, null,
            "§7Every: §c7 kills",
            " ",
            "§7Get a single-use weapon with",
            "§9+8.5 Damage §7and §c+0.5❤",
            "§7true damage."
    ),
    ASSURED_STRIKE(MAGNUM_OPUS, 7, "Assured Strike", COOKED_RABBIT, 10, 80, 10000, null,
            "§7Every: §c7 kills",
            " ",
            "§7Your next melee hit deals §c+35%",
            "§cdamage §7and grants §eSpeed I",
            "§7for 20 seconds."
    ),
    AURA_OF_PROTECTION(HERMIT, 10, "Aura of Protection", SLIME_BALL, 4, 50, 8000, null,
            "§7Every: §c10 kills",
            " ",
            "§7Gain an §aAura of Protection §7spell item.",
            " ",
            "§aAura of Protection",
            "§9Resistance II §7(0:04)",
            "§eTrue Damage §7immunity (0:15)"
    ),
    ICE_CUBE(HERMIT, 10, "Ice Cube", PACKED_ICE, 4, 60, 9000, null,
            "§7Every: §c10 kills",
            " ",
            "§7Get an §bIce Cube §7item.",
            " ",
            "§bIce Cube",
            "§7Single-Use on melee strike.",
            "§7Deals §c1❤ §7true damage to victim.",
            "§7Gain §b40 XP§7.",
            "§7Attacks slow enemies for 10 seconds."
    ),
    SUPER_STREAKER(TO_THE_MOON, 10, "Super Streaker", BREAD, 15, 80, 20000, null,
            "§7Every: §c10 kills",
            " ",
            "§7Gain §b+50 XP§7.",
            " ",
            "§7Stack a buff of §b+5% XP §7from kills.",
            "§7(Up to §b+50%§7)"
    ),
    GOLD_STACK(TO_THE_MOON, 10, "Gold Stack", GOLD_ORE, 15, 90, 25000, null,
            "§7Every: §c10 kills",
            " ",
            "§7Permanently gain §6+0.1g §7per kill.",
            "§7Maximum: §6+4g",
            " ",
            "§8Bonus also applies when not selected.",
            "§8Resets on prestige."
    ),
    XP_STACK(TO_THE_MOON, 10, "XP Stack", DIAMOND_ORE, 15, 90, 25000, null,
            "§7Every: §c10 kills",
            " ",
            "§7Permanently gain §b+0.05 XP §7per kill.",
            "§7Maximum: §b+8 XP",
            " ",
            "§8Bonus also applies when not selected.",
            "§8Resets on prestige."
    ),
    MONSTER(BEASTMODE, 25, "Monster", APPLE, 3, 40, 10000, null,
            "§7Every: §c25 kills",
            " ",
            "§7Gain an extra max §c❤ §7(max 2)."
    ),
    SPONGESTEVE(null, 25, "Spongesteve", SPONGE, 0, 70, 12000, null,
            "§7Every: §c25 kills",
            " ",
            "§7Gain §615❤ Absorption§7."
    ),
    APOSTLE_TO_RNGESUS(MAGNUM_OPUS, 25, "Apostle to RNGesus", QUARTZ_SLAB, 10, 100, 50000, null,
            "§7Every: §c25 kills",
            " ",
            "§7Roll a number between 1-100:",
            "§e■ §727 or 43: §e+1 Renown",
            "§e■ §742: §6+10❤ absorption",
            "§e■ §750: §bDiamond Helmet",
            "§e■ §713 or 66: §5+3 Chunk of Vile",
            "§e■ §777 or 88: §d+1 Mystic Drop",
            "§e■ §799: §7Smited for §c4❤",
            "§e■ §7Multiple of 2: §cRegeneration I §7(0:20)",
            "§e■ §7Multiple of 3: §eSpeed I §7(0:15)",
            "§e■ §7Multiple of 4: §cRegeneration II §7(0:10)",
            "§e■ §7Multiple of 7: §7Gain between §6500g§7-§65,000g",
            "§e■ §7Multiple of 8: §7Gain between §b500§7-§b5,000 XP",
            "§e■ §7Multiple of 12: §6+4❤ absorption",
            "§e■ §7Multiple of 13: §c+25% damage §7(0:30)"
    ),
    UNSET(null, 13131313, "unset", GOLD_BLOCK, 13131313, 0, 0, null,
            "§7Select a killstreak for this",
            "§7slot."
    );

    private final StreakBundles bundle;
    private final int trigger;
    private final String name;
    private final Material material;
    private final int prestige;
    private final int level;
    private final int cost;
    private final Ministreak methods;
    private final String[] lore;

    Ministreaks(StreakBundles bundle, int trigger, String name, Material material, int prestige, int level, int cost, Ministreak methods, String... lore) {
        this.bundle = bundle;
        this.trigger = trigger;
        this.name = name;
        this.material = material;
        this.prestige = prestige;
        this.level = level;
        this.cost = cost;
        this.methods = methods;
        this.lore = lore;
    }

    public static Ministreaks findByEnumName(String name) {
        for (Ministreaks mini : values()) {
            if (mini.name().equalsIgnoreCase(name)) return mini;
        }
        return null;
    }

    public StreakBundles getBundle() {
        return bundle;
    }

    public int getTrigger() {
        return trigger;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public int getPrestige() {
        return prestige;
    }

    public int getLevel() {
        return level;
    }

    public int getCost() {
        return cost;
    }

    public Ministreak getMethods() {
        return methods;
    }

    public List<String> getLore() {
        return Arrays.asList(lore);
    }
}























