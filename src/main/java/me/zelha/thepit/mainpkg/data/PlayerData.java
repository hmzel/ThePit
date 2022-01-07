package me.zelha.thepit.mainpkg.data;

import org.bson.Document;

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

        if (bounty != 0) {
            status = "bountied";
        }else {
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

    public void setBounty(int bounty) {this.bounty = bounty;}

    public void setCombatTimer(int Timer) {this.combatTimer = Timer;}

    public void setHideTimer(boolean setBoolean) {this.hideTimer = setBoolean;}

    public void setStreak(double streak) {
        this.streak = streak;
    }

    public void setMultiKill(int multikill) {
        this.multikill = multikill;
    }
}
