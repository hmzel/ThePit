package me.zelha.thepit.utils;

import me.zelha.thepit.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.bukkit.Material.GREEN_TERRACOTTA;
import static org.bukkit.Material.RED_TERRACOTTA;

public class ConfirmGUIHandler implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final Map<UUID, Consumer<Player>> consumerMap = new HashMap<>();

    public void confirmPurchase(Player player, String purchasing, double cost, boolean canSkip, Consumer<Player> consumer) {
        if (canSkip && cost < 1000) {
            consumer.accept(player);
            return;
        }

        Inventory inv = Bukkit.createInventory(player, 27, "Are you sure?");

        inv.setItem(11, zl.itemBuilder(GREEN_TERRACOTTA, 1, "§aConfirm", Arrays.asList(
                "§7Purchasing: " + purchasing,
                "§7Cost: §6" + zl.getFancyGoldString(cost) + "g"
        )));
        inv.setItem(15, zl.itemBuilder(RED_TERRACOTTA, 1, "§cCancel", Arrays.asList(
                "§7Return to previous menu."
        )));

        consumerMap.put(player.getUniqueId(), consumer);
        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (!e.getView().getTitle().equals("Are you sure?")) return;
        if (e.getClickedInventory() == e.getView().getBottomInventory()) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;
        if (!consumerMap.containsKey(p.getUniqueId())) return;

        if (e.getCurrentItem().getType() == GREEN_TERRACOTTA) {
            consumerMap.get(p.getUniqueId()).accept(p);
        } else {
            p.closeInventory();
        }

        consumerMap.remove(p.getUniqueId());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!e.getView().getTitle().equals("Are you sure?")) return;

        consumerMap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        consumerMap.remove(e.getPlayer().getUniqueId());
    }
}










