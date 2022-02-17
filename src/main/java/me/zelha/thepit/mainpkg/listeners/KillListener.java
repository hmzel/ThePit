package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.upgrades.permanent.perks.PerkListenersAndUtils;
import me.zelha.thepit.zelenums.Passives;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;
import java.util.UUID;

import static me.zelha.thepit.zelenums.Perks.BOUNTY_HUNTER;
import static me.zelha.thepit.zelenums.Perks.STREAKER;
import static org.bukkit.Material.GOLDEN_LEGGINGS;

public class KillListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final PerkListenersAndUtils perkUtils = Main.getInstance().getPerkUtils();
    private final RunMethods runTracker = Main.getInstance().generateRunMethods();
    private final RunMethods runTracker2 = Main.getInstance().generateRunMethods();

    private String calculateKillMessage(Player killer) {
        PlayerData pData = Main.getInstance().getPlayerData(killer);

        switch (pData.getMultiKill()) {
            case 1:
                return "§a§lKILL!";
            case 2:
                return "§a§lDOUBLE KILL!";
            case 3:
                return "§a§lTRIPLE KILL!";
            case 4:
                return "§a§lQUADRA KILL!";
            case 5:
                return "§a§lPENTA KILL!";
            default:
                return "§a§lMULTI KILL! §7(" + pData.getMultiKill() + ")";
        }
    }

    private int calculateEXP(Player dead, Player killer) {
        double exp = 5;
        double streakModifier = 0;
        int maxEXP = 250;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData killerData = Main.getInstance().getPlayerData(killer);

        if (killerData.getStreak() <= (killerData.getPassiveTier(Passives.EL_GATO) - 1)) exp += 5;
        if (deadData.getStreak() > 5) exp += (int) Math.min(Math.round(deadData.getStreak()), 25);
        if (killerData.getStreak() <= 3 && killerData.getLevel() <= 30) exp += 4;
        if (deadData.getLevel() > killerData.getLevel()) exp += (int) Math.round((deadData.getLevel() - killerData.getLevel()) / 4.5);

        if (killerData.getStreak() >= 5 && killerData.getStreak() < 20) {
            streakModifier = 3;
        } else if (killerData.getStreak() >= 20 && killerData.getStreak() <= 30) {
            streakModifier = 5;
        } else if (killerData.getStreak() < 200 && killerData.getStreak() > 30) {
            streakModifier = (Math.floor(killerData.getStreak() / 10.0D) - 1) * 3;
        } else if (killerData.getStreak() >= 200) {
            streakModifier = 60;
        }

        if (killerData.hasPerkEquipped(STREAKER)) streakModifier *= 3;

        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) exp *= 0.90;
        if (killerData.getPassiveTier(Passives.XP_BOOST) > 0) exp *= 1 + (killerData.getPassiveTier(Passives.XP_BOOST) / 10.0);

        exp += streakModifier;

        return (int) Math.min(Math.ceil(exp), maxEXP);
    }

    private double calculateGold(Player dead, Player killer) {
        int gold = 10;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData killerData = Main.getInstance().getPlayerData(killer);
        PlayerInventory deadInv = dead.getInventory();
        PlayerInventory killerInv = killer.getInventory();

        if (perkUtils.hasBeenShotBySpammer(killer, dead)) gold *= 3;
        if (killerData.hasPerkEquipped(BOUNTY_HUNTER) && zl.itemCheck(killerInv.getLeggings()) && killerInv.getLeggings().getType() == GOLDEN_LEGGINGS) gold += 4;

        if (killerData.getStreak() <= killerData.getPassiveTier(Passives.EL_GATO)) gold += 5;
        if (deadData.getLevel() > killerData.getLevel()) gold += (int) Math.round((deadData.getLevel() - killerData.getLevel()) / 4.5);
        if (deadData.getStreak() > 5) gold += Math.min((int) Math.round(deadData.getStreak()), 25);
        if (killerData.getStreak() <= 3 && killerData.getLevel() <= 30) gold += 4;
        //genesis thing here
        for (ItemStack item : deadInv.getArmorContents()) if (zl.itemCheck(item) && item.getType().name().contains("DIAMOND")) gold += 2;

        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) gold *= 0.09;
        if (killerData.getPassiveTier(Passives.GOLD_BOOST) > 0) gold *= 1 + (killerData.getPassiveTier(Passives.GOLD_BOOST) / 10.0);
        //renown gold boost
        //celeb

        return gold + deadData.getBounty();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();
        Player damaged;
        Player damager;

        if (zl.spawnCheck(damagedEntity.getLocation()) || zl.spawnCheck(damagerEntity.getLocation())) return;
        if (zl.playerCheck(damagedEntity)) damaged = (Player) damagedEntity; else return;
        if (e.getCause() == DamageCause.FALL) return;

        if (damagerEntity instanceof Arrow && ((Arrow) damagerEntity).getShooter() instanceof Player && zl.playerCheck((Player) ((Arrow) damagerEntity).getShooter())) {
            damager = (Player) ((Arrow) damagerEntity).getShooter();
        } else if (zl.playerCheck(damagerEntity)) {
            damager = (Player) damagerEntity;
        } else {
            return;
        }

        if (runTracker2.hasID(damager.getUniqueId())) runTracker2.stop(damager.getUniqueId());

        BukkitTask multiKillTimer = new BukkitRunnable() {
            @Override
            public void run() {
                Main.getInstance().getPlayerData(damager).setMultiKill(0);
            }
        }.runTaskLater(Main.getInstance(), 60);

        runTracker2.setID(damager.getUniqueId(), multiKillTimer.getTaskId());

        double finalDMG = e.getFinalDamage();
        double currentHP = damaged.getHealth();

        if (currentHP - finalDMG <= 0) {
            String uuid = damaged.getUniqueId().toString();
            PlayerData damagedData = Main.getInstance().getPlayerData(damaged);
            PlayerData damagerData = Main.getInstance().getPlayerData(damager);
            double calculatedGold = calculateGold(damaged, damager);

            damagerData.setStreak(damagerData.getStreak() + 1);
            damagerData.setExp(damagerData.getExp() - calculateEXP(damaged, damager));
            damagerData.setGold(damagerData.getGold() + calculatedGold);
            damagerData.setMultiKill(damagerData.getMultiKill() + 1);

            if ((Math.floor(damagerData.getStreak()) % 10 == 0) || (damagerData.getStreak() < 6 && damagerData.getStreak() >= 5)) {
                Bukkit.broadcastMessage("§c§lSTREAK! §7of §c" + (int) Math.floor(damagerData.getStreak()) + " §7kills by "
                        + zl.getColorBracketAndLevel(damager.getUniqueId().toString()) + " §7" + damager.getName());
            }

            if (damagedData.getBounty() != 0) {
                Bukkit.broadcastMessage("§6§lBOUNTY CLAIMED! " + zl.getColorBracketAndLevel(damager.getUniqueId().toString())
                        + "§7 " + damager.getName() + " killed " + zl.getColorBracketAndLevel(damaged.getUniqueId().toString())
                        + "§7 " + damaged.getName() + " for §6§l" + zl.getFancyGoldString(damagedData.getBounty()) + "g");
                damagedData.setBounty(0);
            }

            damager.sendMessage(calculateKillMessage(damager) + " §7on " + zl.getColorBracketAndLevel(uuid) + " §7" + damaged.getName()
                    + " §b+" + calculateEXP(damaged, damager) + "§bXP §6+" + zl.getFancyGoldString(calculatedGold) + "§6g");

            if (!runTracker.hasID(damager.getUniqueId())) new BountyRunnable(damager).runTaskTimer(Main.getInstance(), 0, 1);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (runTracker.hasID(e.getPlayer().getUniqueId())) runTracker.stop(e.getPlayer().getUniqueId());
    }


    private class BountyRunnable extends BukkitRunnable {

        private final Player player;
        private final UUID uuid;
        private int ticksBetweenKills;
        private int secondsBetweenKills;
        private double streak;

        private BountyRunnable(Player player) {
            this.player = player;
            this.uuid = player.getUniqueId();
            this.ticksBetweenKills = 0;
            this.secondsBetweenKills = 0;
            this.streak = Main.getInstance().getPlayerData(uuid).getStreak();
        }

        private boolean randomBounty() {
            int rng = (streak == 0) ? 0 : (new Random().nextInt((int) Math.round(streak)) + 1) - (int) Math.round(secondsBetweenKills * 0.1);

            if (rng <= 0) return false;

            switch (rng) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    return false;
                default:
                    return true;
            }
        }

        private int calculateBounty() {
            if (randomBounty()) {
                if (streak < 10 && secondsBetweenKills < 5) {
                    return 50;
                } else if (streak < 25 && secondsBetweenKills > 10) {
                    return 100;
                } else if (streak < 25 && secondsBetweenKills < 5) {
                    return 150;
                } else if (streak < 50 && secondsBetweenKills < 5) {
                    return 200;
                } else if (streak < 50) {
                    return 250;
                } else {
                    return 300;
                }
            }
            return 0;
        }

        @Override
        public void run() {
            PlayerData pData = Main.getInstance().getPlayerData(uuid);

            if (!runTracker.hasID(uuid)) runTracker.setID(uuid, getTaskId());

            if (streak != pData.getStreak()) {
                streak = pData.getStreak();
                int calculatedBounty = calculateBounty();

                if (calculatedBounty != 0) {
                    if (pData.getBounty() == 0) {
                        pData.setBounty(calculatedBounty);
                        Bukkit.broadcastMessage("§6§lBOUNTY! §7of §6§l " + calculatedBounty + "g §7on " + zl.getColorBracketAndLevel(uuid.toString())
                                + " §7" + player.getName() + " for high streak");
                    } else if (pData.getBounty() + calculatedBounty <= 5000) {
                        pData.setBounty(pData.getBounty() + calculatedBounty);
                        Bukkit.broadcastMessage("§6§lBOUNTY! §7bump §6§l" + calculatedBounty + "g §7on " + zl.getColorBracketAndLevel(uuid.toString())
                                + " §7" + player.getName() + " for high streak");
                    } else if (pData.getBounty() < 5000) {
                        Bukkit.broadcastMessage("§6§lBOUNTY! §7bump §6§l" + (5000 - pData.getBounty()) + "g §7on "
                                + zl.getColorBracketAndLevel(uuid.toString()) + " §7" + player.getName() + " for high streak");
                        pData.setBounty(5000);
                    }
                }
                secondsBetweenKills = 0;
                ticksBetweenKills = 0;
            }
            ticksBetweenKills++;

            if (ticksBetweenKills == 20) {
                secondsBetweenKills++;
                ticksBetweenKills = 0;
            }
        }
    }
}



















