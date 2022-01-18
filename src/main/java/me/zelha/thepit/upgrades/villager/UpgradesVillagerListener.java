package me.zelha.thepit.upgrades.villager;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.NPCs;
import me.zelha.thepit.zelenums.Passives;
import me.zelha.thepit.zelenums.Perks;
import me.zelha.thepit.zelenums.Worlds;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import static me.zelha.thepit.zelenums.Passives.*;
import static me.zelha.thepit.zelenums.Perks.*;
import static org.bukkit.Material.*;

import java.util.*;

public class UpgradesVillagerListener implements Listener {//i hate this class

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final Map<UUID, Double> costHandler = new HashMap<>();
    private final Map<UUID, String> backHandler = new HashMap<>();
    private final Map<UUID, Passives> passivesHandler = new HashMap<>();
    private final Map<UUID, Integer> slotHandler = new HashMap<>();

    private ItemStack passivesItemBuilder(Player p, Passives passive) {
        Material material;
        String name;
        int cost = determinePassiveCost(p, passive);
        int level = 0;
        List<String> lore = new ArrayList<>();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        //determining level
        switch (passive) {
            case XP_BOOST:
                level = 10;
                break;
            case GOLD_BOOST:
                level = 20;
                break;
            case MELEE_DAMAGE:
            case BOW_DAMAGE:
            case DAMAGE_REDUCTION:
                level = 30;
                break;
            case BUILD_BATTLER:
                level = 40;
                break;
            case EL_GATO:
                level = 50;
                break;
        }
        //end

        //determining material
        if (pData.getLevel() >= level || pData.getPrestige() != 0) {
            material = passive.getMaterial();
        } else {
            return zl.itemBuilder(BEDROCK, 1, "§cUnknown Upgrade", Collections.singletonList(
                    "§7Required level: " + zl.getColorBracketAndLevel(0, level)
            ));
        }
        //end

        //name building
        if ((pData.getLevel() >= level || pData.getPassiveTier(passive) == 5) || (pData.getPassiveTier(passive) > 0 && pData.getGold() - cost >= 0)) {
            name = "§a" + passive.getName();
        } else if (pData.getPassiveTier(passive) == 0 && pData.getGold() - cost >= 0 && pData.getLevel() >= level) {
            name = "§e" + passive.getName();
        } else {
            name = "§c" + passive.getName();
        }
        //end

        //lore building
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
            if (passive == EL_GATO && pData.getPassiveTier(EL_GATO) > 0) {
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
            lore.add("§aMax tier unlocked!");
        }
        //end

        return zl.itemBuilder(material, 1, name, lore);
    }

    private int determinePassiveCost(Player p, Passives passive) {//will update when i figure out the costs
        PlayerData pData = Main.getInstance().getPlayerData(p);

        switch (passive) {
            case EL_GATO:
                return ((pData.getPassiveTier(passive) + 1) * 1000);
        }
        return 0;
    }

    private ItemStack perkSlotItemBuilder(Player p, int slot) {
        String name;
        int level = 0;
        List<String> lore = new ArrayList<>();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        Perks perk = pData.getPerkAtSlot(slot);

        //determining level
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
        //end

        //name building
        if (pData.getPerkAtSlot(slot) != UNSET) {
            name = "§ePerk Slot #" + slot;
        } else {
            name = "§aPerk Slot #" + slot;
        }
        //end

        //lore building
        if (perk != UNSET) {
            lore.add("§7Selected: §a" + perk.getName());
            lore.add("\n");
        }

        lore.addAll(perk.getLore());
        lore.add("\n");
        lore.add("§eClick to choose perk!");
        //end

        //special item handling
        if (pData.getLevel() < level && perk == UNSET) {
            return zl.itemBuilder(BEDROCK, 1, "§cPerk Slot #" + slot, Collections.singletonList(
                    "§7Required level: " + zl.getColorBracketAndLevel(0, level)
            ));
        } else if (pData.getPerkAtSlot(slot) == GOLDEN_HEADS) {
            ItemStack item = zl.itemBuilder(perk.getMaterial(), 1, name, lore);
            //insert head stuff here
            return item;
        } else if (pData.getPerkAtSlot(slot) == OLYMPUS) {
            ItemStack item = zl.itemBuilder(perk.getMaterial(), 1, name, lore);
            //insert potion stuff here
            return item;
        }
        //end

        return zl.itemBuilder(perk.getMaterial(), 1, name, lore);
    }

    private void openMainGUI(Player p) {
        Inventory mainGUI = Bukkit.createInventory(p, 45, "Permanent upgrades");
        PlayerData pData = Main.getInstance().getPlayerData(p);

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

    private void determinePerkItem(Inventory inventory, PlayerData pData, int invSlot, int level) {
        if (pData.getLevel() >= level) {

        } else {
            inventory.setItem(invSlot, zl.itemBuilder(BEDROCK, 1, "§cUnknown Perk", Collections.singletonList(
                    "§7Required level: " + zl.getColorBracketAndLevel(0, level)
            )));
        }
    }

    private List<String> perkLoreBuilder(Player p, Material material) {//unused atm
        List<String> lore = new ArrayList<>();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        return lore;
    }

    private void openPerkGUI(Player p) {
        Inventory perkGUI = Bukkit.createInventory(p, 45, "Choose a perk");

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

    private void purchaseHandler(Player p, Passives passive, double cost, InventoryClickEvent e) {
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (pData.getPassiveTier(passive) < 5) {
            if (pData.getGold() - cost >= 0) {
                if (cost >= 1000) {
                    Inventory inv = Bukkit.createInventory(p, 27, "Are you sure?");

                    inv.setItem(11, zl.itemBuilder(GREEN_TERRACOTTA, 1, "§aConfirm", Arrays.asList(
                            "§7Purchasing: " + passive.getColorfulName() + " " + zl.toRoman((pData.getPassiveTier(passive) + 1)),
                            "§7Cost: §6" + zl.getFancyGoldString(cost)
                    )));
                    inv.setItem(15, zl.itemBuilder(RED_TERRACOTTA, 1, "§cCancel", Arrays.asList(
                            "§7Return to previous menu."
                    )));
                    costHandler.put(p.getUniqueId(), cost);
                    backHandler.put(p.getUniqueId(), e.getView().getTitle());
                    passivesHandler.put(p.getUniqueId(), passive);
                    p.openInventory(inv);
                } else {
                    pData.setGold(pData.getGold() - cost);
                    pData.setPassiveTier(passive, pData.getPassiveTier(passive) + 1);

                    if (e.getView().getTitle().equals("Permanent upgrades")) {
                        openMainGUI(p);
                    }
                }
            } else {
                p.sendMessage("§cYou don't have enough gold to afford this!");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            }
        } else {
            p.sendMessage("§aYou already unlocked the last upgrade!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        }
    }

    private void determineBackInventory(Player p) {
        if (backHandler.get(p.getUniqueId()).equals("Permanent upgrades")) {
            openMainGUI(p);
        }
    }

    private boolean levelCheck(InventoryClickEvent e, int level, String message, Sound sound) {
        Player p = (Player) e.getWhoClicked();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if (pData.getLevel() >= level) {
            return true;
        } else {
            p.sendMessage(message);
            p.playSound(p.getLocation(), sound, 1, 1);
            return false;
        }
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

            if (zl.noObstructions(Worlds.valueOfName(worldName), NPCs.UPGRADES).contains(x, y, z)) {
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

        if (zl.noObstructions(Worlds.valueOfName(worldName), NPCs.UPGRADES).contains(x, y, z)) {
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

            if (zl.noObstructions(Worlds.valueOfName(worldName), NPCs.UPGRADES).contains(x, y, z)) {
                openMainGUI(damager);
            }
        }
    }

    @EventHandler
    public void mainGUIInteract(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getView().getTitle().equals("Permanent upgrades") && e.getClickedInventory() != e.getView().getBottomInventory()) {
            e.setCancelled(true);

            if (e.getCurrentItem() != null) {
                switch (e.getSlot()) {
                    case 12:
                        if (levelCheck(e, 10, "§cSlot not unlocked yet!", Sound.ENTITY_ENDERMAN_TELEPORT)) {
                            openPerkGUI(p);
                            slotHandler.put(p.getUniqueId(), 1);
                        }
                        break;
                    case 13:
                        if (levelCheck(e, 35, "§cSlot not unlocked yet!", Sound.ENTITY_ENDERMAN_TELEPORT)) {
                            openPerkGUI(p);
                            slotHandler.put(p.getUniqueId(), 2);
                        }
                        break;
                    case 14:
                        if (levelCheck(e, 70, "§cSlot not unlocked yet!", Sound.ENTITY_ENDERMAN_TELEPORT)) {
                            openPerkGUI(p);
                            slotHandler.put(p.getUniqueId(), 3);
                        }
                        break;
                    case 28:
                        purchaseHandler(p, XP_BOOST, determinePassiveCost(p, XP_BOOST), e);
                        break;
                    case 29:
                        purchaseHandler(p, GOLD_BOOST, determinePassiveCost(p, GOLD_BOOST), e);
                        break;
                    case 30:
                        purchaseHandler(p, MELEE_DAMAGE, determinePassiveCost(p, MELEE_DAMAGE), e);
                        break;
                    case 31:
                        purchaseHandler(p, BOW_DAMAGE, determinePassiveCost(p, BOW_DAMAGE), e);
                        break;
                    case 32:
                        purchaseHandler(p, DAMAGE_REDUCTION, determinePassiveCost(p, DAMAGE_REDUCTION), e);
                        break;
                    case 33:
                        purchaseHandler(p, BUILD_BATTLER, determinePassiveCost(p, BUILD_BATTLER), e);
                        break;
                    case 34:
                        purchaseHandler(p, EL_GATO, determinePassiveCost(p, EL_GATO), e);
                        break;
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
                    pData.setPassiveTier(passivesHandler.get(uuid), pData.getPassiveTier(passivesHandler.get(uuid)) + 1);
                    determineBackInventory(p);
                } else if (e.getCurrentItem().getType() == RED_TERRACOTTA) {
                    determineBackInventory(p);
                }

                backHandler.remove(uuid);
                costHandler.remove(uuid);
                passivesHandler.remove(uuid);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (e.getView().getTitle().equals("Are you sure?")) {
            backHandler.remove(uuid);
            costHandler.remove(uuid);
            passivesHandler.remove(uuid);
        } else if (e.getView().getTitle().equals("Choose a perk")) {
            slotHandler.remove(uuid);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTitle().equals("Permanent upgrades") && e.getInventory() != e.getView().getBottomInventory()) {
            e.setCancelled(true);
        } else if (e.getView().getTitle().equals("Choose a perk") && e.getInventory() != e.getView().getBottomInventory()) {
            e.setCancelled(true);
        }
    }
}

















