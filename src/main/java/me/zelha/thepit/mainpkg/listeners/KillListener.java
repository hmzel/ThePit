package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.RunTracker;
import me.zelha.thepit.utils.ZelLogic;
import me.zelha.thepit.zelenums.Megastreaks;
import me.zelha.thepit.zelenums.Ministreaks;
import me.zelha.thepit.zelenums.Passives;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
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

import java.util.Random;

public class KillListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final RunTracker runTracker = new RunTracker();
    private final RunTracker runTracker2 = new RunTracker();
    private final RunTracker runTracker3 = new RunTracker();

    public int calculateEXP(Player dead, Player killer, PitKillEvent event) {
        double exp = 0;
//        double streakModifier = 0;
        int maxEXP = 250;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData killerData = Main.getInstance().getPlayerData(killer);

        for (Pair<String, Double> pair : event.getExpAdditions()) {
            exp += pair.getValue();
        }

//        if (killerData.getStreak() <= (killerData.getPassiveTier(Passives.EL_GATO) - 1)) exp += 5;

//        if (killerData.getStreak() == 4) {
//            streakModifier = 3;
//        } else if (killerData.getStreak() >= 5 && killerData.getStreak() < 20) {
//            streakModifier = 5;
//        } else if (killerData.getStreak() < 100 && killerData.getStreak() >= 20) {
//            streakModifier = Math.floor(killerData.getStreak() / 10.0D) * 3;
//        } else if (killerData.getStreak() >= 100) {
//            streakModifier = 30;
//        }
//
//        if (killerData.hasPerkEquipped(STREAKER)) streakModifier *= 3;

//        exp += streakModifier;

//        if (deadData.getStreak() > 5) exp += (int) Math.min(Math.round(deadData.getStreak()), 25);
//        if (killerData.getStreak() <= 3 && (killerData.getLevel() <= 30 || killerData.getPrestige() == 0)) exp += 4;
//        if (deadData.getLevel() > killerData.getLevel()) exp += (int) Math.round((deadData.getLevel() - killerData.getLevel()) / 4.5);

//        exp += killerData.getXpStack();

        for (Pair<String, Double> pair : event.getExpModifiers()) exp *= pair.getValue();

//        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) exp *= 0.90;
        if (killerData.getPassiveTier(Passives.XP_BOOST) > 0) exp *= 1 + (killerData.getPassiveTier(Passives.XP_BOOST) / 10.0);

        if (killerData.isMegaActive() && killerData.getMegastreak().getMethods() != null) {
            exp *= killerData.getMegastreak().getMethods().getEXPModifier(killer);
        }

        for (Ministreaks ministreak : killerData.getEquippedMinistreaks()) {
            if (ministreak.getMethods() == null) continue;

            exp *= ministreak.getMethods().getEXPModifier(killer);
        }

        if (killerData.isMegaActive() && killerData.getMegastreak() == Megastreaks.TO_THE_MOON) {
            maxEXP += 100;
        }

        return (int) Math.min(Math.ceil(exp), maxEXP);
    }

    public double calculateGold(Player dead, Player killer, PitKillEvent event) {
        double gold = 0;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData killerData = Main.getInstance().getPlayerData(killer);
        boolean baseGoldModifiersApplied = false;

        for (Pair<String, Double> pair : event.getGoldAdditions()) {
            gold += pair.getValue();

            if (!baseGoldModifiersApplied) {
                for (Pair<String, Double> pair2 : event.getBaseGoldModifiers()) gold *= pair2.getValue();

                baseGoldModifiersApplied = true;
            }
        }

//        if (((SpammerPerk) Perks.SPAMMER.getMethods()).hasBeenShotBySpammer(killer, dead)) gold *= 3;
//        if (killerData.hasPerkEquipped(BOUNTY_HUNTER) && zl.itemCheck(killerInv.getLeggings()) && killerInv.getLeggings().getType() == GOLDEN_LEGGINGS) gold += 4;
//        if (killerData.getStreak() < killerData.getPassiveTier(Passives.EL_GATO)) gold += 5;
//        if (deadData.getStreak() > 5) gold += Math.min((int) Math.round(deadData.getStreak()), 30);
//        if (killerData.getStreak() <= 3 && (killerData.getLevel() <= 30 || killerData.getPrestige() == 0)) gold += 4;
//        if (dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() > killer.getAttribute(Attribute.GENERIC_ARMOR).getValue()) {
//            gold += Math.round((dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() - killer.getAttribute(Attribute.GENERIC_ARMOR).getValue()) / 5);
//        }
//        gold += killerData.getGoldStack();

        for (Pair<String, Double> pair : event.getGoldModifiers()) gold *= pair.getValue();

//        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) gold *= 0.90;
        if (killerData.getPassiveTier(Passives.GOLD_BOOST) > 0) gold *= 1 + (killerData.getPassiveTier(Passives.GOLD_BOOST) / 10.0);

        if (killerData.isMegaActive() && killerData.getMegastreak().getMethods() != null) {
            gold *= killerData.getMegastreak().getMethods().getGoldModifier(killer);
        }

        for (Ministreaks ministreak : killerData.getEquippedMinistreaks()) {
            if (ministreak.getMethods() == null) continue;

            gold *= ministreak.getMethods().getGoldModifier(killer);
        }

        return Math.min(gold, 2500) + deadData.getBounty();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PitKillEvent e) {
        Player dead = e.getDead();
        Player killer = e.getKiller();
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData killerData = Main.getInstance().getPlayerData(killer);
        double calculatedGold = calculateGold(dead, killer, e);

        killerData.setStreak(killerData.getStreak() + 1);
        killerData.setExp(killerData.getExp() - calculateEXP(dead, killer, e));
        killerData.setGold(killerData.getGold() + calculatedGold);
        killerData.setMultiKill(killerData.getMultiKill() + 1);
        multiKillTimer(killer);

        if ((Math.floor(killerData.getStreak()) % 10 == 0) || (killerData.getStreak() < 6 && killerData.getStreak() >= 5)) {
            Bukkit.broadcastMessage("§c§lSTREAK! §7of §c" + (int) Math.floor(killerData.getStreak()) + " §7kills by "
                    + zl.getColorBracketAndLevel(killer) + " §7" + killer.getName());
        }

        if (deadData.getBounty() != 0) {
            Bukkit.broadcastMessage("§6§lBOUNTY CLAIMED! " + zl.getColorBracketAndLevel(killer)
                    + "§7 " + killer.getName() + " killed " + zl.getColorBracketAndLevel(dead)
                    + "§7 " + dead.getName() + " for §6§l" + zl.getFancyNumberString(deadData.getBounty()) + "g");
            deadData.setBounty(0);
        }

        String killMessage;

        switch (killerData.getMultiKill()) {
            case 1:
                killMessage = "§a§lKILL!";
                break;
            case 2:
                killMessage = "§a§lDOUBLE KILL!";
                break;
            case 3:
                killMessage = "§a§lTRIPLE KILL!";
                break;
            case 4:
                killMessage = "§a§lQUADRA KILL!";
                break;
            case 5:
                killMessage = "§a§lPENTA KILL!";
                break;
            default:
                killMessage = "§a§lMULTI KILL! §7(" + killerData.getMultiKill() + ")";
                break;
        }

        killer.spigot().sendMessage(
                new ComponentBuilder(killMessage + " §7on " + zl.getColorBracketAndLevel(dead)
                + " §7" + dead.getName() + " §b+" + calculateEXP(dead, killer, e) + "§bXP §6+" + zl.getFancyGoldString(calculatedGold) + "§6g")
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§eClick to view kill recap!")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/killrecap " + dead.getUniqueId()))
                .create()
        );

        new BukkitRunnable() {

            int i = 0;

            @Override
            public void run() {
                killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3F, 1.75F + (0.05F * i));
                i++;

                if (i == Math.min(killerData.getMultiKill(), 5)) {
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 2);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        new BountyRunnable(e.getPlayer()).runTaskTimer(Main.getInstance(), 0, 1);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (runTracker.hasID(e.getPlayer().getUniqueId())) runTracker.stop(e.getPlayer().getUniqueId());
    }

    private void multiKillTimer(Player player) {
        if (runTracker2.hasID(player.getUniqueId())) runTracker2.stop(player.getUniqueId());

        runTracker2.setID(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                Main.getInstance().getPlayerData(player).setMultiKill(0);
            }
        }.runTaskLater(Main.getInstance(), 60).getTaskId());
    }


    private class BountyRunnable extends BukkitRunnable {

        private final Player player;
        private int ticksBetweenKills;
        private int secondsBetweenKills;
        private double streak;
        private boolean hasAnimation = false;

        private BountyRunnable(Player player) {
            this.player = player;
            this.ticksBetweenKills = 0;
            this.secondsBetweenKills = 0;
            this.streak = Main.getInstance().getPlayerData(player).getStreak();
        }

        @Override
        public void run() {
            PlayerData pData = Main.getInstance().getPlayerData(player);

            if (!runTracker.hasID(player.getUniqueId())) runTracker.setID(player.getUniqueId(), getTaskId());

            if (streak != pData.getStreak()) {
                streak = pData.getStreak();
                int calculatedBounty = calculateBounty();

                if (calculatedBounty != 0) {
                    if (pData.getBounty() == 0) {
                        pData.setBounty(calculatedBounty);
                        Bukkit.broadcastMessage("§6§lBOUNTY! §7of §6§l " + calculatedBounty + "g §7on " + zl.getColorBracketAndLevel(player)
                                + " §7" + player.getName() + " for high streak");
                    } else if (pData.getBounty() + calculatedBounty <= pData.getMaxBounty()) {
                        pData.setBounty(pData.getBounty() + calculatedBounty);
                        Bukkit.broadcastMessage("§6§lBOUNTY! §7bump §6§l" + calculatedBounty + "g §7on " + zl.getColorBracketAndLevel(player)
                                + " §7" + player.getName() + " for high streak");
                    } else if (pData.getBounty() < pData.getMaxBounty()) {
                        Bukkit.broadcastMessage("§6§lBOUNTY! §7bump §6§l" + (pData.getMaxBounty() - pData.getBounty()) + "g §7on "
                                + zl.getColorBracketAndLevel(player) + " §7" + player.getName() + " for high streak");
                        pData.setBounty(pData.getMaxBounty());
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

            if (pData.getBounty() != 0 && !zl.spawnCheck(player.getLocation()) && !hasAnimation) {
                new BukkitRunnable() {
                    private final Random rng = new Random();

                    @Override
                    public void run() {
                        if (!zl.playerCheck(player)) {
                            cancel();
                            return;
                        }

                        if (!runTracker3.hasID(player.getUniqueId())) runTracker3.setID(player.getUniqueId(), getTaskId());

                        double x;
                        double z;

                        do {
                            x = rng.nextInt(85) / 100D;
                            z = rng.nextInt(85) / 100D;
                        } while (player.getLocation().distance(player.getLocation().add(x, -0.5, z)) < 0.6);

                        if (rng.nextBoolean()) x = -x;
                        if (rng.nextBoolean()) z = -z;

                        double finalX = x;
                        double finalZ = z;

                        new BukkitRunnable() {
                            private int timer = 0;
                            private ArmorStand particle = null;
                            private final Location location = player.getLocation().add(finalX, -0.5, finalZ);

                            @Override
                            public void run() {
                                if (!zl.playerCheck(player) || timer == 10) {
                                    if (particle != null) particle.remove();

                                    cancel();
                                    return;
                                }

                                if (particle == null) {
                                    particle = player.getWorld().spawn(location, ArmorStand.class,
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

                                                for (EquipmentSlot slots : EquipmentSlot.values()) {
                                                    armorstand.addEquipmentLock(slots, ArmorStand.LockType.ADDING_OR_CHANGING);
                                                }
                                            });

                                    ((CraftPlayer) player).getHandle().b.sendPacket(new PacketPlayOutEntityDestroy(particle.getEntityId()));
                                }

                                particle.teleport(location.add(0, 0.25, 0));
                                timer++;
                            }
                        }.runTaskTimer(Main.getInstance(), 0, 1);
                    }
                }.runTaskTimer(Main.getInstance(), 0, 4);

                hasAnimation = true;
            } else if (pData.getBounty() == 0 || zl.spawnCheck(player.getLocation())) {
                if (runTracker3.hasID(player.getUniqueId())) runTracker3.stop(player.getUniqueId());

                hasAnimation = false;
            }
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
    }
}



















