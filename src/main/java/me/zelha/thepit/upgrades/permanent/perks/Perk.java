package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitAssistEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.ZelLogic;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nullable;

import static org.bukkit.Material.PLAYER_HEAD;

public class Perk {

    protected final ZelLogic zl = Main.getInstance().getZelLogic();

    public Perk() {
    }

    public double getDamageModifier(Player damager, Player damaged) {
        return 0;
    }

    public void onAttacked(Player damager, Player damaged) {
    }

    public void onAttack(Player damager, Player damaged, @Nullable Arrow arrow) {
    }

    public void onKill(Player killer, Player dead) {
    }

    public void addResourceModifiers(PitKillEvent event) {
    }

    public void addResourceModifiers(PitAssistEvent event) {
    }

    public void onReset(Player player, PlayerData playerData) {
    }

    protected void removeAll(PlayerInventory inventory, ItemStack item) {
        for (ItemStack items : inventory.all(item.getType()).values()) {
            if (items.isSimilar(item)) inventory.remove(items);
        }
    }

    protected boolean containsLessThan(int amount, ItemStack item, Inventory inv) {
        int count = 0;

        if (item.getType() == PLAYER_HEAD) {
            for (ItemStack item2 : inv.all(PLAYER_HEAD).values()) {
                if (zl.itemCheck(item2) && item2.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                    count += item2.getAmount();
                }
            }
            return count < amount;
        }

        for (ItemStack invItem : inv.all(item.getType()).values()) {
            if (zl.itemCheck(invItem) && invItem.isSimilar(item)) {
                count += invItem.getAmount();
            }
        }
        return count < amount;
    }
}
