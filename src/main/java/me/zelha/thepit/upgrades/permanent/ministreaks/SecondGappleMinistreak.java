package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Material.GOLDEN_APPLE;

public class SecondGappleMinistreak extends Ministreak {

    @Override
    public void onTrigger(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);

        pData.setExp(pData.getExp() - 5);
        pData.setGold(pData.getGold() + 5);

        if (pData.hasPerkEquipped(Perks.GOLDEN_HEADS)) {
            Perks.GOLDEN_HEADS.getMethods().onKill(player, null);
            return;
        }

        if (pData.hasPerkEquipped(Perks.VAMPIRE)) return;
        if (pData.hasPerkEquipped(Perks.RAMBO)) return;
        if (pData.hasPerkEquipped(Perks.OLYMPUS)) return;

        int count = 0;

        for (ItemStack invItem : player.getInventory().all(GOLDEN_APPLE).values()) {
            count += invItem.getAmount();
        }

        if (count < 2) player.getInventory().addItem(new ItemStack(GOLDEN_APPLE, 1));
    }
}
