package me.zelha.thepit.zelenums;

import me.zelha.thepit.upgrades.permanent.megastreaks.BeastmodeMegastreak;
import me.zelha.thepit.upgrades.permanent.megastreaks.HermitMegastreak;
import me.zelha.thepit.upgrades.permanent.megastreaks.Megastreak;
import me.zelha.thepit.upgrades.permanent.megastreaks.OverdriveMegastreak;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.Material.*;

public enum Megastreaks {
    OVERDRIVE(null, "§c§lOVRDRV", "§c§lOVERDRIVE", "Overdrive", BLAZE_POWDER,
            50, 0, 0, 0, new OverdriveMegastreak(),
            "§7Triggers on: §c50 kills",
            " ",
            "§7On trigger:",
            "§a■ §7Perma §eSpeed I§7.",
            "§a■ §7Earn §b+100% XP §7from kills.",
            "§a■ §7Earn §6+50% gold §7from kills.",
            " ",
            "§7BUT:",
            "§c■ §7Receive §c+0.1❤ §7§overy",
            "§7true damage per 5 kills over 50.",
            " ",
            "§7On death:",
            "§e■ §7Gain §b4,000 XP§7."
    ),
    BEASTMODE(StreakBundles.BEASTMODE, "§a§lBEAST", "§a§lBEASTMODE", "Beastmode", DIAMOND_HELMET,
            50, 3, 30, 10000, new BeastmodeMegastreak(),
            "§7Triggers on: §c50 kills",
            " ",
            "§7On trigger:",
            "§a■ §7Gain a §bDiamond Helmet§7.",
            "§a■ §7Deal §c+25% §7damage.",
            "§a■ §7Earn §b+50% XP §7from kills.",
            "§a■ §7Earn §6+75% gold §7from kills.",
            " ",
            "§7BUT:",
            "§c■ §7Receive §c+0.1❤ §7damage per 5 kills above 50.",
            " ",
            "§7On death:",
            "§e■ §7Keep the §bDiamond Helmet§7."
    ),
    HERMIT(StreakBundles.HERMIT, "§9§lHERMIT", "§9§lHERMIT", "Hermit", RED_BED,
            50, 4, 50, 20000, new HermitMegastreak(),
            "§7Triggers on: §c50 kills",
            " ",
            "§7From 0 kills:",
            "§a■ §7Placed blocks stay §f2x §7longer.",
            "§c■ §7Permanent §9Slowness I§7.",
            " ",
            "§7On trigger:",
            "§a■ §7Permanent §9Resistance I§7.",
            "§a■ §7True damage immunity.",
            "§a■ §7Gain §f32 Bedrock §7+ §f16 §7every 10 kills.",
            "§a■ §7Earn §6+5% gold §7and §b+5%",
            "§bXP §7from kills for each 10 kills",
            "§7over 50, up to 200.",
            " ",
            "§7BUT",
            "§c■ §7Receive §c+0.3% §7damage per",
            "§7kill over 50."
    ),
    HIGHLANDER(StreakBundles.HIGHLANDER, "§6§lHIGH", "§6§lHIGHLANDER", "Highlander", GOLDEN_BOOTS,
            50, 7, 60, 30000, null,
            "§7Triggers on: §c50 kills",
            " ",
            "§7On trigger:",
            "§a■ §7Perma §eSpeed I§7.",
            "§a■ §7Earn §6+110% gold §7from kills.",
            "§a■ §7Deal §c+33% damage §7vs bountied players.",
            " ",
            "§7BUT:",
            "§c■ §7§6+5000g §7max bounty.",
            "§c■ §7Receive §c+0.3% §7damage",
            "§7from §6Bounty Hunter §7wearers per",
            "§7kill over 50.",
            " ",
            "§7On death:",
            "§e■ §7Earn your own bounty aswell."
    ),
    MAGNUM_OPUS(StreakBundles.MAGNUM_OPUS, "", "§e§lMAGNUM OPUS", "Magnum Opus", NETHER_STAR,
            50, 10, 70, 40000, null,
            "§7Triggers on: §c50 kills",
            " ",
            "§7On trigger:",
            "§a■ §7EXPLODE!!!",
            " ",
            "§7BUT:",
            "§c■ §7Die.",
            " ",
            "§7On death:",
            "§e■ §7Earn §e1 Renown§7.",
            "§e■ §7Consume §54 Chunks of Vile",
            "§7to fix §c4 lives §7on the item",
            "§7in the §dMystic Well§7."
    ),
    TO_THE_MOON(StreakBundles.TO_THE_MOON, "§b§lMOON", "§a§lTO THE MOON", "To the Moon", END_STONE,
            100, 15, 80, 50000, null,
            "§7Triggers on: §c100 kills",
            " ",
            "§7On trigger:",
            "§a■ §7Earn §b+20% XP §7from kills.",
            "§a■ §7Get §b+100 max XP §7from kills.",
            " ",
            "§7BUT:",
            "§c■ §7Receive §c+10% §7damage per 20 kills.",
            "   §7(starting from 100)",
            "§c■ §7Receive §c+0.1❤ §7true damage per 20 kills.",
            "   §7(starting from 200)",
            " ",
            "§7During the streak:",
            "§b■ §7Copy and store your kills §bXP§7.",
            " ",
            "§7On death:",
            "§b■ §7Earn the stored §bXP§7. but",
            "§7multiply it by §b0.005x §7per kill",
            "§7above 100, up to §b1x§7."
    ),
    UBERSTREAK(StreakBundles.UBERSTREAK, "§d§lUBER", "§d§lUBERSTREAK", "Uberstreak", GOLDEN_SWORD,
            100, 20, 90, 50000, null,
            "§7Triggers on: §c100 kills",
            " ",
            "§7On trigger:",
            "§a■ §7Gain §d+50% §7chance to find mystic items.",
            " ",
            "§7BUT:",
            "§c■ §7Receive §c+10% §7damage per 100 kills.",
            " ",
            "§7During the streak:",
            "§d■ §7100 kills: Deal §c-40% damage §7vs non-prestiged",
            "§d■ §7200 kills: §c-2 max ❤",
            "§d■ §7300 kills: §7Potion Effects last §c-50% §7as long",
            "§d■ §7400 kills: §cNo longer gain health",
            " ",
            "§7On death:",
            "§7Earn a random §dUberdrop §7if you have at least 400 streak."
    );

    private final StreakBundles bundle;
    private final String displayName;
    private final String chatName;
    private final String name;
    private final Material material;
    private final int trigger;
    private final int prestige;
    private final int level;
    private final int cost;
    private final Megastreak methods;
    private final String[] lore;

    Megastreaks(StreakBundles bundle, String displayName, String chatName, String name, Material material, int trigger, int prestige, int level, int cost, Megastreak methods, String... lore) {
        this.bundle = bundle;
        this.displayName = displayName;
        this.chatName = chatName;
        this.name = name;
        this.material = material;
        this.trigger = trigger;
        this.prestige = prestige;
        this.level = level;
        this.cost = cost;
        this.methods = methods;
        this.lore = lore;
    }

    public static Megastreaks findByEnumName(String name) {
        for (Megastreaks mega : values()) {
            if (mega.name().equalsIgnoreCase(name)) return mega;
        }
        return null;
    }

    public StreakBundles getBundle() {
        return bundle;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getChatName() {
        return chatName;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public int getTrigger() {
        return trigger;
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

    public Megastreak getMethods() {
        return methods;
    }

    public List<String> getLore() {
        return Arrays.asList(lore);
    }
}
