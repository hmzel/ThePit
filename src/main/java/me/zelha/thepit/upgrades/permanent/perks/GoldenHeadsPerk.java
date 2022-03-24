package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static me.zelha.thepit.zelenums.Perks.*;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.PLAYER_HEAD;

public class GoldenHeadsPerk extends AbstractPerk implements Listener {

    private final Set<UUID> gheadCooldown = new HashSet<>();
    private final ItemStack goldenHeadItem = zl.headItemBuilder("PhantomTupac", 1, "§6Golden Head", Arrays.asList(
            "§9Speed I (0:08)",
            "§9Regeneration II (0:05)",
            "§63❤ absorption!",
            "§71 second between eats"
    ));

    public GoldenHeadsPerk() {
        super(Perks.GOLDEN_HEADS);
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void onKill(Player killer, Player dead) {
        PlayerData killerData = Main.getInstance().getPlayerData(killer);
        PlayerInventory inv = killer.getInventory();

        if (!killerData.hasPerkEquipped(GOLDEN_HEADS)) return;
        if (killerData.hasPerkEquipped(VAMPIRE)) return;
        if (killerData.hasPerkEquipped(RAMBO)) return;
        if (killerData.hasPerkEquipped(OLYMPUS)) return;

        if (containsLessThan(2, goldenHeadItem, inv)) {
            if (inv.first(PLAYER_HEAD) != -1) {
                inv.getItem(inv.first(PLAYER_HEAD)).setAmount(inv.getItem(inv.first(PLAYER_HEAD)).getAmount() + 1);
            } else {
                inv.addItem(goldenHeadItem);
            }
        }
    }

    @Override
    public void onReset(Player player, PlayerData playerData) {
        for (ItemStack item : player.getInventory().all(PLAYER_HEAD).values()) {
            if (zl.itemCheck(item) && item.getItemMeta().getDisplayName().equals("§6Golden Head")) {
                player.getInventory().remove(item);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if (zl.itemCheck(item) && item.getItemMeta().getDisplayName().equals("§6Golden Head") && !gheadCooldown.contains(p.getUniqueId())) {
            e.setCancelled(true);

            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                p.getInventory().setItemInMainHand(new ItemStack(AIR));
            }

            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 1, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2, false, false));
            p.setAbsorptionAmount(6);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
            gheadCooldown.add(p.getUniqueId());

            new BukkitRunnable() {
                @Override
                public void run() {
                    gheadCooldown.remove(p.getUniqueId());
                }
            }.runTaskLater(Main.getInstance(), 20);
        }
    }

    @EventHandler
    public void noGheadOnHead1(InventoryClickEvent e) {
        if (!zl.itemCheck(e.getCursor()) || e.getCursor().getType() != PLAYER_HEAD) return;
        if (e.getSlotType() == InventoryType.SlotType.ARMOR) e.setCancelled(true);
    }

    @EventHandler
    public void noGheadOnHead2(InventoryDragEvent e) {
        if (!zl.itemCheck(e.getCursor()) || e.getCursor().getType() != PLAYER_HEAD) return;

        for (Integer slot : e.getRawSlots()) {
            if (e.getView().getSlotType(slot) == InventoryType.SlotType.ARMOR) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        gheadCooldown.remove(e.getPlayer().getUniqueId());
    }
}