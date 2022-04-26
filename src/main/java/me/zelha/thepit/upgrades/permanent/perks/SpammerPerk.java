package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.zelha.thepit.zelenums.Perks.SPAMMER;
import static org.bukkit.Material.ARROW;

public class SpammerPerk extends Perk implements Listener {

    private final Map<UUID, UUID> spammerShotIdentifier = new HashMap<>();

    public SpammerPerk() {
        super(Perks.SPAMMER);
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public boolean hasBeenShotBySpammer(Player damager, Player damaged) {
        boolean bool = spammerShotIdentifier.containsKey(damager.getUniqueId())
                && spammerShotIdentifier.get(damager.getUniqueId()) == damaged.getUniqueId()
                && Main.getInstance().getPlayerData(damager).hasPerkEquipped(SPAMMER);

        if (bool) new BukkitRunnable() {
            @Override
            public void run() {
                spammerShotIdentifier.remove(damager.getUniqueId());
            }
        }.runTaskLater(Main.getInstance(), 1);

        return bool;
    }

    @Override
    public void onAttack(Player damager, Player damaged, Arrow arrow) {
        if (arrow == null) return;

        damager.getInventory().addItem(new ItemStack(ARROW, 3));
        spammerShotIdentifier.put(damager.getUniqueId(), damaged.getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        spammerShotIdentifier.remove(e.getPlayer().getUniqueId());
    }
}










