package me.zelha.thepit.mainpkg.data;

import me.zelha.thepit.zelenums.Passives;
import me.zelha.thepit.zelenums.Perks;
import org.bson.Document;

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
    private final Map<Passives, Integer> passivesMap = new HashMap<>();
    private String perkSlot1;
    private String perkSlot2;
    private String perkSlot3;
    private String perkSlot4;

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
        perkSlot1 = document.getString("perk_slot_1");
        perkSlot2 = document.getString("perk_slot_2");
        perkSlot3 = document.getString("perk_slot_3");
        perkSlot4 = document.getString("perk_slot_4");

        for (Passives passive : Passives.values()) {
            passivesMap.put(passive, document.getInteger(passive.getID()));
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
        if (slot == 1) {
            return Perks.findByName(perkSlot1);
        } else if (slot == 2) {
            return Perks.findByName(perkSlot2);
        } else if (slot == 3) {
            return Perks.findByName(perkSlot3);
        } else if (slot == 4) {
            return Perks.findByName(perkSlot4);
        } else {
            return null;
        }
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
        if (slot == 1) {
            perkSlot1 = perk.getName();
        } else if (slot == 2) {
            perkSlot2 = perk.getName();
        } else if (slot == 3) {
            perkSlot3 = perk.getName();
        } else if (slot == 4) {
            perkSlot4 = perk.getName();
        }
    }
}

















