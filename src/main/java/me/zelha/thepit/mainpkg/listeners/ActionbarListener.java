package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.utils.ZelLogic;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class ActionbarListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();
        Player damaged;
        Player damager;

        if (zl.spawnCheck(damagedEntity.getLocation()) || zl.spawnCheck(damagerEntity.getLocation())) return;
        if (zl.playerCheck(damagedEntity)) damaged = (Player) damagedEntity; else return;

        if (damagerEntity instanceof Arrow && ((Arrow) damagerEntity).getShooter() instanceof Player) {
            damager = (Player) ((Arrow) damagerEntity).getShooter();
        } else if (zl.playerCheck(damagerEntity)) {
            damager = (Player) damagerEntity;
        } else {
            return;
        }

        String bar = "§7" + damaged.getName() + " ";

        if (damaged.getHealth() - e.getFinalDamage() <= 0) {
            damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(bar + "§a§lKILL!"));
            return;
        }

        StringBuilder barBuilder = new StringBuilder();
        StringBuilder barBuilder2 = new StringBuilder();

        int health = (int) Math.ceil(damaged.getHealth() / 2);
        int healthAfterDmg = (int) Math.floor(Math.max(((damaged.getHealth() / 2D) - (e.getFinalDamage() / 2D)), 0));
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
}
