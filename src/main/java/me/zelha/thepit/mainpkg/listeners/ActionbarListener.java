package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitKillEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static org.bukkit.event.entity.EntityDamageEvent.DamageModifier.ABSORPTION;

public class ActionbarListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(PitDamageEvent e) {
        Player damaged = e.getDamaged();
        StringBuilder barBuilder = new StringBuilder();
        StringBuilder barBuilder2 = new StringBuilder();

        for (int i = 0; i < (int) damaged.getMaxHealth() / 2; i++) barBuilder.append("❤");

        if (damaged.getAbsorptionAmount() > 0) {
            for (int i = 0; i < (int) Math.ceil(damaged.getAbsorptionAmount() / 2); i++) barBuilder2.append("❤");

            barBuilder2.insert(Math.max((int) Math.ceil((damaged.getAbsorptionAmount() + e.getDamage(ABSORPTION)) / 2), 0), "§6");
            barBuilder2.insert(0, "§e");
        }

        barBuilder.insert((int) Math.ceil(damaged.getHealth() / 2), "§0");
        barBuilder.insert((int) Math.floor(Math.max((damaged.getHealth() / 2) - (e.getFinalDamage() / 2), 0)), "§c");
        barBuilder.insert(0, "§4");
        e.getDamager().spigot().sendMessage(
                ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7" + damaged.getName() + " " + barBuilder + barBuilder2)
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onKill(PitKillEvent e) {
        e.getKiller().spigot().sendMessage(
                ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7" + e.getDead().getName() + " " + "§a§lKILL!")
        );
    }
}
