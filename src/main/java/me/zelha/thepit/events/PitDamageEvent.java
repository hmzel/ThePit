package me.zelha.thepit.events;

import com.google.common.base.Function;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;

public class PitDamageEvent extends Event implements Cancellable {
    //bunch of copied & semi-copied code here to try and get accurate results when calculating final damage using this class

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player damaged;
    private final Player damager;
    private Map<EntityDamageEvent.DamageModifier, Double> modifiers = new EnumMap<>(DamageModifier.class);
    private Map<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> modifierFunctions = new EnumMap<>(DamageModifier.class);
    private final Arrow arrow;
    private boolean cancelled = false;
    private double boost;
    private double finalDamageModifier = 0;

    public PitDamageEvent(EntityDamageByEntityEvent event, double boost) {
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
        this.boost = boost;

        try {
            Field modifiersField = EntityDamageEvent.class.getDeclaredField("modifiers");
            Field modifierFunctionsField = EntityDamageEvent.class.getDeclaredField("modifierFunctions");

            modifiersField.setAccessible(true);
            modifierFunctionsField.setAccessible(true);

            Map<DamageModifier, Double> modifiers = (Map<EntityDamageEvent.DamageModifier, Double>) modifiersField.get(event);
            Map<DamageModifier, Function<? super Double, Double>> modifierFunctions = (Map<EntityDamageEvent.DamageModifier, Function<? super Double, Double>>) modifierFunctionsField.get(event);

            this.modifiers.put(DamageModifier.BASE, modifiers.get(DamageModifier.BASE));
            this.modifiers.put(DamageModifier.HARD_HAT, modifiers.get(DamageModifier.HARD_HAT));
            this.modifiers.put(DamageModifier.BLOCKING, modifiers.get(DamageModifier.BLOCKING));
            this.modifiers.put(DamageModifier.ARMOR, modifiers.get(DamageModifier.ARMOR));
            this.modifiers.put(DamageModifier.RESISTANCE, modifiers.get(DamageModifier.RESISTANCE));
            this.modifiers.put(DamageModifier.MAGIC, modifiers.get(DamageModifier.MAGIC));
            this.modifiers.put(DamageModifier.ABSORPTION, modifiers.get(DamageModifier.ABSORPTION));
            this.modifierFunctions.put(DamageModifier.BASE, modifierFunctions.get(DamageModifier.BASE));
            this.modifierFunctions.put(DamageModifier.HARD_HAT, modifierFunctions.get(DamageModifier.HARD_HAT));
            this.modifierFunctions.put(DamageModifier.BLOCKING, modifierFunctions.get(DamageModifier.BLOCKING));
            this.modifierFunctions.put(DamageModifier.ARMOR, modifierFunctions.get(DamageModifier.ARMOR));
            this.modifierFunctions.put(DamageModifier.RESISTANCE, modifierFunctions.get(DamageModifier.RESISTANCE));
            this.modifierFunctions.put(DamageModifier.MAGIC, modifierFunctions.get(DamageModifier.MAGIC));
            this.modifierFunctions.put(DamageModifier.ABSORPTION, modifierFunctions.get(DamageModifier.ABSORPTION));
        } catch (NoSuchFieldException | IllegalAccessException err) {
            err.printStackTrace();
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
        return getDamage(DamageModifier.BASE);
    }

    public double getDamage(DamageModifier modifier) {
        return (modifiers.get(modifier) == null) ? 0 : modifiers.get(modifier);
    }

    public double getFinalDamage() {
        double old = getDamage();
        double damage = 0;
        Map<DamageModifier, Double> finalModifiers = new EnumMap<>(modifiers);

        setDamage(old * boost);
        finalModifiers.put(DamageModifier.ARMOR, finalModifiers.get(DamageModifier.ARMOR) + finalDamageModifier);

        for (DamageModifier modifier : DamageModifier.values()) damage += getDamage(modifier);

        setDamage(old);

        return damage;
    }

    public double getBoost() {
        return boost;
    }

    @Nullable
    public Arrow getArrow() {
        return arrow;
    }

    public double getFinalDamageModifier() {
        return finalDamageModifier;
    }

    /**
     * Sets the raw amount of damage caused by the event.
     * <p>
     * For compatibility this also recalculates the modifiers and scales
     * them by the difference between the modifier for the previous damage
     * value and the new one.
     *
     * @param damage The raw amount of damage caused by the event
     */
    public void setDamage(double damage) {//stolen code poggers
        // These have to happen in the same order as the server calculates them, keep the enum sorted
        double remaining = damage;
        double oldRemaining = getDamage(DamageModifier.BASE);

        for (DamageModifier modifier : DamageModifier.values()) {
            if (modifiers.get(modifier) == null) continue;
            if (modifierFunctions.get(modifier) == null) continue;

            Function<? super Double, Double> modifierFunction = modifierFunctions.get(modifier);
            double newVanilla = modifierFunction.apply(remaining);
            double oldVanilla = modifierFunction.apply(oldRemaining);
            double difference = oldVanilla - newVanilla;

            // Don't allow value to cross zero, assume zero values should be negative
            double old = getDamage(modifier);

            if (old > 0) {
                setDamage(modifier, Math.max(0, old - difference));
            } else {
                setDamage(modifier, Math.min(0, old - difference));
            }

            remaining += newVanilla;
            oldRemaining += oldVanilla;
        }

        setDamage(DamageModifier.BASE, damage);
    }

    public void setBoost(double boost) {
        this.boost = boost;
    }

    public void addFinalDamageModifier(double damage) {
        this.finalDamageModifier += damage;
    }

    private void setDamage(EntityDamageEvent.DamageModifier type, double damage) throws IllegalArgumentException, UnsupportedOperationException {
        if (!modifiers.containsKey(type)) {
            throw type == null ? new IllegalArgumentException("Cannot have null DamageModifier") : new UnsupportedOperationException(type + " is not applicable to " + damaged);
        }

        modifiers.put(type, damage);
    }
}