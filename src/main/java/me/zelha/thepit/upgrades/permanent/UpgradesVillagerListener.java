package me.zelha.thepit.upgrades.permanent;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.NPCInteractEvent;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.utils.ConfirmGUIHandler;
import me.zelha.thepit.utils.ZelLogic;
import me.zelha.thepit.zelenums.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static me.zelha.thepit.zelenums.Perks.*;
import static org.bukkit.Material.*;

public class UpgradesVillagerListener implements Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final ConfirmGUIHandler confirmGUIHandler = Main.getInstance().getConfirmGUIHandler();
    private final Map<UUID, Integer> slotHandler = new HashMap<>();

    @EventHandler
    public void onNPCInteract(NPCInteractEvent e) {
        if (e.getNPC() == NPCs.UPGRADES) openMainGUI(e.getPlayer());
    }

    @EventHandler
    public void mainGUIInteract(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (!e.getView().getTitle().equals("Permanent upgrades")) return;
        if (e.getClickedInventory() == e.getView().getBottomInventory()) return;

        e.setCancelled(true);

        if (!zl.itemCheck(e.getCurrentItem())) return;

        if (e.getSlot() >= 12 && e.getSlot() <= 14) openPerkGUI(p, e.getSlot() - 11);

        if (e.getSlot() == 15) openMainStreakGUI(p);

        if (e.getSlot() <= 27) return;

        Passives passive = Passives.values()[e.getSlot() - 28];
        PlayerData pData = Main.getInstance().getPlayerData(p);
        double cost = passive.getCost(p);

        if (pData.getPassiveTier(passive) >= 5) {
            p.sendMessage("§aYou already unlocked the last upgrade!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        } else if (e.getCurrentItem().getType() == BEDROCK || pData.getLevel() < passive.getLevelReq(p)) {
            p.sendMessage("§cYou are too low level to acquire this!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        } else if (pData.getGold() - cost < 0) {
            p.sendMessage("§cYou don't have enough gold to afford this!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        confirmGUIHandler.confirmPurchase(p, passive.getColorfulName() + " " + zl.toRoman((pData.getPassiveTier(passive) + 1)), cost, true,
                player -> {
                    pData.setGold(pData.getGold() - cost);
                    pData.setPassiveTier(passive, pData.getPassiveTier(passive) + 1);
                    p.sendMessage("§a§lPURCHASE! §6" + passive.getName() + " " + zl.toRoman(pData.getPassiveTier(passive)));
                    p.playSound(p.getLocation(),  Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                    openMainGUI(p);
                });
    }

    @EventHandler
    public void perkGUIInteract(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack clicked = e.getCurrentItem();

        if (!e.getView().getTitle().equals("Choose a perk")) return;
        if (e.getClickedInventory() == e.getView().getBottomInventory()) return;

        e.setCancelled(true);

        if (clicked == null) return;

        Perks perk = Perks.findByMaterial(clicked.getType());
        PlayerData pData = Main.getInstance().getPlayerData(p);
        double cost = (perk != null) ? perk.getCost() : 13131313;

        if (clicked.getType() == DIAMOND_BLOCK) {
            Main.getInstance().getPlayerData(p).setPerkAtSlot(slotHandler.get(p.getUniqueId()), UNSET);
            zl.pitReset(p);
            slotHandler.remove(p.getUniqueId());
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
            openMainGUI(p);
            return;
        } else if (clicked.getType() == ARROW) {
            openMainGUI(p);
            return;
        } else if (clicked.getType() == BEDROCK || pData.getLevel() < perk.getLevel()) {
            p.sendMessage("§cYou are too low level to acquire this perk!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        } else if (pData.hasPerkEquipped(perk)) {
            p.sendMessage("§cThis perk is already selected!");
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            return;
        } else if (pData.getPerkUnlockStatus(perk)) {
            pData.setPerkAtSlot(slotHandler.get(p.getUniqueId()), perk);
            zl.pitReset(p);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
            openMainGUI(p);
            return;
        } else if (pData.getGold() - cost < 0) {
            p.sendMessage("§cYou don't have enough gold to afford this!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        confirmGUIHandler.confirmPurchase(p, "§6" + perk.getName(), cost, false,
                player -> {
                    pData.setGold(pData.getGold() - cost);
                    pData.setPerkUnlockStatus(perk, true);
                    pData.setPerkAtSlot(slotHandler.get(p.getUniqueId()), perk);
                    zl.pitReset(p);
                    p.sendMessage("§a§lPURCHASE! §6" + perk.getName());
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                    openMainGUI(p);
                    slotHandler.remove(p.getUniqueId());
                });
    }

    @EventHandler
    public void mainKillstreakGUIInteract(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack clicked = e.getCurrentItem();

        if (!e.getView().getTitle().equals("Killstreaks")) return;
        if (e.getClickedInventory() == e.getView().getBottomInventory()) return;

        e.setCancelled(true);

        if (clicked == null) return;
        if (clicked.getType() == BEDROCK) return;

        if (e.getSlot() < 15) {
            openMinistreakGUI(p, clicked.getAmount());
            slotHandler.put(p.getUniqueId(), clicked.getAmount());
        }

        if (e.getSlot() == 15 || e.getSlot() == 16) {
            openMegastreakGUI(p);
        }

        if (e.getSlot() == 22) {
            openMainGUI(p);
        }
    }

    @EventHandler
    public void megastreakGUIInteract(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        ItemStack clicked = e.getCurrentItem();

        if (!e.getView().getTitle().equals("Choose a killstreak§2")) return;
        if (e.getClickedInventory() == e.getView().getBottomInventory()) return;

        e.setCancelled(true);

        if (clicked == null) return;

        if (clicked.getType() == ARROW) {
            openMainStreakGUI(p);
            return;
        }

        StringBuilder finder = new StringBuilder(clicked.getItemMeta().getDisplayName()).replace(0, 2, "");

        while (finder.lastIndexOf(" ") != -1) finder.setCharAt(finder.lastIndexOf(" "), '_');

        Megastreaks mega = Megastreaks.findByEnumName(finder.toString());

        if (pData.getMegastreak() == mega) {
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            p.sendMessage("§cThis killstreak is already selected!");
            return;
        } else if (pData.getMegastreakUnlockStatus(mega)) {
            pData.setMegastreak(mega);
            zl.pitReset(p);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
            openMainStreakGUI(p);
            return;
        } else if (pData.getLevel() < mega.getLevel()) {
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            p.sendMessage("§cYou are too low level to acquire this killstreak!");
            return;
        } else if (pData.getGold() < mega.getCost()) {
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            p.sendMessage("§cYou don't have enough gold to afford this!");
            return;
        }

        confirmGUIHandler.confirmPurchase(p, "§6" + mega.getName(), mega.getCost(), false,
                player -> {
                    pData.setGold(pData.getGold() - mega.getCost());
                    pData.setMegastreakUnlockStatus(mega, true);
                    pData.setMegastreak(mega);
                    zl.pitReset(p);
                    p.sendMessage("§a§lPURCHASE! §6" + mega.getName());
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                    openMainStreakGUI(p);
                });
    }

    @EventHandler
    public void ministreakGUIInteract(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        UUID uuid = p.getUniqueId();
        PlayerData pData = Main.getInstance().getPlayerData(p);
        ItemStack clicked = e.getCurrentItem();

        if (!e.getView().getTitle().equals("Choose a killstreak§1")) return;
        if (e.getClickedInventory() == e.getView().getBottomInventory()) return;

        e.setCancelled(true);

        if (clicked == null) return;

        if (clicked.getType() == ARROW && clicked.getAmount() == 1) {
            openMainStreakGUI(p);
            return;
        } else if (clicked.getType() == GOLD_BLOCK) {
            pData.setMinistreakAtSlot(slotHandler.get(uuid), Ministreaks.UNSET);
            zl.pitReset(p);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
            openMainStreakGUI(p);
            return;
        } else if (clicked.getType() == BEDROCK) {
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            p.sendMessage("§cYou are too low level to acquire this killstreak!");
            return;
        }

        StringBuilder finder = new StringBuilder(clicked.getItemMeta().getDisplayName()).replace(0, 2, "");

        while (finder.lastIndexOf(" ") != -1) finder.setCharAt(finder.lastIndexOf(" "), '_');
        while (finder.lastIndexOf("-") != -1) finder.setCharAt(finder.lastIndexOf("-"), '_');
        while (finder.lastIndexOf("&") != -1) finder.replace(1, 2, "_AND_");
        while (finder.lastIndexOf("'") != -1) finder.deleteCharAt(finder.lastIndexOf("'"));

        Ministreaks mini = Ministreaks.findByEnumName(finder.toString());
        Ministreaks sameFrequency = null;
        int badSlot = 0;

        for (Ministreaks mini2 : pData.getEquippedMinistreaks()) {
            badSlot++;

            if (badSlot == slotHandler.get(uuid)) continue;
            if (mini2.getTrigger() == mini.getTrigger()) {
                sameFrequency = mini2;
                break;
            }
        }

        if (pData.getMinistreakAtSlot(slotHandler.get(uuid)) == mini) {
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            p.sendMessage("§cThis killstreak is already selected!");
            return;
        } else if (pData.getMinistreakUnlockStatus(mini) && sameFrequency == null) {
            pData.setMinistreakAtSlot(slotHandler.get(uuid), mini);
            zl.pitReset(p);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
            openMainStreakGUI(p);
            return;
        } else if (pData.getMinistreakUnlockStatus(mini)) {
            pData.setMinistreakAtSlot(slotHandler.get(uuid), mini);
            pData.setMinistreakAtSlot(badSlot, Ministreaks.UNSET);
            zl.pitReset(p);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
            p.sendMessage("§c§lZOOP! §7Disabled §c" + sameFrequency.getName() + "§7! Can't have two killstreaks with the same kills frequency!");
            openMainStreakGUI(p);
            return;
        } else if (pData.getLevel() < mini.getLevel()) {
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            p.sendMessage("§cYou are too low level to acquire this killstreak!");
            return;
        } else if (pData.getGold() < mini.getCost()) {
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            p.sendMessage("§cYou don't have enough gold to afford this!");
            return;
        }

        confirmGUIHandler.confirmPurchase(p, "§6" + mini.getName(), mini.getCost(), false,
                player -> {
                    pData.setGold(pData.getGold() - mini.getCost());
                    pData.setMinistreakUnlockStatus(mini, true);
                    pData.setMinistreakAtSlot(slotHandler.get(uuid), mini);
                    zl.pitReset(p);
                    p.sendMessage("§a§lPURCHASE! §6" + mini.getName());
                    openMainStreakGUI(p);
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                    slotHandler.remove(p.getUniqueId());
                });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        slotHandler.remove(e.getPlayer().getUniqueId());
    }

    private void openMainGUI(Player p) {
        PlayerData pData = Main.getInstance().getPlayerData(p);
        Inventory mainGUI = Bukkit.createInventory(p, 45, "Permanent upgrades");
        int passiveIndex = 28;

        for (int slot = 1; slot <= 3; slot++) {
            ItemStack item;
            int level = 0;
            List<String> lore = new ArrayList<>();
            Perks perk = pData.getPerkAtSlot(slot);
            String name = ((perk != UNSET) ? "§e" : "§a") + "Perk Slot #" + slot;

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

            if (pData.getLevel() < level && perk == UNSET) {
                mainGUI.setItem(slot + 11, zl.itemBuilder(BEDROCK, 1, "§cPerk Slot #" + slot, Collections.singletonList(
                        "§7Required level: " + zl.getColorBracketAndLevel(0, level)
                )));
                continue;
            }

            if (perk != UNSET) {
                lore.add("§7Selected: §a" + perk.getName());
                lore.add("");
            }

            lore.addAll(perk.getLore());
            lore.add("");
            lore.add("§eClick to choose perk!");

            //special item handling
            if (perk == GOLDEN_HEADS) {
                item = zl.headItemBuilder("PhantomTupac", slot, name, lore);
            } else if (perk == OLYMPUS) {
                item = zl.potionItemBuilder(Color.LIME, slot, name, lore);
            } else {
                item = zl.itemBuilder(perk.getMaterial(), slot, name, lore);
            }

            mainGUI.setItem(slot + 11, item);
        }

        if (pData.getPrestige() != 0 || pData.getLevel() >= 60) {
            List<String> lore = new ArrayList<>(Arrays.asList(
                    "§7Choose killstreak perks which",
                    "§7trigger every time you get X",
                    "§7kills.",
                    " "
            ));
            List<Ministreaks> sortedMinis = new ArrayList<>();

            for (Ministreaks mini : pData.getEquippedMinistreaks()) {
                if (mini == Ministreaks.UNSET) continue;

                if (sortedMinis.isEmpty()) {
                    sortedMinis.add(mini);
                    continue;
                }

                for (int i = sortedMinis.size() - 1; i >= 0; i--) {
                    if (sortedMinis.get(i).getTrigger() < mini.getTrigger()) {
                        sortedMinis.add(i + 1, mini);
                        break;
                    }

                    if (i == 0) sortedMinis.add(0, mini);
                }
            }

            for (Ministreaks mini : sortedMinis) lore.add("§7Every §c" + mini.getTrigger() + " §7kills: §a" + mini.getName());

            lore.addAll(Arrays.asList(
                    "§7Megastreak: §a" + pData.getMegastreak().getName(),
                    " ",
                    "§eClick to edit killstreaks!"
            ));

            ItemStack item = zl.itemBuilder(pData.getMegastreak().getMaterial(), 1, "§aKillstreaks", lore);

            if (pData.getMegastreak() == Megastreaks.UBERSTREAK) {
                ItemMeta meta = item.getItemMeta();

                meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }

            mainGUI.setItem(15, item);
        }

        for (Passives passive : Passives.values()) {
            String name;
            int cost = passive.getCost(p);
            int level = passive.getLevelReq(p);

            if (pData.getLevel() < passive.getBaseLevelReq() && pData.getPrestige() == 0) {
                mainGUI.setItem(passiveIndex, zl.itemBuilder(BEDROCK, 1, "§cUnknown Upgrade", Collections.singletonList(
                        "§7Required level: " + zl.getColorBracketAndLevel(0, passive.getBaseLevelReq())
                )));
                passiveIndex++;
                continue;
            }

            if ((pData.getLevel() >= level && pData.getPassiveTier(passive) == 5) || (pData.getPassiveTier(passive) > 0 && pData.getGold() - cost >= 0)) {
                name = "§a" + passive.getName();
            } else if (pData.getPassiveTier(passive) == 0 && pData.getGold() - cost >= 0 && pData.getLevel() >= level) {
                name = "§e" + passive.getName();
            } else {
                name = "§c" + passive.getName();
            }

            mainGUI.setItem(passiveIndex, zl.itemBuilder(passive.getMaterial(), 1, name, passive.getLore(p)));
            passiveIndex++;
        }

        p.openInventory(mainGUI);
    }

    private void openPerkGUI(Player p, int slot) {
        PlayerData pData = Main.getInstance().getPlayerData(p);
        int slotLevel = 0;

        switch (slot) {
            case 1:
                slotLevel = 10;
                break;
            case 2:
                slotLevel = 35;
                break;
            case 3:
                slotLevel = 70;
                break;
            case 4:
                slotLevel = 100;
                break;
        }

        if (pData.getLevel() < slotLevel) {
            p.sendMessage("§cSlot not unlocked yet!");
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            return;
        }

        Inventory perkGUI = Bukkit.createInventory(p, 36, "Choose a perk");
        int perkIndex = 10;
        int overflowPrevention = 0;

        //not bothering with prestige perks rn
        for (Perks perk : Perks.values()) {
            if (perk.getPrestige() != 0) continue;

            String name;
            int cost = perk.getCost();
            int level = perk.getLevel();
            ItemStack item;

            if (pData.getLevel() < level && pData.getPrestige() == 0) {
                perkGUI.setItem(perkIndex,  zl.itemBuilder(BEDROCK, 1, "§cUnknown perk", Collections.singletonList(
                        "§7Required level: " + zl.getColorBracketAndLevel(0, level)
                )));
                perkIndex++;
                overflowPrevention++;
                continue;
            }

            if (pData.getPerkUnlockStatus(perk)) {
                name = "§a" + perk.getName();
            } else if (pData.getGold() - cost >= 0 && pData.getLevel() >= level) {
                name = "§e" + perk.getName();
            } else {
                name = "§c" + perk.getName();
            }

            List<String> lore = new ArrayList<>(perk.getLore());
            lore.add("");

            if (pData.hasPerkEquipped(perk)) {
                lore.add("§aAlready selected!");
            } else if (pData.getPerkUnlockStatus(perk)) {
                lore.add("§eClick to select!");
            } else {
                lore.add("§7Cost: §6" + zl.getFancyNumberString(cost) + "g");

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
                item = zl.potionItemBuilder(Color.LIME, 1, name, lore);
            } else {
                item = zl.itemBuilder(perk.getMaterial(), 1, name, lore);
            }

            if (pData.hasPerkEquipped(perk)) {
                ItemMeta meta = item.getItemMeta();

                meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }

            perkGUI.setItem(perkIndex, item);
            perkIndex++;
            overflowPrevention++;

            if (overflowPrevention == 7) {
                perkIndex += 2;
                overflowPrevention = 0;
            }
        }

        perkGUI.setItem(perkGUI.getSize() - 5, zl.itemBuilder(ARROW, 1, "§aGo Back", Collections.singletonList(
                "§7To Permanent upgrades"
        )));

        if (pData.getPerkAtSlot(slot) != UNSET) {
            perkGUI.setItem(perkGUI.getSize() - 4, zl.itemBuilder(DIAMOND_BLOCK, 1, "§cNo perk", Arrays.asList(
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

    private void openMainStreakGUI(Player p) {
        PlayerData pData = Main.getInstance().getPlayerData(p);
        Inventory streakGUI = Bukkit.createInventory(p, 27, "Killstreaks");
        int index = 11;
        int level = 0;
        int amount = 1;

        for (int i = 1; i <= 2; i++) {
            if (i == 2) level = 75;
            if (i == 3) level = 100;

            Ministreaks mini = pData.getMinistreakAtSlot(i);

            if (pData.getPrestige() == 0 && i == 2) {
                streakGUI.setItem(index, zl.itemBuilder(BEDROCK, amount, "§cPerk Slot #" + i, Arrays.asList(
                        "§cRequires Prestige I",
                        "§7Reach level [§b§l120§7] to prestige!"
                )));
                index += 2;
                amount++;
                continue;
            }

            if (pData.getLevel() < level && mini == Ministreaks.UNSET) {
                streakGUI.setItem(index, zl.itemBuilder(BEDROCK, amount, "§cPerk Slot #" + i, Collections.singletonList(
                        "§7Required level: " + zl.getColorBracketAndLevel(pData.getPrestige(), level)
                )));
                index += 2;
                amount++;
                continue;
            }

            List<String> lore = new ArrayList<>();

            if (mini != Ministreaks.UNSET) {
                lore.add("§7Selected: §a" + mini.getName());
                lore.add(" ");
            }

            lore.addAll(mini.getLore());
            lore.add(" ");
            lore.add("§eClick to switch streak!");

            ItemStack item = zl.itemBuilder(mini.getMaterial(), amount, "§aPerk Slot #" + i, lore);

            if (mini == Ministreaks.GLASS_PICKAXE) item = zl.itemBuilder(mini.getMaterial(), amount, "§aPerk Slot #" + i, lore, false, false, Pair.of(Enchantment.ARROW_INFINITE, 1));

            streakGUI.setItem(index, item);

            index += 2;
            amount++;
        }

        List<String> megaLore = new ArrayList<>();

        megaLore.add("§7Selected: §a" + pData.getMegastreak().getName());
        megaLore.add(" ");
        megaLore.addAll(pData.getMegastreak().getLore());
        megaLore.add(" ");
        megaLore.add("§eClick to switch megastreak!");

        if (pData.getMegastreak() == Megastreaks.UBERSTREAK) {
            streakGUI.setItem(index, zl.itemBuilder(pData.getMegastreak().getMaterial(), amount, "§eMegastreak", megaLore, false, false, Pair.of(Enchantment.ARROW_INFINITE, 1)));
        } else {
            streakGUI.setItem(index, zl.itemBuilder(pData.getMegastreak().getMaterial(), amount, "§eMegastreak", megaLore));
        }
        streakGUI.setItem(22, zl.itemBuilder(ARROW, 1, "§aGo Back", Collections.singletonList("§7To Permanent upgrades")));

        p.openInventory(streakGUI);
    }

    private void openMegastreakGUI(Player p) {
        PlayerData pData = Main.getInstance().getPlayerData(p);
        Inventory gui = Bukkit.createInventory(p, 27, "Choose a killstreak§2");

        for (int i = 10; i < 17; i++) {
            String color;
            Megastreaks mega = Megastreaks.values()[i - 10];
            List<String> lore = new ArrayList<>(mega.getLore());

            if (pData.getPrestige() < mega.getPrestige()) continue;

            if (pData.getMegastreak() == mega || pData.getMegastreakUnlockStatus(mega)) {
                color = "§a";
            } else if (pData.getGold() >= mega.getCost()) {
                color = "§e";
            } else {
                color = "§c";
            }

            lore.add(" ");

            if (pData.getMegastreak() == mega) {
                lore.add("§aAlready selected!");
            } else if (pData.getMegastreakUnlockStatus(mega)) {
                lore.add("§eClick to select!");
            } else {
                lore.add("§7Cost: §6" + zl.getFancyNumberString(mega.getCost()) + "g");

                if (pData.getLevel() < mega.getLevel()) {
                    lore.add("§7Required level: " + zl.getColorBracketAndLevel(pData.getPrestige(), mega.getLevel()));
                    lore.add("§cToo low level!");
                } else if (pData.getGold() >= mega.getCost()) {
                    lore.add("§eClick to purchase!");
                } else {
                    lore.add("§cNot enough gold!");
                }
            }

            if (pData.getMegastreak() == mega || mega == Megastreaks.UBERSTREAK) {
                gui.setItem(i, zl.itemBuilder(mega.getMaterial(), 1, color + mega.getName(), lore, false, false, Pair.of(Enchantment.ARROW_INFINITE, 1)));
            } else {
                gui.setItem(i, zl.itemBuilder(mega.getMaterial(), 1, color + mega.getName(), lore));
            }
        }

        gui.setItem(22, zl.itemBuilder(ARROW, 1, "§aGo Back", Collections.singletonList("§7To Killstreaks")));
        p.openInventory(gui);
    }

    private void openMinistreakGUI(Player p, int slot) {
        PlayerData pData = Main.getInstance().getPlayerData(p);
        Inventory gui = Bukkit.createInventory(p, 54, "Choose a killstreak§1");
        int trigger = 0;
        int index = (pData.getPrestige() >= 4) ? 1 : 10;
        int frame = index;
        Ministreaks willBeDisabled = null;

        for (Ministreaks mini : Ministreaks.values()) {
            if (pData.getPrestige() < mini.getPrestige()) continue;

            if (trigger != mini.getTrigger()) {
                if (trigger != 0) frame += 9;

                gui.setItem(frame, zl.itemBuilder(ITEM_FRAME, 1, "§c" + mini.getTrigger() + " Kills", null));

                trigger = mini.getTrigger();
                index = frame + 1;
                willBeDisabled = null;

                for (int i = 1; i <= 3; i++) {
                    if (i == slot) continue;

                    if (pData.getMinistreakAtSlot(i).getTrigger() == trigger) {
                        willBeDisabled = pData.getMinistreakAtSlot(i);
                    }
                }
            }

            if (pData.getPrestige() == 0 && pData.getLevel() < mini.getLevel()) {
                gui.setItem(index, zl.itemBuilder(BEDROCK, 1, "§cUnknown killstreak", Collections.singletonList(
                        "§7Required level: " + zl.getColorBracketAndLevel(0, mini.getLevel())
                )));
                index++;
                continue;
            }

            List<String> lore = new ArrayList<>(mini.getLore());
            String color;

            if (pData.hasMinistreakEquipped(mini) || pData.getMinistreakUnlockStatus(mini)) {
                color = "§a";
            } else if (pData.getGold() >= mini.getCost()) {
                color = "§e";
            } else {
                color = "§c";
            }

            lore.add(" ");

            if (pData.hasMinistreakEquipped(mini)) {
                lore.add("§aAlready selected!");
            } else if (pData.getMinistreakUnlockStatus(mini)) {
                lore.add("§eClick to select!");

                if (willBeDisabled != null) {
                    lore.add("§7§oDisables " + willBeDisabled.getName());
                }
            } else {
                lore.add("§7Cost: §6" + zl.getFancyNumberString(mini.getCost()) + "g");

                if (pData.getLevel() < mini.getLevel()) {
                    lore.add("§7Required level: " + zl.getColorBracketAndLevel(pData.getPrestige(), mini.getLevel()));
                    lore.add("§cToo low level!");
                } else if (pData.getGold() >= mini.getCost()) {
                    lore.add("§eClick to purchase!");
                } else {
                    lore.add("§cNot enough gold!");
                }
            }

            ItemStack item = zl.itemBuilder(mini.getMaterial(), 1, color + mini.getName(), lore);

            if (pData.hasMinistreakEquipped(mini)) {
                gui.setItem(frame, zl.itemBuilder(ITEM_FRAME, 1, "§c" + mini.getTrigger() + " Kills", Collections.singletonList(
                        "§7Selected: §e" + mini.getName()
                ), false, false, Pair.of(Enchantment.ARROW_INFINITE, 1)));

                item = zl.itemBuilder(mini.getMaterial(), 1, color + mini.getName(), lore, false, false, Pair.of(Enchantment.ARROW_INFINITE, 1));
            }

            if (mini == Ministreaks.ARQUEBUSIER) item.setAmount(12);
            if (mini == Ministreaks.GLASS_PICKAXE) item = zl.itemBuilder(mini.getMaterial(), 1, color + mini.getName(), lore, false, false, Pair.of(Enchantment.ARROW_INFINITE, 1));

            gui.setItem(index, item);
            index++;
        }

        gui.setItem(49, zl.itemBuilder(ARROW, 1, "§aGo Back", Collections.singletonList("§7To Killstreaks")));

        if (pData.getMinistreakAtSlot(slot) != Ministreaks.UNSET) {
            gui.setItem(50, zl.itemBuilder(GOLD_BLOCK, 1, "§cNo killstreak", Arrays.asList(
                    "§7Wanna free up this slot for some",
                    "§7reason?",
                    " ",
                    "§eClick to remove killstreak!"
            )));
        }

        slotHandler.put(p.getUniqueId(), slot);
        p.openInventory(gui);
    }
}