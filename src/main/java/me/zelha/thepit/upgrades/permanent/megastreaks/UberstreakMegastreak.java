package me.zelha.thepit.upgrades.permanent.megastreaks;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Megastreaks;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

import static org.bukkit.Material.ENDER_CHEST;

public class UberstreakMegastreak extends Megastreak implements Listener {

    private final Random rng = new Random();
    private final Set<UUID> hasHeartsTaken = new HashSet<>();
    private final List<PotionEffect> halvedEffects = new ArrayList<>();
    private final AttributeModifier removeHearts = new AttributeModifier("Uberstreak", -4, AttributeModifier.Operation.ADD_NUMBER);

    public UberstreakMegastreak() {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public double getDamagedModifier(Player damaged, PitDamageEvent event) {
        return Math.floor(Main.getInstance().getPlayerData(damaged).getStreak() / 100) * 0.1;
    }

    @Override
    public double getDamagerModifier(Player damager, PitDamageEvent event) {
        PlayerData pData = Main.getInstance().getPlayerData(damager);
        PlayerData damagedData = Main.getInstance().getPlayerData(event.getDamaged());

        if (damagedData.getPrestige() == 0 && pData.getStreak() >= 100) {
            return -0.4;
        }

        return 0;
    }

    @Override
    public void onDeath(Player player) {
        if (hasHeartsTaken.contains(player.getUniqueId())) {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(removeHearts);
        }

        PlayerData pData = Main.getInstance().getPlayerData(player);

        if (pData.getStreak() < 400) return;

        int rng = this.rng.nextInt(99) + 1;
        int current = 0;
        Uberdrops uberdrop = null;

        while (rng <= Uberdrops.MYSTIC_DROP_CHANCE.getChance() && pData.getUberdropMysticChance() == 10) {
            rng = this.rng.nextInt(99) + 1;
        }

        for (Uberdrops uberdrops : Uberdrops.values()) {
            current += uberdrops.getChance();

            if (rng <= current) {
                uberdrop = uberdrops;
                break;
            }
        }

        if (uberdrop == null) {
            player.sendMessage("§c§lERROR! §7Something went wrong determining uberdrop!");
            return;
        }

        String count = "";

        if (uberdrop.name().contains("PHILO"))  count = uberdrop.getCount() + " ";
        if (uberdrop.name().contains("FEATHER"))  count = uberdrop.getCount() + " ";

        ItemStack uberItem = zl.itemBuilder(ENDER_CHEST, 1, "§dUberdrop", Arrays.asList(
                "§7Kept on death",
                "§7Contains: " + uberdrop.getColor() + count + uberdrop.getDisplayName(),
                "",
                "§eHold and click to open!"
        ));

        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(uberItem);
        NBTTagCompound nbt = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();

        nbt.setString("uberdrop", uberdrop.name().toLowerCase());
        nmsItem.setTag(nbt);
        player.getInventory().addItem(CraftItemStack.asBukkitCopy(nmsItem));
        //add a case for a full inventory and make it go into stash when stash is added
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        ItemStack item = p.getInventory().getItemInMainHand();

        if (!zl.itemCheck(item)) return;
        if (item.getType() != ENDER_CHEST) return;
        if (item.getItemMeta() == null) return;
        if (!item.getItemMeta().getDisplayName().equals("§dUberdrop")) return;

        Uberdrops uberdrop = Uberdrops.findByKey(CraftItemStack.asNMSCopy(item).getTag().getString("uberdrop"));

        if (uberdrop == null) {
            p.sendMessage("§c§lERROR! §7Something went wrong determining uberdrop!");
            return;
        }

        if (uberdrop == Uberdrops.MYSTIC_DROP_CHANCE) {
            if (pData.getUberdropMysticChance() == 10) {
                p.sendMessage("§c§lNOPE! §7You already have max mystic chance from uberdrops!");
                return;
            }

            pData.setUberdropMysticChance(pData.getUberdropMysticChance() + 1);
            p.sendMessage("§d§lUBERCHANCE! §7Now have §d+" + pData.getUberdropMysticChance() + "% chance §7(max 10%");
        }

        if (uberdrop == Uberdrops.JEWEL_SWORD || uberdrop == Uberdrops.TOTALLY_LEGIT_GEM || uberdrop.name().contains("PHILOS") || uberdrop.name().contains("FEATHER")) {
            p.sendMessage("§5Currently not added");
            return;
        }
    }

    @EventHandler
    public void onKill(PitKillEvent e) {
        PlayerData pData = Main.getInstance().getPlayerData(e.getKiller());

        if ((int) pData.getStreak() + 1 == 200 && pData.isMegaActive() && pData.getMegastreak() == Megastreaks.UBERSTREAK) {
            e.getKiller().getAttribute(Attribute.GENERIC_MAX_HEALTH).addModifier(removeHearts);
            hasHeartsTaken.add(e.getKiller().getUniqueId());
        }
    }

    @EventHandler
    public void onPotion(EntityPotionEffectEvent e) {
        if (!zl.playerCheck(e.getEntity())) return;

        if (e.getAction() == EntityPotionEffectEvent.Action.REMOVED) {
            halvedEffects.remove(e.getNewEffect());
            halvedEffects.remove(e.getOldEffect());
        }

        if (e.getAction() == EntityPotionEffectEvent.Action.REMOVED) return;
        if (halvedEffects.contains(e.getNewEffect())) return;

        Player p = (Player) e.getEntity();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (!pData.isMegaActive()) return;
        if (pData.getStreak() < 300) return;
        if (pData.getMegastreak() != Megastreaks.UBERSTREAK) return;

        e.setCancelled(true);
        PotionEffect old = e.getNewEffect();
        PotionEffect effect = new PotionEffect(old.getType(), old.getDuration() / 2, old.getAmplifier(), old.isAmbient(), old.hasParticles(), old.hasIcon());

        p.removePotionEffect(effect.getType());
        halvedEffects.add(effect);
        p.addPotionEffect(effect);
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        if (!zl.playerCheck(e.getEntity())) return;

        Player p = (Player) e.getEntity();

        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (!pData.isMegaActive()) return;
        if (pData.getStreak() < 400) return;
        if (pData.getMegastreak() != Megastreaks.UBERSTREAK) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (hasHeartsTaken.contains(p.getUniqueId())) {
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(removeHearts);
            hasHeartsTaken.remove(p.getUniqueId());
        }
    }


    private enum Uberdrops {
        MYSTIC_DROP_CHANCE("Perma +1% Mystic Drop Chance", "§d", 1, 1),
        JEWEL_SWORD("Hidden Jewel Sword", "§d", 1, 5),
        FIVE_PHILO("Philosopher's Cactus", "§a", 5, 18),
        TEN_PHILO("Philosopher's Cactus", "§a", 10, 12),
        FIFTEEN_PHILO("Philosopher's Cactus", "§a", 15, 9),
        TWENTY_PHILO("Philosopher's Cactus", "§a", 20, 4),
        TOTALLY_LEGIT_GEM("Totally Legit Gem", "§a", 1, 33),
        ONE_FEATHER("Funky Feather", "§3", 1, 10),
        TWO_FEATHER("Funky Feather", "§3", 2, 5),
        THREE_FEATHER("Funky Feather", "§3", 3, 3);

        private final String displayName;
        private final String color;
        private final int count;
        private final int chance;

        Uberdrops(String displayName, String color, int count, int chance) {
            this.displayName = displayName;
            this.color = color;
            this.count = count;
            this.chance = chance;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getColor() {
            return color;
        }

        public int getCount() {
            return count;
        }

        public int getChance() {
            return chance;
        }

        public static Uberdrops findByKey(String key) {
            for (Uberdrops uberdrops : values()) {
                if (key.equalsIgnoreCase(uberdrops.name())) return uberdrops;
            }
            return null;
        }
    }
}




















