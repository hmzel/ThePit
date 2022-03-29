package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;

public class FishingRodPerk extends Perk {

    private final ItemStack fishingRodItem = zl.itemBuilder(Material.FISHING_ROD, 1, null, Collections.singletonList("§7Perk item"), true);

    public FishingRodPerk() {
        super(Perks.FISHING_ROD);
    }

    @Override
    public void onReset(Player player, PlayerData playerData) {
        PlayerInventory inv = player.getInventory();

        if (!playerData.hasPerkEquipped(Perks.FISHING_ROD)) {
            removeAll(inv, fishingRodItem);
            return;
        }

        if (!inv.contains(fishingRodItem)) inv.addItem(fishingRodItem);
    }
}
