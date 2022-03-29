package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;

import static me.zelha.thepit.zelenums.Perks.SAFETY_FIRST;
import static org.bukkit.Material.CHAINMAIL_HELMET;
import static org.bukkit.Material.LEATHER_HELMET;

public class SafetyFirstPerk extends Perk {

    private final ItemStack safetyFirstItem = zl.itemBuilder(CHAINMAIL_HELMET, 1, null, Collections.singletonList("§7Perk item"));

    public SafetyFirstPerk() {
        super(Perks.SAFETY_FIRST);
    }

    @Override
    public void onReset(Player player, PlayerData playerData) {
        PlayerInventory inv = player.getInventory();

        if (!playerData.hasPerkEquipped(SAFETY_FIRST)) {
            removeAll(inv, safetyFirstItem);
            return;
        }

        if (!zl.itemCheck(inv.getHelmet()) || inv.getHelmet().getType() == LEATHER_HELMET) inv.setHelmet(safetyFirstItem);
    }
}
