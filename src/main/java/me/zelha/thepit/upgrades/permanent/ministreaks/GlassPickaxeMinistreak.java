package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class GlassPickaxeMinistreak extends Ministreak {///why is the item called glass sword???

    private final ItemStack glassPickaxeItem = zl.itemBuilder(Material.DIAMOND_PICKAXE, 1, "§bGlass Sword", Arrays.asList(
            "§7Perk item",
            "",
            "§7Single Use",
            "§7Deals §c0.5❤ §7true damage",
            "§a5s cooldown §7by player",
            "",
            "§9+8.5 Attack Damage"
    ), false, true, Pair.of(Enchantment.DAMAGE_ALL, 6));

    @Override
    public void onTrigger(Player player) {
        if (player.getInventory().contains(glassPickaxeItem)) {
            player.sendMessage("§c§lFULL! §7You may only have 1 Glass Sword!");
            return;
        }

        player.getInventory().addItem(glassPickaxeItem);
    }

    @Override
    public double getDamagerModifier(Player damager, PitDamageEvent event) {
        ItemStack item = damager.getInventory().getItemInMainHand();

        if (!zl.itemCheck(item)) return 0;
        if (item.getType() != Material.DIAMOND_PICKAXE) return 0;
        if (item.getItemMeta() == null) return 0;
        if (!item.getItemMeta().getDisplayName().equals("§bGlass Sword")) return 0;

        zl.trueDamage(event.getDamaged(), damager, 1, "§cGlass Pickaxe", false);
        damager.playSound(damager.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);

        new BukkitRunnable() {

            private final int slot = damager.getInventory().getHeldItemSlot();

            @Override
            public void run() {
                damager.getInventory().setItem(slot, new ItemStack(Material.AIR));
            }
        }.runTaskLater(Main.getInstance(), 1);

        return 0;
    }

    @Override
    public void onReset(Player player) {
        removeAll(player.getInventory(), glassPickaxeItem);
    }
}








