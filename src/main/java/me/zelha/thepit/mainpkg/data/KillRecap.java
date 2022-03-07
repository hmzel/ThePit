package me.zelha.thepit.mainpkg.data;

import me.zelha.thepit.Main;
import me.zelha.thepit.ZelLogic;
import me.zelha.thepit.mainpkg.listeners.AssistListener;
import me.zelha.thepit.mainpkg.listeners.KillListener;
import me.zelha.thepit.upgrades.permanent.perks.PerkListenersAndUtils;
import me.zelha.thepit.zelenums.Passives;
import me.zelha.thepit.zelenums.Perks;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static me.zelha.thepit.zelenums.Perks.BOUNTY_HUNTER;
import static me.zelha.thepit.zelenums.Perks.STREAKER;
import static org.bukkit.Material.GOLDEN_LEGGINGS;

public class KillRecap implements CommandExecutor, Listener {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final KillListener killUtils = Main.getInstance().getKillUtils();
    private final AssistListener assistUtils = Main.getInstance().getAssistUtils();
    private final PerkListenersAndUtils perkUtils = Main.getInstance().getPerkUtils();
    private final Map<UUID, ItemStack> bookMap = new HashMap<>();
    private static final Map<UUID, List<DamageLog>> damageTrackerMap = new HashMap<>();

    //i dislike using static but i think its fine here since its only one method
    /**
     * adds a new DamageLog to the given player's recap log <p>
     * note: must be called before damage is dealt,
     * in case the player is killed by the dealt damage and the death method is called before the log is added
     * @param player player to add the log to
     * @param log new log to add
     */
    public static void addDamageLog(Player player, DamageLog log) {
        damageTrackerMap.get(player.getUniqueId()).add(log);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (damageTrackerMap.get(player.getUniqueId()) != null) damageTrackerMap.get(player.getUniqueId()).remove(log);
            }
        }.runTaskLater(Main.getInstance(), 200);
    }

    private BaseComponent[] expComponent(Player dead, Player receiver) {
        boolean isKiller = receiver.getUniqueId().equals(assistUtils.getLastDamager(dead).getUniqueId());
        StringBuilder builder = new StringBuilder();
        double streakModifier = 0;
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData receiverData = Main.getInstance().getPlayerData(receiver);

        builder.append("§fBase §bXP§f: §b5\n");

        //xp bump "§fRenown XP Bump: §b+?"
        if (receiverData.getStreak() <= (receiverData.getPassiveTier(Passives.EL_GATO) - 1) && isKiller) builder.append("§fEl Gato: §b+5\n");

        if (receiverData.getStreak() == 4) {
            streakModifier = 3;
        } else if (receiverData.getStreak() >= 5 && receiverData.getStreak() < 20) {
            streakModifier = 5;
        } else if (receiverData.getStreak() < 200 && receiverData.getStreak() >= 20) {
            streakModifier = Math.floor(receiverData.getStreak() / 10.0D) * 3;
        } else if (receiverData.getStreak() >= 200) {
            streakModifier = 60;
        }

        if (receiverData.getStreak() >= 4 && receiverData.hasPerkEquipped(STREAKER) && isKiller) {
            builder.append("§fKiller on streak (Streaker Perk): §b+" + (streakModifier * 3) + "\n");
        } else if (receiverData.getStreak() >= 4 && isKiller) {
            builder.append("§fKiller on streak: §b+" + (int) streakModifier + "\n");
        }

        //second gapple "§fSecond Gapple: §b+?"
        //explicious "§fExplicious: §b+?"
        if (deadData.getStreak() > 5 && isKiller) builder.append("§fStreak Shutdown: §b+" + (int) Math.min(Math.round(deadData.getStreak()), 25) + "\n");
        if (receiverData.getStreak() <= 3 && (receiverData.getLevel() <= 30 || receiverData.getPrestige() == 0) && isKiller) builder.append("§fFirst 3 kills: §b+4\n");
        if (deadData.getLevel() > receiverData.getLevel()) builder.append("§fLevel difference: §b+" + (int) Math.round((deadData.getLevel() - receiverData.getLevel()) / 4.5) + "\n");

        //koth "§fKOTH: §b+300%"
        //2x event "§f2x Event: §b+100%"
        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) builder.append("§fKilled a noob: §b-10%\n");
        if (receiverData.getPassiveTier(Passives.XP_BOOST) > 0) builder.append("§fXP Boost: §b+" + receiverData.getPassiveTier(Passives.XP_BOOST) * 10 + "%\n");
        //overdrive "§fOverdrive: §b+?%"
        //beastmode "§fBeastmode: §b+?%"
        //royalty "§fRoyalty: §b+10%"
        //genesis "§fGenesis: §b+?%"
        //assistant "§fAssistant: §b+?%"

        if (!isKiller) builder.append("§fKill participation: §b-"
                + (int) ((1 - (Double.parseDouble(BigDecimal.valueOf(assistUtils.getAssistMap(dead).get(receiver.getUniqueId()) / dead.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()).setScale(2, RoundingMode.HALF_EVEN).toString()))) * 100)
                + "%\n");

        builder.append("§fRounded up!\n");

        //pit day "§fGame Multiplier: §6+100%"

        builder.replace(builder.length() - 1, builder.length(), "");

        if (isKiller) {
            return new ComponentBuilder("§3+" + killUtils.calculateEXP(dead, receiver) + "XP ").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(builder.toString()))).create();
        } else {
            return new ComponentBuilder("§3+" + assistUtils.calculateAssistEXP(dead, receiver) + "XP ").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(builder.toString()))).create();
        }
    }

    private BaseComponent[] goldComponent(Player dead, Player receiver) {
        boolean isKiller = receiver.equals(assistUtils.getLastDamager(dead));
        StringBuilder builder = new StringBuilder();
        PlayerData deadData = Main.getInstance().getPlayerData(dead);
        PlayerData receiverData = Main.getInstance().getPlayerData(receiver);
        PlayerInventory killerInv = receiver.getInventory();

        builder.append("§fBase §6gold (g)§f: §610\n");

        if (perkUtils.hasBeenShotBySpammer(receiver, dead) && isKiller) builder.append("§fSpammer: §6+200%\n");
        if (receiverData.hasPerkEquipped(BOUNTY_HUNTER) && zl.itemCheck(killerInv.getLeggings()) && killerInv.getLeggings().getType() == GOLDEN_LEGGINGS && isKiller) {
            builder.append("§fBounty Hunter: §6+4\n");
        }

        if (receiverData.getStreak() <= receiverData.getPassiveTier(Passives.EL_GATO) && isKiller) builder.append("§fEl Gato: §6+5\n");
        if (deadData.getStreak() > 5 && isKiller) builder.append("§fStreak shutdown: §6+" + Math.min((int) Math.round(deadData.getStreak()), 30) + "\n");
        if (receiverData.getStreak() <= 3 && (receiverData.getLevel() <= 30 || receiverData.getPrestige() == 0) && isKiller) builder.append("§fFirst 3 kills: §6+4\n");
        //genesis "§fGenesis: §6+?"
        //moctezuma "§fMoctezuma: §6+?"
        //gold bump enchant "§fGold Bump Enchant: §6+?"
        if (dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() > receiver.getAttribute(Attribute.GENERIC_ARMOR).getValue() && Math.round((dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() - receiver.getAttribute(Attribute.GENERIC_ARMOR).getValue()) / 5) != 0) {
            builder.append("§fArmor difference: §6+" + Math.round((dead.getAttribute(Attribute.GENERIC_ARMOR).getValue() - receiver.getAttribute(Attribute.GENERIC_ARMOR).getValue()) / 5) + "\n");
        }
        //gold stack "§fGold Stack: §6+?"
        //assistant "§fAssistant: §6+?"

        //koth "§fKOTH: §6+300%"
        //2x event "§f2x Event: §6+100%"
        if (deadData.getPrestige() == 0 && deadData.getLevel() <= 20) builder.append("§fKilled a noob: §6-10%\n");
        if (receiverData.getPassiveTier(Passives.GOLD_BOOST) > 0) builder.append("§fGold Boost: §6+" + receiverData.getPassiveTier(Passives.GOLD_BOOST) * 10 + "%\n");
        //renown gold boost "§fRenown Gold Boost: §6+?%"
        //gold boost enchant "§fGold Boost Enchant: §6+?%"
        //overdrive "§fOverdrive: §6+?%"
        //beastmode "§fBeastmode: §6+?%"
        //highlander "§fHighlander: §6+?%"

        if (!isKiller) builder.append("§fKill participation: §6-"
                + (int) ((1 - (Double.parseDouble(BigDecimal.valueOf(assistUtils.getAssistMap(dead).get(receiver.getUniqueId()) / dead.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()).setScale(2, RoundingMode.HALF_EVEN).toString()))) * 100)
                + "%\n");

        //celebrity "§fCelebrity: §6+100%"
        //pit day "§fGame Multiplier: §6+100%"
        //conglomerate "§fConglomerate: §6+?"
        if (receiverData.hasPerkEquipped(Perks.SPAMMER) && !isKiller) builder.append("§fSpammer Assist: §6+2\n");
        if (receiverData.hasPerkEquipped(Perks.BOUNTY_HUNTER) && zl.itemCheck(receiver.getInventory().getLeggings()) && receiver.getInventory().getLeggings().getType() == Material.GOLDEN_LEGGINGS && deadData.getBounty() != 0 && !isKiller) {
            builder.append("§fBounty Hunter Assist: §6+" + zl.getFancyGoldString(deadData.getBounty() * (assistUtils.getAssistMap(dead).get(receiver.getUniqueId()) / dead.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())) + "\n");
        }

        if (deadData.getBounty() != 0 && isKiller) builder.append("§fBounty: §6+" + deadData.getBounty() + "\n");

        builder.replace(builder.length() - 1, builder.length(), "");

        if (isKiller) {
            return new ComponentBuilder("§6" + zl.getFancyGoldString(killUtils.calculateGold(dead, receiver)) + "g\n").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(builder.toString()))).create();
        } else {
            return new ComponentBuilder("§6" + zl.getFancyGoldString(assistUtils.calculateAssistGold(dead, receiver)) + "g\n").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(builder.toString()))).create();
        }
    }

    private BaseComponent[] playerComponent(Player player) {
        StringBuilder builder = new StringBuilder(zl.getColorBracketAndLevel(player.getUniqueId().toString()) + "§7 " + player.getName() + "\n");
        PlayerData pData = Main.getInstance().getPlayerData(player);

        if (pData.getStreak() != 0) builder.append("§7Streak: §c" + (int) pData.getStreak() + "\n");

        for (int i = 1; i <= 4; i++) {
            if (pData.getPerkAtSlot(i) != Perks.UNSET) builder.append("§e" + pData.getPerkAtSlot(i).getName() + "\n");
        }

        builder.replace(builder.length() - 1, builder.length(), "");

        return new ComponentBuilder("§7" + player.getName() + "\n").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(builder.toString()))).create();
    }

    private void deathMethod(Player player, boolean disconnected) {
        if (assistUtils.getLastDamager(player) == null) return;

        Player killer = assistUtils.getLastDamager(player);
        ItemStack recapBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) recapBook.getItemMeta();
        List<BaseComponent[]> raw = new ArrayList<>();

        bookMeta.setTitle(player.getName());
        bookMeta.setAuthor("13");

        raw.add(new ComponentBuilder("§c§lKILL RECAP\n").create());
        raw.add(new ComponentBuilder("§8" + DateTimeFormatter.ofPattern("MM/dd/yy h:mm a").format(LocalDateTime.now()) + "\n")
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Hypixel server time"))).create());
        raw.add(playerComponent(player));
        raw.add(new ComponentBuilder("\n").create());
        raw.add(new ComponentBuilder("Killer:\n").create());
        raw.add(playerComponent(killer));
        raw.add(new ComponentBuilder("§8for ").create());
        raw.add(expComponent(player, killer));
        raw.add(goldComponent(player, killer));
        raw.add(new ComponentBuilder("\n").create());

        Map<UUID, Double> sortedAssistsMap = assistUtils.getAssistMap(player).entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        boolean addAssistTitle = true;

        for (UUID uuid : sortedAssistsMap.keySet()) {
            if (Bukkit.getPlayer(uuid) == null || uuid.equals(player.getUniqueId()) || uuid.equals(killer.getUniqueId())) {
                continue;
            }

            if (addAssistTitle) raw.add(new ComponentBuilder("Assists:\n").create());

            raw.add(new ComponentBuilder((int) ((Double.parseDouble(BigDecimal.valueOf(sortedAssistsMap.get(uuid) / player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()).setScale(2, RoundingMode.HALF_EVEN).toString())) * 100) + "% ").create());
            raw.add(playerComponent(Bukkit.getPlayer(uuid)));
            raw.add(new ComponentBuilder("§8for ").create());
            raw.add(expComponent(player, Bukkit.getPlayer(uuid)));
            raw.add(goldComponent(player, Bukkit.getPlayer(uuid)));

            addAssistTitle = false;
        }

        raw.add(new ComponentBuilder("Damage Log:\n").create());

        for (DamageLog damageLog : damageTrackerMap.get(player.getUniqueId())) {
            int timeBeforeDeath = (MinecraftServer.currentTick - damageLog.time()) / 20;
            NBTTagCompound nbt = (damageLog.item() != null && CraftItemStack.asNMSCopy(damageLog.item()).hasTag()) ? CraftItemStack.asNMSCopy(damageLog.item()).getTag() : new NBTTagCompound();

            raw.add(new ComponentBuilder("§8" + timeBeforeDeath + "s ")
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Happened " + timeBeforeDeath + " second(s) before death"))).create());
            raw.add(new ComponentBuilder("§c" + BigDecimal.valueOf(damageLog.damage()).setScale(1, RoundingMode.DOWN) + " ")
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§cDamage"))).create());

            if (zl.itemCheck(damageLog.item())) {
                raw.add(new ComponentBuilder(damageLog.pitDamageType() + "\n")
                        .event(new HoverEvent(HoverEvent.Action.SHOW_ITEM
                                , new Item(damageLog.item().getType().getKey().toString()
                                , damageLog.item().getAmount()
                                , ItemTag.ofNbt(nbt.toString())))).create());
            } else {
                ComponentBuilder builder2 = new ComponentBuilder(damageLog.pitDamageType() + "\n");

                if (damageLog.environmental()) builder2.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aEnvironmental damage")));

                raw.add(builder2.create());
            }

            if (!damageLog.hasPlayer()) {
                raw.add(new ComponentBuilder("\n").create());
                continue;
            }

            if (damageLog.isAttacker()) {
                raw.add(new ComponentBuilder("§8to ").create());
            } else {
                raw.add(new ComponentBuilder("§8by ").create());
            }

            raw.add(new ComponentBuilder("§7" + damageLog.mainName() + "\n")
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(damageLog.prestigeToShow() + " §7" + damageLog.mainName() + "\n"
                            + "§7" + damageLog.subName() + " §fHP after: §c"
                            + Math.max(Double.parseDouble(BigDecimal.valueOf(damageLog.damagedHealth()).setScale(1, RoundingMode.DOWN).toString()), 0.0))))
                    .create());
            raw.add(new ComponentBuilder("\n").create());
        }

        if (disconnected) {
            raw.add(new ComponentBuilder("§80s §cDISCONNECTED").create());
        } else {
            raw.add(new ComponentBuilder("§80s §cDEAD").create());
        }

        List<BaseComponent[]> pages = new ArrayList<>();
        ComponentBuilder builder = new ComponentBuilder();
        int lines = 0;

        //yeah yeah i know duplicate code is bad but this is a lot less janky than anything else i could do im pretty sure
        for (BaseComponent[] rawComponent : raw) {
            if (rawComponent[0].toPlainText().contains("Damage Log:") && !addAssistTitle && lines < 13 && lines != 0) {
                pages.add(builder.create());
                builder = new ComponentBuilder();
                lines = 0;
            }

            if (rawComponent[0].toPlainText().contains("\n")) lines++;

            if (rawComponent[0].getHoverEvent() != null) {
                builder.append(rawComponent);
            } else {
                builder.append(new ComponentBuilder(rawComponent[0]).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(""))).create());
            }

            if (lines == 13) {
                pages.add(builder.create());
                builder = new ComponentBuilder();
                lines = 0;
            }
        }

        if (!builder.getParts().isEmpty()) pages.add(builder.create());

        for (BaseComponent[] page : pages) bookMeta.spigot().addPage(page);

        recapBook.setItemMeta(bookMeta);
        bookMap.put(player.getUniqueId(), recapBook);
        damageTrackerMap.put(player.getUniqueId(), new ArrayList<>());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (args.length != 1) return true;
        if (!args[0].matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) return true;
        if (bookMap.get(UUID.fromString(args[0])) == null) {
            sender.sendMessage("§cThis recap has expired!");
            return true;
        }

        ((Player) sender).openBook(bookMap.get(UUID.fromString(args[0])));
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity damagedEntity = e.getEntity();
        Entity damagerEntity = e.getDamager();
        Player damaged;
        Player damager;

        if (zl.spawnCheck(damagedEntity.getLocation()) || zl.spawnCheck(damagerEntity.getLocation())) return;
        if (zl.playerCheck(damagedEntity)) damaged = (Player) damagedEntity; else return;
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) return;
        if (e.getFinalDamage() <= 0) return;

        if (damagerEntity instanceof Arrow && ((Arrow) damagerEntity).getShooter() instanceof Player && zl.playerCheck((Player) ((Arrow) damagerEntity).getShooter())) {
            damager = (Player) ((Arrow) damagerEntity).getShooter();
        } else if (zl.playerCheck(damagerEntity)) {
            damager = (Player) damagerEntity;
        } else {
            return;
        }

        DamageLog currentLog1 = new DamageLog(e, false);
        DamageLog currentLog2 = new DamageLog(e, true);

        damageTrackerMap.get(damaged.getUniqueId()).add(currentLog1);
        damageTrackerMap.get(damager.getUniqueId()).add(currentLog2);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (damageTrackerMap.get(damaged.getUniqueId()) != null) damageTrackerMap.get(damaged.getUniqueId()).remove(currentLog1);
                if (damageTrackerMap.get(damaged.getUniqueId()) != null) damageTrackerMap.get(damager.getUniqueId()).remove(currentLog2);
            }
        }.runTaskLater(Main.getInstance(), 200);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent e) {
        if (zl.spawnCheck(e.getEntity().getLocation())) return;
        if (!zl.playerCheck(e.getEntity())) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) return;
        if (e.getFinalDamage() <= 0) return;

        Player p = (Player) e.getEntity();
        String damageType = null;

        switch (e.getCause()) {
            case DROWNING:
                damageType = "§9Drowned";
        }

        if (damageType != null) addDamageLog(p, new DamageLog(e.getDamage(), damageType, true));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDamageEvent e) {
        if (!zl.playerCheck(e.getEntity())) return;
        if (((Player) e.getEntity()).getHealth() - e.getFinalDamage() > 0) return;

        deathMethod((Player) e.getEntity(), false);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        damageTrackerMap.put(e.getPlayer().getUniqueId(), new ArrayList<>());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeave(PlayerQuitEvent e) {
        if (Main.getInstance().getPlayerData(e.getPlayer()).getCombatLogged()) deathMethod(e.getPlayer(), true);

        damageTrackerMap.remove(e.getPlayer().getUniqueId());
    }
}






















































