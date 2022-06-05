package me.zelha.thepit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

public class TrueDamageEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player damaged;
    private final Player damager;
    private final boolean isVeryTrue;
    private boolean cancelled = false;
    private double damage;

    public TrueDamageEvent(Player damaged, @Nullable Player damager, double damage, boolean isVeryTrue) {
        this.damaged = damaged;
        this.damager = damager;
        this.damage = damage;
        this.isVeryTrue = isVeryTrue;
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

    public Player getDamaged() {
        return damaged;
    }

    @Nullable
    public Player getDamager() {
        return damager;
    }

    public double getDamage() {
        return damage;
    }

    public boolean isVeryTrue() {
        return isVeryTrue;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
