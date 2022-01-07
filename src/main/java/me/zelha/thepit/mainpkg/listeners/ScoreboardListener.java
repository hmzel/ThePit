package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
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
import java.time.temporal.TemporalField;
import java.util.*;

public class ScoreboardListener implements Listener {

    RunMethods methods = Main.getInstance().getRunMethods();

    @EventHandler
    public void addOnJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        new UpdateAndAnimation(p, methods).runTaskTimer(Main.getInstance(),0, 1);
    }

    @EventHandler
    public void removeOnLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (methods.hasID(p.getUniqueId())) {
            methods.stop(p.getUniqueId());
        }
    }
}


class UpdateAndAnimation extends BukkitRunnable {

    private final Player p;
    private final RunMethods methods;
    int ticks = 0;
    DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yy");

    ZelLogic zl = Main.getInstance().getZelLogic();

    public UpdateAndAnimation(Player player, RunMethods methods) {
        this.p = player; this.methods = methods;
    }

    void setDisplay(String string) {
        if (p.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
            p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(string);
        }
    }

    List<String> getBoardScores(PlayerData pData) {
        List<String> boardScores = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        boardScores.add("§7" + dateTimeFormat.format(now) + " §8mega13Z");
        boardScores.add("§1");

        if (pData.getPrestige() >= 1) {
            boardScores.add("§fPrestige: §e" + zl.toRoman(pData.getPrestige()));
        }

        boardScores.add("§fLevel: " + zl.getColorBracketAndLevel(p.getUniqueId().toString()));

        if (pData.getLevel() >= 120) {
            boardScores.add("§fXP: §bMAXED!");
        }else {
            boardScores.add("§fNeeded XP: §b" + pData.getExp());
        }

        boardScores.add("§2");
        boardScores.add("§fGold: §6" + zl.getFancyGoldString(pData.getGold()) + "g");
        boardScores.add("§3");

        if (!pData.hideTimer()) {
            boardScores.add("§fStatus: " + zl.getColorStatus(p.getUniqueId().toString()) + " §7(" + pData.getCombatTimer() + ")");
        }else {
            boardScores.add("§fStatus: " + zl.getColorStatus(p.getUniqueId().toString()));
        }

        if (pData.getBounty() != 0) {
            boardScores.add("§fBounty: §6" + zl.getFancyGoldString(pData.getBounty()) + "g");
        }

        if (pData.getStreak() > 0) {
            if (pData.getStreak() % 1 == 0) {
                boardScores.add("§fStreak: §a" + (int) pData.getStreak());
            }else {
                boardScores.add("§fStreak: §a" + pData.getStreak());
            }
        }

        boardScores.add("§4");
        boardScores.add("§eheckyou.zel");

        return boardScores;
    }

    void createBoard(Player p, String displayName) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("heckyou", "dummy", displayName);

        PlayerData pData = Main.getInstance().getStorage().getPlayerData(p.getUniqueId().toString());

        List<String> scoreList = getBoardScores(pData);

        for (int i = 0; i < scoreList.size(); i++) {
            objective.getScore(scoreList.get(i)).setScore(scoreList.size() - i);
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        p.setScoreboard(scoreboard);
    }

    @Override
    public void run() {

        if (!methods.hasID(p.getUniqueId())) {
            methods.setID(p.getUniqueId(), super.getTaskId());
        }

        if (ticks == 160) {ticks = 0;}

        switch(ticks) {
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















