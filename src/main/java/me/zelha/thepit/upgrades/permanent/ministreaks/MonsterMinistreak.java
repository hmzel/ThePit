package me.zelha.thepit.upgrades.permanent.ministreaks;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

public class MonsterMinistreak extends Ministreak {

    private final AttributeModifier firstHeart = new AttributeModifier("Monster", 2, AttributeModifier.Operation.ADD_NUMBER);
    private final AttributeModifier secondHeart = new AttributeModifier("Monster", 2, AttributeModifier.Operation.ADD_NUMBER);

    @Override
    public void onTrigger(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (!attribute.getModifiers().contains(firstHeart)) {
            attribute.addModifier(firstHeart);
            return;
        }

        if (!attribute.getModifiers().contains(secondHeart)) attribute.addModifier(secondHeart);
    }

    @Override
    public void onReset(Player player) {
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(firstHeart);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(secondHeart);
    }
}
