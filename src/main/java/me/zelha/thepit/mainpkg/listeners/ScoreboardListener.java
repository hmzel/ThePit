package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.upgrades.permanent.PerkListenersAndUtils;
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

public class ScoreboardListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final RunMethods runTracker = Main.getInstance().generateRunMethods();
    private final RunMethods runTracker2 = Main.getInstance().generateRunMethods();
    private final PerkListenersAndUtils perkUtils = Main.getInstance().getPerkUtils();
    private final Map<UUID, Team> teamMap = new HashMap<>();

    public void startAnimation() {
        new BukkitRunnable() {
            int ticks = 0;
            int anim = 0;

            private void setDisplay(String name) {
                Scoreboard mainBoard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
                Objective mainObjective;

                if (mainBoard.getObjective(DisplaySlot.SIDEBAR) != null) {
                    mainObjective = mainBoard.getObjective(DisplaySlot.SIDEBAR);
                } else {
                    mainObjective = mainBoard.registerNewObjective("main", "dummy", name);
                    mainObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                }

                mainObjective.setDisplayName(name);
            }

            @Override
            public void run() {
                if (ticks == 160) ticks = 0;
                if (Bukkit.getServer().getScoreboardManager() == null) return;

                String display = "§e§l  THE HYPIXEL PIT  ";
                StringBuilder builder = new StringBuilder(display);

                if (ticks >= 100) display = builder.replace(1, 2, "f").toString();

                if ((ticks >= 145 && ticks <= 150) || (ticks >= 155 && ticks <= 160)) {
                    display = builder.replace(1, 2, "e").toString();
                }

                if (ticks >= 100 && ticks <= 124) {
                    if (builder.charAt(5 + anim) == ' ') anim++;

                    if (5 + anim < builder.length() && 6 + anim < builder.length()) {
                        builder.replace(5 + anim, 5 + anim, "§6§l").replace(10 + anim, 10 + anim, "§e§l");
                    } else if (5 + anim < builder.length()) {
                        builder.replace(5 + anim, 5 + anim, "§6§l");
                    }

                    if (ticks % 2 == 0) anim++;
                    if (anim == 16) anim = 0;

                    display = builder.toString();
                }

                setDisplay(display);
                ticks++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    @EventHandler
    public void addOnJoin(PlayerJoinEvent e) {
        new SidebarUpdater(e.getPlayer()).runTaskTimer(Main.getInstance(),0, 20);
        new TabAndNameUpdater(e.getPlayer()).runTaskTimer(Main.getInstance(),0, 20);
    }

    @EventHandler
    public void removeOnLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        if (runTracker.hasID(uuid)) runTracker.stop(uuid);
        if (runTracker2.hasID(uuid)) runTracker2.stop(uuid);

        if (teamMap.containsKey(uuid)) {
            teamMap.get(uuid).unregister();
            teamMap.remove(uuid);
        }
    }


    private class SidebarUpdater extends BukkitRunnable {

        private final Player p;
        private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yy");
        private List<String> previousScores;

        public SidebarUpdater(Player player) {
            this.p = player;
            this.previousScores = getBoardScores(player);
        }

        private List<String> getBoardScores(Player player) {
            List<String> boardScores = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            PlayerData pData = Main.getInstance().getPlayerData(player);
            String status;

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

            boardScores.add("§7" + dateTimeFormat.format(now) + " §8mega13Z");
            boardScores.add("§1");

            if (pData.getPrestige() >= 1) boardScores.add("§fPrestige: §e" + zl.toRoman(pData.getPrestige()));

            boardScores.add("§fLevel: " + zl.getColorBracketAndLevel(p.getUniqueId().toString()));

            if (pData.getLevel() < 120) boardScores.add("§fNeeded XP: §b" + pData.getExp()); else boardScores.add("§fXP: §bMAXED!");

            boardScores.add("§2");

            if (pData.getGold() < 10000) {
                boardScores.add("§fGold: §6" + zl.getFancyGoldString(pData.getGold()) + "g");
            } else {
                boardScores.add("§fGold: §6" + zl.getFancyGoldString((int) pData.getGold()) + "g");
            }

            boardScores.add("§3");

            if (!pData.hideTimer()) {
                boardScores.add("§fStatus: " + status + " §7(" + pData.getCombatTimer() + ")");
            } else {
                boardScores.add("§fStatus: " + status);
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
            } else if (perkUtils.getGladiatorDamageReduction(p) != (double) 0 && !zl.spawnCheck(p.getLocation())) {
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

        @Override
        public void run() {
            //i know this sends a lot of unnecessary packets but i genuinely couldnt find a better way to do it that didnt
            //completely break with the slightest touch

            if (!runTracker.hasID(p.getUniqueId())) runTracker.setID(p.getUniqueId(), super.getTaskId());

            List<String> scoreList = getBoardScores(p);
            int scoreIndex = scoreList.size();
            PlayerConnection pConnect = ((CraftPlayer) p).getHandle().b;

            for (String prevScore : previousScores) {
                pConnect.sendPacket(new PacketPlayOutScoreboardScore(ScoreboardServer.Action.b, "main", prevScore, 0));
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
        private final char[] randomCharList = {'', 'ঙ', 'މ', 'ॄ', 'ͩ', 'ٖ', 'ࡒ', '̡', 'ɘ', '॑', 'ݓ', '¡', 'ڕ', 'ॖ', '㉘', 'ᅖ', '入', 'ᙔ', '̡', 'æ', 'ঈ', 'Ⅵ', '⅘', '﴿', '﴾', 'ꬾ', 'ꟿ', 'Ꞩ', 'ꜳ', 'ꜩ', 'ꝙ', 'Ꝏ', 'ꝰ', '▓', '▼', '♥', '♪', '≈', '≡', '╬', '₯', '№', '₻', '↕', '↔', '∏', '∆', '∑', '₪', '₢', '₡', 'ᵺ', 'ᴥ', 'ᴨ', '۩', '۝', '۞', '֎', 'Җ', '҂', '҈', 'ϡ'};
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
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!zl.playerCheck(p)) {
                            cancel();
                            return;
                        }

                        StringBuilder builder = new StringBuilder("§" + ChatColor.values()[new Random().nextInt(ChatColor.values().length)].getChar());

                        for (int i = 0; i < 10; i++) builder.append(randomCharList[new Random().nextInt(randomCharList.length)]);

                        p.setPlayerListHeader("§bYou are playing on " + builder);
                    }
                }.runTaskTimer(Main.getInstance(), 0, 1);

                p.setPlayerListFooter("§ebunnies deserve pets too");

                hasHeaderAndFooter = true;
            }

            UUID uuid = p.getUniqueId();
            Scoreboard main = Bukkit.getScoreboardManager().getMainScoreboard();
            int level = pData.getLevel();
            Team team;
            String suffix = "";
            StringBuilder sort = new StringBuilder();

            if (pData.getBounty() != 0) suffix += " §6§l" + pData.getBounty() + "g";

            if (level >= 100) {
                sort.append("a");
                level -= 100;
            }

            for (int i = 0; i < String.valueOf(level).length(); i++) {
                sort.append(sortHelp[Integer.parseInt(String.valueOf(String.valueOf(level).charAt(i)))]);
            }


            if (!teamMap.containsKey(uuid)) {
                if (main.getTeam(sort + p.getName()) != null) {
                    team = main.getTeam(sort + p.getName());
                } else {
                    team = main.registerNewTeam(sort + p.getName());
                }

                teamMap.put(uuid, team);
            } else if (teamMap.containsKey(uuid) && !teamMap.get(uuid).getName().equals(sort + p.getName())) {
                teamMap.get(uuid).unregister();

                team = main.registerNewTeam(sort + p.getName());

                teamMap.put(uuid, team);
            } else {
                team = teamMap.get(uuid);
            }

            team.setPrefix(zl.getColorBracketAndLevel(uuid.toString()) + " ");
            team.setSuffix(suffix);
            team.addEntry(p.getName());
            team.setColor(ChatColor.GRAY);
        }
    }
}


















