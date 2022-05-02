package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.upgrades.permanent.perks.StrengthChainingPerk;
import me.zelha.thepit.utils.RunTracker;
import me.zelha.thepit.utils.ZelLogic;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static me.zelha.thepit.zelenums.Perks.GLADIATOR;
import static me.zelha.thepit.zelenums.Perks.STRENGTH_CHAINING;

public class ScoreboardListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final RunTracker runTracker = Main.getInstance().generateRunTracker();
    private final RunTracker runTracker2 = Main.getInstance().generateRunTracker();
    private final Map<UUID, Team> teamMap = new HashMap<>();
    private final Map<UUID, SidebarUpdater> sidebarMap = new HashMap<>();

    public void startAnimation() {
        new BukkitRunnable() {

            private final Scoreboard mainBoard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
            private Objective mainObjective = null;
            private int ticks = 0;
            private int anim = 0;

            @Override
            public void run() {
                if (ticks == 160) ticks = 0;
                if (Bukkit.getServer().getScoreboardManager() == null) return;

                StringBuilder builder = new StringBuilder("§e§l  THE HYPIXEL PIT  ");

                if (ticks >= 100) builder.replace(1, 2, "f");

                if ((ticks >= 145 && ticks <= 150) || (ticks >= 155 && ticks <= 160)) {
                    builder.replace(1, 2, "e");
                }

                if (ticks >= 100 && ticks <= 124) {
                    if (builder.charAt(5 + anim) == ' ') anim++;

                    if (6 + anim < builder.length()) {
                        builder.replace(5 + anim, 5 + anim, "§6§l").replace(10 + anim, 10 + anim, "§e§l");
                    } else if (5 + anim < builder.length()) {
                        builder.replace(5 + anim, 5 + anim, "§6§l");
                    }

                    if (ticks % 2 == 0) anim++;
                    if (anim == 16) anim = 0;
                }

                if (mainObjective == null) {
                    if (mainBoard.getObjective(DisplaySlot.SIDEBAR) != null) {
                        mainObjective = mainBoard.getObjective(DisplaySlot.SIDEBAR);
                    } else {
                        mainObjective = mainBoard.registerNewObjective("main", "dummy", builder.toString());
                        mainObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    }
                }

                mainObjective.setDisplayName(builder.toString());
                ticks++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    public void clearSidebar() {
        for (SidebarUpdater sidebarUpdater : sidebarMap.values()) {
            sidebarUpdater.clearSidebar();
        }
    }

    @EventHandler
    public void addOnJoin(PlayerJoinEvent e) {
        sidebarMap.put(e.getPlayer().getUniqueId(), new SidebarUpdater(e.getPlayer()));
        new TabAndNameUpdater(e.getPlayer()).runTaskTimer(Main.getInstance(),0, 20);
    }

    @EventHandler
    public void removeOnLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        sidebarMap.remove(uuid);

        if (runTracker.hasID(uuid)) runTracker.stop(uuid);
        if (runTracker2.hasID(uuid)) runTracker2.stop(uuid);

        if (teamMap.containsKey(uuid)) {
            teamMap.get(uuid).unregister();
            teamMap.remove(uuid);
        }
    }


    private class SidebarUpdater extends BukkitRunnable {

        private final Player p;
        private final StrengthChainingPerk strengthPerk = (StrengthChainingPerk) STRENGTH_CHAINING.getMethods();
        private List<String> previousScores = new ArrayList<>();
        private boolean needsToClear = false;

        public SidebarUpdater(Player player) {
            this.p = player;

            runTaskTimer(Main.getInstance(),0, 20);
        }

        public void clearSidebar() {
            needsToClear = true;

            for (String prevScore : previousScores) {
                ((CraftPlayer) p).getHandle().b.sendPacket(new PacketPlayOutScoreboardScore(ScoreboardServer.Action.b, "main", prevScore, 0));
            }
        }

        @Override
        public void run() {
            //i know this sends a lot of unnecessary packets but i genuinely couldnt find a better way to do it that didnt
            //completely break with the slightest touch

            if (!runTracker.hasID(p.getUniqueId())) runTracker.setID(p.getUniqueId(), getTaskId());

            List<String> scoreList = new ArrayList<>();
            PlayerData pData = Main.getInstance().getPlayerData(p);
            String status;

            if (pData.getDummyStatus() != null) {
                status = ChatColor.translateAlternateColorCodes('&', pData.getDummyStatus());
            } else {
                switch (pData.getStatus()) {
                    case "idling":
                        status = "§aIdling";
                        break;
                    case "fighting":
                        status = "§cFighting";
                        break;
                    case "bountied":
                        status = "§cBountied";
                        break;
                    default:
                        status = ChatColor.translateAlternateColorCodes('&', pData.getStatus());
                        break;
                }
            }

            scoreList.add("§7" + DateTimeFormatter.ofPattern("MM/dd/yy").format(LocalDateTime.now()) + " §8mega13Z");
            scoreList.add("§1");

            if (pData.getPrestige() >= 1) scoreList.add("§fPrestige: §e" + zl.toRoman(pData.getPrestige()));

            scoreList.add("§fLevel: " + zl.getColorBracketAndLevel(p));

            if (pData.getLevel() < 120) scoreList.add("§fNeeded XP: §b" + pData.getExp()); else scoreList.add("§fXP: §bMAXED!");

            scoreList.add("§2");

            if (pData.getGold() < 10000) {
                scoreList.add("§fGold: §6" + zl.getFancyGoldString(pData.getGold()) + "g");
            } else {
                scoreList.add("§fGold: §6" + zl.getFancyGoldString((int) pData.getGold()) + "g");
            }

            scoreList.add("§3");

            if (!pData.hideTimer() && pData.getStatus().equals("fighting") && pData.getDummyStatus() == null) {
                scoreList.add("§fStatus: " + status + " §7(" + pData.getCombatTimer() + ")");
            } else {
                scoreList.add("§fStatus: " + status);
            }

            if (pData.getBounty() != 0) scoreList.add("§fBounty: §6" + zl.getFancyGoldString(pData.getBounty()) + "g");

            if (pData.getStreak() > 0) {
                if (pData.getStreak() % 1 == 0) {
                    scoreList.add("§fStreak: §a" + (int) pData.getStreak());
                } else {
                    scoreList.add("§fStreak: §a" + pData.getStreak());
                }
            }

            if (strengthPerk.getLevel(p) != null) {
                scoreList.add("§fStrength: §c" + zl.toRoman(strengthPerk.getLevel(p)) + " §7(" + strengthPerk.getTimer(p) + ")");
            } else if (GLADIATOR.getMethods().getDamageModifier(null, p) != 0D && !zl.spawnCheck(p.getLocation())) {
                scoreList.add("§fGladiator: §9" + (int) (GLADIATOR.getMethods().getDamageModifier(null, p) * 100) + "%");
            }

            scoreList.add("§4");
            scoreList.add("§epet a cat");

            for (String string : scoreList) {
                if (string.length() > 40) {
                    StringBuilder builder = new StringBuilder(string);

                    builder.replace(builder.indexOf(":") + 2, builder.length(), "§cERROR");
                    scoreList.set(scoreList.indexOf(string), builder.toString());
                }
            }

            int scoreIndex = scoreList.size();
            PlayerConnection pConnect = ((CraftPlayer) p).getHandle().b;

            for (String prevScore : previousScores) {
                pConnect.sendPacket(new PacketPlayOutScoreboardScore(ScoreboardServer.Action.b, "main", prevScore, 0));
            }

            if (needsToClear) {
                cancel();
                return;
            }

            for (String score : scoreList) {
                pConnect.sendPacket(new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, "main", score, scoreIndex));
                scoreIndex--;
            }

            previousScores = scoreList;
        }
    }


    private class TabAndNameUpdater extends BukkitRunnable {

        private final Player p;
        private final PlayerData pData;
        private final char[] sortHelp = {'z', 'y', 'x', 'w', 'v', 'u', 't', 's', 'r', 'q'};
        private boolean hasHeaderAndFooter = false;

        public TabAndNameUpdater(Player player) {
            this.p = player;
            this.pData = Main.getInstance().getPlayerData(player);
        }

        @Override
        public void run() {
            if (!runTracker2.hasID(p.getUniqueId())) runTracker2.setID(p.getUniqueId(), getTaskId());

            if (!hasHeaderAndFooter) {
                p.setPlayerListHeader("§bYou are playing on §5§lHYPIXZEL PIT");
                p.setPlayerListFooter("§ebeepis");

                hasHeaderAndFooter = true;
            }

            UUID uuid = p.getUniqueId();
            Scoreboard main = Bukkit.getScoreboardManager().getMainScoreboard();
            int level = pData.getLevel();
            Team team;
            String suffix = "";
            String prefix = zl.getColorBracketAndLevel(p) + " ";
            StringBuilder sort = new StringBuilder();

            if (pData.getBounty() != 0) suffix += " §6§l" + pData.getBounty() + "g";
            if (pData.isMegaActive()) prefix = pData.getMegastreak().getDisplayName() + " ";

            while (level >= 100) {
                if (!sort.toString().contains("a")) sort.append("a");

                level -= 100;
            }

            for (int i = 0; i < (level + "").length(); i++) {
                if ((level + "").charAt(i) == '-') continue;

                sort.append(sortHelp[Integer.parseInt((level + "").charAt(i) + "")]);
            }

            sort.append(p.getName());

            if (!teamMap.containsKey(uuid)) {
                if (main.getTeam(sort.toString()) != null) {
                    team = main.getTeam(sort.toString());
                } else {
                    team = main.registerNewTeam(sort.toString());
                }

                teamMap.put(uuid, team);
            } else if (!teamMap.get(uuid).getName().equals(sort.toString())) {
                teamMap.get(uuid).unregister();

                team = main.registerNewTeam(sort.toString());

                teamMap.put(uuid, team);
            } else {
                team = teamMap.get(uuid);
            }

            team.setPrefix(prefix);
            team.setSuffix(suffix);
            team.addEntry(p.getName());
            team.setColor(ChatColor.GRAY);
        }
    }
}


















