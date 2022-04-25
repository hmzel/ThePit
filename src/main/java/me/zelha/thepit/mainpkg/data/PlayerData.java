package me.zelha.thepit.mainpkg.data;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.ExpChangeEvent;
import me.zelha.thepit.zelenums.Megastreaks;
import me.zelha.thepit.zelenums.Ministreaks;
import me.zelha.thepit.zelenums.Passives;
import me.zelha.thepit.zelenums.Perks;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.*;

public class PlayerData {

    private final UUID uuid;
    private final Map<Integer, Perks> perkSlots = new HashMap<>();
    private final Map<Integer, Ministreaks> ministreakSlots = new HashMap<>();
    private final Map<Passives, Integer> passivesMap = new HashMap<>();
    private final Map<Perks, Boolean> perkUnlocks = new HashMap<>();
    private final Map<Megastreaks, Boolean> megastreakUnlocks = new HashMap<>();
    private final Map<Ministreaks, Boolean> ministreakUnlocks = new HashMap<>();
    private final List<String> slots = Arrays.asList("one", "two", "three", "four");
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
    private Megastreaks megastreak;
    private boolean combatLogged;

    public PlayerData(Document document) {
        this.uuid = UUID.fromString(document.getString("uuid"));
        prestige = document.getInteger("prestige");
        level = document.getInteger("level");
        gold = document.getDouble("gold");
        exp = document.getInteger("exp");
        bounty = document.getInteger("bounty");
        streak = 0;
        combatTimer = 0;
        hideTimer = true;
        multikill = 0;
        megastreak = Megastreaks.findByEnumName(document.getString("megastreak"));
        combatLogged = document.getBoolean("combat_logged");

        for (String slot : slots) {
            perkSlots.put((slots.indexOf(slot) + 1), Perks.findByEnumName(document.getEmbedded(Arrays.asList("perk_slots", slot), String.class)));
        }

        for (int i = 0; i < 3; i++) {
            ministreakSlots.put(i + 1, Ministreaks.findByEnumName(document.getEmbedded(Arrays.asList("ministreak_slots", slots.get(i)), String.class)));
        }

        for (Passives passive : Passives.values()) {
            passivesMap.put(passive, document.getEmbedded(Arrays.asList("passives", passive.name().toLowerCase()), Integer.class));
        }

        for (Perks perk : Perks.values()) {
            perkUnlocks.put(perk, document.getEmbedded(Arrays.asList("perk_unlocks", perk.name().toLowerCase()), Boolean.class));
        }

        for (Megastreaks mega : Megastreaks.values()) {
            megastreakUnlocks.put(mega, document.getEmbedded(Arrays.asList("megastreak_unlocks", mega.name().toLowerCase()), Boolean.class));
        }

        for (Ministreaks mini : Ministreaks.values()) {
            ministreakUnlocks.put(mini, document.getEmbedded(Arrays.asList("ministreak_unlocks", mini.name().toLowerCase()), Boolean.class));
        }

        if (bounty != 0) {
            status = "bountied";
        } else {
            status = "idling";
        }
    }

    public boolean hasPerkEquipped(Perks perk) {
        for (Perks slotPerk : perkSlots.values()) {
            if (slotPerk == perk) return true;
        }
        return false;
    }

    public boolean hasMinistreakEquipped(Ministreaks mini) {
        for (Ministreaks slotMini : ministreakSlots.values()) {
            if (slotMini == mini) return true;
        }
        return false;
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

    public int getBounty() {
        return bounty;
    }

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

    public Megastreaks getMegastreak() {
        return megastreak;
    }

    public int getPassiveTier(Passives passive) {
        return passivesMap.get(passive);
    }

    public Perks getPerkAtSlot(int slot) {
        return perkSlots.get(slot);
    }

    public Ministreaks getMinistreakAtSlot(int slot) {
        return  ministreakSlots.get(slot);
    }

    public boolean getPerkUnlockStatus(Perks perk) {
        return perkUnlocks.get(perk);
    }

    public boolean getMegastreakUnlockStatus(Megastreaks mega) {
        return megastreakUnlocks.get(mega);
    }

    public boolean getMinistreakUnlockStatus(Ministreaks mini) {
        return ministreakUnlocks.get(mini);
    }

    public Perks[] getEquippedPerks() {
        return perkSlots.values().toArray(new Perks[0]);
    }

    public Ministreaks[] getEquippedMinistreaks() {
        return ministreakSlots.values().toArray(new Ministreaks[0]);
    }

    public boolean getCombatLogged() {
        return combatLogged;
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
        Main.getInstance().getServer().getPluginManager().callEvent(new ExpChangeEvent(Bukkit.getPlayer(uuid), exp));
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

    public void setMegastreak(Megastreaks mega) {
        this.megastreak = mega;
    }

    public void setPassiveTier(Passives passive, int tier) {
        passivesMap.put(passive, tier);
    }

    public void setPerkAtSlot(int slot, Perks perk) {
        perkSlots.put(slot, perk);
    }

    public void setMinistreakAtSlot(int slot, Ministreaks mini) {
        ministreakSlots.put(slot, mini);
    }

    public void setPerkUnlockStatus(Perks perk, boolean bool) {
        perkUnlocks.put(perk, bool);
    }

    public void setMegastreakUnlockStatus(Megastreaks mega, boolean bool) {
        megastreakUnlocks.put(mega, bool);
    }

    public void setMinistreakUnlockStatus(Ministreaks mini, boolean bool) {
        ministreakUnlocks.put(mini, bool);
    }

    public void setCombatLogged(boolean bool) {
        this.combatLogged = bool;
    }
}

















