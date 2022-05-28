package me.zelha.thepit.events;

import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.*;

public class PitKillEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    private final Player dead;
    private final Player killer;
    //i wish i could use maps here but they were causing weird issues and i couldnt figure out how to fix it
    private final List<Pair<String, Integer>> expAdditions = new ArrayList<>();
    private final List<Pair<String, Double>> expBoosts = new ArrayList<>();
    private final List<Pair<String, Double>> goldAdditions = new ArrayList<>();
    private final List<Pair<String, Double>> goldBoosts = new ArrayList<>();
    private final boolean disconnected;

    public PitKillEvent(Player dead, Player killer, boolean disconnected) {
        this.dead = dead;
        this.killer = killer;
        this.disconnected = disconnected;

        addExp(10, "Base §bXP");
        addGold(5, "Base §6gold (g)");
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
        expAdditions.add(Pair.of(reason, exp));
    }

    public void addExpBoost(double boost, String reason) {
        expBoosts.add(Pair.of(reason, boost));
    }

    public void addGold(double gold, String reason) {
        goldAdditions.add(Pair.of(reason, gold));
    }

    public void addGoldBoost(double boost, String reason) {
        goldBoosts.add(Pair.of(reason, boost));
    }

    public Player getDead() {
        return dead;
    }

    public Player getKiller() {
        return killer;
    }

    public List<Pair<String, Integer>> getExpAdditions() {
        return expAdditions;
    }

    public List<Pair<String, Double>> getExpBoosts() {
        return expBoosts;
    }

    public List<Pair<String, Double>> getGoldAdditions() {
        return goldAdditions;
    }

    public List<Pair<String, Double>> getGoldBoosts() {
        return goldBoosts;
    }

    public boolean causedByDisconnect() {
        return disconnected;
    }
}










