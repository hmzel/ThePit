package me.zelha.thepit.upgrades.permanent.megastreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class MagnumOpusMegastreak extends Megastreak {

    private final Random rng = new Random();

    @Override
    public void onTrigger(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);
        player.damage(131313);
        player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getEyeLocation(), 10, 1, 1, 1, 0);

        new BukkitRunnable() {

            private int i = 0;
            private final Location location = player.getLocation();

            @Override
            public void run() {
                Vector vector = new Vector();
                Item item = player.getWorld().dropItem(location, new ItemStack(Material.GOLD_INGOT));

                double rotX = rng.nextInt(360) - 180;
                double rotY = -(rng.nextInt(45) + 45);
                double xz = Math.cos(Math.toRadians(rotY));

                vector.setY(-Math.sin(Math.toRadians(rotY)));
                vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
                vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
                item.getLocation().setDirection(vector);
                item.setVelocity(vector.divide(new Vector(1.3, 1.3, 1.3)));
                i++;

                if (i == 10) cancel();
            }
        }.runTaskTimer(Main.getInstance(), 10, 10);

        Bukkit.broadcastMessage(
                "§c§lMEGSTREAK! " + zl.getColorBracketAndLevel(player) + " §7" + player.getName() + " §7activated "
                + pData.getMegastreak().getChatName() + " §7and promptly exploded!"
        );
    }
}
