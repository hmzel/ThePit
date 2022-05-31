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
    private final List<Pair<String, Double>> expAdditions = new ArrayList<>();
    private final List<Pair<String, Double>> expModifiers = new ArrayList<>();
    private final List<Pair<String, Double>> baseGoldModifiers = new ArrayList<>();
    private final List<Pair<String, Double>> goldAdditions = new ArrayList<>();
    private final List<Pair<String, Double>> goldModifiers = new ArrayList<>();
    private final List<Pair<String, Double>> addAfterGoldModifiers = new ArrayList<>();
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

    public Player getDead() {
        return dead;
    }

    public Player getKiller() {
        return killer;
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

    public List<Pair<String, Double>> getAddAfterGoldModifiers() {
        return addAfterGoldModifiers;
    }

    public boolean causedByDisconnect() {
        return disconnected;
    }
}










