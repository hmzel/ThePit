package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
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

public class GoldNanofactoryMinistreak extends Ministreak implements Listener {

    private final ItemStack nanofactoryItem = zl.itemBuilder(Material.GOLD_NUGGET, 1, "§6Gold Nano-factory", Arrays.asList(
            "§7Perk item",
            "§7Spawns §67 gold ingots",
            "§7Grants §cRegen IV §7(0:02)"
    ));

    public GoldNanofactoryMinistreak() {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void onTrigger(Player player) {
        player.getInventory().addItem(nanofactoryItem);
    }

    @Override
    public void onReset(Player player) {
        removeAll(player.getInventory(), nanofactoryItem);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();

        if (e.getAction() == Action.PHYSICAL) return;
        if (!zl.itemCheck(item)) return;
        if (item.getType() != Material.GOLD_NUGGET) return;
        if (item.getItemMeta() == null) return;
        if (!item.getItemMeta().getDisplayName().equals("§6Gold Nano-factory")) return;

        if (item.getAmount() == 1) {
            e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        } else {
            item.setAmount(item.getAmount() - 1);
        }

        new BukkitRunnable() {

            int i = 0;

            @Override
            public void run() {
                if (!zl.playerCheck(p)) {
                    cancel();
                    return;
                }

                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.5F + (0.05F * i));
                ((CraftPlayer) p).getHandle().a(CraftItemStack.asNMSCopy(new ItemStack(Material.GOLD_INGOT)), false, true);

                i++;

                if (i == 7) cancel();
            }
        }.runTaskTimer(Main.getInstance(), 0, 3);

        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 3, false, false, true));
    }
}






