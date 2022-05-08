package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.mainpkg.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.bukkit.Material.ARROW;

public class ArquebusierMinistreak extends Ministreak {

    @Override
    public void onTrigger(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);

        pData.setGold(pData.getGold() + 7);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0, false, false, true));

        int count = 0;

        for (ItemStack invItem : player.getInventory().all(ARROW).values()) {
            count += invItem.getAmount();
        }

        if (count <= 112) {
            player.getInventory().addItem(new ItemStack(ARROW, 16));
        } else if (count < 128) {
            player.getInventory().addItem(new ItemStack(ARROW, 128 - count));
        }
    }
}
