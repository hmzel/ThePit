package me.zelha.thepit.upgrades.permanent.villager;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.upgrades.permanent.perks.PerkListenersAndUtils;
import me.zelha.thepit.zelenums.NPCs;
import me.zelha.thepit.zelenums.Passives;
import me.zelha.thepit.zelenums.Perks;
import me.zelha.thepit.zelenums.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static me.zelha.thepit.zelenums.Passives.*;
import static me.zelha.thepit.zelenums.Perks.*;
import static org.bukkit.Material.*;

public class UpgradesVillagerListener implements Listener {//i hate this class

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final PerkListenersAndUtils perkUtils = Main.getInstance().getPerkUtils();
    private final Map<UUID, Double> costHandler = new HashMap<>();
    private final Map<UUID, Passives> passivesHandler = new HashMap<>();
    private final Map<UUID, Perks> perksHandler = new HashMap<>();
    private final Map<UUID, Integer> slotHandler = new HashMap<>();

    private ItemStack passivesItemBuilder(Player p, Passives passive) {
        String name;
        int cost = passive.getCost(p);
        int level = passive.getLevelRequirement(p);
        List<String> lore = new ArrayList<>();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (pData.getLevel() < passive.getBaseLevelReq() && pData.getPrestige() == 0) {
            return zl.itemBuilder(BEDROCK, 1, "§cUnknown Upgrade", Collections.singletonList(
                    "§7Required level: " + zl.getColorBracketAndLevel(0, passive.getBaseLevelReq())
            ));
        }

        if ((pData.getLevel() >= level || pData.getPassiveTier(passive) == 5) || (pData.getPassiveTier(passive) > 0 && pData.getGold() - cost >= 0)) {
            name = "§a" + passive.getName();
        } else if (pData.getPassiveTier(passive) == 0 && pData.getGold() - cost >= 0 && pData.getLevel() >= level) {
            name = "§e" + passive.getName();
        } else {
            name = "§c" + passive.getName();
        }

        switch (passive) {
            case XP_BOOST:
                if (pData.getPassiveTier(XP_BOOST) > 0) {
                    lore.add("§7Current: §b+" + 10 * pData.getPassiveTier(XP_BOOST) + "% XP");
                    lore.add("§7Tier: §a" + zl.toRoman(pData.getPassiveTier(XP_BOOST)));
                    lore.add("");
                }
                break;
            case GOLD_BOOST:
                if (pData.getPassiveTier(GOLD_BOOST) > 0) {
                    lore.add("§7Current: §6+" + 10 * pData.getPassiveTier(GOLD_BOOST) + "% gold (g)");
                    lore.add("§7Tier: §a" + zl.toRoman(pData.getPassiveTier(GOLD_BOOST)));
                    lore.add("");
                }
                break;
            case MELEE_DAMAGE:
                if (pData.getPassiveTier(MELEE_DAMAGE) > 0) {
                    lore.add("§7Current: §c+" + pData.getPassiveTier(MELEE_DAMAGE) + "%");
                    lore.add("§7Tier: §a" + zl.toRoman(pData.getPassiveTier(MELEE_DAMAGE)));
                    lore.add("");
                }
                break;
            case BOW_DAMAGE:
                if (pData.getPassiveTier(BOW_DAMAGE) > 0) {
                    lore.add("§7Current: §c+" + 3 * pData.getPassiveTier(BOW_DAMAGE) + "%");
                    lore.add("§7Tier: §a" + zl.toRoman(pData.getPassiveTier(BOW_DAMAGE)));
                    lore.add("");
                }
                break;
            case DAMAGE_REDUCTION:
                if (pData.getPassiveTier(DAMAGE_REDUCTION) > 0) {
                    lore.add("§7Current: §9+" + pData.getPassiveTier(DAMAGE_REDUCTION) + "%");
                    lore.add("§7Tier: §a" + zl.toRoman(pData.getPassiveTier(DAMAGE_REDUCTION)));
                    lore.add("");
                }
                break;
            case BUILD_BATTLER:
            if (pData.getPassiveTier(BUILD_BATTLER) > 0) {
                lore.add("§7Current: §a+" + 60 * pData.getPassiveTier(BUILD_BATTLER) + "%");
                lore.add("§7Tier: §a" + zl.toRoman(pData.getPassiveTier(BUILD_BATTLER)));
                lore.add("");
            }
            break;
            case EL_GATO:
                if (pData.getPassiveTier(EL_GATO) > 0) {
                    if (pData.getPassiveTier(EL_GATO) == 1) {
                        lore.add("§7Current: §dFirst kill");
                    } else {
                        lore.add("§7Current: §dFirst " + pData.getPassiveTier(EL_GATO) + " kills");
                    }
                    lore.add("§7Tier: §a" + zl.toRoman(pData.getPassiveTier(EL_GATO)));
                    lore.add("");
                }
                break;
        }

        if (passive == EL_GATO) {
            if (pData.getPassiveTier(passive) < 5) {
                lore.add("§7Next tier:");
            } else {
                lore.add("§7Description:");
            }
        } else {
            lore.add("§7Each tier:");
        }

        switch (passive) {
            case XP_BOOST:
                lore.add("§7Earn §b+10% XP §7from all");
                lore.add("§7sources.");
                break;
            case GOLD_BOOST:
                lore.add("§7Earn §6+10% gold (g) §7from");
                lore.add("§7kills and coin pickups.");
                break;
            case MELEE_DAMAGE:
                lore.add("§7Deal §c+1% §7melee damage.");
                break;
            case BOW_DAMAGE:
                lore.add("§7Deal §c+3% §7bow damage.");
                break;
            case DAMAGE_REDUCTION:
                lore.add("§7Receive §9-1% §7damage.");
                break;
            case BUILD_BATTLER:
                lore.add("§7Your blocks stay §a+60%");
                lore.add("§7longer.");
                break;
            case EL_GATO:
                if (pData.getPassiveTier(EL_GATO) == 0) {
                    lore.add("§dFirst kill §7each life rewards");
                    lore.add("§6+5g §b+5 XP§7.");
                } else if (pData.getPassiveTier(EL_GATO) < 5) {
                    lore.add("§dFirst " + (1 + pData.getPassiveTier(EL_GATO)) + " kills §7each life");
                    lore.add("§7reward §6+5g §b+5 XP§7.");
                } else {
                    lore.add("§dFirst " + pData.getPassiveTier(EL_GATO) + " kills §7each life");
                    lore.add("§7reward §6+5g §b+5 XP§7.");
                }
                break;
        }

        lore.add("\n");


        if (pData.getPassiveTier(passive) < 5) {
            if (pData.getLevel() >= level) {
                if (pData.getPassiveTier(passive) > 0) {
                    lore.add("§7Upgrade cost: §6" + zl.getFancyGoldString(cost) + "g");
                } else {
                    lore.add("§7Cost: §6" + zl.getFancyGoldString(cost) + "g");
                }

                if ((pData.getGold() - cost) >= 0) {
                    lore.add("§eClick to purchase!");
                } else {
                    lore.add("§cNot enough gold!");
                }
            } else {
                lore.add("§7Required level: " + zl.getColorBracketAndLevel(0, level));
                lore.add("§cLevel too low to upgrade!");
            }
        } else {

            lore.add("§aMax tier unlocked!");
        }

        return zl.itemBuilder(passive.getMaterial(), 1, name, lore);
    }

    private void passivePurchaseHandler(Player p, Passives passive) {
        PlayerData pData = Main.getInstance().getPlayerData(p);
        double cost = passive.getCost(p);

        if (pData.getPassiveTier(passive) >= 5) {
            p.sendMessage("§aYou already unlocked the last upgrade!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        } else if (pData.getLevel() < passive.getLevelRequirement(p)) {
            p.sendMessage("§cYou are too low level to acquire this!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        } else if (pData.getGold() - cost < 0) {
            p.sendMessage("§cYou don't have enough gold to afford this!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        } else if (cost < 1000) {
            pData.setGold(pData.getGold() - cost);
            pData.setPassiveTier(passive, pData.getPassiveTier(passive) + 1);
            p.playSound(p.getLocation(),  Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
            openMainGUI(p);
            return;
        }

        Inventory inv = Bukkit.createInventory(p, 27, "Are you sure?");

        inv.setItem(11, zl.itemBuilder(GREEN_TERRACOTTA, 1, "§aConfirm", Arrays.asList(
                "§7Purchasing: " + passive.getColorfulName() + " " + zl.toRoman((pData.getPassiveTier(passive) + 1)),
                "§7Cost: §6" + zl.getFancyGoldString(cost) + "g"
        )));
        inv.setItem(15, zl.itemBuilder(RED_TERRACOTTA, 1, "§cCancel", Arrays.asList(
                "§7Return to previous menu."
        )));
        costHandler.put(p.getUniqueId(), cost);
        passivesHandler.put(p.getUniqueId(), passive);
        p.openInventory(inv);
    }

    private ItemStack perkSlotItemBuilder(Player p, int slot) {
        String name;
        int level = 0;
        List<String> lore = new ArrayList<>();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        Perks perk = pData.getPerkAtSlot(slot);

        switch (slot) {
            case 1:
                level = 10;
                break;
            case 2:
                level = 35;
                break;
            case 3:
                level = 70;
                break;
            case 4:
                level = 100;
                break;
        }

        if (pData.getPerkAtSlot(slot) != UNSET) {
            name = "§ePerk Slot #" + slot;
        } else {
            name = "§aPerk Slot #" + slot;
        }

        if (perk != UNSET) {
            lore.add("§7Selected: §a" + perk.getName());
            lore.add("\n");
        }

        lore.addAll(perk.getLore());
        lore.add("\n");
        lore.add("§eClick to choose perk!");

        //special item handling
        if (pData.getLevel() < level && perk == UNSET) {
            return zl.itemBuilder(BEDROCK, 1, "§cPerk Slot #" + slot, Collections.singletonList(
                    "§7Required level: " + zl.getColorBracketAndLevel(0, level)
            ));
        } else if (pData.getPerkAtSlot(slot) == GOLDEN_HEADS) {
            return zl.headItemBuilder("PhantomTupac", slot, name, lore);
        } else if (pData.getPerkAtSlot(slot) == OLYMPUS) {
            return zl.potionItemBuilder(Color.LIME, null, slot, name, lore);
        }

        return zl.itemBuilder(perk.getMaterial(), slot, name, lore);
    }

    private void openMainGUI(Player p) {
        Inventory mainGUI = Bukkit.createInventory(p, 45, "Permanent upgrades");

        mainGUI.setItem(12, perkSlotItemBuilder(p, 1));
        mainGUI.setItem(13, perkSlotItemBuilder(p, 2));
        mainGUI.setItem(14, perkSlotItemBuilder(p, 3));
        mainGUI.setItem(28, passivesItemBuilder(p, XP_BOOST));
        mainGUI.setItem(29, passivesItemBuilder(p, GOLD_BOOST));
        mainGUI.setItem(30, passivesItemBuilder(p, MELEE_DAMAGE));
        mainGUI.setItem(31, passivesItemBuilder(p, BOW_DAMAGE));
        mainGUI.setItem(32, passivesItemBuilder(p, DAMAGE_REDUCTION));
        mainGUI.setItem(33, passivesItemBuilder(p, BUILD_BATTLER));
        mainGUI.setItem(34, passivesItemBuilder(p, EL_GATO));

        p.openInventory(mainGUI);
    }

    private ItemStack perkItemBuilder(Player p, Perks perk) {
        String name;
        int cost = perk.getCost();
        int level = perk.getLevel();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        ItemStack item;

        if (pData.getLevel() < level && pData.getPrestige() == 0) {
            return zl.itemBuilder(BEDROCK, 1, "§cUnknown perk", Collections.singletonList(
                    "§7Required level: " + zl.getColorBracketAndLevel(0, level)
            ));
        }

        if (pData.getPerkUnlockStatus(perk)) {
            name = "§a" + perk.getName();
        } else if (pData.getGold() - cost >= 0 && pData.getLevel() >= level) {
            name = "§e" + perk.getName();
        } else {
            name = "§c" + perk.getName();
        }

        List<String> lore = new ArrayList<>(perk.getLore());
        lore.add("\n");

        if (pData.hasPerkEquipped(perk)) {
            lore.add("§aAlready selected!");
        } else if (pData.getPerkUnlockStatus(perk)) {
            lore.add("§eClick to select!");
        } else {
            lore.add("§7Cost: §6" + zl.getFancyGoldString(cost) + "g");

            if (pData.getLevel() >= level) {
                if (pData.getGold() - cost >= 0) {
                    lore.add("§eClick to purchase!");
                } else {
                    lore.add("§cNot enough gold!");
                }
            } else {
                lore.add("§7Required level: " + zl.getColorBracketAndLevel(pData.getPrestige(), level));
                lore.add("§cToo low level!");
            }
        }

        //special item handling
        if (perk == GOLDEN_HEADS) {
            item = zl.headItemBuilder("PhantomTupac", 1, name, lore);
        } else if (perk == OLYMPUS) {
            item = zl.potionItemBuilder(Color.LIME, null, 1, name, lore);
        } else {
            item = zl.itemBuilder(perk.getMaterial(), 1, name, lore);
        }

        if (pData.hasPerkEquipped(perk)) {
            item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }

        return item;
    }

    private void perkSelectHandler(Player p, Perks perk) {
        PlayerData pData = Main.getInstance().getPlayerData(p);
        double cost = perk.getCost();

        if (pData.getLevel() < perk.getLevel()) {
            p.sendMessage("§cYou are too low level to acquire this!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        } else if (pData.getGold() - cost < 0) {
            p.sendMessage("§cYou don't have enough gold to afford this!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        } else if (pData.hasPerkEquipped(perk)) {
            p.sendMessage("§cThis perk is already selected!");
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            return;
        } else if (pData.getPerkUnlockStatus(perk)) {
            pData.setPerkAtSlot(slotHandler.get(p.getUniqueId()), perk);
            perkUtils.perkReset(p);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
            openMainGUI(p);
            return;
        }

        Inventory inv = Bukkit.createInventory(p, 27, "Are you sure?");

        inv.setItem(11, zl.itemBuilder(GREEN_TERRACOTTA, 1, "§aConfirm", Arrays.asList(
                "§7Purchasing: §6" + perk.getName(),
                "§7Cost: §6" + zl.getFancyGoldString(cost) + "g"
        )));
        inv.setItem(15, zl.itemBuilder(RED_TERRACOTTA, 1, "§cCancel", Collections.singletonList(
                "§7Return to previous menu."
        )));
        costHandler.put(p.getUniqueId(), cost);
        perksHandler.put(p.getUniqueId(), perk);
        p.openInventory(inv);
    }

    private void openPerkGUI(Player p, int slot) {
        PlayerData pData = Main.getInstance().getPlayerData(p);
        int level = 0;

        switch (slot) {
            case 1:
                level = 10;
                break;
            case 2:
                level = 35;
                break;
            case 3:
                level = 70;
                break;
            case 4:
                level = 100;
                break;
        }

        if (pData.getLevel() < level) {
            p.sendMessage("§cSlot not unlocked yet!");
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            return;
        }

        Inventory perkGUI = Bukkit.createInventory(p, 36, "Choose a perk");

        perkGUI.setItem(10, perkItemBuilder(p, GOLDEN_HEADS));
        perkGUI.setItem(11, perkItemBuilder(p, Perks.FISHING_ROD));
        perkGUI.setItem(12, perkItemBuilder(p, Perks.LAVA_BUCKET));
        perkGUI.setItem(13, perkItemBuilder(p, STRENGTH_CHAINING));
        perkGUI.setItem(14, perkItemBuilder(p, SAFETY_FIRST));
        perkGUI.setItem(15, perkItemBuilder(p, MINEMAN));
        perkGUI.setItem(16, perkItemBuilder(p, INSURANCE));
        perkGUI.setItem(19, perkItemBuilder(p, TRICKLE_DOWN));
        perkGUI.setItem(20, perkItemBuilder(p, LUCKY_DIAMOND));
        perkGUI.setItem(21, perkItemBuilder(p, SPAMMER));
        perkGUI.setItem(22, perkItemBuilder(p, BOUNTY_HUNTER));
        perkGUI.setItem(23, perkItemBuilder(p, STREAKER));
        perkGUI.setItem(24, perkItemBuilder(p, GLADIATOR));
        perkGUI.setItem(25, perkItemBuilder(p, VAMPIRE));
        perkGUI.setItem(31, zl.itemBuilder(ARROW, 1, "§aGo Back", Collections.singletonList("§7To Permanent upgrades")));

        if (pData.getPerkAtSlot(slot) != UNSET) {
            perkGUI.setItem(32, zl.itemBuilder(DIAMOND_BLOCK, 1, "§cNo perk", Arrays.asList(
                    "§7Are you hardcore enough that you",
                    "§7don't need any perk for this",
                    "§7slot?",
                    "\n",
                    "§eClick to remove perk!"
            )));
        }

        slotHandler.put(p.getUniqueId(), slot);
        p.openInventory(perkGUI);
    }

    private List<String> streakLoreBuilder(Player p, Material material) {//unused atm
        List<String> lore = new ArrayList<>();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        return lore;
    }

    private void openMainStreakGUI(Player p) {//unused atm
        Inventory streakGUI = Bukkit.createInventory(p, 27, "Killstreaks");

        p.openInventory(streakGUI);
    }

    @EventHandler
    public void onDirectRightClick(InventoryOpenEvent e) {
        if (e.getView().getTopInventory().getType() == InventoryType.MERCHANT) {
            Player p = (Player) e.getPlayer();
            Villager villager = (Villager) e.getInventory().getHolder();
            String worldName = e.getPlayer().getWorld().getName();
            double x = villager.getLocation().getX();
            double y = villager.getLocation().getY();
            double z = villager.getLocation().getZ();

            e.setCancelled(true);

            if (zl.noObstructions(Worlds.findByName(worldName), NPCs.UPGRADES).contains(x, y, z)) {
                openMainGUI(p);
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent e) {
        String worldName = e.getPlayer().getWorld().getName();
        double x = e.getRightClicked().getLocation().getX();
        double y = e.getRightClicked().getLocation().getY();
        double z = e.getRightClicked().getLocation().getZ();

        if (zl.noObstructions(Worlds.findByName(worldName), NPCs.UPGRADES).contains(x, y, z)) {
            openMainGUI(e.getPlayer());
        }
    }

    @EventHandler
    public void onLeftClick(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damagerEntity = e.getDamager();

        if (zl.playerCheck(damagerEntity)) {
            Player damager = (Player) e.getDamager();
            String worldName = damager.getWorld().getName();
            double x = damaged.getLocation().getX();
            double y = damaged.getLocation().getY();
            double z = damaged.getLocation().getZ();

            if (zl.noObstructions(Worlds.findByName(worldName), NPCs.UPGRADES).contains(x, y, z)) {
                openMainGUI(damager);
            }
        }
    }

    @EventHandler
    public void mainGUIInteract(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getView().getTitle().equals("Permanent upgrades") && e.getClickedInventory() != e.getView().getBottomInventory()) {
            e.setCancelled(true);

            if (e.getCurrentItem() != null) {//reminder: simplify this so passives do something like the perkguiinteract listener
                switch (e.getSlot()) {
                    case 12:
                        openPerkGUI(p, 1);
                        break;
                    case 13:
                        openPerkGUI(p, 2);
                        break;
                    case 14:
                        openPerkGUI(p, 3);
                        break;
                    case 28:
                        passivePurchaseHandler(p, XP_BOOST);
                        break;
                    case 29:
                        passivePurchaseHandler(p, GOLD_BOOST);
                        break;
                    case 30:
                        passivePurchaseHandler(p, MELEE_DAMAGE);
                        break;
                    case 31:
                        passivePurchaseHandler(p, BOW_DAMAGE);
                        break;
                    case 32:
                        passivePurchaseHandler(p, DAMAGE_REDUCTION);
                        break;
                    case 33:
                        passivePurchaseHandler(p, BUILD_BATTLER);
                        break;
                    case 34:
                        passivePurchaseHandler(p, EL_GATO);
                        break;
                }
            }
        }
    }

    @EventHandler
    public void perkGUIInteract(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack clicked = e.getCurrentItem();

        if (e.getView().getTitle().equals("Choose a perk") && e.getClickedInventory() != e.getView().getBottomInventory()) {
            e.setCancelled(true);

            if (clicked == null) {
                return;
            }

            //special case handling
            if (clicked.getType() == BEDROCK) {
                p.sendMessage("§cYou are too low level to acquire this perk!");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return;
            } else if (clicked.getType() == DIAMOND_BLOCK) {
                Main.getInstance().getPlayerData(p).setPerkAtSlot(slotHandler.get(p.getUniqueId()), UNSET);
                perkUtils.perkReset(p);
                slotHandler.remove(p.getUniqueId());
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                openMainGUI(p);
                return;
            } else if (clicked.getType() == ARROW) {
                openMainGUI(p);
                return;
            }

            for (Perks perk : Perks.values()) {
                if (clicked.getItemMeta().getLore().containsAll(perk.getLore())) {//theres no logical case where this should be null so im ignoring the warning
                    perkSelectHandler(p, perk);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void confirmGUIInteract(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        UUID uuid = p.getUniqueId();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (e.getView().getTitle().equals("Are you sure?") && e.getClickedInventory() != e.getView().getBottomInventory()) {
            e.setCancelled(true);

            if (e.getCurrentItem() != null) {
                if (e.getCurrentItem().getType() == GREEN_TERRACOTTA) {
                    pData.setGold(pData.getGold() - costHandler.get(uuid));

                    if (passivesHandler.get(uuid) != null) {
                        pData.setPassiveTier(passivesHandler.get(uuid), pData.getPassiveTier(passivesHandler.get(uuid)) + 1);
                        openMainGUI(p);
                    } else if (perksHandler.get(uuid) != null) {
                        pData.setPerkUnlockStatus(perksHandler.get(uuid), true);
                        pData.setPerkAtSlot(slotHandler.get(uuid), perksHandler.get(uuid));
                        perkUtils.perkReset(p);
                        openMainGUI(p);
                    }

                    p.playSound(p.getLocation(),  Sound.ENTITY_PLAYER_LEVELUP, 1, 2);

                } else if (e.getCurrentItem().getType() == RED_TERRACOTTA) {
                    if (passivesHandler.get(uuid) != null || perksHandler.get(uuid) != null) {
                        openMainGUI(p);
                    }
                }

                costHandler.remove(uuid);
                perksHandler.remove(uuid);
                passivesHandler.remove(uuid);
            }
        }
    }

    private final List<String> inventoryNames = Arrays.asList(
            "Permanent upgrades",
            "Choose a perk",
            "Are you sure?"
    );

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        for (String name : inventoryNames) {
            if (e.getView().getTitle().equals(name) && e.getInventory() != e.getView().getBottomInventory()) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        costHandler.remove(uuid);
        passivesHandler.remove(uuid);
        perksHandler.remove(uuid);
        slotHandler.remove(uuid);
    }
}

















