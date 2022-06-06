package me.zelha.thepit.events;

import org.bukkit.event.Event;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ResourceManager extends Event {

    protected double percentage = 1;
    private int maxExp = 250;
    private int maxGold = 2500;
    //i wish i could use maps here but they were causing weird issues and i couldnt figure out how to fix it
    private final Map<String, Double> expAdditions = new LinkedHashMap<>();
    private final Map<String, Double> expModifiers = new LinkedHashMap<>();
    private final Map<String, Double> baseGoldModifiers = new LinkedHashMap<>();
    private final Map<String, Double> goldAdditions = new LinkedHashMap<>();
    private final Map<String, Double> goldModifiers = new LinkedHashMap<>();
    private final Map<String, Double> secondaryGoldAdditions = new LinkedHashMap<>();

    public int calculateEXP() {
        double exp = 0;

        for (Double value : expAdditions.values()) exp += value;
        for (Double value : expModifiers.values()) exp *= value;

        return (int) Math.min(Math.ceil(exp), maxExp);
    }

    public double calculateGold() {
        double gold = 0;
        boolean baseGoldModifiersApplied = false;

        for (Double value : goldAdditions.values()) {
            gold += value;

            if (!baseGoldModifiersApplied) {
                for (Double value2 : baseGoldModifiers.values()) gold *= value2;

                baseGoldModifiersApplied = true;
            }
        }

        for (Double value : goldModifiers.values()) gold *= value;

        if (gold > maxGold) gold = maxGold;

        for (Double value : secondaryGoldAdditions.values()) gold += value;

        return gold;
    }

    public void setMaxExp(int maxExp) {
        this.maxExp = maxExp;
    }

    public void setMaxGold(int maxGold) {
        this.maxGold = maxGold;
    }

    public void addExp(double exp, String reason) {
        expAdditions.put(reason, exp);
    }

    public void addExpModifier(double modifier, String reason) {
        expModifiers.put(reason, modifier);
    }

    public void addBaseGoldModifier(double modifier, String reason) {
        baseGoldModifiers.put(reason, modifier);
    }

    public void addGold(double gold, String reason) {
        goldAdditions.put(reason, gold);
    }

    public void addGoldModifier(double modifier, String reason) {
        goldModifiers.put(reason, modifier);
    }

    public void addSecondaryGold(double gold, String reason) {
        secondaryGoldAdditions.put(reason, gold);
    }

    public Map<String, Double> getExpAdditions() {
        return expAdditions;
    }

    public Map<String, Double> getExpModifiers() {
        return expModifiers;
    }

    public Map<String, Double> getBaseGoldModifiers() {
        return baseGoldModifiers;
    }

    public Map<String, Double> getGoldAdditions() {
        return goldAdditions;
    }

    public Map<String, Double> getGoldModifiers() {
        return goldModifiers;
    }

    public Map<String, Double> getSecondaryGoldAdditions() {
        return secondaryGoldAdditions;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public int getMaxGold() {
        return maxGold;
    }
}
