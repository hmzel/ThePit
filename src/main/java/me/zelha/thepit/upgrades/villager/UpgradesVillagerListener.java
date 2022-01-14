package me.zelha.thepit.upgrades.villager;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.zelenums.NPCs;
import me.zelha.thepit.zelenums.Passives;
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

import static me.zelha.thepit.zelenums.Passives.*;
import static org.bukkit.Material.*;

import java.util.*;

public class UpgradesVillagerListener implements Listener {//i hate this class

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final Map<UUID, Double> costHandler = new HashMap<>();
    private final Map<UUID, String> backHandler = new HashMap<>();
    private final Map<UUID, Passives> passivesHandler = new HashMap<>();

    private String getPassivesNameColor(Player p, Passives passive, double cost) {
        PlayerData pData = Main.getInstance().getPlayerData(p);

        if ((pData.getPassiveTier(passive) > 0 && pData.getGold() - cost >= 0) || pData.getPassiveTier(passive) == 5) {
            return "§a" + passive.getName();
        } else if (pData.getGold() - cost >= 0) {
            return "§e" + passive.getName();
        } else {
            return "§c" + passive.getName();
        }
    }

    private List<String> passivesLoreBuilder(Player p, Material material, Passives passive) {
        List<String> lore = new ArrayList<>();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        int cost = 0;

        switch (material) {
            case LIGHT_BLUE_DYE:
                if (pData.getPassiveTier(XP_BOOST) > 0) {
                    lore.add("§7Current: §b+" + 10 * pData.getPassiveTier(XP_BOOST) + "% XP");
                    lore.add("§7Tier: §a" + zl.toRoman(pData.getPassiveTier(XP_BOOST)));
                    lore.add("");
                }
                break;
            case ORANGE_DYE:
                if (pData.getPassiveTier(GOLD_BOOST) > 0) {
                    lore.add("§7Current: §6+" + 10 * pData.getPassiveTier(GOLD_BOOST) + "% gold (g)");
                    lore.add("§7Tier: §a" + zl.toRoman(pData.getPassiveTier(GOLD_BOOST)));
                    lore.add("");
                }
                break;
            case RED_DYE:
                if (pData.getPassiveTier(MELEE_DAMAGE) > 0) {
                    lore.add("§7Current: §c+" + pData.getPassiveTier(MELEE_DAMAGE) + "%");
                    lore.add("§7Tier: §a" + zl.toRoman(pData.getPassiveTier(MELEE_DAMAGE)));
                    lore.add("");
                }
                break;
            case YELLOW_DYE:
                if (pData.getPassiveTier(BOW_DAMAGE) > 0) {
                    lore.add("§7Current: §c+" + 3 * pData.getPassiveTier(BOW_DAMAGE) + "%");
                    lore.add("§7Tier: §a" + zl.toRoman(pData.getPassiveTier(BOW_DAMAGE)));
                    lore.add("");
                }
                break;
            case CYAN_DYE:
                if (pData.getPassiveTier(DAMAGE_REDUCTION) > 0) {
                    lore.add("§7Current: §9+" + pData.getPassiveTier(DAMAGE_REDUCTION) + "%");
                    lore.add("§7Tier: §a" + zl.toRoman(pData.getPassiveTier(DAMAGE_REDUCTION)));
                    lore.add("");
                }
                break;
            case BONE_MEAL:
            if (pData.getPassiveTier(BUILD_BATTLER) > 0) {
                lore.add("§7Current: §a+" + 60 * pData.getPassiveTier(BUILD_BATTLER) + "%");
                lore.add("§7Tier: §a" + zl.toRoman(pData.getPassiveTier(BUILD_BATTLER)));
                lore.add("");
            }
            break;
            case CAKE:
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

        if (material == CAKE) {
            if (pData.getPassiveTier(passive) < 5) {
                lore.add("§7Next tier:");
            } else {
                lore.add("§7Description:");
            }
        } else {
            lore.add("§7Each tier:");
        }

        switch (material) {
            case LIGHT_BLUE_DYE:
                lore.add("§7Earn §b+10% XP §7from all");
                lore.add("§7sources.");
                break;
            case ORANGE_DYE:
                lore.add("§7Earn §6+10% gold (g) §7from");
                lore.add("§7kills and coin pickups.");
                break;
            case RED_DYE:
                lore.add("§7Deal §c+1% §7melee damage.");
                break;
            case YELLOW_DYE:
                lore.add("§7Deal §c+3% §7bow damage.");
                break;
            case CYAN_DYE:
                lore.add("§7Receive §9-1% §7damage.");
                break;
            case BONE_MEAL:
                lore.add("§7Your blocks stay §a+60%");
                lore.add("§7longer.");
                break;
            case CAKE:
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
            switch (material) {
                case CAKE:
                    if (pData.getPassiveTier(EL_GATO) == 0) {
                        lore.add("§7Cost: §6" + zl.getFancyGoldString(1000) + "g");
                        cost = 1000;
                    } else {
                        lore.add("§7Upgrade cost: §6" + zl.getFancyGoldString((pData.getPassiveTier(EL_GATO) + 1) * 1000) + "g");
                        cost = (pData.getPassiveTier(EL_GATO) + 1) * 1000;
                    }
                    break;
                default:
                    lore.add("§7Cost: §60g");
                    break;
            }

            if ((pData.getGold() - cost) >= 0) {
                lore.add("§eClick to purchase!");
            } else {
                lore.add("§cNot enough gold!");
            }
        } else {
            lore.add("§aMax tier unlocked!");
        }

        return lore;
    }

    private List<String> perkLoreBuilder(Player p, Material material) {//unused atm
        List<String> lore = new ArrayList<>();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        return lore;
    }

    private List<String> streakLoreBuilder(Player p, Material material) {//unused atm
        List<String> lore = new ArrayList<>();
        PlayerData pData = Main.getInstance().getPlayerData(p);

        return lore;
    }

    private void openMainGUI(Player p) {
        Inventory mainGUI = Bukkit.createInventory(p, 45, "Permanent upgrades");

        mainGUI.setItem(28, zl.itemBuilder(LIGHT_BLUE_DYE, 1, getPassivesNameColor(p, XP_BOOST, determineCost(p, XP_BOOST)), passivesLoreBuilder(p, LIGHT_BLUE_DYE, XP_BOOST)));
        mainGUI.setItem(29, zl.itemBuilder(ORANGE_DYE, 1, getPassivesNameColor(p, GOLD_BOOST, determineCost(p, GOLD_BOOST)), passivesLoreBuilder(p, ORANGE_DYE, GOLD_BOOST)));
        mainGUI.setItem(30, zl.itemBuilder(RED_DYE, 1, getPassivesNameColor(p, MELEE_DAMAGE, determineCost(p, MELEE_DAMAGE)), passivesLoreBuilder(p, RED_DYE, MELEE_DAMAGE)));
        mainGUI.setItem(31, zl.itemBuilder(YELLOW_DYE, 1, getPassivesNameColor(p, BOW_DAMAGE, determineCost(p, BOW_DAMAGE)), passivesLoreBuilder(p, YELLOW_DYE, BOW_DAMAGE)));
        mainGUI.setItem(32, zl.itemBuilder(CYAN_DYE, 1, getPassivesNameColor(p, DAMAGE_REDUCTION, determineCost(p, DAMAGE_REDUCTION)), passivesLoreBuilder(p, CYAN_DYE, DAMAGE_REDUCTION)));
        mainGUI.setItem(33, zl.itemBuilder(BONE_MEAL, 1, getPassivesNameColor(p, BUILD_BATTLER, determineCost(p, BUILD_BATTLER)), passivesLoreBuilder(p, BONE_MEAL, BUILD_BATTLER)));
        mainGUI.setItem(34, zl.itemBuilder(CAKE, 1, getPassivesNameColor(p, EL_GATO, determineCost(p, EL_GATO)), passivesLoreBuilder(p, CAKE, EL_GATO)));

        p.openInventory(mainGUI);
    }

    private void openPerkGUI(Player p) {//unused atm
        Inventory perkGUI = Bukkit.createInventory(p, 45, "Choose a perk");

        p.openInventory(perkGUI);
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
            }
        } else {
            p.sendMessage("§aYou already unlocked the last upgrade!");
        }
    }

    private double determineCost(Player p, Passives passive) {//will update when i figure out the costs
        PlayerData pData = Main.getInstance().getPlayerData(p);

        switch (passive) {
            case EL_GATO:
                return ((pData.getPassiveTier(passive) + 1) * 1000);
        }
        return 0;
    }

    private void determineBackInventory(Player p) {
        if (backHandler.get(p.getUniqueId()).equals("Permanent upgrades")) {
            openMainGUI(p);
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
                    case 28:
                        purchaseHandler(p, XP_BOOST, determineCost(p, XP_BOOST), e);
                        break;
                    case 29:
                        purchaseHandler(p, GOLD_BOOST, determineCost(p, GOLD_BOOST), e);
                        break;
                    case 30:
                        purchaseHandler(p, MELEE_DAMAGE, determineCost(p, MELEE_DAMAGE), e);
                        break;
                    case 31:
                        purchaseHandler(p, BOW_DAMAGE, determineCost(p, BOW_DAMAGE), e);
                        break;
                    case 32:
                        purchaseHandler(p, DAMAGE_REDUCTION, determineCost(p, DAMAGE_REDUCTION), e);
                        break;
                    case 33:
                        purchaseHandler(p, BUILD_BATTLER, determineCost(p, BUILD_BATTLER), e);
                        break;
                    case 34:
                        purchaseHandler(p, EL_GATO, determineCost(p, EL_GATO), e);
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
        if (e.getView().getTitle().equals("Are you sure?")) {
            UUID uuid = e.getPlayer().getUniqueId();
            backHandler.remove(uuid);
            costHandler.remove(uuid);
            passivesHandler.remove(uuid);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTitle().equals("Permanent upgrades") && e.getInventory() != e.getView().getBottomInventory()) {
            e.setCancelled(true);
        }
    }
}

















