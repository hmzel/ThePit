package me.zelha.thepit.zelenums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Perks {
    GOLDEN_HEADS("Golden Heads", Arrays.asList(
            "§7Golden apples you earn turn into",
            "§6Golden Heads§7."
    )
            , 500),
    FISHING_ROD("Fishing Rod", Collections.singletonList(
            "§7Spawn with a fishing rod."
    )
            , 1000),
    LAVA_BUCKET("Lava Bucket", Collections.singletonList(
            "§7Spawn with a lava bucket."
    )
            , 1000),
    STRENGTH_CHAINING("Strength-Chaining", Arrays.asList(
            "§c+8% damage §7for 7s stacking",
            "§7on kill."
    )
            , 2000),
    SAFETY_FIRST("Safety First", Collections.singletonList(
            "§7Spawn with a helmet."
    )
            , 3000),
    BARBARIAN("Barbarian", Arrays.asList(
            "§7Replaces your sword with a",
            "§7stronger axe."
    )
            , 3000),
    MINEMAN("Mineman", Arrays.asList(
            "§7Spawn with §f24 cobblestone",
            "§7and a diamond pickaxe."
    )
            , 3000),
    INSURANCE("Insurance", Arrays.asList(
            "§7If you die within §a2s §7of",
            "§7being damaged, §cfull heal §7and",
            "§7gain §9Resistance II §7(4s).",
            "§820s cooldown."
    )
            , 2000),
    TRICKLE_DOWN("Trickle-down", Arrays.asList(
            "§7Gold ingots reward §6+10g",
            "§7and heal §c2❤§7."
    )
            , 1000),
    LUCKY_DIAMOND("Lucky Diamond", Arrays.asList(
            "§730% chance to upgrade dropped",
            "§6armor pieces from kills to",
            "§bdiamond§7."
    )
            , 4000),
    SPAMMER("Spammer", Arrays.asList(
            "§7Get §f3 arrows §7on arrow hit.",
            "\n",
            "§7Gain §63x base gold §7reward on",
            "§7targets you've shot an arrow in.",
            "\n",
            "§7Earn §6+2g §7on assists."
    )
            , 4000),
    BOUNTY_HUNTER("Bounty Hunter", Arrays.asList(
            "§6+4g §7on all kills.",
            "§7Earn bounty assist shares.",
            "\n",
            "§c+1% damage§7/100g bounty on",
            "§7target."
    )
            , 2000),
    STREAKER("Streaker", Collections.singletonList(
            "§7Triple streak kill §bXP §7bonus."
    )
            , 8000),
    ASSISTANT_STREAKER("Assistant Streaker", Arrays.asList(
            "§7Assists count their",
            "§aparticipation §7towards",
            "§7killstreaks.",
            "\n",
            "§7Earn §6+2g §7and §b+15% XP",
            "§7from kills and assists.",
            "\n",
            "§7Gain §e+1 §7streak every §c4th",
            "§ckill§7."
    )
            , 8000),
    CO_OP_CAT("Co-op Cat", Arrays.asList(
            "§7Earn §b+50% XP §7and §6+50%g",
            "§7on all assists."
    )
            , 6000),
    CONGLOMERATE("Conglomerate", Arrays.asList(
            "§7Don't earn §bXP §7from kills.",
            "§7The §bXP §7you would earn is",
            "§7converted to §6gold §7at a §e20%",
            "§7ratio."
    )
            , 20000),
    GLADIATOR("Gladiator", Arrays.asList(
            "§7Receive §9-3% §7damage per",
            "§7nearby player.",
            "\n",
            "§712 blocks range.",
            "§7Minimum 3, max 10 players."
    )
            , 4000),
    VAMPIRE("Vampire", Arrays.asList(
            "§7Don't earn golden apples.",
            "§7Heal §c0.5❤ §7on hit.",
            "§7Tripled on arrow crit.",
            "§cRegen I §7(8s) on kill."
    )
            , 4000),
    RECON("Recon", Arrays.asList(
            "§7Each fourth arrow shot at",
            "§7someone rewards §b+40 XP §7and",
            "§7deals §c+50% damage§7."
    )
            , 6000),
    OVERHEAL("Overheal", Collections.singletonList(
            "§7Double healing item limits."
    )
            , 6000),
    RAMBO("Rambo", Arrays.asList(
            "§7Don't earn golden apples.",
            "§7Max health: §c8❤",
            "§7Refill all health on kill."
    )
            , 6000),
    OLYMPUS("Olympus", Arrays.asList(
            "§7Golden apples you earn turn into",
            "§bOlympus Potions§7.",
            "\n",
            "§bOlympus Potion",
            "§9Speed I (0:24)",
            "§9Regeneration III (0:10)",
            "§9Resistance II (0:04)",
            "§bGain +27 XP!",
            "§7Can only hold 1"
    )
            , 6000),
    DIRTY("Dirty", Arrays.asList(
            "§7Gain §9Resistance II §7(4s) on",
            "§7kill."
    )
            , 8000),
    FIRST_STRIKE("First Strike", Arrays.asList(
            "§7First hit on a player deals",
            "§c+35% damage §7and grants",
            "§eSpeed I §7(5s)."
    )
            , 8000),
    SOUP("Soup", Arrays.asList(
            "§7Golden apples you earn turn into",
            "§aTasty Soup§7. You also earn",
            "§7soup on assists.",
            "\n",
            "§aTasty Soup",
            "§9Speed I (0:07)",
            "§a1.5❤ Heal §7+ §61❤ Absorption",
            "§cNext melee hit +15% damage"
    )
            , 8000),
    MARATHON("Marathon", Arrays.asList(
            "§7Cannot wear boots.",
            "§7While you have speed:",
            "§8◾ §7Deal §c+18% §7damage",
            "§8◾ §7Receive §9-18% §7damage"
    )
            , 8000),
    THICK("Thick", Collections.singletonList(
            "§7You have §c+2 Max ❤§7."
    )
            , 10000),
    KUNG_FU_KNOWLEDGE("Kung Fu Knowledge", Arrays.asList(
            "§7No sword damage.",
            "§7Fists hit like a truck.",
            "§7Gain speed II (5s) every fourth",
            "§7strike on a player."
    )
            , 10000);

    private final String name;
    private final List<String> lore;
    private final int cost;

    Perks(String name, List<String> lore, int cost) {
        this.name = name;
        this.lore = lore;
        this.cost = cost;
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
}






















