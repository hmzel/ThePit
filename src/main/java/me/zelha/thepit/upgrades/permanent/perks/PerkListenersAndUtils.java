package me.zelha.thepit.upgrades.permanent.perks;

import me.zelha.thepit.Main;
import me.zelha.thepit.RunMethods;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static me.zelha.thepit.zelenums.Perks.*;
import static org.bukkit.Material.*;
import static org.bukkit.inventory.EquipmentSlot.CHEST;
import static org.bukkit.inventory.EquipmentSlot.*;

//trickle down is handled in GoldIngotListener because thats just way easier
//all resource-related stuff is handled in KillListener
public class PerkListenersAndUtils implements Listener {

    private PlayerData pData(Player player) {
        return Main.getInstance().getPlayerData(player);
    }

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final RunMethods runTracker = Main.getInstance().generateRunMethods();
    private final RunMethods runTracker2 = Main.getInstance().generateRunMethods();

    private final Set<UUID> gheadCooldown = new HashSet<>();
    private final Map<UUID, Integer> insuranceTimer = new HashMap<>();
    private final Map<UUID, Integer> insuranceCooldown = new HashMap<>();
    private final Map<UUID, Integer> lavaExistTimer = new HashMap<>();
    private final Map<UUID, Block> placedLava = new HashMap<>();
    private final Map<UUID, Material> previousLavaBlock = new HashMap<>();
    private final Map<UUID, Integer> strengthChaining = new HashMap<>();
    private final Map<UUID, Integer> strengthChainingTimer = new HashMap<>();
    private final Map<UUID, UUID> spammerShotIdentifier = new HashMap<>();

    private final ItemStack bountyHunterItem = zl.itemBuilder(GOLDEN_LEGGINGS, 1, null, Collections.singletonList("§7Perk item"), true);
    private final ItemStack minemanPickaxeItem = zl.itemBuilder(DIAMOND_PICKAXE, 1, null, Collections.singletonList("§7Perk item"),
            new Enchantment[] {Enchantment.DIG_SPEED}, new Integer[] {4}, true, true);
    private final ItemStack minemanCobblestoneItem = zl.itemBuilder(COBBLESTONE, 24, null, Collections.singletonList("§7Perk item"));
    private final ItemStack safetyFirstItem = zl.itemBuilder(CHAINMAIL_HELMET, 1, null, Collections.singletonList("§7Perk item"));
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

        for (ItemStack item : inv.all(ARROW).values()) arrowCount += item.getAmount();

        strengthChaining.remove(p.getUniqueId());

        if (insuranceTimer.get(p.getUniqueId()) != null) Bukkit.getScheduler().cancelTask(insuranceTimer.get(p.getUniqueId()));
        if (insuranceCooldown.get(p.getUniqueId()) != null) Bukkit.getScheduler().cancelTask(insuranceCooldown.get(p.getUniqueId()));

        insuranceTimer.remove(p.getUniqueId());
        insuranceCooldown.remove(p.getUniqueId());

        removeAll(inv, gapple);

        for (ItemStack items : inv.all(PLAYER_HEAD).values()) {
            if (zl.itemCheck(items) && items.getItemMeta().getDisplayName().equals("§6Golden Head")) {
                inv.remove(items);
            }
        }

        if (!inv.contains(IRON_SWORD) && inv.contains(DIAMOND_SWORD) && !pData.hasPerkEquipped(BARBARIAN)) {
            inv.addItem(zl.itemBuilder(IRON_SWORD, 1));
        }

        if (!inv.contains(zl.itemBuilder(BOW, 1))) inv.addItem(zl.itemBuilder(BOW, 1));

        if (pData.hasPerkEquipped(Perks.FISHING_ROD)) {
            if (!inv.contains(fishingRodItem)) inv.addItem(fishingRodItem);
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

        if (pData.hasPerkEquipped(SAFETY_FIRST)) {
            if (!zl.itemCheck(inv.getHelmet()) || inv.getHelmet().getType() == LEATHER_HELMET) {
                inv.setHelmet(safetyFirstItem);
            }
        } else {
            removeAll(inv, safetyFirstItem);
        }

        if (pData.hasPerkEquipped(MINEMAN)) {
            if (!inv.contains(minemanPickaxeItem)) inv.addItem(minemanPickaxeItem);

            if (!inv.contains(minemanCobblestoneItem)) {
                if (inv.first(COBBLESTONE) != -1) {
                    int slot = inv.first(COBBLESTONE);

                    removeAll(inv, minemanCobblestoneItem);
                    inv.setItem(slot, minemanCobblestoneItem);
                } else {
                    inv.addItem(minemanCobblestoneItem);
                }
            }
        } else {
            removeAll(inv, minemanPickaxeItem);
            removeAll(inv, minemanCobblestoneItem);
        }

        if (!pData.hasPerkEquipped(LUCKY_DIAMOND)) {
            if (isLuckyDiamondItem(inv.getHelmet())) inv.setHelmet(new ItemStack(AIR));
            if (isLuckyDiamondItem(inv.getChestplate())) inv.setChestplate(new ItemStack(AIR));
            if (isLuckyDiamondItem(inv.getLeggings())) inv.setLeggings(new ItemStack(AIR));
            if (isLuckyDiamondItem(inv.getBoots())) inv.setBoots(new ItemStack(AIR));

            for (ItemStack item : inv.getStorageContents()) {
                if (isLuckyDiamondItem(item)) inv.remove(item);
            }
        }

        if (pData.hasPerkEquipped(BOUNTY_HUNTER)) {
            if (!inv.contains(bountyHunterItem)) {
                if (!zl.itemCheck(inv.getLeggings()) || inv.getLeggings().getType() == CHAINMAIL_LEGGINGS || inv.getLeggings().getType() == IRON_LEGGINGS) {
                    inv.setLeggings(bountyHunterItem);
                }
            }
        } else {
            removeAll(inv, bountyHunterItem);
            if (zl.itemCheck(inv.getLeggings()) && inv.getLeggings().equals(bountyHunterItem)) inv.setLeggings(zl.itemBuilder(CHAINMAIL_LEGGINGS, 1));
        }

        if (arrowCount < 32 && arrowCount != 0) {
            inv.addItem(new ItemStack(ARROW, 32 - arrowCount));
        } else if (arrowCount == 0 && !zl.itemCheck(inv.getItem(8))) {
            inv.setItem(8, new ItemStack(ARROW, 32));
        } else if (arrowCount == 0) {
            inv.addItem(new ItemStack(ARROW, 32));
        }
    }

    private boolean isLuckyDiamondItem(ItemStack item) {
        return zl.itemCheck(item)
                && item.getItemMeta() != null
                && item.getItemMeta().getLore() != null
                && item.getItemMeta().getLore().contains("§7Perk item")
                && item.getType().name().contains("DIAMOND")
                && !item.getType().name().contains("PICKAXE");
    }

    public double getPerkDamageBoost(Player damager, Player damaged) {
        double boost = 0;

        if (getStrengthChaining(damager)[0] != null) boost += 0.08 * getStrengthChaining(damager)[0];

        if (pData(damager).hasPerkEquipped(Perks.BOUNTY_HUNTER) && zl.itemCheck(damager.getInventory().getLeggings())
           && damager.getInventory().getLeggings().getType() == Material.GOLDEN_LEGGINGS) {
            boost += Math.floor((double) pData(damaged).getBounty() / 100) / 100;
        }
        return boost;
    }

    public double getPerkDamageReduction(Player damaged) {
        double reduction = 0;

        reduction += getGladiatorDamageReduction(damaged);

        return reduction;
    }

    public double getGladiatorDamageReduction(Player player) {
        double reduction = 0;
        int nearbyPlayers = 0;

        if (pData(player).hasPerkEquipped(GLADIATOR)) {
            for (Entity entity : player.getNearbyEntities(12, 12, 12)) {
                if (zl.playerCheck(entity)) nearbyPlayers++;
            }

            if (nearbyPlayers >= 3 && nearbyPlayers <= 10) {
                reduction += nearbyPlayers * 0.03;
            } else if (nearbyPlayers > 10) {
                reduction += 0.3;
            }
        }
        return reduction;
    }

    /**
     *@Returns an array where [0] is the level of strength and [1] is the timer
     */
    public Integer[] getStrengthChaining(Player p) {
        return new Integer[] {strengthChaining.get(p.getUniqueId()), strengthChainingTimer.get(p.getUniqueId())};
    }

    public boolean hasBeenShotBySpammer(Player damager, Player damaged) {
        return spammerShotIdentifier.containsKey(damager.getUniqueId())
                && spammerShotIdentifier.get(damager.getUniqueId()) == damaged.getUniqueId()
                && pData(damager).hasPerkEquipped(SPAMMER);
    }

    private void removeAll(PlayerInventory inventory, ItemStack item) {
        for (ItemStack items : inventory.all(item.getType()).values()) {
            if (items.isSimilar(item)) inventory.remove(items);
        }
    }

    private boolean containsLessThan(int amount, ItemStack item, Inventory inv) {
        int count = 0;

        if (item.getType() == PLAYER_HEAD) {
            for (ItemStack item2 : inv.all(PLAYER_HEAD).values()) {
                if (zl.itemCheck(item2) && item2.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                    count += item2.getAmount();
                }
            }
            return count < amount;
        }

        for (ItemStack invItem : inv.all(item.getType()).values()) {
            if (zl.itemCheck(invItem) && invItem.isSimilar(item)) {
                count += invItem.getAmount();
            }
        }
        return count < amount;
    }

    private void determineKillRewards(Player killer, Player dead) {
        PlayerInventory inv = killer.getInventory();
        boolean doHealingItem = true;

        if (pData(killer).hasPerkEquipped(VAMPIRE)) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 160, 0, false, false));
            doHealingItem = false;
        }

        if (pData(killer).hasPerkEquipped(RAMBO)) {

            doHealingItem = false;
        }

        if (doHealingItem && pData(killer).hasPerkEquipped(OLYMPUS)) {

            doHealingItem = false;
        }

        if (doHealingItem && pData(killer).hasPerkEquipped(GOLDEN_HEADS)) {
            if (containsLessThan(2, goldenHeadItem, inv)) {
                if (inv.first(PLAYER_HEAD) != -1) {
                    inv.getItem(inv.first(PLAYER_HEAD)).setAmount(inv.getItem(inv.first(PLAYER_HEAD)).getAmount() + 1);
                } else {
                    inv.addItem(goldenHeadItem);
                }
            }
            doHealingItem = false;
        }

        if (doHealingItem && containsLessThan(2, gapple, inv)) {
            inv.addItem(gapple);
        }

        if (pData(killer).hasPerkEquipped(MINEMAN) && containsLessThan(64, minemanCobblestoneItem, inv)) {
            ItemStack item = new ItemStack(minemanCobblestoneItem);
            item.setAmount(3);
            inv.addItem(item);
        }

        if (pData(killer).hasPerkEquipped(LUCKY_DIAMOND)) {
            EquipmentSlot[] slotsToCheck = {HEAD, CHEST, LEGS, FEET};
            PlayerInventory deadInv = dead.getInventory();

            for (EquipmentSlot slot : slotsToCheck) {
                ItemStack slotItem = deadInv.getItem(slot);

                if (zl.itemCheck(slotItem) && slotItem.getType().name().contains("IRON") && new Random().nextInt(100) < 30) {
                    Material diamondType = Material.getMaterial(new StringBuilder(slotItem.getType().name()).replace(0, 4, "DIAMOND").toString());
                    ItemStack diamondItem = zl.itemBuilder(diamondType, 1, null, Collections.singletonList("§7Perk item"), true);
                    String stringedType = new StringBuilder(diamondType.name().toLowerCase(Locale.ROOT))
                            .replace(7, 8, " ")
                            .replace(0, 1, "D")
                            .replace(8, 9, String.valueOf(diamondType.name().charAt(8)))
                            .toString();

                    if (!zl.itemCheck(inv.getItem(slot)) || weightCheck(inv.getItem(slot))) {
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
    }

    private boolean weightCheck(ItemStack item) {
        return item.getType().name().contains("IRON") || item.getType().name().contains("CHAINMAIL");
    }

    @EventHandler
    public void onAttackAndKill(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();
        Player damaged;
        Player damager;
        boolean damageCauseArrow = false;

        if (zl.spawnCheck(damagedEntity.getLocation()) || zl.spawnCheck(damagerEntity.getLocation())) return;
        if (zl.playerCheck(damagedEntity)) damaged = (Player) damagedEntity; else return;

        if (damagerEntity instanceof Arrow && ((Arrow) damagerEntity).getShooter() instanceof Player) {
            damager = (Player) ((Arrow) damagerEntity).getShooter();
            damageCauseArrow = true;
        } else if (zl.playerCheck(damagerEntity)) {
            damager = (Player) damagerEntity;
        } else {
            return;
        }

        UUID damagedUUID = damaged.getUniqueId();
        UUID damagerUUID = damager.getUniqueId();
        double finalDMG = e.getFinalDamage();
        double damagedHP = damaged.getHealth();

        if (pData(damager).hasPerkEquipped(VAMPIRE)) {
            if (damageCauseArrow && ((Arrow) damagerEntity).isCritical()) {
                damager.setHealth(Math.min(damager.getHealth() + 3, damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
            } else {
                damager.setHealth(Math.min(damager.getHealth() + 1, damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
            }
        }

        if (e.getCause() == DamageCause.PROJECTILE && pData(damager).hasPerkEquipped(SPAMMER) && damageCauseArrow) {
            damager.getInventory().addItem(new ItemStack(ARROW, 3));
            spammerShotIdentifier.put(damager.getUniqueId(), damaged.getUniqueId());
        }

        if (pData(damaged).hasPerkEquipped(INSURANCE) && !insuranceTimer.containsKey(damagedUUID) && !insuranceCooldown.containsKey(damaged.getUniqueId())) {
            BukkitTask insuranceRunnable1 = new BukkitRunnable() {
                @Override
                public void run() {
                    insuranceTimer.remove(damagedUUID);

                    BukkitTask insuranceRunnable2 = new BukkitRunnable() {
                        @Override
                        public void run() {
                            insuranceCooldown.remove(damagedUUID);
                        }
                    }.runTaskLater(Main.getInstance(), 400);

                    insuranceCooldown.put(damagedUUID, insuranceRunnable2.getTaskId());
                }
            }.runTaskLater(Main.getInstance(), 40);

            insuranceTimer.put(damagedUUID, insuranceRunnable1.getTaskId());
        }

        if (e.getCause() != DamageCause.FALL && (damagedHP - finalDMG) <= 0) {
            if (pData(damaged).hasPerkEquipped(INSURANCE) && insuranceTimer.containsKey(damagedUUID) && !insuranceCooldown.containsKey(damagedUUID)) {
                e.setDamage(0);
                damaged.setHealth(damaged.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                damaged.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 2, false, false, true));

                for (Player player : Arrays.asList(damaged, damager)) {
                    player.spawnParticle(Particle.HEART, damaged.getLocation(), 20, 0.3, 0.6, 0.3, 0);
                    player.playSound(damaged.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.25F, 2F);

                    new BukkitRunnable() {
                        double i = 1;

                        @Override
                        public void run() {
                            player.playSound(damaged.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5F, (float) i);
                            i += 0.2;
                            if (i >= 2) cancel();
                        }
                    }.runTaskTimer(Main.getInstance(), 0, 1);
                }

                BukkitTask insuranceRunnable3 = new BukkitRunnable() {
                    @Override
                    public void run() {
                        insuranceCooldown.remove(damagedUUID);
                    }
                }.runTaskLater(Main.getInstance(), 400);

                damaged.sendMessage("§a§lINSURANCE! §7Triggered against " + zl.getColorBracketAndLevel(damagerUUID.toString()) + " §7" + damager.getName());
                insuranceCooldown.put(damagedUUID, insuranceRunnable3.getTaskId());
                return;
            }

            determineKillRewards(damager, damaged);

            if (pData(damager).hasPerkEquipped(STRENGTH_CHAINING)) {
                if (getStrengthChaining(damager)[0] == null) {
                    strengthChaining.put(damagerUUID, 1);
                } else if (getStrengthChaining(damager)[0] != 5) {
                    strengthChaining.put(damagerUUID, getStrengthChaining(damager)[0] + 1);
                }

                if (runTracker2.hasID(damagerUUID)) runTracker2.stop(damagerUUID);

                strengthChainingTimer.put(damagerUUID, 7);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!runTracker2.hasID(damagerUUID)) runTracker2.setID(damagerUUID, getTaskId());

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (!zl.playerCheck(entity)) return;
        if (zl.spawnCheck(entity.getLocation())) return;

        Player p = (Player) e.getEntity();
        double finalDMG = e.getFinalDamage();
        double currentHP = p.getHealth();

        if (e.getCause() != DamageCause.FALL && (currentHP - finalDMG <= 0)) perkReset(p);
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
                p.getInventory().setItemInMainHand(new ItemStack(AIR));
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
        Player p = e.getPlayer();
        Block block = e.getBlock();

        if (zl.spawnCheck(block.getLocation())) return;
        if (block.getType() == LAVA || block.getType() == WATER) return;

        if (e.getBucket() == Material.LAVA_BUCKET) {
            previousLavaBlock.put(p.getUniqueId(), e.getBlock().getType());
            placedLava.put(p.getUniqueId(), block);
            block.setType(LAVA);

             new BukkitRunnable() {
                @Override
                public void run() {
                    if (!runTracker.hasID(p.getUniqueId())) runTracker.setID(p.getUniqueId(), getTaskId());

                    if (block.getType() != LAVA) {
                        lavaExistTimer.put(p.getUniqueId(), 0);
                        runTracker.stop(p.getUniqueId());
                    }

                    lavaExistTimer.putIfAbsent(p.getUniqueId(), 0);
                    lavaExistTimer.put(p.getUniqueId(), lavaExistTimer.get(p.getUniqueId()) + 1);

                    if (lavaExistTimer.get(p.getUniqueId()) == 240) {
                        lavaExistTimer.put(p.getUniqueId(), 0);
                        block.setType(previousLavaBlock.get(p.getUniqueId()));
                        previousLavaBlock.remove(p.getUniqueId());
                        lavaExistTimer.remove(p.getUniqueId());
                        placedLava.remove(p.getUniqueId());
                        runTracker.stop(p.getUniqueId());
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

        if (e.getBucket() != BUCKET) return;

        if (block.getType() == LAVA && runTracker.getID(p.getUniqueId()) != null && placedLava.containsValue(block)) {
            block.setType(previousLavaBlock.get(p.getUniqueId()));
            previousLavaBlock.remove(p.getUniqueId());
            placedLava.remove(p.getUniqueId());
            runTracker.stop(p.getUniqueId());
            p.getInventory().setItemInMainHand(lavaBucketItem);
        }

        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDrop(PlayerDropItemEvent e) {
        ItemMeta meta = e.getItemDrop().getItemStack().getItemMeta();

        if (meta != null && meta.getLore() != null && meta.getLore().contains("§7Perk item")) {
            e.getPlayer().sendMessage("§c§lNOPE! §7You cannot drop this item!");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void noGheadOnHead1(InventoryClickEvent e) {
        if (!zl.itemCheck(e.getCursor()) || e.getCursor().getType() != PLAYER_HEAD) return;
        if (e.getSlotType() == InventoryType.SlotType.ARMOR) e.setCancelled(true);
    }

    @EventHandler
    public void noGheadOnHead2(InventoryDragEvent e) {
        if (!zl.itemCheck(e.getCursor()) || e.getCursor().getType() != PLAYER_HEAD) return;

        for (Integer slot : e.getRawSlots()) {
            if (e.getView().getSlotType(slot) == InventoryType.SlotType.ARMOR) {
                e.setCancelled(true);
                return;
            }
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
        spammerShotIdentifier.remove(uuid);
        perkReset(e.getPlayer());
    }
}










