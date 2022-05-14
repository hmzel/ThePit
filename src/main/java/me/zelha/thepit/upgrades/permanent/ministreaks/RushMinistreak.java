package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RushMinistreak extends Ministreak implements Listener {

    private final Map<UUID, Double> speedMap = new HashMap<>();

    public RushMinistreak() {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void onTrigger(Player player) {
        UUID uuid = player.getUniqueId();

        speedMap.putIfAbsent(uuid, 0D);

        if (Main.getInstance().getPlayerData(player).getStreak() > 50) return;

        AttributeInstance speed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);

        speed.setBaseValue(speed.getBaseValue() + 0.0015);
        speedMap.put(uuid, speedMap.get(uuid) + 0.0015);
    }

    @Override
    public void onReset(Player player) {
        if (!speedMap.containsKey(player.getUniqueId())) return;

        AttributeInstance speed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        speed.setBaseValue(speed.getBaseValue() - speedMap.get(player.getUniqueId()));
        speedMap.remove(player.getUniqueId());
    }
}
