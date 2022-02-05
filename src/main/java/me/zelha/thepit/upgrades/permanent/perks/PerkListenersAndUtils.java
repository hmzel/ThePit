package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.mainpkg.listeners.SpawnListener;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.zelha.thepit.zelenums.Perks.*;
import static org.bukkit.Material.*;

public class PerkListenersAndUtils implements Listener {

    private PlayerData pData(Player player) {
        return Main.getInstance().getPlayerData(player);
    }

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final SpawnListener spawnUtils = Main.getInstance().getSpawnListener();
    private final RunMethods methods = Main.getInstance().generateRunMethods();
    private final RunMethods methods2 = Main.getInstance().generateRunMethods();

    private final Set<UUID> gheadCooldown = new HashSet<>();
    private final Map<UUID, Integer> lavaExistTimer = new HashMap<>();
    private final Map<UUID, Block> placedLava = new HashMap<>();
    private final Map<UUID, Material> previousLavaBlock = new HashMap<>();
    private final Map<UUID, Integer> strengthChaining = new HashMap<>();
    private final Map<UUID, Integer> strengthChainingTimer = new HashMap<>();

    private final ItemStack lavaBucketItem = zl.itemBuilder(Material.LAVA_BUCKET, 1, null, Collections.singletonList("§7Perk item"));
    private final ItemStack emptyBucketItem = zl.itemBuilder(BUCKET, 1, null, Collections.singletonList("§7Perk item"));
    private final ItemStack fishingRodItem = zl.itemBuilder(Material.FISHING_ROD, 1, null, Collections.singletonList("§7Perk item"), true);
    private final ItemStack goldenHeadItem = zl.headItemBuilder("PhantomTupac", 1, "§6Golden Head", Arrays.asList(
                "§9Speed I (0:08)",
                "§9Regeneration II (0:05)",
                "§63❤ absorption!",
                "§71 second between eats"
        ));
    private final ItemStack gapple = new ItemStack(GOLDEN_APPLE, 1);

    /**
     * supposed to be called every time perk items should be reset <p>
     * ex: dying, selecting a perk, etc <p>
     * must be called *after* the perk slot is set, in conditions where that applies
     */
    public void perkReset(Player p) {
        PlayerInventory inv = p.getInventory();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        int arrowCount = 0;

        for (ItemStack item : inv.all(ARROW).values()) {
            arrowCount+= item.getAmount();
        }

        strengthChaining.remove(p.getUniqueId());

        removeAll(inv, gapple);

        for (ItemStack items : inv.all(PLAYER_HEAD).values()) {
            if (zl.itemCheck(items) && items.getItemMeta().getDisplayName().equals("§6Golden Head")) {
                inv.remove(items);
            }
        }

        if (!inv.contains(IRON_SWORD) && !pData.hasPerkEquipped(Perks.BARBARIAN)) inv.addItem(zl.itemBuilder(IRON_SWORD, 1));
        if (!inv.contains(BOW)) inv.addItem(zl.itemBuilder(BOW, 1));

        if (pData.hasPerkEquipped(Perks.FISHING_ROD)) {
            if (!inv.contains(fishingRodItem)) {
                inv.addItem(fishingRodItem);
            }
        } else {
            removeAll(inv, fishingRodItem);
        }

        if (pData.hasPerkEquipped(Perks.LAVA_BUCKET)) {
            if (inv.contains(emptyBucketItem)) {
                inv.setItem(inv.first(emptyBucketItem), lavaBucketItem);
            } else if (!inv.contains(lavaBucketItem)) {
                inv.addItem(lavaBucketItem);
            }
        } else {
            removeAll(inv, emptyBucketItem);
            removeAll(inv, lavaBucketItem);
        }

        if (arrowCount < 32 && arrowCount != 0) {
            inv.addItem(new ItemStack(ARROW, 32 - arrowCount));
        } else if (arrowCount == 0 && !zl.itemCheck(inv.getItem(8))) {
            inv.setItem(8, new ItemStack(ARROW, 32));
        } else if (arrowCount == 0) {
            inv.addItem(new ItemStack(ARROW, 32));
        }
    }

    public double getPerkDamageBoost(Player player) {
        double boost = 0;

        if (getStrengthChaining(player)[0] != null) boost+= 0.08 * getStrengthChaining(player)[0];
        return boost;
    }

    /**
     *returns an array where [0] is the level of strength and [1] is the timer
     */
    public Integer[] getStrengthChaining(Player p) {
        return new Integer[] {strengthChaining.get(p.getUniqueId()), strengthChainingTimer.get(p.getUniqueId())};
    }

    private void removeAll(PlayerInventory inventory, ItemStack item) {
        for (ItemStack items : inventory.all(item.getType()).values()) {
            if (items.isSimilar(item)) {
                inventory.remove(items);
            }
        }
    }

    private boolean containsLessThan(int amount, ItemStack item, Inventory inv) {
        int count = 0;

        for (ItemStack invItem : inv.all(item.getType()).values()) {
            if (zl.itemCheck(invItem) && invItem.isSimilar(item)) {
                count += invItem.getAmount();
            }
        }

        return count < amount;
    }

    private boolean containsLessThan(int amount, String name, Material material, Inventory inv) {
        int count = 0;

        for (ItemStack item : inv.all(material).values()) {
            if (zl.itemCheck(item) && item.getItemMeta().getDisplayName().equals(name)) {
                count += item.getAmount();
            }
        }

        return count < amount;
    }//i hate player heads. with a passion.

    private void determineKillReward(Player p) {
        PlayerInventory inv = p.getInventory();

        if (!pData(p).hasPerkEquipped(VAMPIRE) && !pData(p).hasPerkEquipped(RAMBO)) {
            if (pData(p).hasPerkEquipped(OLYMPUS)) {

            } else if (pData(p).hasPerkEquipped(GOLDEN_HEADS) && containsLessThan(2, "§6Golden Head", PLAYER_HEAD, inv)) {
                inv.addItem(goldenHeadItem);
            } else if (containsLessThan(2, gapple, inv)) {
                inv.addItem(gapple);
            }
        }

        if (pData(p).hasPerkEquipped(VAMPIRE)) {

        }

        if (pData(p).hasPerkEquipped(RAMBO)) {

        }
    }

    @EventHandler
    public void onAttackAndKill(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();

        if (spawnUtils.spawnCheck(damagedEntity.getLocation()) || spawnUtils.spawnCheck(damagerEntity.getLocation())) {
            return;
        }

        if (zl.playerCheck(damagedEntity) && zl.playerCheck(damagerEntity)) {
            Player damaged = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();
            UUID damagerUUID = damager.getUniqueId();
            double finalDMG = e.getFinalDamage();
            double damagedHP = damaged.getHealth();

            if (e.getCause() != DamageCause.FALL && (damagedHP - finalDMG) <= 0) {
                determineKillReward(damager);

                if (pData(damager).hasPerkEquipped(STRENGTH_CHAINING)) {
                    if (getStrengthChaining(damager)[0] == null) {
                        strengthChaining.put(damagerUUID, 1);
                    } else if (getStrengthChaining(damager)[0] != 5) {
                        strengthChaining.put(damagerUUID, getStrengthChaining(damager)[0] + 1);
                    }

                    if (methods2.hasID(damagerUUID)) methods2.stop(damagerUUID);
                    strengthChainingTimer.put(damagerUUID, 7);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!methods2.hasID(damagerUUID)) methods2.setID(damagerUUID, getTaskId());

                            strengthChainingTimer.put(damagerUUID, getStrengthChaining(damager)[1] - 1);

                            if (strengthChainingTimer.get(damagerUUID) == 0) {
                                strengthChaining.remove(damagerUUID);
                                strengthChainingTimer.remove(damagerUUID);
                                cancel();
                            }
                        }
                    }.runTaskTimer(Main.getInstance(), 20, 20);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (zl.playerCheck(entity)) {
            if (spawnUtils.spawnCheck(entity.getLocation())) {
                return;
            }

            Player p = (Player) e.getEntity();
            double finalDMG = e.getFinalDamage();
            double currentHP = p.getHealth();

            if (e.getCause() != DamageCause.FALL && (currentHP - finalDMG <= 0)) {
                perkReset(p);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if (zl.itemCheck(item) && item.getItemMeta().getDisplayName().equals("§6Golden Head") && !gheadCooldown.contains(p.getUniqueId())) {
            e.setCancelled(true);

            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                item.setType(AIR);
            }

            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 1, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2, false, false));
            p.setAbsorptionAmount(6);
            gheadCooldown.add(p.getUniqueId());

            new BukkitRunnable() {
                @Override
                public void run() {
                    gheadCooldown.remove(p.getUniqueId());
                }
            }.runTaskLater(Main.getInstance(), 20);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {

        e.setCancelled(true);
        Player p = e.getPlayer();
        Block block = e.getBlock();

        if (spawnUtils.spawnCheck(block.getLocation())) return;
        if (block.getType() == LAVA || block.getType() == WATER) return;

        if (e.getBucket() == Material.LAVA_BUCKET) {

            previousLavaBlock.put(p.getUniqueId(), e.getBlock().getType());
            placedLava.put(p.getUniqueId(), block);
            block.setType(LAVA);

             new BukkitRunnable() {

                @Override
                public void run() {

                    if (!methods.hasID(p.getUniqueId())) {
                        methods.setID(p.getUniqueId(), getTaskId());
                    }

                    if (block.getType() != LAVA) {
                        lavaExistTimer.put(p.getUniqueId(), 0);
                        methods.stop(p.getUniqueId());
                    }

                    if (!lavaExistTimer.containsKey(p.getUniqueId())) lavaExistTimer.put(p.getUniqueId(), 0);
                    lavaExistTimer.put(p.getUniqueId(), lavaExistTimer.get(p.getUniqueId()) + 1);

                    if (lavaExistTimer.get(p.getUniqueId()) == 240) {
                        lavaExistTimer.put(p.getUniqueId(), 0);
                        block.setType(previousLavaBlock.get(p.getUniqueId()));
                        previousLavaBlock.remove(p.getUniqueId());
                        lavaExistTimer.remove(p.getUniqueId());
                        placedLava.remove(p.getUniqueId());
                        methods.stop(p.getUniqueId());
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 1);

            p.getInventory().setItemInMainHand(emptyBucketItem);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {

        Player p = e.getPlayer();
        Block block = e.getBlock();

        if (e.getBucket() == BUCKET) {
            if (block.getType() == LAVA && methods.getID(p.getUniqueId()) != null && placedLava.containsValue(block)) {
                block.setType(previousLavaBlock.get(p.getUniqueId()));
                previousLavaBlock.remove(p.getUniqueId());
                placedLava.remove(p.getUniqueId());
                methods.stop(p.getUniqueId());
                p.getInventory().setItemInMainHand(lavaBucketItem);
            }
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {

        ItemMeta meta = e.getItemDrop().getItemStack().getItemMeta();

        if (meta != null && meta.getLore() != null && meta.getLore().contains("§7Perk item")) {
            e.getPlayer().sendMessage("§c§lNOPE! §7You cannot drop this item!");
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        if (placedLava.containsKey(uuid)) placedLava.get(uuid).setType(previousLavaBlock.get(uuid));

        gheadCooldown.remove(uuid);
        lavaExistTimer.remove(uuid);
        previousLavaBlock.remove(uuid);
        placedLava.remove(uuid);
        perkReset(e.getPlayer());
    }
}










