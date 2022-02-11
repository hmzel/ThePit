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
    private final SpawnListener spawnUtils = Main.getInstance().getSpawnListener();
    private final PerkListenersAndUtils perkUtils = Main.getInstance().getPerkUtils();
    private final RunMethods methods = Main.getInstance().generateRunMethods();
    private final RunMethods methods2 = Main.getInstance().generateRunMethods();

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
        int exp = 5;
        int baseModifier = 0;
        double percentageModifier = 1;
        int streakModifier = 0;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData killerData = Main.getInstance().getPlayerData(killer);

        if (deadData.getPrestige() == 0) percentageModifier -= 0.09;
        if (killerData.getStreak() <= 3 && killerData.getLevel() <= 30) baseModifier += 4;
        if (killerData.getStreak() <= (killerData.getPassiveTier(Passives.EL_GATO) - 1)) baseModifier += 5;
        if (killerData.getPassiveTier(Passives.XP_BOOST) > 0) percentageModifier += (killerData.getPassiveTier(Passives.XP_BOOST) / 10.0);
        if (deadData.getStreak() > 5) baseModifier += (int) Math.min(Math.round(deadData.getStreak()), 25);
        if (deadData.getLevel() > killerData.getLevel()) baseModifier += (int) Math.round((deadData.getLevel() - killerData.getLevel()) / 4.5);

        if (killerData.getStreak() < 5) {
            streakModifier += 3;
        } else if (killerData.getStreak() < 20) {
            streakModifier += 5;
        } else if (killerData.getStreak() < 200) {
            streakModifier += (Math.floor(killerData.getStreak() / 10) - 1) * 3;
        } else {
            streakModifier += 60;
        }

        if (killerData.hasPerkEquipped(STREAKER)) streakModifier *= 3;

        baseModifier += streakModifier;

        return Math.toIntExact(Math.round((exp + baseModifier) * percentageModifier));
    }

    private double calculateGold(Player dead, Player killer) {
        int gold = 10;
        int baseModifier = 0;
        double percentageModifier = 1;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData killerData = Main.getInstance().getPlayerData(killer);
        PlayerInventory deadInv = dead.getInventory();
        PlayerInventory killerInv = killer.getInventory();

        if (perkUtils.hasBeenShotBySpammer(killer, dead)) gold *= 3;
        if (killerData.hasPerkEquipped(BOUNTY_HUNTER) && zl.itemCheck(killerInv.getLeggings()) && killerInv.getLeggings().getType() == GOLDEN_LEGGINGS) baseModifier += 4;

        for (ItemStack item : deadInv.getArmorContents()) {
            if (zl.itemCheck(item) && item.getType().name().contains("DIAMOND")) baseModifier++;
        }

        if (deadData.getPrestige() == 0) percentageModifier -= 0.09;
        if (killerData.getStreak() <= 3 && killerData.getLevel() <= 30) baseModifier += 4;
        if (killerData.getStreak() <= killerData.getPassiveTier(Passives.EL_GATO)) baseModifier += 5;
        if (killerData.getPassiveTier(Passives.GOLD_BOOST) > 0) percentageModifier += (killerData.getPassiveTier(Passives.GOLD_BOOST) / 10.0);
        if (deadData.getStreak() > 5) baseModifier += Math.min((int) Math.round(deadData.getStreak()), 25);
        if (deadData.getLevel() > killerData.getLevel()) baseModifier += (int) Math.round((deadData.getLevel() - killerData.getLevel()) / 4.5);

        return ((gold + baseModifier) * percentageModifier) + deadData.getBounty();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();
        Player damaged;
        Player damager;

        if (spawnUtils.spawnCheck(damagedEntity.getLocation()) || spawnUtils.spawnCheck(damagerEntity.getLocation())) return;
        if (zl.playerCheck(damagedEntity)) damaged = (Player) damagedEntity; else return;
        if (e.getCause() == DamageCause.FALL) return;

        if (damagerEntity instanceof Arrow && ((Arrow) damagerEntity).getShooter() instanceof Player) {
            damager = (Player) ((Arrow) damagerEntity).getShooter();
        } else if (zl.playerCheck(damagerEntity)) {
            damager = (Player) damagerEntity;
        } else {
            return;
        }

        if (methods2.hasID(damager.getUniqueId())) methods2.stop(damager.getUniqueId());

        BukkitTask multiKillTimer = new BukkitRunnable() {
            @Override
            public void run() {
                Main.getInstance().getPlayerData(damager).setMultiKill(0);
            }
        }.runTaskLater(Main.getInstance(), 60);

        methods2.setID(damager.getUniqueId(), multiKillTimer.getTaskId());

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

            if (!methods.hasID(damager.getUniqueId())) new BountyRunnable(damager).runTaskTimer(Main.getInstance(), 0, 1);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (methods.hasID(e.getPlayer().getUniqueId())) methods.stop(e.getPlayer().getUniqueId());
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
                } else if (streak < 50) {
                    return 200;
                } else {
                    return 250;
                }
            }
            return 0;
        }

        @Override
        public void run() {
            PlayerData pData = Main.getInstance().getPlayerData(uuid);

            if (!methods.hasID(uuid)) methods.setID(uuid, getTaskId());

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
                        Bukkit.broadcastMessage("§6§lBOUNTY! §7bump §6§l " + calculatedBounty + "g §7on " + zl.getColorBracketAndLevel(uuid.toString())
                                + " §7" + player.getName() + " for high streak");
                    } else if (pData.getBounty() < 5000) {
                        Bukkit.broadcastMessage("§6§lBOUNTY! §7bump §6§l " + (5000 - pData.getBounty()) + "g §7on "
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



















