package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.upgrades.permanent.perks.PerkListenersAndUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ScoreboardListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final RunMethods runTracker = Main.getInstance().generateRunMethods();
    private final RunMethods runTracker2 = Main.getInstance().generateRunMethods();
    private final PerkListenersAndUtils perkUtils = Main.getInstance().getPerkUtils();

    @EventHandler
    public void addOnJoin(PlayerJoinEvent e) {
        new UpdateAndAnimation(e.getPlayer()).runTaskTimer(Main.getInstance(),0, 1);
        new UpdateTab(e.getPlayer()).runTaskTimer(Main.getInstance(),0, 20);
    }

    @EventHandler
    public void removeOnLeave(PlayerQuitEvent e) {
        if (runTracker.hasID(e.getPlayer().getUniqueId())) runTracker.stop(e.getPlayer().getUniqueId());
        if (runTracker2.hasID(e.getPlayer().getUniqueId())) runTracker2.stop(e.getPlayer().getUniqueId());
    }


    private class UpdateAndAnimation extends BukkitRunnable {

        private final Player p;
        int ticks = 0;
        private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yy");

        public UpdateAndAnimation(Player player) {
            this.p = player;
        }

        private void setDisplay(String string) {
            if (p.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(string);
        }

        private List<String> getBoardScores(Player player) {
            List<String> boardScores = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            PlayerData pData = Main.getInstance().getPlayerData(player);

            boardScores.add("§7" + dateTimeFormat.format(now) + " §8mega13Z");
            boardScores.add("§1");

            if (pData.getPrestige() >= 1) boardScores.add("§fPrestige: §e" + zl.toRoman(pData.getPrestige()));

            boardScores.add("§fLevel: " + zl.getColorBracketAndLevel(p.getUniqueId().toString()));

            if (pData.getLevel() < 120) boardScores.add("§fNeeded XP: §b" + pData.getExp()); else boardScores.add("§fXP: §bMAXED!");

            boardScores.add("§2");

            if (pData.getGold() < 10000) {
                boardScores.add("§fGold: §6" + zl.getFancyGoldString(pData.getGold()) + "g");
            } else {
                boardScores.add("§fGold: §6" + zl.getFancyGoldString((int) Math.floor(pData.getGold())) + "g");
            }

            boardScores.add("§3");

            if (!pData.hideTimer()) {
                boardScores.add("§fStatus: " + zl.getColorStatus(p.getUniqueId().toString()) + " §7(" + pData.getCombatTimer() + ")");
            } else {
                boardScores.add("§fStatus: " + zl.getColorStatus(p.getUniqueId().toString()));
            }

            if (pData.getBounty() != 0) boardScores.add("§fBounty: §6" + zl.getFancyGoldString(pData.getBounty()) + "g");

            if (pData.getStreak() > 0) {
                if (pData.getStreak() % 1 == 0) {
                    boardScores.add("§fStreak: §a" + (int) pData.getStreak());
                } else {
                    boardScores.add("§fStreak: §a" + pData.getStreak());
                }
            }

            if (perkUtils.getStrengthChaining(p)[0] != null) {
                boardScores.add("§fStrength: §c" + zl.toRoman(perkUtils.getStrengthChaining(p)[0]) + " §7(" + perkUtils.getStrengthChaining(p)[1] + ")");
            } else if (perkUtils.getGladiatorDamageReduction(p) != (double) 0 && !Main.getInstance().getSpawnListener().spawnCheck(p.getLocation())) {
                boardScores.add("§fGladiator: §9-" + (int) (perkUtils.getGladiatorDamageReduction(p) * 100) + "%");
            }

            boardScores.add("§4");
            boardScores.add("§epet a cat");

            for (String string : boardScores) {
                if (string.length() > 40) {
                    StringBuilder builder = new StringBuilder(string);

                    builder.replace(builder.indexOf(":") + 2, builder.length(), "§cERROR");
                    boardScores.set(boardScores.indexOf(string), builder.toString());
                }
            }

            return boardScores;
        }

        private void createBoard(Player p, String displayName) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("heckyou", "dummy", displayName);
            List<String> scoreList = getBoardScores(p);

            for (int i = 0; i < scoreList.size(); i++) objective.getScore(scoreList.get(i)).setScore(scoreList.size() - i);

            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            p.setScoreboard(scoreboard);
        }

        @Override
        public void run() {

            if (!runTracker.hasID(p.getUniqueId())) {
                runTracker.setID(p.getUniqueId(), super.getTaskId());
            }

            if (ticks == 160) {
                ticks = 0;
            }

            switch (ticks) {
                case 0:
                case 20:
                case 40:
                case 60:
                case 80:
                    createBoard(p, "§e§l  THE HYPIXEL PIT  ");
                    break;
                case 100:
                    createBoard(p, "§6§l  T§e§lHE HYPIXEL PIT  ");
                    break;
                case 102:
                    setDisplay("§f§l  T§6§lH§e§lE HYPIXEL PIT  ");
                    break;
                case 104:
                    setDisplay("§f§l  TH§6§lE§e§l HYPIXEL PIT  ");
                    break;
                case 106:
                    setDisplay("§f§l  THE§6§l H§e§lYPIXEL PIT  ");
                    break;
                case 108:
                    setDisplay("§f§l  THE H§6§lY§e§lPIXEL PIT  ");
                    break;
                case 110:
                    setDisplay("§f§l  THE HY§6§lP§e§lIXEL PIT  ");
                    break;
                case 112:
                    setDisplay("§f§l  THE HYP§6§lI§e§lXEL PIT  ");
                    break;
                case 114:
                    setDisplay("§f§l  THE HYPI§6§lX§e§lEL PIT  ");
                    break;
                case 116:
                    setDisplay("§f§l  THE HYPIX§6§lE§e§lL PIT  ");
                    break;
                case 118:
                    setDisplay("§f§l  THE HYPIXE§6§lL§e§l PIT  ");
                    break;
                case 120:
                    createBoard(p, "§f§l  THE HYPIXEL§6§l P§e§lIT  ");
                    break;
                case 122:
                    setDisplay("§f§l  THE HYPIXEL P§6§lI§e§lT  ");
                    break;
                case 124:
                    setDisplay("§f§l  THE HYPIXEL PI§6§lT  ");
                    break;
                case 126:
                    setDisplay("§f§l  THE HYPIXEL PIT  ");
                    break;
                case 140:
                    createBoard(p,"§f§l  THE HYPIXEL PIT  ");
                    break;
                case 146:
                    setDisplay("§e§l  THE HYPIXEL PIT  ");
                    break;
                case 151:
                    setDisplay("§f§l  THE HYPIXEL PIT  ");
                    break;
                case 156:
                    setDisplay("§e§l  THE HYPIXEL PIT  ");
                    break;
            }

            ticks++;
        }
    }


    private class UpdateTab extends BukkitRunnable {

        private final Player p;
        private final PlayerData pData;

        public UpdateTab(Player player) {
            this.p = player;
            this.pData = Main.getInstance().getPlayerData(player);
        }

        @Override
        public void run() {

            if (!runTracker2.hasID(p.getUniqueId())) {
                runTracker2.setID(p.getUniqueId(), super.getTaskId());
            }

            if (pData.getBounty() == 0) {
                p.setPlayerListName(zl.getColorBracketAndLevel(p.getUniqueId().toString()) + " §7" + p.getName());
            } else {
                p.setPlayerListName(zl.getColorBracketAndLevel(p.getUniqueId().toString()) + " §7" + p.getName() + " §6§l" + pData.getBounty() + "g");
            }
        }
    }
}


















