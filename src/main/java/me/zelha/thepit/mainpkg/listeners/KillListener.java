package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.upgrades.permanent.perks.SpammerPerk;
import me.zelha.thepit.utils.RunTracker;
import me.zelha.thepit.utils.ZelLogic;
import me.zelha.thepit.zelenums.Passives;
import me.zelha.thepit.zelenums.Perks;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
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
    private final AssistListener assistUtils = Main.getInstance().getAssistUtils();
    private final RunTracker runTracker = Main.getInstance().generateRunTracker();
    private final RunTracker runTracker2 = Main.getInstance().generateRunTracker();
    private final RunTracker runTracker3 = Main.getInstance().generateRunTracker();

    public int calculateEXP(Player dead, Player killer) {
        double exp = 5;
        double streakModifier = 0;
        int maxEXP = 250;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData killerData = Main.getInstance().getPlayerData(killer);

        if (killerData.getStreak() <= (killerData.getPassiveTier(Passives.EL_GATO) - 1)) exp += 5;

        if (killerData.getStreak() == 4) {
            streakModifier = 3;
        } else if (killerData.getStreak() >= 5 && killerData.getStreak() < 20) {
            streakModifier = 5;
        } else if (killerData.getStreak() < 200 && killerData.getStreak() >= 20) {
            streakModifier = Math.floor(killerData.getStreak() / 10.0D) * 3;
        } else if (killerData.getStreak() >= 200) {
            streakModifier = 60;
        }
        //note for later: streak xp might not go up to 200
        //skewed result from dying to a high pres

        if (killerData.hasPerkEquipped(STREAKER)) streakModifier *= 3;

        exp += streakModifier;

        if (deadData.getStreak() > 5) exp += (int) Math.min(Math.round(deadData.getStreak()), 25);
        if (killerData.getStreak() <= 3 && (killerData.getLevel() <= 30 || killerData.getPrestige() == 0)) exp += 4;
        if (deadData.getLevel() > killerData.getLevel()) exp += (int) Math.round((deadData.getLevel() - killerData.getLevel()) / 4.5);

        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) exp *= 0.91;
        if (killerData.getPassiveTier(Passives.XP_BOOST) > 0) exp *= 1 + (killerData.getPassiveTier(Passives.XP_BOOST) / 10.0);

        return (int) Math.min(Math.ceil(exp), maxEXP);
    }

    public double calculateGold(Player dead, Player killer) {
        double gold = 10;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData killerData = Main.getInstance().getPlayerData(killer);
        PlayerInventory killerInv = killer.getInventory();

        if (((SpammerPerk) Perks.SPAMMER.getMethods()).hasBeenShotBySpammer(killer, dead)) gold *= 3;
        if (killerData.hasPerkEquipped(BOUNTY_HUNTER) && zl.itemCheck(killerInv.getLeggings()) && killerInv.getLeggings().getType() == GOLDEN_LEGGINGS) gold += 4;

        if (killerData.getStreak() <= killerData.getPassiveTier(Passives.EL_GATO)) gold += 5;
        if (deadData.getStreak() > 5) gold += Math.min((int) Math.round(deadData.getStreak()), 30);
        if (killerData.getStreak() <= 3 && (killerData.getLevel() <= 30 || killerData.getPrestige() == 0)) gold += 4;
        if (dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() > killer.getAttribute(Attribute.GENERIC_ARMOR).getValue()) {
            gold += Math.round((dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() - killer.getAttribute(Attribute.GENERIC_ARMOR).getValue()) / 5);
        }

        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) gold *= 0.91;
        if (killerData.getPassiveTier(Passives.GOLD_BOOST) > 0) gold *= 1 + (killerData.getPassiveTier(Passives.GOLD_BOOST) / 10.0);

        return Math.min(gold, 2500) + deadData.getBounty();
    }

    @EventHandler(priority = EventPriority.MONITOR)
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

        if (damaged.equals(damager)) return;

        multiKillTimer(damager);

        Bukkit.broadcastMessage(e.getFinalDamage() + "");//testing line

        if (damaged.getHealth() - e.getFinalDamage() <= 0) pitKill(damaged, damager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOtherDeath(EntityDamageEvent e) {
        Player p;

        if (zl.playerCheck(e.getEntity())) p = (Player) e.getEntity(); else return;
        if (e.getCause() == DamageCause.FALL) return;
        if (e.getCause() == DamageCause.PROJECTILE) return;
        if (e.getCause() == DamageCause.ENTITY_ATTACK) return;
        if (p.getHealth() - e.getFinalDamage() > 0) return;
        if (assistUtils.getLastDamager(p) == null) return;

        pitKill(p, assistUtils.getLastDamager(p));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        new BountyRunnable(e.getPlayer()).runTaskTimer(Main.getInstance(), 0, 1);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (runTracker.hasID(e.getPlayer().getUniqueId())) runTracker.stop(e.getPlayer().getUniqueId());
        if (assistUtils.getLastDamager(p) == null) return;
        if (Main.getInstance().getPlayerData(p).getCombatLogged()) pitKill(p, assistUtils.getLastDamager(p));
    }

    private void pitKill(Player damaged, Player damager) {
        PlayerData damagerData = Main.getInstance().getPlayerData(damager);
        PlayerData damagedData = Main.getInstance().getPlayerData(damaged);
        double calculatedGold = calculateGold(damaged, damager);

        damagerData.setStreak(damagerData.getStreak() + 1);
        damagerData.setExp(damagerData.getExp() - calculateEXP(damaged, damager));
        damagerData.setGold(damagerData.getGold() + calculatedGold);
        damagerData.setMultiKill(damagerData.getMultiKill() + 1);
        multiKillTimer(damager);

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

        String killMessage;

        switch (damagerData.getMultiKill()) {
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
                killMessage = "§a§lMULTI KILL! §7(" + damagerData.getMultiKill() + ")";
                break;
        }

        damager.spigot().sendMessage(new ComponentBuilder(killMessage + " §7on " + zl.getColorBracketAndLevel(damaged.getUniqueId().toString())
                + " §7" + damaged.getName() + " §b+" + calculateEXP(damaged, damager) + "§bXP §6+" + zl.getFancyGoldString(calculatedGold) + "§6g")
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§eClick to view kill recap!")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/killrecap " + damaged.getUniqueId()))
                .create());

        new BukkitRunnable() {

            int i = 0;

            @Override
            public void run() {
                damager.playSound(damager.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3F, 1.75F + (0.05F * i));
                i++;

                if (i == Math.min(damagerData.getMultiKill(), 5)) {
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 2);
    }

    private void multiKillTimer(Player player) {
        if (runTracker2.hasID(player.getUniqueId())) runTracker2.stop(player.getUniqueId());

        BukkitTask multiKillTimer = new BukkitRunnable() {
            @Override
            public void run() {
                Main.getInstance().getPlayerData(player).setMultiKill(0);
            }
        }.runTaskLater(Main.getInstance(), 60);

        runTracker2.setID(player.getUniqueId(), multiKillTimer.getTaskId());
    }



    private class BountyRunnable extends BukkitRunnable {

        private final Player player;
        private final UUID uuid;
        private int ticksBetweenKills;
        private int secondsBetweenKills;
        private double streak;
        private boolean hasAnimation = false;

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
                                            });

                                    ((CraftPlayer) player).getHandle().b.sendPacket(new PacketPlayOutEntityDestroy(particle.getEntityId()));

                                    for (EquipmentSlot slots : EquipmentSlot.values()) particle.addEquipmentLock(slots, ArmorStand.LockType.ADDING_OR_CHANGING);
                                }

                                location.add(0, 0.25, 0);
                                particle.teleport(location);
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
    }
}



















