package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Passives;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.UUID;

public class KillListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final RunMethods methods = Main.getInstance().generateRunMethods();
    private final RunMethods methods2 = Main.getInstance().generateRunMethods();

    private String calculateKillMessage(Player damager) {
        PlayerData pData = Main.getInstance().getPlayerData(damager);

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

    private int calculateEXP(Player damaged, Player damager) {
        int exp = 5;
        int baseModifier = 0;
        double percentageModifier = 1;
        int streakModifier = 0;

        PlayerData damagedData = Main.getInstance().getPlayerData(damaged);
        PlayerData damagerData = Main.getInstance().getPlayerData(damager);

        if (damagedData.getPrestige() == 0) {
            percentageModifier = percentageModifier - 0.09;
        }

        if (damagerData.getStreak() <= 3 && damagerData.getLevel() <= 30) {
            baseModifier = baseModifier + 4;
        }

        if (damagerData.getStreak() <= damagerData.getPassiveTier(Passives.EL_GATO)) {
            baseModifier = baseModifier + 5;
        }

        if (damagerData.getPassiveTier(Passives.XP_BOOST) > 0) {
            percentageModifier = percentageModifier + (damagerData.getPassiveTier(Passives.XP_BOOST) / 10.0);
        }

        if (damagedData.getStreak() > 5) {

            if (damagedData.getStreak() <= 25) {
                baseModifier = baseModifier + (int) Math.round(damagedData.getStreak());
            } else {
                baseModifier = baseModifier + 25;
            }
        }

        if (damagedData.getLevel() > damagerData.getLevel()) {
            baseModifier = baseModifier + (int) Math.round((damagedData.getLevel() - damagerData.getLevel()) / 4.5);
        }

        if (damagerData.getStreak() < 5) {
            streakModifier = streakModifier + 3;
        } else if (damagerData.getStreak() < 20) {
            streakModifier = streakModifier + 5;
        } else if (damagerData.getStreak() < 30) {
            streakModifier = streakModifier + 6;
        } else if (damagerData.getStreak() < 40) {
            streakModifier = streakModifier + 9;
        } else if (damagerData.getStreak() < 50) {
            streakModifier = streakModifier + 12;
        } else if (damagerData.getStreak() < 60) {
            streakModifier = streakModifier + 15;
        } else if (damagerData.getStreak() < 70) {
            streakModifier = streakModifier + 18;
        } else if (damagerData.getStreak() < 80) {
            streakModifier = streakModifier + 21;
        } else if (damagerData.getStreak() < 90) {
            streakModifier = streakModifier + 24;
        } else if (damagerData.getStreak() < 100) {
            streakModifier = streakModifier + 27;
        } else if (damagerData.getStreak() < 110) {
            streakModifier = streakModifier + 30;
        } else if (damagerData.getStreak() < 120) {
            streakModifier = streakModifier + 33;
        } else if (damagerData.getStreak() < 130) {
            streakModifier = streakModifier + 36;
        } else if (damagerData.getStreak() < 140) {
            streakModifier = streakModifier + 39;
        } else if (damagerData.getStreak() < 150) {
            streakModifier = streakModifier + 42;
        } else if (damagerData.getStreak() < 160) {
            streakModifier = streakModifier + 45;
        } else if (damagerData.getStreak() < 170) {
            streakModifier = streakModifier + 48;
        } else if (damagerData.getStreak() < 180) {
            streakModifier = streakModifier + 51;
        } else if (damagerData.getStreak() < 190) {
            streakModifier = streakModifier + 54;
        } else if (damagerData.getStreak() < 200) {
            streakModifier = streakModifier + 57;
        } else {
            streakModifier = streakModifier + 60;
        }

        baseModifier = baseModifier + streakModifier;

        return Math.toIntExact(Math.round((exp + baseModifier) * percentageModifier));
    }

    private double calculateGold(Player damaged, Player damager) {
        int gold = 10;
        int baseModifier = 0;
        double percentageModifier = 1;

        PlayerData damagedData = Main.getInstance().getPlayerData(damaged);
        PlayerData damagerData = Main.getInstance().getPlayerData(damager);

        if (zl.itemCheck(damaged.getInventory().getHelmet())
           && damaged.getInventory().getHelmet().getType() == Material.DIAMOND_HELMET) {
            baseModifier++;
        }
        if (zl.itemCheck(damaged.getInventory().getChestplate())
           && damaged.getInventory().getChestplate().getType() == Material.DIAMOND_CHESTPLATE) {
            baseModifier++;
        }
        if (zl.itemCheck(damaged.getInventory().getLeggings())
           && damaged.getInventory().getLeggings().getType() == Material.DIAMOND_LEGGINGS) {
            baseModifier++;
        }
        if (zl.itemCheck(damaged.getInventory().getBoots())
           && damaged.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS) {
            baseModifier++;
        }

        if (damagedData.getPrestige() == 0) {
            percentageModifier = percentageModifier - 0.09;
        }

        if (damagerData.getStreak() <= 3 && damagerData.getLevel() <= 30) {
            baseModifier = baseModifier + 4;
        }

        if (damagerData.getStreak() <= damagerData.getPassiveTier(Passives.EL_GATO)) {
            baseModifier = baseModifier + 5;
        }

        if (damagerData.getPassiveTier(Passives.GOLD_BOOST) > 0) {
            percentageModifier = percentageModifier + (damagerData.getPassiveTier(Passives.GOLD_BOOST) / 10.0);
        }

        if (damagedData.getStreak() > 5) {

            if (damagedData.getStreak() <= 25) {
                baseModifier = baseModifier + (int) Math.round(damagedData.getStreak());
            } else {
                baseModifier = baseModifier + 25;
            }
        }

        if (damagedData.getLevel() > damagerData.getLevel()) {
            baseModifier = baseModifier + (int) Math.round((damagedData.getLevel() - damagerData.getLevel()) / 4.5);
        }

        return ((gold + baseModifier) * percentageModifier) + damagedData.getBounty();
    }

    @EventHandler
    public void onPlayerDeath(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();

        if (zl.playerCheck(damagedEntity) && zl.playerCheck(damagerEntity)) {
            Player damaged = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();
            double finalDMG = e.getFinalDamage();
            double currentHP = damaged.getHealth();

            if (methods2.hasID(damager.getUniqueId())) {
                methods2.stop(damager.getUniqueId());
            }

            new MultiKillRunnable(damager).runTaskTimer(Main.getInstance(),0, 1);

            if (e.getCause() != DamageCause.FALL && (currentHP - finalDMG) <= 0) {
                String uuid = damaged.getUniqueId().toString();
                PlayerData damagedData = Main.getInstance().getPlayerData(damaged);
                PlayerData damagerData = Main.getInstance().getPlayerData(damager);
                double calculatedGold = calculateGold(damaged, damager);

                damagerData.setExp(damagerData.getExp() - calculateEXP(damaged, damager));
                damagerData.setGold(damagerData.getGold() + calculatedGold);
                damagerData.setStreak(damagerData.getStreak() + 1);
                damagerData.setMultiKill(damagerData.getMultiKill() + 1);

                if ((Math.floor(damagerData.getStreak()) % 10 == 0)
                || (damagerData.getStreak() < 6 && damagerData.getStreak() >= 5)) {
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

                if (!methods.hasID(damager.getUniqueId())) {
                    new BountyRunnable(damager).runTaskTimer(Main.getInstance(),0, 1);
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        if (methods.hasID(uuid)) {
            methods.stop(uuid);
        }
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
            int rng = (new Random().nextInt((int) Math.round(streak)) + 1) - (int) Math.round(secondsBetweenKills * 0.1);

            if (rng <= 0) {
                return false;
            }

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

            if (!methods.hasID(uuid)) {
                methods.setID(uuid, super.getTaskId());
            }

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


    private class MultiKillRunnable extends BukkitRunnable {

        private final UUID uuid;
        private int ticksBetweenKills;

        private MultiKillRunnable(Player player) {
            this.uuid = player.getUniqueId();
            this.ticksBetweenKills = 0;
        }

        @Override
        public void run() {
            PlayerData pData = Main.getInstance().getPlayerData(uuid);

            if (!methods2.hasID(uuid)) {
                methods2.setID(uuid, super.getTaskId());
            }

            ticksBetweenKills++;

            if (ticksBetweenKills == 60) {
                pData.setMultiKill(0);
                cancel();
            }
        }
    }
}



















