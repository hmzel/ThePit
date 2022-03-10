package me.zelha.thepit.zelenums;

import static me.zelha.thepit.zelenums.Ministreaks.*;

public enum StreakBundles {
    BEASTMODE("§a", 10, Megastreaks.BEASTMODE, R_AND_R, TOUGH_SKIN, TACTICAL_RETREAT, MONSTER),
    HERMIT("§9", 20, Megastreaks.HERMIT, PUNGENT, GLASS_PICKAXE, AURA_OF_PROTECTION, ICE_CUBE),
    HIGHLANDER("§6", 40, Megastreaks.HIGHLANDER, KHANATE, RUSH, GOLD_NANO_FACTORY),
    MAGNUM_OPUS("§e", 60, Megastreaks.MAGNUM_OPUS, LEECH, ASSURED_STRIKE, APOSTLE_TO_RNGESUS),
    TO_THE_MOON("§b", 150, Megastreaks.TO_THE_MOON, SUPER_STREAKER, GOLD_STACK, XP_STACK),
    UBERSTREAK("§e", 50, Megastreaks.UBERSTREAK);

    private final String color;
    private final int renownCost;
    private final Megastreaks megastreak;
    private final Ministreaks[] ministreaks;

    StreakBundles(String color, int renownCost, Megastreaks megastreak, Ministreaks... ministreaks) {
        this.color = color;
        this.renownCost = renownCost;
        this.megastreak = megastreak;
        this.ministreaks = ministreaks;
    }

    public String getColor() {
        return color;
    }

    public int getRenownCost() {
        return renownCost;
    }

    public Megastreaks getMegastreak() {
        return megastreak;
    }

    public Ministreaks[] getMinistreaks() {
        return ministreaks;
    }
}
