package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitAssistEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Passives;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static me.zelha.thepit.zelenums.Perks.STREAKER;

public class BaseResourceListener implements Listener {

    //using multiple eventhandlers to mimic behavior in pit, i dont know why its actually in this order in regular pit but i like being accurate

    @EventHandler
    public void onKill(PitKillEvent e) {
        PlayerData killerData = Main.getInstance().getPlayerData(e.getKiller());
        PlayerData deadData = Main.getInstance().getPlayerData(e.getDead());
        double streakModifier = 0;

        if (killerData.getStreak() <= (killerData.getPassiveTier(Passives.EL_GATO) - 1)) {
            e.addExp(5, "El Gato");
            e.addGold(5, "El Gato");
        }

        if (killerData.getStreak() == 4) {
            streakModifier = 3;
        } else if (killerData.getStreak() >= 5 && killerData.getStreak() < 20) {
            streakModifier = 5;
        } else if (killerData.getStreak() < 100 && killerData.getStreak() >= 20) {
            streakModifier = Math.floor(killerData.getStreak() / 10.0D) * 3;
        } else if (killerData.getStreak() >= 100) {
            streakModifier = 30;
        }

        if (killerData.getStreak() >= 4 && killerData.hasPerkEquipped(STREAKER)) {
            e.addExp((int) (streakModifier * 3), "Killer on streak (Streaker Perk)");
        } else if (killerData.getStreak() >= 4) {
            e.addExp((int) streakModifier, "Killer on streak");
        }

        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) {
            e.addExpModifier(0.90, "Killed a noob");
            e.addGoldModifier(0.90, "Killed a noob");
        }

        if (killerData.getPassiveTier(Passives.XP_BOOST) > 0) {
            e.addExpModifier(1 + (killerData.getPassiveTier(Passives.XP_BOOST) / 10.0), "XP Boost");
        }

        if (killerData.getPassiveTier(Passives.GOLD_BOOST) > 0) {
            e.addGoldModifier(1 + (killerData.getPassiveTier(Passives.GOLD_BOOST) / 10.0), "Gold Boost");
        }

        if (deadData.getBounty() != 0) e.addSecondaryGold(deadData.getBounty(), "Bounty");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onKill2(PitKillEvent e) {
        PlayerData killerData = Main.getInstance().getPlayerData(e.getKiller());
        PlayerData deadData = Main.getInstance().getPlayerData(e.getDead());

        if (deadData.getStreak() > 5) {
            e.addExp((int) Math.min(Math.round(deadData.getStreak()), 25), "Streak Shutdown");
            e.addGold(Math.min((int) Math.round(deadData.getStreak()), 30), "Streak Shutdown");
        }

        if (killerData.getStreak() <= 3 && (killerData.getLevel() <= 30 || killerData.getPrestige() == 0)) {
            e.addExp(4, "First 3 kills");
            e.addGold(4, "First 3 kills");
        }

        if (deadData.getLevel() > killerData.getLevel()) {
            e.addExp((int) Math.round((deadData.getLevel() - killerData.getLevel()) / 4.5), "Level difference");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKill3(PitKillEvent e) {
        Player dead = e.getDead();
        Player killer = e.getKiller();
        PlayerData killerData = Main.getInstance().getPlayerData(killer);

        if (dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() > killer.getAttribute(Attribute.GENERIC_ARMOR).getValue() && Math.round((dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() - killer.getAttribute(Attribute.GENERIC_ARMOR).getValue()) / 5) != 0) {
            e.addGold(Math.round((dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() - killer.getAttribute(Attribute.GENERIC_ARMOR).getValue()) / 5), "Armor difference");
        }

        if (killerData.getXpStack() != 0) {
            e.addExp(killerData.getXpStack(), "XP Stack");
        }

        if (killerData.getGoldStack() != 0) {
            e.addGold(killerData.getGoldStack(), "Gold Stack");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAssist(PitAssistEvent e) {
        Player dead = e.getDead();
        Player assister = e.getAssisted();
        PlayerData assisterData = Main.getInstance().getPlayerData(e.getAssisted());
        PlayerData deadData = Main.getInstance().getPlayerData(e.getDead());

        if (dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() > assister.getAttribute(Attribute.GENERIC_ARMOR).getValue()) {
            e.addGold(Math.round((dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() - assister.getAttribute(Attribute.GENERIC_ARMOR).getValue()) / 5), "Armor difference");
        }

        if (deadData.getStreak() > 5) {
            e.addExp(Math.min((int) Math.round(deadData.getStreak()), 25), "Streak Shutdown");
            e.addGold(Math.min((int) Math.round(deadData.getStreak()), 30), "Streak Shutdown");
        }

        if (deadData.getLevel() > assisterData.getLevel()) {
            e.addExp((int) Math.round((deadData.getLevel() - assisterData.getLevel()) / 4.5), "Level difference");
        }

        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) {
            e.addExpModifier(0.90, "Killed a noob");
            e.addGoldModifier(0.90, "Killed a noob");
        }

        if (assisterData.getPassiveTier(Passives.XP_BOOST) > 0) {
            e.addExpModifier(1 + (assisterData.getPassiveTier(Passives.XP_BOOST) / 10.0), "XP Boost");
        }

        if (assisterData.getPassiveTier(Passives.GOLD_BOOST) > 0) {
            e.addGoldModifier(1 + (assisterData.getPassiveTier(Passives.GOLD_BOOST) / 10.0), "Gold Boost");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAssist2(PitAssistEvent e) {
        double percentage = Double.parseDouble(BigDecimal.valueOf(e.getPercentage()).setScale(2, RoundingMode.HALF_EVEN).toString());

        e.addExpModifier(percentage, "Kill participation");
        e.addGoldModifier(percentage, "Kill participation");
    }
}

























