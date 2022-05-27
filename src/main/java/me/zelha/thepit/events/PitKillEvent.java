package me.zelha.thepit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;

public class PitKillEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    private final Player dead;
    private final Player killer;
    private final Map<String, Integer> expAdditions = new HashMap<>();
    private final Map<String, Double> expBoosts = new HashMap<>();
    private final Map<String, Double> goldAdditions = new HashMap<>();
    private final Map<String, Double> goldBoosts = new HashMap<>();
    private final boolean disconnected;

    public PitKillEvent(Player dead, Player killer, boolean disconnected) {
        this.dead = dead;
        this.killer = killer;
        this.disconnected = disconnected;

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
        this.cancelled = cancel;
    }

    public void addExp(int exp, String reason) {
        expAdditions.put(reason, exp);
    }

    public void addExpBoost(double boost, String reason) {
        expBoosts.put(reason, boost);
    }

    public void addGold(double gold, String reason) {
        goldAdditions.put(reason, gold);
    }

    public void addGoldBoost(double boost, String reason) {
        goldBoosts.put(reason, boost);
    }

    public Player getDead() {
        return dead;
    }

    public Player getKiller() {
        return killer;
    }

    public Map<String, Integer> getExpAdditions() {
        return expAdditions;
    }

    public Map<String, Double> getExpBoosts() {
        return expBoosts;
    }

    public Map<String, Double> getGoldAdditions() {
        return goldAdditions;
    }

    public Map<String, Double> getGoldBoosts() {
        return goldBoosts;
    }

    public boolean causedByDisconnect() {
        return disconnected;
    }
}










