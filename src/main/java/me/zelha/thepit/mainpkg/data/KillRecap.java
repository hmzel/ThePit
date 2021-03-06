package me.zelha.thepit.mainpkg.data;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitAssistEvent;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.events.PitKillEvent;
import me.zelha.thepit.events.ResourceManager;
import me.zelha.thepit.utils.ZelLogic;
import me.zelha.thepit.zelenums.Perks;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;

public class KillRecap implements CommandExecutor, Listener {

    private static final Map<UUID, List<DamageLog>> damageTrackerMap = new HashMap<>();
    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final Map<UUID, ItemStack> bookMap = new HashMap<>();

    /**
     * adds a new DamageLog to the given player's kill recap<p>
     * note: must be called before damage is dealt,
     * in case the player is killed by the dealt damage and the death method is called before the log is added
     *
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (args.length != 1) return true;
        //regex checks if its a uuid (totally did not copy)
        if (!args[0].matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) return true;
        if (bookMap.get(UUID.fromString(args[0])) == null) {
            sender.sendMessage("??cThis recap has expired!");
            return true;
        }

        ((Player) sender).openBook(bookMap.get(UUID.fromString(args[0])));
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAttack(PitDamageEvent e) {
        addDamageLog(e.getDamaged(), new DamageLog(e, false));
        addDamageLog(e.getDamager(), new DamageLog(e, true));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent e) {
        if (zl.spawnCheck(e.getEntity().getLocation())) return;
        if (!zl.playerCheck(e.getEntity())) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) return;
        if (e.getDamage() <= 0) return;

        Player p = (Player) e.getEntity();
        String damageType = null;

        switch (e.getCause()) {
            case SUFFOCATION:
                damageType = "??fSuffocation";
                break;
            case FIRE:
                damageType = "??6Fire";
                break;
            case DROWNING:
                damageType = "??9Drowned";
                break;
            case VOID:
                damageType = "??fVoid";
                break;
        }

        if (damageType != null) addDamageLog(p, new DamageLog(e.getDamage(), damageType, true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(PitKillEvent e) {
        Player dead = e.getDead();
        Player killer = e.getKiller();
        ItemStack recapBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) recapBook.getItemMeta();
        List<BaseComponent[]> raw = new ArrayList<>();

        bookMeta.setTitle(dead.getName());
        bookMeta.setAuthor("13");

        raw.add(new ComponentBuilder("??c??lKILL RECAP\n").create());
        raw.add(new ComponentBuilder("??8" + DateTimeFormatter.ofPattern("MM/dd/yy h:mm a").format(LocalDateTime.now()) + "\n")
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("??7Hypixel server time"))).create());
        raw.add(playerComponent(dead));
        raw.add(new ComponentBuilder("\n").create());
        raw.add(new ComponentBuilder("Killer:\n").create());
        raw.add(playerComponent(killer));
        raw.add(new ComponentBuilder("??8for ").create());
        raw.add(expComponent(e));
        raw.add(goldComponent(e));
        raw.add(new ComponentBuilder("\n").create());

        boolean addAssistTitle = true;

        for (PitAssistEvent assist : e.getAssistEvents()) {
            if (assist.getAssisted() == null) continue;
            if (addAssistTitle) raw.add(new ComponentBuilder("Assists:\n").create());

            raw.add(new ComponentBuilder((int) ((Double.parseDouble(BigDecimal.valueOf(assist.getPercentage()).setScale(2, RoundingMode.HALF_EVEN).toString())) * 100) + "% ").create());
            raw.add(playerComponent(assist.getAssisted()));
            raw.add(new ComponentBuilder("??8for ").create());
            raw.add(expComponent(assist));
            raw.add(goldComponent(assist));

            addAssistTitle = false;
        }

        raw.add(new ComponentBuilder("Damage Log:\n").create());

        for (DamageLog damageLog : damageTrackerMap.get(dead.getUniqueId())) {
            int timeBeforeDeath = (MinecraftServer.currentTick - damageLog.time()) / 20;
            NBTTagCompound nbt = (damageLog.item() != null && CraftItemStack.asNMSCopy(damageLog.item()).hasTag()) ? CraftItemStack.asNMSCopy(damageLog.item()).getTag() : new NBTTagCompound();

            raw.add(new ComponentBuilder("??8" + timeBeforeDeath + "s ").event(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("??7Happened " + timeBeforeDeath + " second(s) before death"))).create()
            );
            raw.add(new ComponentBuilder("??c" + BigDecimal.valueOf(damageLog.damage()).setScale(1, RoundingMode.DOWN) + " ").event(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("??cDamage"))).create()
            );

            if (zl.itemCheck(damageLog.item())) {
                raw.add(new ComponentBuilder(damageLog.pitDamageType() + "\n").event(
                                new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(damageLog.item().getType().getKey().toString(),
                                damageLog.item().getAmount(), ItemTag.ofNbt(nbt.toString())))).create()
                );
            } else {
                ComponentBuilder builder2 = new ComponentBuilder(damageLog.pitDamageType() + "\n");

                if (damageLog.environmental()) builder2.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("??aEnvironmental damage")));

                raw.add(builder2.create());
            }

            if (!damageLog.hasPlayer()) {
                raw.add(new ComponentBuilder("\n").create());
                continue;
            }

            if (damageLog.isAttacker()) {
                raw.add(new ComponentBuilder("??8to ").create());
            } else {
                raw.add(new ComponentBuilder("??8by ").create());
            }

            raw.add(new ComponentBuilder("??7" + damageLog.mainName() + "\n").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                            damageLog.prestigeToShow() + " ??7" + damageLog.mainName() + "\n" + "??7" + damageLog.subName() + " ??fHP after: ??c"
                            + Math.max(Double.parseDouble(BigDecimal.valueOf(damageLog.damagedHealth()).setScale(1, RoundingMode.DOWN).toString()), 0.0)
                    ))).create());
            raw.add(new ComponentBuilder("\n").create());
        }

        if (e.causedByDisconnect()) {
            raw.add(new ComponentBuilder("??80s ??cDISCONNECTED").create());
        } else {
            raw.add(new ComponentBuilder("??80s ??cDEAD").create());
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
        bookMap.put(dead.getUniqueId(), recapBook);
        damageTrackerMap.put(dead.getUniqueId(), new ArrayList<>());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        damageTrackerMap.put(e.getPlayer().getUniqueId(), new ArrayList<>());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeave(PlayerQuitEvent e) {
        damageTrackerMap.remove(e.getPlayer().getUniqueId());
    }

    private BaseComponent[] playerComponent(Player player) {
        StringBuilder builder = new StringBuilder(zl.getColorBracketAndLevel(player) + "??7 " + player.getName() + "\n");
        PlayerData pData = Main.getInstance().getPlayerData(player);

        if (pData.getStreak() != 0) builder.append("??7Streak: ??c" + (int) pData.getStreak() + "\n");

        for (int i = 1; i <= 4; i++) {
            if (pData.getPerkAtSlot(i) != Perks.UNSET) builder.append("??e" + pData.getPerkAtSlot(i).getName() + "\n");
        }

        builder.replace(builder.length() - 1, builder.length(), "");

        return new ComponentBuilder("??7" + player.getName() + "\n").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(builder.toString()))).create();
    }

    private BaseComponent[] expComponent(ResourceManager resources) {
        double exp = 0;
        StringBuilder builder = new StringBuilder();
        String plus = "";

        for (Entry<String, Double> entry : resources.getExpAdditions().entrySet()) {
            String value;

            if (entry.getValue() == (int) entry.getValue().doubleValue()) {
                value = (int) entry.getValue().doubleValue() + "";
            } else {
                value = BigDecimal.valueOf(entry.getValue()).setScale(2, RoundingMode.HALF_EVEN).toString();

                if (Double.valueOf(value).equals(Double.valueOf(BigDecimal.valueOf(entry.getValue()).setScale(1, RoundingMode.HALF_EVEN).toString()))) {
                    value = BigDecimal.valueOf(entry.getValue()).setScale(1, RoundingMode.HALF_EVEN).toString();
                }
            }

            exp += entry.getValue();
            builder.append("??f" + entry.getKey() + "??f: ??b" + plus + value + "\n");

            if (plus.equals("")) plus = "+";
        }

        for (Entry<String, Double> entry : resources.getExpModifiers().entrySet()) {
            String operation = "+";
            int value = (int) (100 * entry.getValue());

            if (value < 100) {
                operation = "-";
                value = 100 - value;
            } else {
                value -= 100;
            }

            exp *= entry.getValue();
            builder.append("??f" + entry.getKey() + "??f: ??b" + operation + value + "%\n");
        }

        builder.append("??fRounded up!\n");

        builder.replace(builder.length() - 1, builder.length(), "");

        return new ComponentBuilder("??3+" + (int) Math.min(Math.ceil(exp), resources.getMaxExp()) + "XP ").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(builder.toString()))).create();
    }

    private BaseComponent[] goldComponent(ResourceManager resources) {
        double gold = 0;
        StringBuilder builder = new StringBuilder();
        String plus = "";

        for (Entry<String, Double> entry : resources.getGoldAdditions().entrySet()) {
            String value;

            if (entry.getValue() == (int) entry.getValue().doubleValue()) {
                value = (int) entry.getValue().doubleValue() + "";
            } else {
                value = BigDecimal.valueOf(entry.getValue()).setScale(2, RoundingMode.HALF_EVEN).toString();

                if (Double.valueOf(value).equals(Double.valueOf(BigDecimal.valueOf(entry.getValue()).setScale(1, RoundingMode.HALF_EVEN).toString()))) {
                    value = BigDecimal.valueOf(entry.getValue()).setScale(1, RoundingMode.HALF_EVEN).toString();
                }
            }

            gold += entry.getValue();
            builder.append("??f" + entry.getKey() + "??f: ??6" + plus + value + "\n");

            if (plus.equals("")) {
                for (Entry<String, Double> entry2 : resources.getBaseGoldModifiers().entrySet()) {
                    String operation = "+";
                    int value2 = (int) (100 * entry2.getValue());

                    if (value2 < 100) {
                        operation = "-";
                        value2 = 100 - value2;
                    } else {
                        value2 -= 100;
                    }

                    gold *= entry2.getValue();
                    builder.append("??f" + entry2.getKey() + "??f: ??6" + operation + value2 + "%\n");
                }

                plus = "+";
            }
        }

        for (Entry<String, Double> entry : resources.getGoldModifiers().entrySet()) {
            String operation = "+";
            int value = (int) (100 * entry.getValue());

            if (value < 100) {
                operation = "-";
                value = 100 - value;
            } else {
                value -= 100;
            }

            gold *= entry.getValue();
            builder.append("??f" + entry.getKey() + "??f: ??6" + operation + value + "%\n");
        }

        for (Entry<String, Double> entry : resources.getSecondaryGoldAdditions().entrySet()) {
            String value;

            if (entry.getValue() == (int) entry.getValue().doubleValue()) {
                value = (int) entry.getValue().doubleValue() + "";
            } else {
                value = BigDecimal.valueOf(entry.getValue()).setScale(2, RoundingMode.HALF_EVEN).toString();

                if (Double.valueOf(value).equals(Double.valueOf(BigDecimal.valueOf(entry.getValue()).setScale(1, RoundingMode.HALF_EVEN).toString()))) {
                    value = BigDecimal.valueOf(entry.getValue()).setScale(1, RoundingMode.HALF_EVEN).toString();
                }
            }

            gold += entry.getValue();
            builder.append("??f" + entry.getKey() + "??f: ??6+" + value + "\n");
        }

        builder.replace(builder.length() - 1, builder.length(), "");

        return new ComponentBuilder("??6" + zl.getFancyGoldString(gold) + "g\n").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(builder.toString()))).create();
    }
}






















































