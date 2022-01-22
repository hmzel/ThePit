package me.zelha.thepit.zelenums;

import org.bukkit.Material;

public enum Passives {
    XP_BOOST("XP Boost", "§bXP Boost", Material.LIGHT_BLUE_DYE, 10),
    GOLD_BOOST("Gold Boost", "§6Gold Boost", Material.ORANGE_DYE, 20),
    MELEE_DAMAGE("Melee Damage", "§cMelee Damage", Material.RED_DYE, 30),
    BOW_DAMAGE("Bow Damage", "§eBow Damage", Material.YELLOW_DYE, 30),
    DAMAGE_REDUCTION("Damage Reduction", "§9Damage Reduction", Material.CYAN_DYE, 30),
    BUILD_BATTLER("Build Battler", "§aBuild Battler", Material.BONE_MEAL, 40),
    EL_GATO("El Gato", "§dEl Gato", Material.CAKE, 50);

    private final String name;
    private final String colorfulName;
    private final Material material;
    private final int baseLevelReq;

    Passives(String name, String colorfulName, Material material, int baseLevelReq) {
        this.name = name;
        this.colorfulName = colorfulName;
        this.material = material;
        this.baseLevelReq = baseLevelReq;
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

