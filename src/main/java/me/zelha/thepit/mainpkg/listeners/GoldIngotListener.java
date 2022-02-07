package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Passives;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class GoldIngotListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final SpawnListener spawnUtils = Main.getInstance().getSpawnListener();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        new ingotRunnable(e.getPlayer()).runTaskLater(Main.getInstance(), 0);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!zl.playerCheck(e.getEntity())) return;
        e.setCancelled(true);
        e.getItem().setPickupDelay(999999999);

        Player p = (Player) e.getEntity();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        double ingotGold;

        if (pData.getPassiveTier(Passives.GOLD_BOOST) != 0) {
            ingotGold = (2.5 * (1 + ((double) pData.getPassiveTier(Passives.GOLD_BOOST)) / 10)) * e.getItem().getItemStack().getAmount();
        } else {
            ingotGold = 2.5 * e.getItem().getItemStack().getAmount();
        }

        if (pData.hasPerkEquipped(Perks.TRICKLE_DOWN)) {
            ingotGold += 10;
            p.setHealth(Math.min(p.getHealth() + 2, p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        }

        pData.setGold(pData.getGold() + ingotGold);
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1.8F);
        p.sendMessage("§6§lGOLD PICKUP! §7from the ground §6" + zl.getFancyGoldString(ingotGold) + "g");
        zl.fakePickup(p, e.getItem(), 16);
    }


    private class ingotRunnable extends BukkitRunnable {

        private final Player p;

        private ingotRunnable(Player p) {
            this.p = p;
        }

        @Override
        public void run() {
            if (!zl.playerCheck(p)) cancel();

            int rng;

            do rng = new Random().nextInt(61); while (rng < 10);

            if (!spawnUtils.spawnCheck(p.getLocation())) {
                double decimal = new Random().nextInt(100);
                double x;
                double z;
                boolean xNegative = new Random().nextBoolean();
                boolean zNegative = new Random().nextBoolean();

                do x = new Random().nextInt(13) + decimal / 100; while (x < 3);
                do z = new Random().nextInt(13) + decimal / 100; while (z < 3);
                if (xNegative) x = -x;
                if (zNegative) z = -z;

                Location itemSpawnLoc = new Location(p.getWorld(), x, p.getLocation().getY(), z);

                if (!spawnUtils.spawnCheck(itemSpawnLoc)) p.getWorld().dropItemNaturally(itemSpawnLoc, new ItemStack(Material.GOLD_INGOT, 1));
            }
            new ingotRunnable(p).runTaskLater(Main.getInstance(), rng * 20);
        }
    }
}
