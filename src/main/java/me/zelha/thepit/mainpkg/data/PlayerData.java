package me.zelha.thepit.mainpkg.data;

import org.bson.Document;

public class PlayerData {

    private int prestige;
    private int level;
    private double gold;
    private int exp;
    private String status;

    public PlayerData(Document document) {
        prestige = document.getInteger("prestige");
        level = document.getInteger("level");
        gold = document.getDouble("gold");
        exp = document.getInteger("exp");
        status = document.getString("status");
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

}
