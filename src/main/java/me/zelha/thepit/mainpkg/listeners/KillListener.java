package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.RunTracker;
import me.zelha.thepit.utils.ZelLogic;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class KillListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final RunTracker runTracker = new RunTracker();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(PitKillEvent e) {
        Player dead = e.getDead();
        Player killer = e.getKiller();
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData killerData = Main.getInstance().getPlayerData(killer);
        int calculatedExp = e.calculateEXP();
        double calculatedGold = e.calculateGold();

        killerData.setStreak(killerData.getStreak() + 1);
        killerData.setExp(killerData.getExp() - calculatedExp);
        killerData.setGold(killerData.getGold() + calculatedGold);
        killerData.setMultiKill(killerData.getMultiKill() + 1);

        if (runTracker.hasID(killer.getUniqueId())) runTracker.stop(killer.getUniqueId());

        runTracker.setID(killer.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                Main.getInstance().getPlayerData(killer).setMultiKill(0);
            }
        }.runTaskLater(Main.getInstance(), 60).getTaskId());

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
                + " §7" + dead.getName() + " §b+" + calculatedExp + "§bXP §6+" + zl.getFancyGoldString(calculatedGold) + "§6g")
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
}



















