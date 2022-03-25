package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static org.bukkit.Material.PLAYER_HEAD;

public abstract class AbstractPerk {

    protected final ZelLogic zl = Main.getInstance().getZelLogic();
    private final Perks perk;

    public AbstractPerk(Perks perk) {
        this.perk = perk;
    }

    public double getDamageModifier(Player player) {
        return 0;
    }

    public void onAttacked(Player damager, Player damaged) {
    }

    public void onAttack(Player damager, Player damaged, boolean causedByArrow) {
    }

    public void onKill(Player killer, Player dead) {
    }

    public void onReset(Player player, PlayerData playerData) {
    }

    public Perks getPerk() {
        return perk;
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
