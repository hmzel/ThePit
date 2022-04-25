package me.zelha.thepit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PitDamageEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player damagee;
    private final Player damager;
    private boolean cancelled = false;
    private double damage;

    public PitDamageEvent(Player damagee, Player damager, double damage) {
        this.damagee = damagee;
        this.damager = damager;
        this.damage = damage;
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

    public Player getDamagee() {
        return damagee;
    }

    public Player getDamager() {
        return damager;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}