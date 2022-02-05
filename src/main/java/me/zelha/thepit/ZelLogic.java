package me.zelha.thepit;

import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.NPCs;
import me.zelha.thepit.zelenums.Worlds;
import net.minecraft.network.protocol.game.PacketPlayOutCollect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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

import static org.bukkit.Material.*;

public class ZelLogic {//zel

    //split these methods up into zellogic and zelutils next refactor

    public boolean playerCheck(Player player) {return player != null && player.isValid();}
    public boolean playerCheck(Entity entity) {return entity != null && entity.isValid() && entity instanceof Player;}
    public boolean blockCheck(Block block) {return block != null && block.getType() != Material.AIR;}
    public boolean itemCheck(ItemStack item) {return item != null && item.getType() != Material.AIR;}

    public ItemStack headItemBuilder(String playerName, int count, @Nullable String displayName, @Nullable List<String> lore) {
        ItemStack item = null;
        Block block = Bukkit.getWorld("world").getBlockAt(13131313, 0, 13131313);
        block.setType(PLAYER_HEAD);
        Skull state = (Skull) block.getState();
        state.setOwner(playerName);
        state.update();

        for (ItemStack blockItem : block.getDrops()) {
            item = blockItem;
        }

        ItemMeta meta = item.getItemMeta();

        if (displayName != null) meta.setDisplayName(displayName);
        if (lore != null) meta.setLore(lore);

        item.setAmount(count);
        item.setItemMeta(meta);
        block.setType(AIR);
        return item;
    }

    /**
     * note: should handle it fine when "effects" is null, if it's not necessary
     */
    public ItemStack potionItemBuilder(Color color, @Nullable List<PotionEffect> effects, int count, @Nullable String name, @Nullable List<String> lore) {
        ItemStack item = new ItemStack(POTION, count);
        PotionMeta meta = (PotionMeta) item.getItemMeta();

        if (name != null) meta.setDisplayName(name);
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

    public ItemStack itemBuilder(Material material, int count) {
        ItemStack item = new ItemStack(material, count);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setUnbreakable(true);
        item.setItemMeta(itemMeta);

        return item;
    }

    public ItemStack itemBuilder(Material material, int count, @Nullable String name, @Nullable List<String> lore) {
        ItemStack item = new ItemStack(material, count);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setUnbreakable(true);

        if (name != null) itemMeta.setDisplayName(name);
        if (lore != null) itemMeta.setLore(lore);

        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);

        return item;
    }

    public ItemStack itemBuilder(Material material, int count, @Nullable String name, @Nullable List<String> lore, Boolean showUnbreakable) {
        ItemStack item = itemBuilder(material, count, name, lore);
        ItemMeta itemMeta = item.getItemMeta();

        if (showUnbreakable) itemMeta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(itemMeta);

        return item;
    }

    public ItemStack itemBuilder(Material material, int count, @Nullable String name, @Nullable List<String> lore, @Nullable Enchantment[] enchants, Integer[] enchantTiers, Boolean showEnchants, Boolean showUnbreakable) {
        ItemStack item = itemBuilder(material, count, name, lore, showUnbreakable);
        ItemMeta itemMeta = item.getItemMeta();

        if (enchants != null) {
            for (int i = 0; i <= enchants.length - 1; i++) {
                itemMeta.addEnchant(enchants[i], enchantTiers[i], true);
            }
        }

        if (!showUnbreakable) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(itemMeta);

        return item;
    }

    public int firstEmptySlot(PlayerInventory inv) {
        ItemStack[] invItems = inv.getStorageContents();

        for (int i = 9; i < 36; i++) {
            if (!itemCheck(invItems[i])) return i;
        }

        for (int i = 0; i < 9; i++) {
            if (!itemCheck(invItems[i])) return i;
        }
        return -1;
    }

    public BoundingBox noObstructions(Worlds world, NPCs type) {//nested switch statements are so cursed. i love them.

        switch(world) {
            case ELEMENTALS:
            case CORALS:
            case SEASONS:
                switch (type) {
                    case ITEMS:
                        return BoundingBox.of(new Location(Bukkit.getWorld(world.toString()), 2.5, 114, 12.5), 1, 2.5, 1);
                    case UPGRADES:
                        return BoundingBox.of(new Location(Bukkit.getWorld(world.toString()), -1.5, 114, 12.5), 1, 2.5, 1);
                    case PRESTIGE://below not added yet
                        break;
                    case QUEST:
                        break;
                    case STATS:
                        break;
                }
                break;
            case CASTLE:
                switch (type) {
                    case ITEMS:
                        return BoundingBox.of(new Location(Bukkit.getWorld(world.toString()), 2.5, 95, 12.5), 1, 2.5, 1);
                    case UPGRADES:
                        return BoundingBox.of(new Location(Bukkit.getWorld(world.toString()), -1.5, 95, 12.5), 1, 2.5, 1);
                    case PRESTIGE://below not added yet
                        break;
                    case QUEST:
                        break;
                    case STATS:
                        break;
                }
                break;
            case GENESIS:
                switch (type) {
                    case ITEMS:
                        return BoundingBox.of(new Location(Bukkit.getWorld(world.toString()), 2.5, 86, 16.5), 1, 2.5, 1);
                    case UPGRADES:
                        return BoundingBox.of(new Location(Bukkit.getWorld(world.toString()), -1.5, 86, 16.5), 1, 2.5, 1);
                    case PRESTIGE://below not added yet
                        break;
                    case QUEST:
                        break;
                    case STATS:
                        break;
                }
                break;
        }

        return new BoundingBox();
    }

    public void spawnHologramIfAbsent(String name, Location location, Player player) {
        List<Entity> entityList = location.getWorld().getEntities();

        for (Entity entity : entityList) {
            if (entity.getLocation().equals(location) && entity.getName().equals(name)) {
                player.sendMessage("§cHologram " + name + " §cis not absent.");
                return;
            }
        }

        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        hologram.setVisible(false);
        hologram.setBasePlate(false);
        hologram.setMarker(true);
        hologram.setCustomName(name);
        hologram.setCustomNameVisible(true);
        hologram.setInvulnerable(true);
        hologram.setSilent(true);
        hologram.setPersistent(true);
        hologram.setGravity(false);
        hologram.addScoreboardTag("z-entity");
        player.sendMessage("§aHologram " + name + " §asuccessfully spawned!");
    }

    /**
     * note: highly recommended to make sure the event that runs this cant be fired again on the same entity, or else it will probably cause issues <p>
     * example: setting pickupDelay on an Item to 99999999
     */
    public void fakePickup(Player player, Entity entity, int radius) {
        CraftPlayer craftP = (CraftPlayer) player;

        craftP.getHandle().b.sendPacket(new PacketPlayOutCollect(entity.getEntityId(), player.getEntityId(), 1));

        for (Entity nearbyEntity : player.getNearbyEntities(radius, radius, radius)) {
            if (nearbyEntity instanceof Player) {
                craftP = (CraftPlayer) nearbyEntity;
                craftP.getHandle().b.sendPacket(new PacketPlayOutCollect(entity.getEntityId(), player.getEntityId(), 1));
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

    public String toRoman(int number) {
        final TreeMap<Integer, String> romanizer = new TreeMap<Integer, String>();
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

        int nearestRoman = romanizer.floorKey(number);

        if (number == nearestRoman) {
            return romanizer.get(number);
        }

        return romanizer.get(nearestRoman) + toRoman(number - nearestRoman);
    }

    public String getFancyGoldString(double gold) {
        BigDecimal roundedGold = BigDecimal.valueOf(gold).setScale(2, RoundingMode.DOWN);

        return new DecimalFormat("#,##0.00").format(roundedGold);
    }

    public String getFancyGoldString(int gold) {
        return new DecimalFormat("#,##0").format(gold);
    }

    public String getColorStatus(String uuid) {

        PlayerData pData = Main.getInstance().getPlayerData(uuid);

        if (pData.getStatus().equals("idling")) {
            return "§aIdling";
        } else if (pData.getStatus().equals("fighting")) {
            return "§cFighting";
        } else if (pData.getStatus().equals("bountied")) {
            return "§cBountied";
        }
        return ChatColor.translateAlternateColorCodes('&', pData.getStatus());
    }

    public int maxXPReq(String uuid) {

        PlayerData pData = Main.getInstance().getPlayerData(uuid);

        switch (pData.getPrestige()) {
            case 0:
                return baseMaxXPReq(uuid);
            case 1:
                return (int) Math.round(baseMaxXPReq(uuid) * 1.1);
            case 2:
                return (int) Math.round(baseMaxXPReq(uuid) * 1.2);
            case 3:
                return (int) Math.round(baseMaxXPReq(uuid) * 1.3);
            case 4:
                return (int) Math.round(baseMaxXPReq(uuid) * 1.4);
            case 5:
                return (int) Math.round(baseMaxXPReq(uuid) * 1.5);
            case 6:
                return (int) Math.round(baseMaxXPReq(uuid) * 1.75);
            case 7:
                return baseMaxXPReq(uuid) * 2;
            case 8:
                return (int) Math.round(baseMaxXPReq(uuid) * 2.5);
            case 9:
                return baseMaxXPReq(uuid) * 3;
            case 10:
                return baseMaxXPReq(uuid) * 4;
            case 11:
                return baseMaxXPReq(uuid) * 5;
            case 12:
                return baseMaxXPReq(uuid) * 6;
            case 13:
                return baseMaxXPReq(uuid) * 7;
            case 14:
                return baseMaxXPReq(uuid) * 8;
            case 15:
                return baseMaxXPReq(uuid) * 9;
            case 16:
                return baseMaxXPReq(uuid) * 10;
            case 17:
                return baseMaxXPReq(uuid) * 12;
            case 18:
                return baseMaxXPReq(uuid) * 14;
            case 19:
                return baseMaxXPReq(uuid) * 16;
            case 20:
                return baseMaxXPReq(uuid) * 18;
            case 21:
                return baseMaxXPReq(uuid) * 20;
            case 22:
                return baseMaxXPReq(uuid) * 24;
            case 23:
                return baseMaxXPReq(uuid) * 28;
            case 24:
                return baseMaxXPReq(uuid) * 32;
            case 25:
                return baseMaxXPReq(uuid) * 36;
            case 26:
                return baseMaxXPReq(uuid) * 40;
            case 27:
                return baseMaxXPReq(uuid) * 45;
            case 28:
                return baseMaxXPReq(uuid) * 50;
            case 29:
                return baseMaxXPReq(uuid) * 75;
            case 30:
                return baseMaxXPReq(uuid) * 100;
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
                return baseMaxXPReq(uuid) * 101;
            case 36:
                return baseMaxXPReq(uuid) * 150;
            case 37:
                return baseMaxXPReq(uuid) * 250;
            case 38:
                return baseMaxXPReq(uuid) * 400;
            case 39:
                return baseMaxXPReq(uuid) * 650;
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
                return baseMaxXPReq(uuid) * 1000;
            default:
                return 1313131313;//fun
        }
    }

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

    private String getColorLevel(int level) {
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

    public String getColorBracketAndLevel(String uuid) {

        PlayerData pData = Main.getInstance().getPlayerData(uuid);

        if (pData.getPrestige() < 1) {
            return "§7[" + getColorLevel(uuid) + "§7]";
        } else if (pData.getPrestige() < 5) {
            return "§9[" + getColorLevel(uuid) + "§9]";
        } else if (pData.getPrestige() < 10) {
            return "§e[" + getColorLevel(uuid) + "§e]";
        } else if (pData.getPrestige() < 15) {
            return "§6[" + getColorLevel(uuid) + "§6]";
        } else if (pData.getPrestige() < 20) {
            return "§c[" + getColorLevel(uuid) + "§c]";
        } else if (pData.getPrestige() < 25) {
            return "§5[" + getColorLevel(uuid) + "§5]";
        } else if (pData.getPrestige() < 30) {
            return "§d[" + getColorLevel(uuid) + "§d]";
        } else if (pData.getPrestige() < 35) {
            return "§f[" + getColorLevel(uuid) + "§f]";
        } else if (pData.getPrestige() < 40) {
            return "§b[" + getColorLevel(uuid) + "§b]";
        } else if (pData.getPrestige() < 45) {
            return "§2[" + getColorLevel(uuid) + "§2]";
        } else if (pData.getPrestige() < 50) {
            return "§3[" + getColorLevel(uuid) + "§3]";
        } else if (pData.getPrestige() == 50) {
            return "§4[" + getColorLevel(uuid) + "§4]";
        }
        return "§5§l[§5§k|" + getColorLevel(uuid) + "§5§k|§5§l]";
    }

    private String getColorLevel(String uuid) {

        PlayerData pData = Main.getInstance().getPlayerData(uuid);

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

    private int baseMaxXPReq(String uuid) {

        PlayerData pData = Main.getInstance().getPlayerData(uuid);

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
}
