package me.zelha.thepit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PitAssistEvent extends ResourceManager implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    private final Player dead;
    private final Player assisted;
    private final double percentage;

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
