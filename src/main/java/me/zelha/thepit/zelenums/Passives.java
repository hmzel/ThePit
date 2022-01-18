package me.zelha.thepit.zelenums;

import org.bukkit.Material;

public enum Passives {
    XP_BOOST("passive_xp_boost", "XP Boost", "§bXP Boost", Material.LIGHT_BLUE_DYE),
    GOLD_BOOST("passive_gold_boost", "Gold Boost", "§6Gold Boost", Material.ORANGE_DYE),
    MELEE_DAMAGE("passive_melee_damage", "Melee Damage", "§cMelee Damage", Material.RED_DYE),
    BOW_DAMAGE("passive_bow_damage", "Bow Damage", "§eBow Damage", Material.YELLOW_DYE),
    DAMAGE_REDUCTION("passive_damage_reduction", "Damage Reduction", "§9Damage Reduction", Material.CYAN_DYE),
    BUILD_BATTLER("passive_build_battler", "Build Battler", "§aBuild Battler", Material.BONE_MEAL),
    EL_GATO("passive_el_gato", "El Gato", "§dEl Gato", Material.CAKE);

    private final String id;
    private final String name;
    private final String colorfulName;
    private final Material material;

    Passives(String id, String name, String colorfulName, Material material) {
        this.id = id;
        this.name = name;
        this.colorfulName = colorfulName;
        this.material = material;
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

    public Material getMaterial() {
        return material;
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

