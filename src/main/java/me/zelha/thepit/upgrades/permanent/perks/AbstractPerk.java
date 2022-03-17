package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class AbstractPerk {

    private final Perks perk;

    public AbstractPerk(Perks perk) {
        this.perk = perk;
    }

    public Perks getPerk() {
        return perk;
    }

    protected void removeAll(PlayerInventory inventory, ItemStack item) {
        for (ItemStack items : inventory.all(item.getType()).values()) {
            if (items.isSimilar(item)) inventory.remove(items);
        }
    }

    public abstract void onReset(Player player, PlayerData playerData);
}
