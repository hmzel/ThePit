package me.zelha.thepit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class PitKillEvent extends ResourceManager implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    private final Player dead;
    private final Player killer;
    private final List<PitAssistEvent> assistEvents = new ArrayList<>();
    private final boolean disconnected;

    public PitKillEvent(Player dead, Player killer, boolean disconnected) {
        this.dead = dead;
        this.killer = killer;
        this.disconnected = disconnected;

        addExp(5, "Base §bXP");
        addGold(10, "Base §6gold (g)");
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

    public void addAssistEvent(PitAssistEvent event) {
        assistEvents.add(event);
    }

    public Player getDead() {
        return dead;
    }

    public Player getKiller() {
        return killer;
    }

    public List<PitAssistEvent> getAssistEvents() {
        return assistEvents;
    }

    public boolean causedByDisconnect() {
        return disconnected;
    }
}










