package me.zelha.thepit.events;

import me.zelha.thepit.zelenums.NPCs;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NPCInteractEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final NPCs npc;
    private final Player player;

    public NPCInteractEvent(Player player, NPCs npc) {
        this.player = player;
        this.npc = npc;
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

    public NPCs getNPC() {
        return npc;
    }
}
