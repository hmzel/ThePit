package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class PungentMinistreak extends Ministreak implements Listener {

    private final ItemStack pungentItem = zl.itemBuilder(Material.FERMENTED_SPIDER_EYE, 1, "§cSmelly Bomb", Arrays.asList(
            "§7Perk item",
            "§7Confuses players around you,",
            "§7slowing them for 4 seconds."
    ));

    public PungentMinistreak() {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void onTrigger(Player player) {//why was this added to the game
        player.getInventory().addItem(pungentItem);
    }

    @Override
    public void onReset(Player player) {
        removeAll(player.getInventory(), pungentItem);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();

        if (e.getAction() == Action.PHYSICAL) return;
        if (!zl.itemCheck(item)) return;
        if (item.getType() != Material.FERMENTED_SPIDER_EYE) return;
        if (item.getItemMeta() == null) return;
        if (!item.getItemMeta().getDisplayName().equals("§cSmelly Bomb")) return;

        if (item.getAmount() == 1) {
            e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        } else {
            item.setAmount(item.getAmount() - 1);
        }

        for (Entity entity : e.getPlayer().getNearbyEntities(3, 3, 3)) {
            if (!zl.playerCheck(entity)) continue;

            ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0, false, false, true));
        }
    }
}


























