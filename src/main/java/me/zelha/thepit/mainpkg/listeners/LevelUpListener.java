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

import java.util.UUID;

public class LevelUpListener implements Listener {

    RunMethods methods = Main.getInstance().getRunMethods();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        new LevelUpRunnable(p, methods).runTaskTimer(Main.getInstance(),0, 1);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if(methods.hasID(p.getUniqueId())) {
            methods.stop(p.getUniqueId());
        }
    }
}


class LevelUpRunnable extends BukkitRunnable  {

    private final Player player;
    private final UUID uuid;
    private final RunMethods methods;

    ZelLogic zl = Main.getInstance().getZelLogic();

    public LevelUpRunnable(Player player, RunMethods methods) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.methods = methods;
    }

    @Override
    public void run() {
        PlayerData pData = Main.getInstance().getStorage().getPlayerData(uuid.toString());

        if (!methods.hasID(uuid)) {
            methods.setID(uuid, super.getTaskId());
        }

        if (pData.getExp() <= 0 && pData.getLevel() < 120) {
            String previousLevel = zl.getColorBracketAndLevel(uuid.toString());
            int level = pData.getLevel();


            pData.setLevel(level + 1);
            pData.setExp(zl.maxXPReq(uuid.toString()));
            player.setLevel(pData.getLevel());

            if (zl.playerCheck(Bukkit.getPlayer(uuid))) {
                Player p = Bukkit.getPlayer(uuid);

                p.sendTitle("§b§lLEVEL UP!",
                previousLevel + " ➟ " + zl.getColorBracketAndLevel(uuid.toString()),
                10, 0, 20);

                p.sendMessage("§b§lPIT LEVEL UP! " + previousLevel + " ➟ " + zl.getColorBracketAndLevel(uuid.toString()));
            }
        }

        float percentage = zl.maxXPReq(uuid.toString()) - pData.getExp();
        if (player.getExp() != (percentage / zl.maxXPReq(uuid.toString())) && pData.getLevel() < 120) {
            player.setExp(percentage / zl.maxXPReq(uuid.toString()));
        }else if (pData.getLevel() >= 120) {
            player.setExp(1);
        }
    }
}


