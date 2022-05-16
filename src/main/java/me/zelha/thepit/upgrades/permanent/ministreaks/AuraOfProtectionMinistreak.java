package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.TrueDamageEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuraOfProtectionMinistreak extends Ministreak implements Listener {

    private final Set<UUID> triggered = new HashSet<>();
    private final ItemStack auraItem = zl.itemBuilder(Material.SLIME_BALL, 1, "§aAura of Protection", Arrays.asList(
            "§7Perk item",
            "§9Resistance II §7(0:04)",
            "§eTrue Damage §7immunity (0:015)"
    ));

    public AuraOfProtectionMinistreak() {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void onTrigger(Player player) {
        player.getInventory().addItem(auraItem);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (e.getAction() == Action.PHYSICAL) return;
        if (!zl.itemCheck(item)) return;
        if (item.getType() != Material.SLIME_BALL) return;
        if (item.getItemMeta() == null) return;
        if (!item.getItemMeta().getDisplayName().equals("§aAura of Protection")) return;

        if (item.getAmount() == 1) {
            p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        } else {
            item.setAmount(item.getAmount() - 1);
        }

        p.playSound(p.getLocation(), Sound.ENTITY_GUARDIAN_AMBIENT, 1, 1);
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 0, false, false, true));
        triggered.add(p.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                triggered.remove(p.getUniqueId());
            }
        }.runTaskLater(Main.getInstance(), 300);
    }

    @EventHandler
    public void onTrueDamage(TrueDamageEvent e) {
        if (!e.isVeryTrue() && triggered.contains(e.getDamaged().getUniqueId())) e.setCancelled(true);
    }
}







