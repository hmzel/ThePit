package me.zelha.thepit.events;

import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class PitAssistEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    private final Player dead;
    private final Player assisted;
    private final double percentage;
    private final List<Pair<String, Double>> expAdditions = new ArrayList<>();
    private final List<Pair<String, Double>> expBoosts = new ArrayList<>();
    private final List<Pair<String, Double>> baseGoldBoosts = new ArrayList<>();
    private final List<Pair<String, Double>> goldAdditions = new ArrayList<>();
    private final List<Pair<String, Double>> goldBoosts = new ArrayList<>();
    private final List<Pair<String, Double>> addAfterGoldBoosts = new ArrayList<>();

    public PitAssistEvent(Player dead, Player assisted, double percentage) {
        this.dead = dead;
        this.assisted = assisted;
        this.percentage = percentage;

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
        cancelled = cancel;
    }

    public void addExp(double exp, String reason) {
        expAdditions.add(Pair.of(reason, exp));
    }

    public void addExpBoost(double boost, String reason) {
        expBoosts.add(Pair.of(reason, boost));
    }

    public void addBaseGoldBoost(double boost, String reason) {
        baseGoldBoosts.add(Pair.of(reason, boost));
    }

    public void addGold(double gold, String reason) {
        goldAdditions.add(Pair.of(reason, gold));
    }

    public void addGoldBoost(double boost, String reason) {
        goldBoosts.add(Pair.of(reason, boost));
    }

    public void addAfterGoldBoost(double gold, String reason) {
        addAfterGoldBoosts.add(Pair.of(reason, gold));
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

    public List<Pair<String, Double>> getExpAdditions() {
        return expAdditions;
    }

    public List<Pair<String, Double>> getExpBoosts() {
        return expBoosts;
    }

    public List<Pair<String, Double>> getBaseGoldBoosts() {
        return baseGoldBoosts;
    }

    public List<Pair<String, Double>> getGoldAdditions() {
        return goldAdditions;
    }

    public List<Pair<String, Double>> getGoldBoosts() {
        return goldBoosts;
    }

    public List<Pair<String, Double>> getAddAfterGoldBoosts() {
        return addAfterGoldBoosts;
    }
}
