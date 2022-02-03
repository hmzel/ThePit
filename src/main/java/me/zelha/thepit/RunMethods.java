package me.zelha.thepit;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RunMethods {
    private final Map<UUID, Integer> task = new HashMap<UUID, Integer>();

    public void setID(UUID uuid, int id) {
        task.put(uuid, id);
    }

    public int getID(UUID uuid) {
        return task.get(uuid);
    }

    public boolean hasID(UUID uuid) {
        return task.containsKey(uuid);
    }

    public void stop(UUID uuid) {
        Bukkit.getScheduler().cancelTask(task.get(uuid));
        task.remove(uuid);
    }
}
