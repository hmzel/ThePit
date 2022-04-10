package me.zelha.thepit.zelenums;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
    private final ZelLogic zl = Main.getInstance().getZelLogic();

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

        return baseLevelReq + (levelTierMultiplier * pData.getPassiveTier(Passives.findByEnumName(name())));
    }

    public int getCost(Player p) {
        PlayerData pData = Main.getInstance().getPlayerData(p);
        Passives passive = Passives.findByEnumName(name());
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

    public List<String> getLore(Player p) {
        PlayerData pData = Main.getInstance().getPlayerData(p);
        List<String> lore = new ArrayList<>();
        int tier = pData.getPassiveTier(this);

        if (tier > 0) {
            switch (this) {
                case XP_BOOST:
                    lore.add("§7Current: §b+" + 10 * tier + "% XP");
                    break;
                case GOLD_BOOST:
                    lore.add("§7Current: §6+" + 10 * tier + "% gold (g)");
                    break;
                case MELEE_DAMAGE:
                    lore.add("§7Current: §c+" + tier + "%");
                    break;
                case BOW_DAMAGE:
                    lore.add("§7Current: §c+" + 3 * tier + "%");
                    break;
                case DAMAGE_REDUCTION:
                    lore.add("§7Current: §9-" + tier + "%");
                    break;
                case BUILD_BATTLER:
                    lore.add("§7Current: §a+" + 60 * tier + "%");
                    break;
                case EL_GATO:
                    lore.add("§7Current: §dFirst " + ((tier == 1) ? "kill" : tier + " kills"));
                    break;
            }

            lore.add("§7Tier: §a" + zl.toRoman(tier));
            lore.add("");
        }

        if (this == EL_GATO) {
            lore.add((tier < 5) ? "§7Next tier:" : "§7Description:");
        } else {
            lore.add("§7Each tier:");
        }

        switch (this) {
            case XP_BOOST:
                lore.add("§7Earn §b+10% XP §7from all");
                lore.add("§7sources.");
                break;
            case GOLD_BOOST:
                lore.add("§7Earn §6+10% gold (g) §7from");
                lore.add("§7kills and coin pickups.");
                break;
            case MELEE_DAMAGE:
                lore.add("§7Deal §c+1% §7melee damage.");
                break;
            case BOW_DAMAGE:
                lore.add("§7Deal §c+3% §7bow damage.");
                break;
            case DAMAGE_REDUCTION:
                lore.add("§7Receive §9-1% §7damage.");
                break;
            case BUILD_BATTLER:
                lore.add("§7Your blocks stay §a+60%");
                lore.add("§7longer.");
                break;
            case EL_GATO:
                if (tier == 0) {
                    lore.add("§dFirst kill §7each life rewards");
                } else {
                    lore.add("§dFirst " + (((tier < 5) ? 1 : 0) + tier) + " kills §7each life");
                }

                lore.add(((tier == 0) ? "" : "§7reward ") + "§6+5g §b+5 XP§7.");
                break;
        }

        lore.add("");

        if (tier < 5) {
            if (pData.getLevel() >= getLevelRequirement(p)) {
                if (tier > 0) {
                    lore.add("§7Upgrade cost: §6" + zl.getFancyGoldString(getCost(p)) + "g");
                } else {
                    lore.add("§7Cost: §6" + zl.getFancyGoldString(getCost(p)) + "g");
                }

                if ((pData.getGold() - getCost(p)) >= 0) {
                    lore.add("§eClick to purchase!");
                } else {
                    lore.add("§cNot enough gold!");
                }
            } else {
                lore.add("§7Required level: " + zl.getColorBracketAndLevel(0, getLevelRequirement(p)));
                lore.add("§cLevel too low to upgrade!");
            }
        } else {
            lore.add("§aMax tier unlocked!");
        }

        return lore;
    }

    public static Passives findByEnumName(String name) {
        for (Passives passive : values()) {
            if (passive.name().equalsIgnoreCase(name)) return passive;
        }
        return null;
    }
}

