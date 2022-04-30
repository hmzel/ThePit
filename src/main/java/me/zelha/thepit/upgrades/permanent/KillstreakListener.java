package me.zelha.thepit.upgrades.permanent;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitDeathEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.upgrades.permanent.megastreaks.Megastreak;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class KillstreakListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKill(PitKillEvent e) {
        Player p = e.getKiller();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        Megastreak megaMethods = pData.getMegastreak().getMethods();

        if ((int) pData.getStreak() < pData.getMegastreak().getTrigger()) return;
        if (pData.isMegaActive()) return;
        if (megaMethods == null) return;

        megaMethods.onTrigger(p);
        pData.setMegaActive(true);
    }

    @EventHandler
    public void onDamage(PitDamageEvent e) {
        Player damaged = e.getDamaged();
        Player damager = e.getDamager();
        PlayerData damagedData = Main.getInstance().getPlayerData(damaged);
        PlayerData damagerData = Main.getInstance().getPlayerData(damager);

        if (canApply(damaged)) {
            e.setBoost(e.getBoost() + damagedData.getMegastreak().getMethods().getDebuff(damaged, e));
        }

        if (canApply(damager)) {
            e.setBoost(e.getBoost() + damagerData.getMegastreak().getMethods().getBuff(damager, e));
        }
    }

    @EventHandler
    public void onDeath(PitDeathEvent e) {
        Player p = e.getDead();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (!pData.isMegaActive()) return;
        if (pData.getMegastreak().getMethods() == null) return;

        pData.getMegastreak().getMethods().onDeath(p);
        pData.setMegaActive(false);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemMeta meta = e.getItemDrop().getItemStack().getItemMeta();

        if (meta != null && meta.getLore() != null && meta.getLore().contains("§eSpecial item")) {
            e.getPlayer().sendMessage("§c§lNOPE! §7You cannot drop this item!");
            e.setCancelled(true);
        }
    }

    private boolean canApply(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);
        Megastreak megaMethods = pData.getMegastreak().getMethods();

        if ((int) pData.getStreak() < pData.getMegastreak().getTrigger()) return false;
        if (!pData.isMegaActive()) return false;
        if (megaMethods == null) return false;

        return true;
    }
}

























