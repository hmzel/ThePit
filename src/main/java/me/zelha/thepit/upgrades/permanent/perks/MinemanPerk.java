package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;

import static me.zelha.thepit.zelenums.Perks.MINEMAN;
import static org.bukkit.Material.COBBLESTONE;
import static org.bukkit.Material.DIAMOND_PICKAXE;

public class MinemanPerk extends Perk {

    private final ItemStack minemanPickaxeItem = zl.itemBuilder(DIAMOND_PICKAXE, 1, null, Collections.singletonList("§7Perk item"), true, true, Pair.of(Enchantment.DIG_SPEED, 4));
    private final ItemStack minemanCobblestoneItem = zl.itemBuilder(COBBLESTONE, 24, null, Collections.singletonList("§7Perk item"));

    public MinemanPerk() {
        super(Perks.MINEMAN);
    }

    @Override
    public void onKill(Player killer, Player dead) {
        PlayerInventory inv = killer.getInventory();

        if (containsLessThan(62, minemanCobblestoneItem, inv)) {
            ItemStack item = new ItemStack(minemanCobblestoneItem);

            item.setAmount(3);
            inv.addItem(item);
        } else if (containsLessThan(64, minemanCobblestoneItem, inv)) {
            inv.getItem(inv.first(COBBLESTONE)).setAmount(64);
        }
    }

    @Override
    public void onReset(Player player, PlayerData playerData) {
        PlayerInventory inv = player.getInventory();

        if (!playerData.hasPerkEquipped(MINEMAN)) {
            removeAll(inv, minemanPickaxeItem);
            removeAll(inv, minemanCobblestoneItem);
            return;
        }

        if (!inv.contains(minemanPickaxeItem)) inv.addItem(minemanPickaxeItem);
        if (inv.contains(minemanCobblestoneItem)) return;

        if (inv.first(COBBLESTONE) != -1) {
            int slot = inv.first(COBBLESTONE);

            removeAll(inv, minemanCobblestoneItem);
            inv.setItem(slot, minemanCobblestoneItem);
        } else {
            inv.addItem(minemanCobblestoneItem);
        }
    }
}
