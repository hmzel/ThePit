package me.zelha.thepit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PitKillEvent extends ResourceManager implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    private final Player dead;
    private final Player killer;
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

    public Player getDead() {
        return dead;
    }

    public Player getKiller() {
        return killer;
    }

    public boolean causedByDisconnect() {
        return disconnected;
    }
}










