package me.zelha.thepit.utils;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.TrueDamageEvent;
import me.zelha.thepit.mainpkg.data.DamageLog;
import me.zelha.thepit.mainpkg.data.KillRecap;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.Perks;
import me.zelha.thepit.zelenums.Worlds;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.network.protocol.game.PacketPlayOutCollect;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.TreeMap;

import static me.zelha.thepit.zelenums.Perks.BARBARIAN;
import static me.zelha.thepit.zelenums.Worlds.CASTLE;
import static me.zelha.thepit.zelenums.Worlds.GENESIS;
import static org.bukkit.Material.*;

public class ZelLogic {//zel

    //boolean checks
    /**
     * Checks if the player is != null and valid
     * <p>
     * mostly just used to reduce clutter
     *
     * @param player player to check
     * @return true if player wont cause errors, else false
     */
    public boolean playerCheck(Player player) {
        return player != null && player.isValid();
    }

    /**
     * Checks if the entity is != null, valid, and instanceof Player
     * <p>
     * mostly just used to reduce clutter
     *
     * @param entity entity to check
     * @return true if entity is a player and wont cause errors, else false
     */
    public boolean playerCheck(Entity entity) {
        return entity != null && entity.isValid() && entity instanceof Player;
    }

    /**
     * Checks if the block is != null and if the block's material isn't air
     * mostly just used to reduce clutter
     *
     * @param block block to check
     * @return true if block wont cause errors, else false
     */
    public boolean blockCheck(Block block) {
        return block != null && block.getType() != Material.AIR;
    }

    /**
     * Checks if the item is != null and if the item's material isn't air
     * <p>
     * mostly just used to reduce clutter
     *
     * @param item item to check
     * @return true if item wont cause errors, else false
     */
    public boolean itemCheck(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    /**
     * Checks if the given location is in spawn
     *
     * @param location location to check against
     * @return true if the given location is in spawn, else false
     */
    public boolean spawnCheck(Location location) {
        Worlds world = Worlds.findByName(location.getWorld().getName());
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        double spawnY;

        if (world == GENESIS) {
            spawnY = 90.0;
        } else if (world == CASTLE) {
            spawnY = 105.0;
        } else {
            spawnY = 110.0;
        }

        return BoundingBox.of(new Location(location.getWorld(), 0.0, spawnY, 0.0), 25.0, 15.0, 25.0).contains(x, y, z);
    }
    //boolean checks


    //item builders
    /**
     * Creates a block far outside the loaded world and uses {@link Skull#setOwner(String)},
     * updates the blockstate, gets the item via {@link Block#getDrops()}, and sets the block type to air
     * <p></p>
     * the way this method creates a player head is kindof janky and could cause issues later on, but for now its fine
     *
     * @param playerName name of the player that has the skin you want
     * @param count item stack amount
     * @param displayName custom item name, item is given with normal name if param is null
     * @param lore item lore, nothing if param is null
     * @return a constructed head item with the provided parameters
     */
    public ItemStack headItemBuilder(String playerName, int count, @Nullable String displayName, @Nullable List<String> lore) {
        Block block = Bukkit.getWorld("world").getBlockAt(13131313, 0, 13131313);
        block.setType(PLAYER_HEAD);
        Skull state = (Skull) block.getState();
        state.setOwner(playerName);
        state.update();

        ItemStack item = block.getDrops().iterator().next();
        ItemMeta meta = item.getItemMeta();

        if (displayName != null) meta.setDisplayName(displayName);
        if (lore != null) meta.setLore(lore);

        item.setAmount(count);
        item.setItemMeta(meta);
        block.setType(AIR);
        return item;
    }

    /**
     * Creates a potion with the given parameters
     *
     * @param color color of the potion
     * @param count item stack amount
     * @param displayName  custom item name, item is given with normal name if param is null
     * @param lore item lore, nothing if param is null
     * @param effects effects this potion should have, nothing if null
     * @return a constructed potion item with the provided parameters
     */
    public ItemStack potionItemBuilder(Color color, int count, @Nullable String displayName, @Nullable List<String> lore, @Nullable PotionEffect... effects) {
        ItemStack item = new ItemStack(POTION, count);
        PotionMeta meta = (PotionMeta) item.getItemMeta();

        if (displayName != null) meta.setDisplayName(displayName);
        if (lore != null) meta.setLore(lore);

        meta.setColor(color);
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        if (effects != null) {
            for (PotionEffect effect : effects) {
                meta.addCustomEffect(effect, true);
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates an unbreakable item with the given parameters
     * <p></p>
     * doesnt hide any flags, if you want flags hidden you need to use {@link #itemBuilder(Material, int, String, List)}
     *
     * @param material item material
     * @param count item stack amount
     * @return a constructed item with unbreakable set to true
     */
    public ItemStack itemBuilder(Material material, int count) {
        ItemStack item = new ItemStack(material, count);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setUnbreakable(true);
        item.setItemMeta(itemMeta);

        return item;
    }

    /**
     * Creates an item via {@link #itemBuilder(Material, int)} and adds the extra info to it
     * <p></p>
     * automatically adds itemflags hide_unbreakable and hide_attributes, if you want flags shown and a custom name/lore use {@link #itemBuilder(Material, int, String, List, Boolean)}
     *
     * @param material item material
     * @param count item stack amount
     * @param displayName custom item name, item is given with normal name if param is null
     * @param lore item lore, nothing if param is null
     * @return a constructed item with the given parameters, as well as unbreakable true and itemflags hide_unbreakable and hide_attributes
     */
    public ItemStack itemBuilder(Material material, int count, @Nullable String displayName, @Nullable List<String> lore) {
        ItemStack item = itemBuilder(material, count);
        ItemMeta itemMeta = item.getItemMeta();

        if (displayName != null) itemMeta.setDisplayName(displayName);
        if (lore != null) itemMeta.setLore(lore);

        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);

        return item;
    }

    /**
     * Creates an item via {@link #itemBuilder(Material, int, String, List)} and removes itemflags hide_unbreakable and hide_attributes if boolean showJuicyStuff is true
     *
     * @param material item material
     * @param count item stack amount
     * @param displayName custom item name, item is given with normal name if param is null
     * @param lore item lore, nothing if param is null
     * @param showJuicyStuff removes item flags if true, else does nothing
     * @return a constructed item with the given parameters
     */
    public ItemStack itemBuilder(Material material, int count, @Nullable String displayName, @Nullable List<String> lore, Boolean showJuicyStuff) {
        ItemStack item = itemBuilder(material, count, displayName, lore);
        ItemMeta itemMeta = item.getItemMeta();

        if (showJuicyStuff) {
            itemMeta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            itemMeta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        item.setItemMeta(itemMeta);

        return item;
    }

    /**
     * Creates an item via {@link #itemBuilder(Material, int, String, List, Boolean)} and adds enchants to it
     * <p>
     * enchants and enchantTiers must be ordered so that enchants[0]'s tier is enchantTiers[0]
     *
     * @param material item material
     * @param count item stack amount
     * @param displayName custom item name, item is given with normal name if param is null
     * @param lore item lore, nothing if param is null
     * @param showEnchants adds item flag hide_enchants if false, else does nothing
     * @param showJuicyStuff removes item flags if true, else does nothing
     * @param enchants array of a pair, where the enchant is the key and the level is the value, does nothing if param is null
     * @return a constructed item with the given parameters
     */
    public ItemStack itemBuilder(Material material, int count, @Nullable String displayName, @Nullable List<String> lore, Boolean showEnchants, Boolean showJuicyStuff, Pair<Enchantment, Integer>... enchants) {
        ItemStack item = itemBuilder(material, count, displayName, lore, showJuicyStuff);
        ItemMeta itemMeta = item.getItemMeta();

        for (Pair<Enchantment, Integer> enchantPair : enchants) {
            itemMeta.addEnchant(enchantPair.getKey(), enchantPair.getValue(), true);
        }

        if (!showEnchants) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(itemMeta);

        return item;
    }
    //item builders


    //string makers
    /**
     * creates a roman numeral string of the given number
     *
     * @param number number to romanize
     * @return roman numeral equivalent of given number
     */
    public String toRoman(int number) {
        final TreeMap<Integer, String> romanizer = new TreeMap<>();
        romanizer.putIfAbsent(1000, "M");
        romanizer.putIfAbsent(900, "CM");
        romanizer.putIfAbsent(500, "D");
        romanizer.putIfAbsent(400, "CD");
        romanizer.putIfAbsent(100, "C");
        romanizer.putIfAbsent(90, "XC");
        romanizer.putIfAbsent(50, "L");
        romanizer.putIfAbsent(40, "XL");
        romanizer.putIfAbsent(10, "X");
        romanizer.putIfAbsent(9, "VX");
        romanizer.putIfAbsent(5, "V");
        romanizer.putIfAbsent(4, "IV");
        romanizer.putIfAbsent(1, "I");
        romanizer.putIfAbsent(0, "none");

        int nearestRoman = romanizer.floorKey(number);

        if (number == nearestRoman) return romanizer.get(number);
        if (number > 1000000) return String.valueOf(number);

        return romanizer.get(nearestRoman) + toRoman(number - nearestRoman);
    }

    /**
     * formats the given double so that 5000 would be 5,000.00
     *
     * @param gold gold to make fancy
     * @return fancy gold string
     */
    public String getFancyGoldString(double gold) {
        return new DecimalFormat("#,##0.00").format(BigDecimal.valueOf(gold).setScale(2, RoundingMode.DOWN));
    }

    /**
     * formats the given int so that 5000 would be 5,000
     *
     * @param number number to make fancy
     * @return fancy number string
     */
    public String getFancyNumberString(int number) {
        return new DecimalFormat("#,##0").format(number);
    }
    //string makers


    //misc stuff
    /**
     * this method checks your inventory first, then your hotbar, so that if you use setItem(zl.firstEmptySlot, item)
     * as opposed to addItem(item) it will go into your inventory even if you have a free hotbar slot
     * <p>
     * for some reason some items do this in regular pit, so i use this method to mimic that
     * <p>
     * can also be used to check if the inventory is full, as it returns -1 if thats the case
     *
     * @param inv inventory to get the first empty slot from
     * @return first empty slot, -1 if inventory is full
     */
    public int firstEmptySlot(PlayerInventory inv) {
        ItemStack[] invItems = inv.getStorageContents();

        for (int i = 9; i < 36; i++) if (!itemCheck(invItems[i])) return i;
        for (int i = 0; i < 9; i++) if (!itemCheck(invItems[i])) return i;

        return -1;
    }

    /**
     * uses NMS to send fake pickup packets to the given player and all players within the given radius
     * <p></p>
     * note: highly recommended to make sure the event that runs this cant be fired again on the same entity, or else it will probably cause issues <p>
     * example: setting pickupDelay on an Item to 99999999
     *
     * @param player player that is meant to pick up the entity
     * @param entity entity to pick up
     * @param radius radius that players should see the pick up animation
     */
    public void fakePickup(Player player, Entity entity, int radius) {
        CraftPlayer craftP = (CraftPlayer) player;
        int amount = (entity instanceof Item) ? ((Item) entity).getItemStack().getAmount() : 1;

        craftP.getHandle().b.sendPacket(new PacketPlayOutCollect(entity.getEntityId(), player.getEntityId(), amount));

        for (Entity nearbyEntity : player.getNearbyEntities(radius, radius, radius)) {
            if (nearbyEntity instanceof Player) {
                craftP = (CraftPlayer) nearbyEntity;
                craftP.getHandle().b.sendPacket(new PacketPlayOutCollect(entity.getEntityId(), player.getEntityId(), amount));
            }
        }

        player.updateInventory();

        new BukkitRunnable() {
            @Override
            public void run() {
                entity.remove();
            }
        }.runTaskLater(Main.getInstance(), 10);
    }

    /**
     * handles inventory item placement for armor
     *
     * @param player player to give item to
     * @param slot slot to put the item in
     * @param item item to give
     */
    public void itemPlacementHandler(Player player, EquipmentSlot slot, ItemStack item) {
        PlayerInventory inventory = player.getInventory();

        if (!itemCheck(inventory.getItem(slot)) || determineWeight(inventory.getItem(slot).getType()) == 0) {
            inventory.setItem(slot, item);
        } else if (determineWeight(inventory.getItem(slot).getType()) < determineWeight(item.getType())) {
            inventory.setItem(firstEmptySlot(inventory), inventory.getItem(slot));
            inventory.setItem(slot, item);
        } else if (!inventory.contains(item.getType())) {
            inventory.setItem(firstEmptySlot(inventory), item);
        }
    }

    /**
     * a replacement for increasing health via {@link Player#setHealth(double)} <p>
     * which calls EntityRegainHealthEvent and acts accordingly, unlike setHealth
     *
     * @param player player to increase health of
     * @param increase HP to increase player's health by
     */
    public void addHealth(Player player, double increase) {
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double heal = increase;

        if (player.getHealth() == maxHealth) return;

        if (player.getHealth() + increase > maxHealth) {
            heal = maxHealth - player.getHealth();
        }

        EntityRegainHealthEvent event = new EntityRegainHealthEvent(player, heal, EntityRegainHealthEvent.RegainReason.CUSTOM);

        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            player.setHealth(heal);
        }
    }
    //misc stuff


    //pit logic
    /**
     * supposed to be called every time items should be reset <p>
     * ex: dying, selecting a perk, etc <p>
     * must be called *after* a perk slot is set, in conditions where that applies
     */
    public void pitReset(Player p) {
        PlayerInventory inv = p.getInventory();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        int arrowCount = 0;

        for (Perks perk : Perks.values()) {
            if (perk.getMethods() != null) perk.getMethods().onReset(p, pData);
        }

        if (pData.getMegastreak().getMethods() != null) pData.getMegastreak().getMethods().onEquip(p);

        pData.setStreak(0);

        for (PotionEffect effects : p.getActivePotionEffects()) {
            p.removePotionEffect(effects.getType());
        }

        for (ItemStack item : inv.all(ARROW).values()) arrowCount += item.getAmount();

        inv.remove(GOLDEN_APPLE);

        if (!inv.contains(IRON_SWORD) && !inv.contains(DIAMOND_SWORD) && !pData.hasPerkEquipped(BARBARIAN)) {
            inv.addItem(itemBuilder(IRON_SWORD, 1));
        }

        if (!inv.contains(itemBuilder(BOW, 1))) inv.addItem(itemBuilder(BOW, 1));

        if (arrowCount < 32 && arrowCount != 0) {
            inv.addItem(new ItemStack(ARROW, 32 - arrowCount));
        } else if (arrowCount == 0 && !itemCheck(inv.getItem(8))) {
            inv.setItem(8, new ItemStack(ARROW, 32));
        } else if (arrowCount == 0) {
            inv.addItem(new ItemStack(ARROW, 32));
        }
    }

    /**
     * pretty self explanatory, deals true damage to the given damagee and handles it accordingly <p>
     * (death methods, damage logs, etc)
     *
     * @param damagee person to be damaged
     * @param damager person who inflicted the damage
     * @param damage damage to deal
     * @param cause cause to show in kill recap
     */
    public void trueDamage(Player damagee, @Nullable Player damager, double damage, String cause) {
        TrueDamageEvent event = new TrueDamageEvent(damagee, damager, damage);

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        damage = event.getDamage();

        boolean willDie = damagee.getHealth() - damage <= 0;

        if (willDie) damage = damagee.getHealth();

        if (damager != null) {
            KillRecap.addDamageLog(damagee, new DamageLog(damagee, damager, false, damage, cause));
            KillRecap.addDamageLog(damager, new DamageLog(damagee, damager, true, damage, cause));
            Main.getInstance().getAssistUtils().addAssist(damagee, damager, damage);
            Main.getInstance().getAttackUtils().startCombatTimer(damagee, damager);
        } else if (cause.equals("§6Lava")) {
            KillRecap.addDamageLog(damagee, new DamageLog(damage, cause, true));
        } else {
            KillRecap.addDamageLog(damagee, new DamageLog(damage, cause, false));
        }

        Bukkit.broadcastMessage(damage + "");//testing line

        if (willDie) {
            damagee.damage(131313);
            return;
        } else {
            damagee.setHealth(damagee.getHealth() - damage);
        }

        if (damager == null) return;

        String bar = "§7" + damagee.getName() + " ";

        StringBuilder barBuilder = new StringBuilder();
        StringBuilder barBuilder2 = new StringBuilder();

        int health = (int) Math.ceil(damagee.getHealth() / 2);
        int healthAfterDmg = (int) Math.floor(Math.max(((damagee.getHealth() / 2D) - (damage / 2D)), 0));
        int maxHealth = (int) damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2;

        for (int i = 0; i < maxHealth; i++) barBuilder.append("❤");

        if (damagee.getAbsorptionAmount() > 0) {
            for (int i = 0; i < (int) Math.ceil(damagee.getAbsorptionAmount() / 2); i++) barBuilder2.append("❤");

            barBuilder2.replace(0, 0, "§e");
        }

        barBuilder.replace(health, health, "§0");
        barBuilder.replace(healthAfterDmg, healthAfterDmg, "§c");
        barBuilder.replace(0, 0, "§4");
        damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(bar + barBuilder + barBuilder2));
    }

    /**
     * uses a bloody massive switch statement to determine the xp requirement of the current level of the player the given UUID is assigned to
     *
     * @param player player to check the max level XP req of
     * @return max XP req for the given player's level based on prestige
     */
    public int maxXPReq(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);

        switch (pData.getPrestige()) {
            case 0:
                return baseMaxXPReq(player);
            case 1:
                return (int) Math.round(baseMaxXPReq(player) * 1.1);
            case 2:
                return (int) Math.round(baseMaxXPReq(player) * 1.2);
            case 3:
                return (int) Math.round(baseMaxXPReq(player) * 1.3);
            case 4:
                return (int) Math.round(baseMaxXPReq(player) * 1.4);
            case 5:
                return (int) Math.round(baseMaxXPReq(player) * 1.5);
            case 6:
                return (int) Math.round(baseMaxXPReq(player) * 1.75);
            case 7:
                return baseMaxXPReq(player) * 2;
            case 8:
                return (int) Math.round(baseMaxXPReq(player) * 2.5);
            case 9:
                return baseMaxXPReq(player) * 3;
            case 10:
                return baseMaxXPReq(player) * 4;
            case 11:
                return baseMaxXPReq(player) * 5;
            case 12:
                return baseMaxXPReq(player) * 6;
            case 13:
                return baseMaxXPReq(player) * 7;
            case 14:
                return baseMaxXPReq(player) * 8;
            case 15:
                return baseMaxXPReq(player) * 9;
            case 16:
                return baseMaxXPReq(player) * 10;
            case 17:
                return baseMaxXPReq(player) * 12;
            case 18:
                return baseMaxXPReq(player) * 14;
            case 19:
                return baseMaxXPReq(player) * 16;
            case 20:
                return baseMaxXPReq(player) * 18;
            case 21:
                return baseMaxXPReq(player) * 20;
            case 22:
                return baseMaxXPReq(player) * 24;
            case 23:
                return baseMaxXPReq(player) * 28;
            case 24:
                return baseMaxXPReq(player) * 32;
            case 25:
                return baseMaxXPReq(player) * 36;
            case 26:
                return baseMaxXPReq(player) * 40;
            case 27:
                return baseMaxXPReq(player) * 45;
            case 28:
                return baseMaxXPReq(player) * 50;
            case 29:
                return baseMaxXPReq(player) * 75;
            case 30:
                return baseMaxXPReq(player) * 100;
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
                return baseMaxXPReq(player) * 101;
            case 36:
                return baseMaxXPReq(player) * 150;
            case 37:
                return baseMaxXPReq(player) * 250;
            case 38:
                return baseMaxXPReq(player) * 400;
            case 39:
                return baseMaxXPReq(player) * 650;
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
                return baseMaxXPReq(player) * 1000;
            default:
                return 1313131313;//fun
        }
    }

    /**
     * Gives the color bracket and level such that prestige 0 level 10 would be §7[§910§7]
     *
     * @param player player to check the color bracket and level of
     * @return the combined form of the prestige bracket and level, colorized
     */
    public String getColorBracketAndLevel(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);

        if (pData.getPrestige() < 1) {
            return "§7[" + getColorLevel(player) + "§7]";
        } else if (pData.getPrestige() < 5) {
            return "§9[" + getColorLevel(player) + "§9]";
        } else if (pData.getPrestige() < 10) {
            return "§e[" + getColorLevel(player) + "§e]";
        } else if (pData.getPrestige() < 15) {
            return "§6[" + getColorLevel(player) + "§6]";
        } else if (pData.getPrestige() < 20) {
            return "§c[" + getColorLevel(player) + "§c]";
        } else if (pData.getPrestige() < 25) {
            return "§5[" + getColorLevel(player) + "§5]";
        } else if (pData.getPrestige() < 30) {
            return "§d[" + getColorLevel(player) + "§d]";
        } else if (pData.getPrestige() < 35) {
            return "§f[" + getColorLevel(player) + "§f]";
        } else if (pData.getPrestige() < 40) {
            return "§b[" + getColorLevel(player) + "§b]";
        } else if (pData.getPrestige() < 45) {
            return "§1[" + getColorLevel(player) + "§1]";
        } else if (pData.getPrestige() < 50) {
            return "§3[" + getColorLevel(player) + "§3]";
        } else if (pData.getPrestige() == 50) {
            return "§4[" + getColorLevel(player) + "§4]";
        }
        return "§5§l[§5§k|" + getColorLevel(player) + "§5§k|§5§l]";
    }

    /**
     * Gives the color bracket and level such that prestige 0 level 10 would be §7[§910§7]
     *
     * @param prestige prestige used to determine the color of brackets
     * @param level level to be put in the string and used to determine the color of said level
     * @return the combined form of the prestige bracket and level, colorized
     */
    public String getColorBracketAndLevel(int prestige, int level) {
        if (prestige < 1) {
            return "§7[" + getColorLevel(level) + "§7]";
        } else if (prestige < 5) {
            return "§9[" + getColorLevel(level) + "§9]";
        } else if (prestige < 10) {
            return "§e[" + getColorLevel(level) + "§e]";
        } else if (prestige < 15) {
            return "§6[" + getColorLevel(level) + "§6]";
        } else if (prestige < 20) {
            return "§c[" + getColorLevel(level) + "§c]";
        } else if (prestige < 25) {
            return "§5[" + getColorLevel(level) + "§5]";
        } else if (prestige < 30) {
            return "§d[" + getColorLevel(level) + "§d]";
        } else if (prestige < 35) {
            return "§f[" + getColorLevel(level) + "§f]";
        } else if (prestige < 40) {
            return "§b[" + getColorLevel(level) + "§b]";
        } else if (prestige < 45) {
            return "§1[" + getColorLevel(level) + "§1]";
        } else if (prestige < 50) {
            return "§3[" + getColorLevel(level) + "§3]";
        } else if (prestige == 50) {
            return "§4[" + getColorLevel(level) + "§4]";
        }
        return "§5§l[§5§k|" + getColorLevel(level) + "§5§k|§5§l]";
    }

    /**
     * Only used in its parent class and {@link me.zelha.thepit.admin.commands.SetLevelCommand}
     *
     * @param level level to put into string and colorize
     * @return colorized level
     */
    public String getColorLevel(int level) {
        if (level < 10) {
            return "§7" + level;
        } else if (level < 20) {
            return "§9" + level;
        } else if (level < 30) {
            return "§3" + level;
        } else if (level < 40) {
            return "§2" + level;
        } else if (level < 50) {
            return "§a" + level;
        } else if (level < 60) {
            return "§e" + level;
        } else if (level < 70) {
            return "§6§l" + level;
        } else if (level < 80) {
            return "§c§l" + level;
        } else if (level < 90) {
            return "§4§l" + level;
        } else if (level < 100) {
            return "§5§l" + level;
        } else if (level < 110) {
            return "§d§l" + level;
        } else if (level < 120) {
            return "§f§l" + level;
        } else if (level == 120) {
            return "§b§l" + level;
        }
        return "§5§l" + level;
    }
    //pit logic


    //private stuff
    private String getColorLevel(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);

        if (pData.getLevel() < 10) {
            return "§7" + pData.getLevel();
        } else if (pData.getLevel() < 20) {
            return "§9" + pData.getLevel();
        } else if (pData.getLevel() < 30) {
            return "§3" + pData.getLevel();
        } else if (pData.getLevel() < 40) {
            return "§2" + pData.getLevel();
        } else if (pData.getLevel() < 50) {
            return "§a" + pData.getLevel();
        } else if (pData.getLevel() < 60) {
            return "§e" + pData.getLevel();
        } else if (pData.getLevel() < 70) {
            return "§6§l" + pData.getLevel();
        } else if (pData.getLevel() < 80) {
            return "§c§l" + pData.getLevel();
        } else if (pData.getLevel() < 90) {
            return "§4§l" + pData.getLevel();
        } else if (pData.getLevel() < 100) {
            return "§5§l" + pData.getLevel();
        } else if (pData.getLevel() < 110) {
            return "§d§l" + pData.getLevel();
        } else if (pData.getLevel() < 120) {
            return "§f§l" + pData.getLevel();
        } else if (pData.getLevel() == 120) {
            return "§b§l" + pData.getLevel();
        }
        return "§5§l" + pData.getLevel();
    }

    private int baseMaxXPReq(Player player) {
        PlayerData pData = Main.getInstance().getPlayerData(player);

        if (pData.getLevel() < 10) {
            return 15;
        } else if (pData.getLevel() < 20) {
            return 30;
        } else if (pData.getLevel() < 30) {
            return 50;
        } else if (pData.getLevel() < 40) {
            return 75;
        } else if (pData.getLevel() < 50) {
            return 125;
        } else if (pData.getLevel() < 60) {
            return 300;
        } else if (pData.getLevel() < 70) {
            return 600;
        } else if (pData.getLevel() < 80) {
            return 800;
        } else if (pData.getLevel() < 90) {
            return 900;
        } else if (pData.getLevel() < 100) {
            return 1000;
        } else if (pData.getLevel() < 110) {
            return 1200;
        } else if (pData.getLevel() < 120) {
            return 1500;
        }
        return 0;
    }

    private int determineWeight(Material type) {
        if (type == LEATHER_HELMET) return 0;

        for (Material material : new Material[] {CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS}) {
            if (material == type) return 1;
        }

        for (Material material : new Material[] {IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS}) {
            if (material == type) return 2;
        }

        for (Material material : new Material[] {DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS}) {
            if (material == type) return 3;
        }

        return 13;
    }
}
