package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class IceCubeMinistreak extends Ministreak {

    private final ItemStack iceCubeItem = zl.itemBuilder(Material.PACKED_ICE, 1, "§bIce Cube", Arrays.asList(
            "§7Perk item",
            "§7Single-Use on melee strike.",
            "§7Deals §c1❤ §7true damage to victim.",
            "§7Gain §b40 XP§7.",
            "§7Attacks slow enemies for 10 seconds."
    ));

    @Override
    public void onTrigger(Player player) {
        player.getInventory().addItem(iceCubeItem);
    }

    @Override
    public double getDamagerModifier(Player damager, PitDamageEvent event) {
        PlayerData pData = Main.getInstance().getPlayerData(damager);
        ItemStack item = damager.getInventory().getItemInMainHand();

        if (!zl.itemCheck(item)) return 0;
        if (item.getType() != Material.PACKED_ICE) return 0;
        if (item.getItemMeta() == null) return 0;
        if (!item.getItemMeta().getDisplayName().equals("§bIce Cube")) return 0;

        zl.trueDamage(event.getDamaged(), damager, 2, "§cIce Cube", false);
        pData.setExp(pData.getExp() - 40);
        event.getDamaged().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 0, false, false, true));
        damager.playSound(damager.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);

        if (item.getAmount() == 1) {
            new BukkitRunnable() {

                private final int slot = damager.getInventory().getHeldItemSlot();

                @Override
                public void run() {
                    damager.getInventory().setItem(slot, new ItemStack(Material.AIR));
                }
            }.runTaskLater(Main.getInstance(), 1);
        } else {
            item.setAmount(item.getAmount() - 1);
        }

        return 0;
    }

    @Override
    public void onReset(Player player) {
        removeAll(player.getInventory(), iceCubeItem);
    }
}













