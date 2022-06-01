package me.zelha.thepit.upgrades.permanent;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitDeathEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.upgrades.permanent.megastreaks.Megastreak;
import me.zelha.thepit.zelenums.Ministreaks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class KillstreakListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(PitKillEvent e) {
        Player p = e.getKiller();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        Megastreak megaMethods = pData.getMegastreak().getMethods();

        for (Ministreaks ministreak : pData.getEquippedMinistreaks()) {
            if ((int) pData.getStreak() % ministreak.getTrigger() != 0) continue;
            if (ministreak.getMethods() == null) continue;

            ministreak.getMethods().onTrigger(p);
        }

        if ((int) pData.getStreak() < pData.getMegastreak().getTrigger()) return;
        if (megaMethods == null) return;
        if (pData.isMegaActive()) return;

        megaMethods.onTrigger(p);
    }

    //using two eventhandlers to preserve accuracy between normal pit and this recreation
    @EventHandler(priority = EventPriority.HIGH)
    public void addResourceModifiers(PitKillEvent e) {
        Player p = e.getKiller();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        for (Ministreaks ministreak : pData.getEquippedMinistreaks()) {
            if (ministreak.getMethods() == null) continue;

            ministreak.getMethods().addResourceModifiers(e);
        }

        if (pData.isMegaActive()) pData.getMegastreak().getMethods().addResourceModifiers(e);
    }

    @EventHandler
    public void onDamage(PitDamageEvent e) {
        Player damaged = e.getDamaged();
        Player damager = e.getDamager();
        PlayerData damagedData = Main.getInstance().getPlayerData(damaged);
        PlayerData damagerData = Main.getInstance().getPlayerData(damager);

        for (Ministreaks ministreak : damagedData.getEquippedMinistreaks()) {
            if (ministreak.getMethods() == null) continue;

            e.setBoost(e.getBoost() + ministreak.getMethods().getDamagedModifier(damaged, e));
        }

        for (Ministreaks ministreak : damagerData.getEquippedMinistreaks()) {
            if (ministreak.getMethods() == null) continue;

            e.setBoost(e.getBoost() + ministreak.getMethods().getDamagerModifier(damager, e));
        }

        if (canApply(damaged)) {
            e.setBoost(e.getBoost() + damagedData.getMegastreak().getMethods().getDamagedModifier(damaged, e));
        }

        if (canApply(damager)) {
            e.setBoost(e.getBoost() + damagerData.getMegastreak().getMethods().getDamagerModifier(damager, e));
        }
    }

    @EventHandler(ignoreCancelled = true)
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

























