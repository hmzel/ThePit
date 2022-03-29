package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;

import static me.zelha.thepit.zelenums.Perks.BOUNTY_HUNTER;
import static org.bukkit.Material.*;

public class BountyHunterPerk extends Perk {

    private final ItemStack bountyHunterItem = zl.itemBuilder(GOLDEN_LEGGINGS, 1, null, Collections.singletonList("§7Perk item"), true);

    public BountyHunterPerk() {
        super(Perks.BOUNTY_HUNTER);
    }

    @Override
    public double getDamageModifier(Player damager, Player damaged) {
        if (zl.itemCheck(damager.getInventory().getLeggings()) && damager.getInventory().getLeggings().getType() == Material.GOLDEN_LEGGINGS) {
            return Math.floor((double) Main.getInstance().getPlayerData(damaged).getBounty() / 100) / 100;
        }
        return 0;
    }

    @Override
    public void onReset(Player player, PlayerData playerData) {
        PlayerInventory inv = player.getInventory();

        if (!playerData.hasPerkEquipped(BOUNTY_HUNTER)) {
            removeAll(inv, bountyHunterItem);

            if (zl.itemCheck(inv.getLeggings()) && inv.getLeggings().equals(bountyHunterItem)) {
                inv.setLeggings(zl.itemBuilder(CHAINMAIL_LEGGINGS, 1));
            }

            return;
        }


        if (!inv.contains(bountyHunterItem)) {
            if (!zl.itemCheck(inv.getLeggings()) || inv.getLeggings().getType() == CHAINMAIL_LEGGINGS || inv.getLeggings().getType() == IRON_LEGGINGS) {
                inv.setLeggings(bountyHunterItem);
            }
        }
    }
}
