package me.zelha.thepit.mainpkg.listeners;

import me.zelha.thepit.Main;
import me.zelha.thepit.utils.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();

    private String formatBrackets(int prestige, String string) {
        if (prestige < 1) {
            return "§7[" + string + "§7]";
        } else if (prestige < 5) {
            return "§9[" + string + "§9]";
        } else if (prestige < 10) {
            return "§e[" + string + "§e]";
        } else if (prestige < 15) {
            return "§6[" + string + "§6]";
        } else if (prestige < 20) {
            return "§c[" + string + "§c]";
        } else if (prestige < 25) {
            return "§5[" + string + "§5]";
        } else if (prestige < 30) {
            return "§d[" + string + "§d]";
        } else if (prestige < 35) {
            return "§f[" + string + "§f]";
        } else if (prestige < 40) {
            return "§b[" + string + "§b]";
        } else if (prestige < 45) {
            return "§1[" + string + "§1]";
        } else if (prestige < 50) {
            return "§3[" + string + "§3]";
        } else if (prestige == 50) {
            return "§4[" + string + "§4]";
        }
        return "§5§l[§5§k|" + string + "§5§k|§5§l]";
    }

    private String getPrestigeColor(int prestige) {
        if (prestige < 1) {
            return "§7";
        } else if (prestige < 5) {
            return "§9";
        } else if (prestige < 10) {
            return "§e";
        } else if (prestige < 15) {
            return "§6";
        } else if (prestige < 20) {
            return "§c";
        } else if (prestige < 25) {
            return "§5";
        } else if (prestige < 30) {
            return "§d";
        } else if (prestige < 35) {
            return "§f";
        } else if (prestige < 40) {
            return "§b";
        } else if (prestige < 45) {
            return "§1";
        } else if (prestige < 50) {
            return "§3";
        } else if (prestige == 50) {
            return "§4";
        }
        return "§5§l";
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String prefix;
        String numerals;
        PlayerData pData = Main.getInstance().getPlayerData(e.getPlayer());

        if (pData.getPrestige() <= 1000) numerals = "§e" + zl.toRoman(pData.getPrestige()); else numerals = "§cERROR";

        if (pData.getPrestige() == 0) {
            prefix = formatBrackets(0, zl.getColorLevel(pData.getLevel()));
        } else {
            prefix = formatBrackets(pData.getPrestige(), numerals + getPrestigeColor(pData.getPrestige()) + "-" + zl.getColorLevel(pData.getLevel()));
        }

        e.setFormat(prefix + "§7 %s§f: %s");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        e.setQuitMessage(null);
    }
}
