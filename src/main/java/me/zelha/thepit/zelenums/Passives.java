package me.zelha.thepit.zelenums;

public enum Passives {
    XP_BOOST("passive_xp_boost", "§bXP Boost"),
    GOLD_BOOST("passive_gold_boost", "§6Gold Boost"),
    MELEE_DAMAGE("passive_melee_damage", "§cMelee Damage"),
    BOW_DAMAGE("passive_bow_damage", "§7Bow Damage"),
    DAMAGE_REDUCTION("passive_damage_reduction", "§9Damage Reduction"),
    BUILD_BATTLER("passive_build_battler", "§aBuild Battler"),
    EL_GATO("passive_el_gato", "§dEl Gato");

    private final String id;
    private final String colorfulName;

    Passives(String id, String colorfulName) {
        this.id = id;
        this.colorfulName = colorfulName;
    }

    public String getID() {
        return id;
    }

    public String getColorfulName() {
        return colorfulName;
    }
}

