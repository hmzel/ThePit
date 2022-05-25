package me.zelha.thepit.upgrades.permanent.ministreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ApostleToRngesusMinistreak extends Ministreak {

    private final Random random = new Random();
    private final Set<UUID> triggered = new HashSet<>();

    @Override
    public void onTrigger(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);
        int rng = random.nextInt(99) + 1;
        List<String> messages = new ArrayList<>();

        player.sendMessage("§d§lRNGESUS! §7Rolled a §e" + rng + "§7!");

        if (rng == 27 || rng == 42) {
            //+1 renown
            messages.add("§d➜ §e+1 Renown§7!");
        }

        if (rng == 42) {
            player.setAbsorptionAmount(player.getAbsorptionAmount() + 20);
            messages.add("§d➜ §6+10❤ absorption§7!");
        }

        if (rng == 50) {
            if (zl.itemPlacementHandler(player, EquipmentSlot.HEAD, zl.itemBuilder(Material.DIAMOND_HELMET, 1, "§dRNGesus Helmet", null, true))) {
                messages.add("§d➜ §b+1 Diamond Helmet!");
            } else {
                messages.add("§d➜ §b+0 Diamond Helmet! §c(Inventory full!)");
            }
        }

        if (rng == 13 || rng == 66) {
            //give 3 vile
            messages.add("§d➜ §5+3 Chunk of Vile§7!");
        }

        if (rng == 77 || rng == 88) {
            //give mystic drop
            messages.add("§d➜ §7(insert mystic item name here)");
        }

        if (rng == 99) {
            EntityLightning lightning = new EntityLightning(EntityTypes.U, ((CraftWorld) player.getWorld()).getHandle());

            lightning.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());

            ((CraftPlayer) player).getHandle().b.sendPacket(new PacketPlayOutSpawnEntity(lightning));

            zl.trueDamage(player, null, 8, "RNGesus", false);

            messages.add("§d➜ §c§lGET SMITED!");
        }

        if (rng % 4 == 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1, false, false, true));
            messages.add("§d➜ §cRegeneration II §7(0:10)");
        } else if (rng % 2 == 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 400, 0, false, false, true));
            messages.add("§d➜ §cRegeneration I §7(0:20)");
        }

        if (rng % 3 == 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 0, false, false, true));
            messages.add("§d➜ §eSpeed I §7(0:15)");
        }

        if (rng % 7 == 0) {
            int gold = random.nextInt(4500) + 500;

            pData.setGold(pData.getGold() + gold);
            messages.add("§d➜ §6+" + zl.getFancyNumberString(gold) + "g");
        }

        if (rng % 8 == 0) {
            int xp = random.nextInt(4500) + 500;

            pData.setExp(pData.getExp() - xp);
            messages.add("§d➜ §b+" + zl.getFancyNumberString(xp) + " XP");
        }

        if (rng % 12 == 0) {
            player.setAbsorptionAmount(player.getAbsorptionAmount() + 8);
            messages.add("§d➜ §6+4❤ absorption§7!");
        }

        if (rng % 13 == 0) {
            triggered.add(player.getUniqueId());
            messages.add("§d➜ §c+25% damage §7(0:30)");

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!zl.playerCheck(player)) return;

                    triggered.remove(player.getUniqueId());
                }
            }.runTaskLater(Main.getInstance(), 600);
        }

        if (messages.isEmpty()) {
            messages.add("§d➜ §7Nothing happens! Nice!");
        }

        for (String message : messages) player.sendMessage(message);
    }

    @Override
    public double getDamagerModifier(Player damager, PitDamageEvent event) {
        if (triggered.contains(damager.getUniqueId())) return 0.25;

        return 0;
    }

    @Override
    public void onReset(Player player) {
        triggered.remove(player.getUniqueId());
    }
}

























/*    private enum Rolls {
        RENOWN(new Roll() {
            @Override
            public boolean shouldRoll(int rng) {
                return rng == 27 || rng == 42;
            }

            @Override
            public void onRoll() {
                //+1 renown
            }
        }, message),
        TEN_ABSORPTION(roll, message),
        DIAMOND_HELMET(roll, message),
        THREE_VILE(roll, message),
        MYSTIC_DROP(roll, message),
        SMITE(roll, message),
        REGENERATION_ONE(roll, message),
        SPEED_ONE(roll, message),
        REGENERATION_TWO(roll, message),
        GOLD(roll, message),
        EXP(roll, message),
        FOUR_ABSORPTION(roll, message),
        DAMAGE_BOOST(roll, message);

        private final Roll roll;
        private final String message;

        Rolls(Roll roll, String message) {
            this.roll = roll;
            this.message = message;
        }
    }


    private static abstract class Roll {
        public abstract boolean shouldRoll(int rng);

        public abstract void onRoll();
    }*/