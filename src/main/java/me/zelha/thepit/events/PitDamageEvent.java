package me.zelha.thepit.events;

import me.zelha.thepit.Main;
import me.zelha.thepit.utils.ZelLogic;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class PitDamageEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final Player damaged;
    private final Player damager;
    private final Map<EntityDamageEvent.DamageModifier, Double> modifiers;
    private boolean cancelled = false;
    private double damage;
    private final Arrow arrow;

    public PitDamageEvent(EntityDamageByEntityEvent event) {
        Entity damagerEntity = event.getDamager();
        Player damaged = (Player) event.getEntity();
        Player damager;

        if (damagerEntity instanceof Arrow) {
            damager = (Player) ((Arrow) damagerEntity).getShooter();
            this.arrow = (Arrow) damagerEntity;
        } else {
            damager = (Player) damagerEntity;
            this.arrow = null;
        }

        this.damaged = damaged;
        this.damager = damager;
        this.damage = event.getFinalDamage();
        this.modifiers = new HashMap<>();

        for (EntityDamageEvent.DamageModifier modifier : EntityDamageEvent.DamageModifier.values()) {
            modifiers.put(modifier, event.getDamage(modifier));
        }
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

    public Player getDamager() {
        return damager;
    }

    public double getDamage() {
        return damage;
    }

    public double getDamage(EntityDamageEvent.DamageModifier modifier) {
        return modifiers.get(modifier);
    }

    @Nullable
    public Arrow getArrow() {
        return arrow;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}