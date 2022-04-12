package me.zelha.thepit;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RunTracker {
    private final Map<UUID, Integer> tasks = new HashMap<UUID, Integer>();

    public void setID(UUID uuid, int id) {
        tasks.put(uuid, id);
    }

    public Integer getID(UUID uuid) {
        return tasks.get(uuid);
    }

    public boolean hasID(UUID uuid) {
        return tasks.containsKey(uuid);
    }

    public void stop(UUID uuid) {
        Bukkit.getScheduler().cancelTask(tasks.get(uuid));
        tasks.remove(uuid);
    }
}
