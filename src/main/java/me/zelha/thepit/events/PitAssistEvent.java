package me.zelha.thepit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;

public class PitAssistEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    private final Player dead;
    private final Player assisted;
    private final double percentage;
    private final Map<String, Integer> expAdditions = new HashMap<>();
    private final Map<String, Double> expBoosts = new HashMap<>();
    private final Map<String, Double> goldAdditions = new HashMap<>();
    private final Map<String, Double> goldBoosts = new HashMap<>();

    public PitAssistEvent(Player dead, Player assisted, double percentage) {
        this.dead = dead;
        this.assisted = assisted;
        this.percentage = percentage;

        expAdditions.put("Base §bXP", 10);
        goldAdditions.put("Base §6gold (g)", 5D);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public void addExp(Integer exp, String reason) {
        expAdditions.put(reason, exp);
    }

    public void addExpBoost(Double boost, String reason) {
        expBoosts.put(reason, boost);
    }

    public void addGold(Double gold, String reason) {
        goldAdditions.put(reason, gold);
    }

    public void addGoldBoost(Double boost, String reason) {
        goldBoosts.put(reason, boost);
    }

    public Player getDead() {
        return dead;
    }

    public Player getAssisted() {
        return assisted;
    }

    public double getPercentage() {
        return percentage;
    }
}
