package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Ministreaks;
import org.bukkit.entity.Player;

public class KhanateMinistreak extends Ministreak {

    @Override
    public double getDamagerModifier(Player damager, PitDamageEvent event) {
        PlayerData pData = Main.getInstance().getPlayerData(damager);

        if (Main.getInstance().getPlayerData(event.getDamaged()).getBounty() == 0) {
            return 0;
        }

        return Math.min((int) ((int) pData.getStreak() / 3D) * 0.04, 0.4);
    }

    @Override
    public void addResourceModifiers(PitKillEvent e) {
        if (((int) Main.getInstance().getPlayerData(e.getKiller()).getStreak() + 1) % Ministreaks.KHANATE.getTrigger() != 0) return;

        e.addGold(8, "Khanate");
    }
}
