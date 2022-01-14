package me.zelha.thepit.zelenums;

public enum Passives {
    XP_BOOST("passive_xp_boost", "XP Boost", "§bXP Boost"),
    GOLD_BOOST("passive_gold_boost", "Gold Boost", "§6Gold Boost"),
    MELEE_DAMAGE("passive_melee_damage", "Melee Damage", "§cMelee Damage"),
    BOW_DAMAGE("passive_bow_damage", "Bow Damage", "§eBow Damage"),
    DAMAGE_REDUCTION("passive_damage_reduction", "Damage Reduction", "§9Damage Reduction"),
    BUILD_BATTLER("passive_build_battler", "Build Battler", "§aBuild Battler"),
    EL_GATO("passive_el_gato", "El Gato", "§dEl Gato");

    private final String id;
    private final String name;
    private final String colorfulName;

    Passives(String id, String name, String colorfulName) {
        this.id = id;
        this.name = name;
        this.colorfulName = colorfulName;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColorfulName() {
        return colorfulName;
    }

    public static Passives findByName(String name) {
        Passives result = null;

        for (Passives passive : values()) {
            if (passive.name().equalsIgnoreCase(name)) {
                result = passive;
                break;
            }
        }
        return result;
    }
}

