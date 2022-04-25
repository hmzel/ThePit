package me.zelha.thepit.zelenums;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.ZelLogic;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public enum Passives {//0 tierMultipler is just a placeholder dont hurt me please
    XP_BOOST("XP Boost", "§bXP Boost", Material.LIGHT_BLUE_DYE),
    GOLD_BOOST("Gold Boost", "§6Gold Boost", Material.ORANGE_DYE),
    MELEE_DAMAGE("Melee Damage", "§cMelee Damage", Material.RED_DYE),
    BOW_DAMAGE("Bow Damage", "§eBow Damage", Material.YELLOW_DYE),
    DAMAGE_REDUCTION("Damage Reduction", "§9Damage Reduction", Material.CYAN_DYE),
    BUILD_BATTLER("Build Battler", "§aBuild Battler", Material.BONE_MEAL),
    EL_GATO("El Gato", "§dEl Gato", Material.CAKE);

    private final String name;
    private final String colorfulName;
    private final Material material;
    private final ZelLogic zl = Main.getInstance().getZelLogic();

    Passives(String name, String colorfulName, Material material) {
        this.name = name;
        this.colorfulName = colorfulName;
        this.material = material;
    }

    public static Passives findByEnumName(String name) {
        for (Passives passive : values()) {
            if (passive.name().equalsIgnoreCase(name)) return passive;
        }
        return null;
    }

    public int getBaseLevelReq() {
        switch (this) {
            case XP_BOOST:
                return 10;
            case GOLD_BOOST:
                return 20;
            case MELEE_DAMAGE:
            case BOW_DAMAGE:
            case DAMAGE_REDUCTION:
                return 30;
            case BUILD_BATTLER:
                return 40;
            case EL_GATO:
                return 50;
        }
        return 0;
    }

    public int getLevelReq(Player p) {
        PlayerData pData = Main.getInstance().getPlayerData(p);

        switch (this) {
            case XP_BOOST://t5 is 100
                break;
            case GOLD_BOOST:
                return 20 + (15 * pData.getPassiveTier(this));
            case MELEE_DAMAGE:
            case BOW_DAMAGE:
            case DAMAGE_REDUCTION:
            case BUILD_BATTLER:
            case EL_GATO:
        }

        return getBaseLevelReq();
    }

    public int getCost(Player p) {
        PlayerData pData = Main.getInstance().getPlayerData(p);

        switch (this) {//will update when i figure out all the costs
            case XP_BOOST://t2 is 2500, t5 is 25000, wtf is this calculation
                return 500;
            case GOLD_BOOST:
                return 1000;
            case MELEE_DAMAGE:
            case BOW_DAMAGE:
            case DAMAGE_REDUCTION:
                return 450;
            case BUILD_BATTLER://t2 is 2750
                return 750;
            case EL_GATO:
                return 1000 + (pData.getPassiveTier(this) * 1000);
        }

        return 0;
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
            if (pData.getLevel() >= getLevelReq(p)) {
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
                lore.add("§7Required level: " + zl.getColorBracketAndLevel(0, getLevelReq(p)));
                lore.add("§cLevel too low to upgrade!");
            }
        } else {
            lore.add("§aMax tier unlocked!");
        }

        return lore;
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
}

