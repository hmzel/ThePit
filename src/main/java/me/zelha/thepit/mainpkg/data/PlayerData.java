package me.zelha.thepit.mainpkg.data;

import me.zelha.thepit.zelenums.Passives;
import me.zelha.thepit.zelenums.Perks;
import org.bson.Document;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PlayerData {

    private int prestige;
    private int level;
    private double gold;
    private int exp;
    private String status;
    private int bounty;
    private int combatTimer;
    private double streak;
    private boolean hideTimer;
    private int multikill;
    private final Map<Integer, Perks> perkSlots = new HashMap<>();
    private final Map<Passives, Integer> passivesMap = new HashMap<>();
    private final Map<Perks, Boolean> perkUnlocks = new HashMap<>();

    public PlayerData(Document document) {
        prestige = document.getInteger("prestige");
        level = document.getInteger("level");
        gold = document.getDouble("gold");
        exp = document.getInteger("exp");
        bounty = document.getInteger("bounty");
        streak = 0;
        combatTimer = 0;
        hideTimer = true;
        multikill = 0;

        for (int i = 1; i <= 4; i++) {
            perkSlots.put(i, Perks.findByName(document.getEmbedded(Arrays.asList("perk_slots", String.valueOf(i)), String.class)));
        }

        for (Passives passive : Passives.values()) {
            passivesMap.put(passive, document.getEmbedded(Arrays.asList("passives", passive.getName()), Integer.class));
        }

        for (Perks perk : Perks.values()) {
            perkUnlocks.put(perk, document.getEmbedded(Arrays.asList("perk_unlocks", perk.getName()), Boolean.class));
        }

        if (bounty != 0) {
            status = "bountied";
        } else {
            status = "idling";
        }
    }

    //getters

    public int getPrestige() {
        return prestige;
    }

    public int getLevel() {
        return level;
    }

    public double getGold() {
        return gold;
    }

    public int getExp() {
        return exp;
    }

    public String getStatus() {
        return status;
    }

    public int getBounty() {return bounty;}

    public int getCombatTimer() {
        return combatTimer;
    }

    public boolean hideTimer() {
        return hideTimer;
    }

    public double getStreak() {
        return streak;
    }

    public int getMultiKill() {
        return multikill;
    }

    public int getPassiveTier(Passives passive) {
        return passivesMap.get(passive);
    }

    public Perks getPerkAtSlot(int slot) {
        return perkSlots.get(slot);
    }

    public boolean getPerkUnlocked(Perks perk) {
        return perkUnlocks.get(perk);
    }

    //setters

    public void setPrestige(int prestige) {
        this.prestige = prestige;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setGold(double gold) {
        this.gold = gold;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBounty(int bounty) {
        this.bounty = bounty;
    }

    public void setCombatTimer(int Timer) {
        this.combatTimer = Timer;
    }

    public void setHideTimer(boolean setBoolean) {
        this.hideTimer = setBoolean;
    }

    public void setStreak(double streak) {
        this.streak = streak;
    }

    public void setMultiKill(int multikill) {
        this.multikill = multikill;
    }

    public void setPassiveTier(Passives passive, int tier) {
        passivesMap.put(passive, tier);
    }

    public void setPerkAtSlot(int slot, Perks perk) {
        perkSlots.put(slot, perk);
    }

    public void setPerkUnlocked(Perks perk, boolean bool) {
        perkUnlocks.put(perk, bool);
    }

    //other

    public boolean hasPerkEquipped(Perks perk) {
        for (Perks slotPerk : perkSlots.values()) {
            if (slotPerk == perk) {
                return true;
            }
        }
        return false;
    }
}

















