package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.ZelLogic;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class BountyListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final Random rng = new Random();
    private final Map<UUID, Long> timeTracker = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(PitKillEvent e) {
        Player killer = e.getKiller();
        PlayerData pData = Main.getInstance().getPlayerData(killer);

        timeTracker.putIfAbsent(killer.getUniqueId(), System.currentTimeMillis());

        int calculatedBounty = calculateBounty(pData.getStreak(), (long) ((timeTracker.get(killer.getUniqueId()) - System.currentTimeMillis()) * 0.001));

        if (pData.getBounty() + calculatedBounty > pData.getMaxBounty()) calculatedBounty = pData.getMaxBounty() - pData.getBounty();

        if (calculatedBounty != 0) {
            String action = "bump";

            if (pData.getBounty() == 0) action = "of";

            pData.setBounty(pData.getBounty() + calculatedBounty);
            Bukkit.broadcastMessage(
                    "§6§lBOUNTY! §7" + action + " §6§l" + calculatedBounty + "g §7on " + zl.getColorBracketAndLevel(killer)
                    + " §7" + killer.getName() + " for high streak"
            );
        }

        timeTracker.put(killer.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!zl.playerCheck(p)) {
                    cancel();
                    return;
                }

                if (pData.getBounty() == 0 || zl.spawnCheck(p.getLocation())) return;

                double x, z;

                do {
                    x = rng.nextInt(85) / 100D;
                    z = rng.nextInt(85) / 100D;
                } while (p.getLocation().distance(p.getLocation().add(x, -0.5, z)) < 0.6);

                if (rng.nextBoolean()) x = -x;
                if (rng.nextBoolean()) z = -z;

                double finalX = x, finalZ = z;

                new BukkitRunnable() {

                    private int timer = 0;
                    private ArmorStand particle = null;
                    private final Location location = p.getLocation().add(finalX, -0.5, finalZ);

                    @Override
                    public void run() {
                        if (!zl.playerCheck(p) || timer == 10) {
                            if (particle != null) particle.remove();

                            cancel();
                            return;
                        }

                        if (particle == null) {
                            particle = p.getWorld().spawn(location, ArmorStand.class,
                                    armorstand -> {
                                        armorstand.setVisible(false);
                                        armorstand.setGravity(false);
                                        armorstand.setPersistent(true);
                                        armorstand.setMarker(true);
                                        armorstand.setInvulnerable(true);
                                        armorstand.setAI(false);
                                        armorstand.setCustomName("§6§l" + pData.getBounty() + "g");
                                        armorstand.setCustomNameVisible(true);
                                        armorstand.addScoreboardTag("bounty");
                                        armorstand.setInvisible(true);

                                        for (EquipmentSlot slots : EquipmentSlot.values()) {
                                            armorstand.addEquipmentLock(slots, ArmorStand.LockType.ADDING_OR_CHANGING);
                                        }
                                    });

                            ((CraftPlayer) p).getHandle().b.sendPacket(new PacketPlayOutEntityDestroy(particle.getEntityId()));
                        }

                        particle.teleport(location.add(0, 0.25, 0));
                        timer++;
                    }
                }.runTaskTimer(Main.getInstance(), 0, 1);
            }
        }.runTaskTimer(Main.getInstance(), 0, 4);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        timeTracker.remove(e.getPlayer().getUniqueId());
    }

    private boolean randomBounty(double streak, long secondsBetweenKills) {
        int rng = (streak <= 0) ? 0 : (this.rng.nextInt((int) Math.round(streak)) + 1) - (int) Math.round(secondsBetweenKills * 0.1);

        return rng > streak / 2;
    }

    private int calculateBounty(double streak, long secondsBetweenKills) {
        if (!randomBounty(streak, secondsBetweenKills)) return 0;

        if (streak < 10 && secondsBetweenKills < 5) {
            return 50;
        } else if (streak < 25 && secondsBetweenKills > 10) {
            return 100;
        } else if (streak < 25 && secondsBetweenKills < 5) {
            return 150;
        } else if (streak < 50 && secondsBetweenKills < 5) {
            return 200;
        } else if (streak < 100 && secondsBetweenKills < 1.5) {
            return 250;
        } else {
            return 300;
        }
    }
}



