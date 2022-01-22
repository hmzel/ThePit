package me.zelha.thepit.zelenums;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public enum Passives {//0 tierMultipler is just a placeholder dont hurt me please
    XP_BOOST("XP Boost", "§bXP Boost", Material.LIGHT_BLUE_DYE, 10, 0),
    GOLD_BOOST("Gold Boost", "§6Gold Boost", Material.ORANGE_DYE, 20, 15),
    MELEE_DAMAGE("Melee Damage", "§cMelee Damage", Material.RED_DYE, 30, 0),
    BOW_DAMAGE("Bow Damage", "§eBow Damage", Material.YELLOW_DYE, 30, 0),
    DAMAGE_REDUCTION("Damage Reduction", "§9Damage Reduction", Material.CYAN_DYE, 30, 0),
    BUILD_BATTLER("Build Battler", "§aBuild Battler", Material.BONE_MEAL, 40, 0),
    EL_GATO("El Gato", "§dEl Gato", Material.CAKE, 50, 0);

    private final String name;
    private final String colorfulName;
    private final Material material;
    private final int baseLevelReq;
    private final int tierMultiplier;

    Passives(String name, String colorfulName, Material material, int baseLevelReq, int tierMultiplier) {
        this.name = name;
        this.colorfulName = colorfulName;
        this.material = material;
        this.baseLevelReq = baseLevelReq;
        this.tierMultiplier = tierMultiplier;
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

    public int getLevelRequirement(Player p) {
        PlayerData pData = Main.getInstance().getPlayerData(p);

        return baseLevelReq + (tierMultiplier * pData.getPassiveTier(Passives.findByName(super.name())));
    }
}

