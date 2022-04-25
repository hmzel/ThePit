package me.zelha.thepit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ExpChangeEvent extends Event {//called in PlayerData

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final int exp;

    public ExpChangeEvent(Player player, int exp) {
        this.player = player;
        this.exp = exp;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public int getExp() {
        return exp;
    }
}
