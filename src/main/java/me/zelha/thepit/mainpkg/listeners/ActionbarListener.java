package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitKillEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class ActionbarListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(PitDamageEvent e) {
        Player damaged = e.getDamaged();
        Player damager = e.getDamager();

        String bar = "§7" + damaged.getName() + " ";
        StringBuilder barBuilder = new StringBuilder();
        StringBuilder barBuilder2 = new StringBuilder();

        int health = (int) Math.ceil(damaged.getHealth() / 2);
        int healthAfterDmg = (int) Math.floor(Math.max(((damaged.getHealth() / 2D) - (e.getDamage() / 2D)), 0));
        int maxHealth = (int) damaged.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2;

        for (int i = 0; i < maxHealth; i++) barBuilder.append("❤");

        if (damaged.getAbsorptionAmount() > 0) {
            int absorption = (int) Math.ceil((damaged.getAbsorptionAmount() + e.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION)) / 2);

            for (int i = 0; i < (int) Math.ceil(damaged.getAbsorptionAmount() / 2); i++) barBuilder2.append("❤");

            barBuilder2.replace(Math.max(absorption, 0), Math.max(absorption, 0), "§6");
            barBuilder2.replace(0, 0, "§e");
        }

        barBuilder.replace(health, health, "§0");
        barBuilder.replace(healthAfterDmg, healthAfterDmg, "§c");
        barBuilder.replace(0, 0, "§4");
        damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(bar + barBuilder + barBuilder2));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onKill(PitKillEvent e) {
        e.getKiller().spigot().sendMessage(
                ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7" + e.getDead().getName() + " " + "§a§lKILL!")
        );
    }
}
