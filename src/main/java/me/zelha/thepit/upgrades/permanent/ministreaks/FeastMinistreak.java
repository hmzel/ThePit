package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FeastMinistreak extends Ministreak implements Listener {

    private final Map<UUID, Integer> timesEaten = new HashMap<>();
    private final ItemStack feastItem = zl.itemBuilder(Material.MUTTON, 1, "§6AAA-Rated Steak", Arrays.asList(
            "§7Perk item",
            "§c+20% damage §7(0:10)",
            "§eSpeed I §7(0:10)",
            "§9Resistance I §7(0:10)"
    ));

    public FeastMinistreak() {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void onTrigger(Player player) {
        player.getInventory().addItem(feastItem);
    }

    @Override
    public double getDamagerModifier(Player damager, PitDamageEvent event) {
        if (timesEaten.get(damager.getUniqueId()) == null) return 0;

        return timesEaten.get(damager.getUniqueId()) * 0.2;
    }

    @Override
    public void onReset(Player player) {
        timesEaten.remove(player.getUniqueId());
        removeAll(player.getInventory(), feastItem);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();

        if (e.getAction() == Action.PHYSICAL) return;
        if (!zl.itemCheck(item)) return;
        if (item.getType() != Material.MUTTON) return;
        if (item.getItemMeta() == null) return;
        if (!item.getItemMeta().getDisplayName().equals("§6AAA-Rated Steak")) return;

        if (item.getAmount() == 1) {
            p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        } else {
            item.setAmount(item.getAmount() - 1);
        }

        timesEaten.putIfAbsent(uuid, 0);
        timesEaten.put(uuid, timesEaten.get(uuid) + 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (timesEaten.get(uuid) == 1) {
                    timesEaten.remove(uuid);
                    return;
                }

                timesEaten.put(uuid, timesEaten.get(uuid) - 1);
            }
        }.runTaskLater(Main.getInstance(), 100);

        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0, false, false, true));
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false, true));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        timesEaten.remove(e.getPlayer().getUniqueId());
    }
}












