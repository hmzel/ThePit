package me.zelha.thepit.events;

import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public abstract class ResourceManager extends Event {

    private int maxExp = 250;
    private int maxGold = 2500;
    //i wish i could use maps here but they were causing weird issues and i couldnt figure out how to fix it
    private final List<Pair<String, Double>> expAdditions = new ArrayList<>();
    private final List<Pair<String, Double>> expModifiers = new ArrayList<>();
    private final List<Pair<String, Double>> baseGoldModifiers = new ArrayList<>();
    private final List<Pair<String, Double>> goldAdditions = new ArrayList<>();
    private final List<Pair<String, Double>> goldModifiers = new ArrayList<>();
    private final List<Pair<String, Double>> addAfterGoldModifiers = new ArrayList<>();

    public void setMaxExp(int maxExp) {
        this.maxExp = maxExp;
    }

    public void setMaxGold(int maxGold) {
        this.maxGold = maxGold;
    }

    public void addExp(double exp, String reason) {
        expAdditions.add(Pair.of(reason, exp));
    }

    public void addExpModifier(double boost, String reason) {
        expModifiers.add(Pair.of(reason, boost));
    }

    public void addBaseGoldModifier(double boost, String reason) {
        baseGoldModifiers.add(Pair.of(reason, boost));
    }

    public void addGold(double gold, String reason) {
        goldAdditions.add(Pair.of(reason, gold));
    }

    public void addGoldModifier(double boost, String reason) {
        goldModifiers.add(Pair.of(reason, boost));
    }

    public void addAfterGoldModifier(double gold, String reason) {
        addAfterGoldModifiers.add(Pair.of(reason, gold));
    }

    public List<Pair<String, Double>> getExpAdditions() {
        return expAdditions;
    }

    public List<Pair<String, Double>> getExpModifiers() {
        return expModifiers;
    }

    public List<Pair<String, Double>> getBaseGoldModifiers() {
        return baseGoldModifiers;
    }

    public List<Pair<String, Double>> getGoldAdditions() {
        return goldAdditions;
    }

    public List<Pair<String, Double>> getGoldModifiers() {
        return goldModifiers;
    }

    public List<Pair<String, Double>> getAddedAfterGoldModifiers() {
        return addAfterGoldModifiers;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public int getMaxGold() {
        return maxGold;
    }
}
