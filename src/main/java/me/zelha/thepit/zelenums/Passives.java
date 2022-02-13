package me.zelha.thepit.zelenums;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public enum Passives {//0 tierMultipler is just a placeholder dont hurt me please
    XP_BOOST("XP Boost", "§bXP Boost", Material.LIGHT_BLUE_DYE, 500, 10, 0),
    GOLD_BOOST("Gold Boost", "§6Gold Boost", Material.ORANGE_DYE, 1000, 20, 15),
    MELEE_DAMAGE("Melee Damage", "§cMelee Damage", Material.RED_DYE, 450, 30, 0),
    BOW_DAMAGE("Bow Damage", "§eBow Damage", Material.YELLOW_DYE, 450, 30, 0),
    DAMAGE_REDUCTION("Damage Reduction", "§9Damage Reduction", Material.CYAN_DYE, 450, 30, 0),
    BUILD_BATTLER("Build Battler", "§aBuild Battler", Material.BONE_MEAL, 750, 40, 0),
    EL_GATO("El Gato", "§dEl Gato", Material.CAKE, 1000, 50, 0);

    private final String name;
    private final String colorfulName;
    private final Material material;
    private final int baseCost;
    private final int baseLevelReq;
    private final int levelTierMultiplier;

    Passives(String name, String colorfulName, Material material, int baseCost, int baseLevelReq, int levelTierMultiplier) {
        this.name = name;
        this.colorfulName = colorfulName;
        this.material = material;
        this.baseCost = baseCost;
        this.baseLevelReq = baseLevelReq;
        this.levelTierMultiplier = levelTierMultiplier;
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

    public int getBaseLevelReq() {
        return baseLevelReq;
    }

    public int getLevelRequirement(Player p) {
        PlayerData pData = Main.getInstance().getPlayerData(p);

        return baseLevelReq + (levelTierMultiplier * pData.getPassiveTier(Passives.findByEnumName(super.name())));
    }

    public int getCost(Player p) {
        PlayerData pData = Main.getInstance().getPlayerData(p);
        Passives passive = Passives.findByEnumName(super.name());
        int cost = 0;

        switch (passive) {//will update when i figure out all the costs
            case XP_BOOST://t2 is 2500, t5 is 25000, wtf is this calculation
            case GOLD_BOOST:
            case MELEE_DAMAGE:
            case BOW_DAMAGE:
            case DAMAGE_REDUCTION:
            case BUILD_BATTLER:
                cost = baseCost;
                break;
            case EL_GATO:
                cost = baseCost + (pData.getPassiveTier(passive) * 1000);
                break;
        }
        return cost;
    }

    public static Passives findByEnumName(String name) {
        for (Passives passive : values()) {
            if (passive.name().equalsIgnoreCase(name)) return passive;
        }
        return null;
    }

    public static Passives findByName(String name) {
        for (Passives passive : values()) {
            if (passive.getName().equalsIgnoreCase(name)) return passive;
        }
        return null;
    }
}

