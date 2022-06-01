package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitKillEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Material.ARROW;

public class SpammerPerk extends Perk implements Listener {

    private final Map<UUID, UUID> spammerShotIdentifier = new HashMap<>();

    public SpammerPerk() {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void onAttack(Player damager, Player damaged, Arrow arrow) {
        if (arrow == null) return;

        damager.getInventory().addItem(new ItemStack(ARROW, 3));
        spammerShotIdentifier.put(damager.getUniqueId(), damaged.getUniqueId());
    }

    @Override
    public void addResourceModifiers(PitKillEvent event) {
        Player killer = event.getKiller();

        if (!spammerShotIdentifier.containsKey(killer.getUniqueId())) return;
        if (spammerShotIdentifier.get(killer.getUniqueId()) != event.getDead().getUniqueId()) return;

        event.addBaseGoldModifier(3, "Spammer");
        spammerShotIdentifier.remove(killer.getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        spammerShotIdentifier.remove(e.getPlayer().getUniqueId());
    }
}










