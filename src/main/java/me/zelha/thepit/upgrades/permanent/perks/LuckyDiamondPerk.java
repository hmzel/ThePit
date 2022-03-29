package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.Locale;
import java.util.Random;

import static me.zelha.thepit.zelenums.Perks.LUCKY_DIAMOND;
import static org.bukkit.Material.AIR;
import static org.bukkit.inventory.EquipmentSlot.*;

public class LuckyDiamondPerk extends Perk {

    public LuckyDiamondPerk() {
        super(LUCKY_DIAMOND);
    }

    private boolean isLuckyDiamondItem(ItemStack item) {
        return Main.getInstance().getZelLogic().itemCheck(item)
                && item.getItemMeta() != null
                && item.getItemMeta().getLore() != null
                && item.getItemMeta().getLore().contains("§7Perk item")
                && item.getType().name().contains("DIAMOND")
                && !item.getType().name().contains("PICKAXE");
    }

    @Override
    public void onKill(Player killer, Player dead) {
        PlayerInventory inv = killer.getInventory();
        PlayerInventory deadInv = dead.getInventory();

        for (EquipmentSlot slot : new EquipmentSlot[] {HEAD, CHEST, LEGS, FEET}) {
            ItemStack slotItem = deadInv.getItem(slot);

            if (zl.itemCheck(slotItem) && slotItem.getType().name().contains("IRON") && new Random().nextInt(100) < 30) {
                Material diamondType = Material.getMaterial(new StringBuilder(slotItem.getType().name()).replace(0, 4, "DIAMOND").toString());
                ItemStack diamondItem = zl.itemBuilder(diamondType, 1, null, Collections.singletonList("§7Perk item"), true);
                String stringedType = new StringBuilder(diamondType.name().toLowerCase(Locale.ROOT))
                        .replace(7, 8, " ")
                        .replace(0, 1, "D")
                        .replace(8, 9, String.valueOf(diamondType.name().charAt(8)))
                        .toString();

                if (!zl.itemCheck(inv.getItem(slot)) || inv.getItem(slot).getType().name().contains("IRON") || inv.getItem(slot).getType().name().contains("CHAINMAIL")) {
                    if (zl.itemCheck(inv.getItem(slot))) inv.setItem(zl.firstEmptySlot(inv), inv.getItem(slot));

                    inv.setItem(slot, diamondItem);
                    killer.sendMessage("§b§lLUCKY DIAMOND! §7" + stringedType);
                } else if (!inv.contains(diamondItem)) {
                    inv.setItem(zl.firstEmptySlot(inv), diamondItem);
                    killer.sendMessage("§b§lLUCKY DIAMOND! §7" + stringedType);
                } else {
                    dead.getWorld().dropItemNaturally(dead.getLocation(), zl.itemBuilder(diamondType, 1));
                }
            }
        }
    }

    @Override
    public void onReset(Player player, PlayerData playerData) {
        PlayerInventory inv = player.getInventory();

        if (!playerData.hasPerkEquipped(LUCKY_DIAMOND)) {
            for (EquipmentSlot slot : new EquipmentSlot[] {HEAD, CHEST, LEGS, FEET}) {
                if (isLuckyDiamondItem(inv.getItem(slot))) inv.setItem(slot, new ItemStack(AIR));
            }

            for (ItemStack item : inv.getStorageContents()) {
                if (isLuckyDiamondItem(item)) inv.remove(item);
            }
        }
    }
}







