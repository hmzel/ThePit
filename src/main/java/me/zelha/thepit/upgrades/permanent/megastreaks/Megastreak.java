package me.zelha.thepit.upgrades.permanent.megastreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.ZelLogic;
import me.zelha.thepit.zelenums.Megastreaks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class Megastreak {

    protected final ZelLogic zl = Main.getInstance().getZelLogic();

    public void onTrigger(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);
        StringBuilder builder = new StringBuilder(pData.getMegastreak().getChatName());

        Bukkit.broadcastMessage(
                "§c§lMEGASTREAK! " + zl.getColorBracketAndLevel(player) + " §7" + player.getName() + " activated " +
                pData.getMegastreak().getChatName()
        );

        pData.setDummyStatus(builder.replace(2, builder.length(), pData.getMegastreak().getName()).toString());
        player.getWorld().playSound(new Location(player.getWorld(), 0.5, -100, 0.5), Sound.ENTITY_WITHER_SPAWN, 50, 1);
    }

    public void onEquip(Player player) {
    }

    public double getDebuff(Player player, PitDamageEvent event) {
        return 0;
    }

    public double getBuff(Player player) {
        return 0;
    }

    public double getEXPModifier(Player player) {
        return 1;
    }

    public double getGoldModifier(Player player) {
        return 1;
    }

    public void onDeath(Player player) {
    }

    protected void permanentEffect(Player player, PotionEffect effect, boolean checkActive) {
        new BukkitRunnable() {

            private final PlayerData pData = Main.getInstance().getPlayerData(player);
            private final Megastreaks currentMega = pData.getMegastreak();

            @Override
            public void run() {
                if (!zl.playerCheck(player)) {
                    cancel();
                    return;
                }

                if ((checkActive && !pData.isMegaActive()) || currentMega != pData.getMegastreak()) {
                    cancel();
                    return;
                }

                player.addPotionEffect(effect);
            }
        }.runTaskTimer(Main.getInstance(), 0, 80);
    }
}






