package me.zelha.thepit.zelenums;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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

    public static int determineLevelRequirement(Passives passive, Player p) {//most levels unknown atm
        PlayerData pData = Main.getInstance().getPlayerData(p);//0 is a placeholder dont yell at me please
        int level = 0;

        switch (passive) {
            case XP_BOOST:
                level = XP_BOOST.baseLevelReq + (0 * pData.getPassiveTier(passive));
                break;
            case GOLD_BOOST://t2 is 35, just going off that, idk the actual levels
                level = GOLD_BOOST.baseLevelReq + (15 * pData.getPassiveTier(passive));
                break;
            case MELEE_DAMAGE:
                level = MELEE_DAMAGE.baseLevelReq + (0 * pData.getPassiveTier(passive));
                break;
            case BOW_DAMAGE:
                level = BOW_DAMAGE.baseLevelReq + (0 * pData.getPassiveTier(passive));
                break;
            case DAMAGE_REDUCTION:
                level = DAMAGE_REDUCTION.baseLevelReq + (0 * pData.getPassiveTier(passive));
                break;
            case BUILD_BATTLER:
                level = BUILD_BATTLER.baseLevelReq + (0 * pData.getPassiveTier(passive));
                break;
            case EL_GATO:
                level = EL_GATO.baseLevelReq + (0 * pData.getPassiveTier(passive));
                break;
        }
        return level;
    }
}

