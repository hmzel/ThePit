package me.zelha.thepit.mainpkg.data;

import com.mongodb.client.MongoCollection;
import me.zelha.thepit.Main;
import me.zelha.thepit.zelenums.Megastreaks;
import me.zelha.thepit.zelenums.Ministreaks;
import me.zelha.thepit.zelenums.Passives;
import me.zelha.thepit.zelenums.Perks;
import org.bson.Document;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Logger;

public class StorageListener implements Listener {

    private final Logger logger = Main.getInstance().getLogger();
    private final MongoCollection<Document> pDataCol = Main.getInstance().getPlayerDataCollection();
    private final Map<String, PlayerData> playerDataMap = new HashMap<>();
    private final List<String> slots = Arrays.asList("one", "two", "three", "four");

    public PlayerData getPlayerData(String uuid) {
        return playerDataMap.get(uuid);
    }

    public void runDataSaver() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String uuid : new ArrayList<>(playerDataMap.keySet())) saveDocument(uuid);
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 1200);

        logger.info("Successfully started data saver");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void assignDataDocument(PlayerJoinEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        Document filter = new Document("uuid", uuid);
        Document playerDocument;

        if (pDataCol.countDocuments(filter) < 1) {
            playerDocument = updateDocument(filter, uuid);

            pDataCol.insertOne(playerDocument);
            logger.info("Created new player data document assigned to " + uuid);
        } else {
             playerDocument = pDataCol.find(filter).first();
        }

        if (!dataCheck(playerDocument)) {
            playerDocument = updateDocument(playerDocument, uuid);

            logger.info("Successfully updated player data document assigned to " + uuid);
        }

        if (dataCheck(playerDocument)) {
            playerDataMap.put(uuid, new PlayerData(playerDocument));
        } else {
            e.getPlayer().sendMessage("§5Something went wrong getting player data.");
            logger.warning("BEPIS.");
            logger.warning("Player data file " + uuid + " failed the data check, what the hell happened?");
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void saveDataDocument(PlayerQuitEvent e) {
        saveDocument(e.getPlayer().getUniqueId().toString());
        playerDataMap.remove(e.getPlayer().getUniqueId().toString());
    }

    private void saveDocument(String uuid) {
        PlayerData pData = getPlayerData(uuid);
        Document pDoc = pDataCol.find(new Document("uuid", uuid)).first();
        Document perkSlotsEmbed = new Document();
        Document ministreakSlotsEmbed = new Document();
        Document passivesEmbed = new Document();
        Document unlockedPerksEmbed = new Document();
        Document unlockedMegastreaksEmbed = new Document();
        Document unlockedMinistreaksEmbed = new Document();
        Document miscEmbed = new Document();

        for (String slot : slots) perkSlotsEmbed.append(slot, pData.getPerkAtSlot((slots.indexOf(slot) + 1)).name().toLowerCase());
        for (int i = 0; i < 3; i++) ministreakSlotsEmbed.append(slots.get(i), pData.getMinistreakAtSlot(i + 1).name().toLowerCase());
        for (Passives passive : Passives.values()) passivesEmbed.append(passive.name().toLowerCase(), pData.getPassiveTier(passive));
        for (Perks perk : Perks.values()) unlockedPerksEmbed.append(perk.name().toLowerCase(), pData.getPerkUnlockStatus(perk));
        for (Megastreaks mega : Megastreaks.values()) unlockedMegastreaksEmbed.append(mega.name().toLowerCase(), pData.getMegastreakUnlockStatus(mega));
        for (Ministreaks mini : Ministreaks.values()) unlockedMinistreaksEmbed.append(mini.name().toLowerCase(), pData.getMinistreakUnlockStatus(mini));

        miscEmbed.put("uberdrop_mystic_chance", pData.getUberdropMysticChance());
        miscEmbed.put("gold_stack", pData.getGoldStack());
        miscEmbed.put("xp_stack", pData.getXpStack());
        pDoc.put("prestige", pData.getPrestige());
        pDoc.put("level", pData.getLevel());
        pDoc.put("exp", pData.getExp());
        pDoc.put("gold", pData.getGold());
        pDoc.put("bounty", pData.getBounty());
        pDoc.put("megastreak", pData.getMegastreak().name().toLowerCase());
        pDoc.put("perk_slots", perkSlotsEmbed);
        pDoc.put("ministreak_slots", ministreakSlotsEmbed);
        pDoc.put("passives", passivesEmbed);
        pDoc.put("perk_unlocks", unlockedPerksEmbed);
        pDoc.put("megastreak_unlocks", unlockedMegastreaksEmbed);
        pDoc.put("ministreak_unlocks", unlockedMinistreaksEmbed);
        pDoc.put("misc", miscEmbed);
        pDoc.put("combat_logged", pData.hasCombatLogged());

        pDataCol.replaceOne(new Document("uuid", uuid), pDoc);
    }

    private Document updateDocument(Document document, String uuid) {
        if (document == null) document = new Document("uuid", uuid);

        Document updating = new Document("uuid", uuid);
        Document perkSlotsEmbed = new Document();
        Document ministreakSlotsEmbed = new Document();
        Document passivesEmbed = new Document();
        Document unlockedPerksEmbed = new Document();
        Document unlockedMegastreaksEmbed = new Document();
        Document unlockedMinistreaksEmbed = new Document();
        Document miscEmbed = new Document();

        for (String slot : slots) {
            perkSlotsEmbed.append(slot, document.getEmbedded(Arrays.asList("perk_slots", slot), "unset"));
        }

        for (int i = 0; i < 3; i++) {
            ministreakSlotsEmbed.append(slots.get(i), document.getEmbedded(Arrays.asList("ministreak_slots", slots.get(i)), "unset"));
        }

        for (Passives passive : Passives.values()) {
            passivesEmbed.append(passive.name().toLowerCase(), document.getEmbedded(Arrays.asList("passives", passive.name().toLowerCase()), 0));
        }

        for (Perks perk : Perks.values()) {
            unlockedPerksEmbed.append(perk.name().toLowerCase(), document.getEmbedded(Arrays.asList("perk_unlocks", perk.name().toLowerCase()), false));
        }

        for (Megastreaks mega : Megastreaks.values()) {
            unlockedMegastreaksEmbed.append(mega.name().toLowerCase(), document.getEmbedded(Arrays.asList("megastreak_unlocks", mega.name().toLowerCase()), false));
        }

        for (Ministreaks mini : Ministreaks.values()) {
            unlockedMinistreaksEmbed.append(mini.name().toLowerCase(), document.getEmbedded(Arrays.asList("ministreak_unlocks", mini.name().toLowerCase()), false));
        }

        miscEmbed.append("uberdrop_mystic_chance", document.getEmbedded(Arrays.asList("misc", "uberdrop_mystic_chance"), 0));
        miscEmbed.append("gold_stack", document.getEmbedded(Arrays.asList("misc", "gold_stack"), 0D));
        miscEmbed.append("xp_stack", document.getEmbedded(Arrays.asList("misc", "xp_stack"), 0D));
        updating.append("prestige", document.get("prestige", 0));
        updating.append("level", document.get("level", 1));
        updating.append("exp", document.get("exp", 15));
        updating.append("gold", document.get("gold", 0D));
        updating.append("bounty", document.get("bounty", 0));
        updating.append("megastreak", document.get("megastreak", Megastreaks.OVERDRIVE.name().toLowerCase()));
        updating.append("perk_slots", perkSlotsEmbed);
        updating.append("ministreak_slots", ministreakSlotsEmbed);
        updating.append("passives", passivesEmbed);
        updating.append("perk_unlocks", unlockedPerksEmbed);
        updating.append("megastreak_unlocks", unlockedMegastreaksEmbed);
        updating.append("ministreak_unlocks", unlockedMinistreaksEmbed);
        updating.append("misc", miscEmbed);
        updating.append("combat_logged", document.get("combat_logged", false));

        return updating;
    }

    private boolean dataCheck(Document document) {
        for (String slot : slots) {
            if (document.getEmbedded(Arrays.asList("perk_slots", slot), String.class) == null) {
                return false;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (document.getEmbedded(Arrays.asList("ministreak_slots", slots.get(i)), String.class) == null) {
                return false;
            }
        }

        for (Passives passive : Passives.values()) {
            if (document.getEmbedded(Arrays.asList("passives", passive.name().toLowerCase()), Integer.class) == null) {
                return false;
            }
        }

        for (Perks perk : Perks.values()) {
            if (document.getEmbedded(Arrays.asList("perk_unlocks", perk.name().toLowerCase()), Boolean.class) == null) {
                return false;
            }
        }

        for (Megastreaks mega : Megastreaks.values()) {
            if (document.getEmbedded(Arrays.asList("megastreak_unlocks", mega.name().toLowerCase()), Boolean.class) == null) {
                return false;
            }
        }

        for (Ministreaks mini : Ministreaks.values()) {
            if (document.getEmbedded(Arrays.asList("ministreak_unlocks", mini.name().toLowerCase()), Boolean.class) == null) {
                return false;
            }
        }

        return document.get("prestige") != null
                && document.get("level") != null
                && document.get("exp") != null
                && document.get("gold") != null
                && document.get("bounty") != null
                && document.get("megastreak") != null
                && document.get("combat_logged") != null
                && document.getEmbedded(Arrays.asList("misc", "uberdrop_mystic_chance"), Integer.class) != null
                && document.getEmbedded(Arrays.asList("misc", "gold_stack"), Double.class) != null
                && document.getEmbedded(Arrays.asList("misc", "xp_stack"), Double.class) != null;
    }
}















