package me.zelha.thepit.mainpkg.data;

import com.mongodb.client.MongoCollection;
import me.zelha.thepit.Main;
import me.zelha.thepit.zelenums.Passives;
import me.zelha.thepit.zelenums.Perks;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.List;

public class StorageListener implements Listener {

    private final MongoCollection<Document> pDataCol = Main.getInstance().getPlayerDataCollection();

    private final static Map<String, PlayerData> playerDataMap = new HashMap<>();
    private final static List<String> playerUUIDList = new ArrayList<>();

    private final List<String> slots = Arrays.asList("one", "two", "three", "four");

    public PlayerData getPlayerData(String uuid) {
        return playerDataMap.get(uuid);
    }

    public void runDataSaver() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String uuid : playerUUIDList) {
                    saveDocument(uuid);
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 1200);

        System.out.println("ThePit: Successfully started data saver");
    }

    private boolean dataCheck(Document document) {

        for (String slot : slots) {
            if (document.getEmbedded(Arrays.asList("perk_slots", slot), String.class) == null) {
                return false;
            }
        }

        for (Passives passive : Passives.values()) {
            if (document.getEmbedded(Arrays.asList("passives", passive.getName()), Integer.class) == null) {
                return false;
            }
        }

        for (Perks perk : Perks.values()) {
            if (document.getEmbedded(Arrays.asList("perk_unlocks", perk.getName()), Boolean.class) == null) {
                return false;
            }
        }

        return document.get("prestige") != null
                && document.get("level") != null
                && document.get("exp") != null
                && document.get("gold") != null
                && document.get("bounty") != null;
    }

    private Document updateDocument(Document document, String uuid) {
        PlayerData pData = getPlayerData(uuid);
        Document perkSlotsEmbed = new Document();
        Document passivesEmbed = new Document();
        Document unlockedPerksEmbed = new Document();

        for (String slot : slots) {
            if (document.getEmbedded(Arrays.asList("perk_slots", slot), String.class) == null) {
                perkSlotsEmbed.append(slot, "unset");
            } else {
                perkSlotsEmbed.append(slot, pData.getPerkAtSlot((slots.indexOf(slot) + 1)).getName());
            }
        }

        for (Passives passive : Passives.values()) {
            if (document.getEmbedded(Arrays.asList("passives", passive.getName()), Integer.class) == null) {
                passivesEmbed.append(passive.getName(), 0);
            } else {
                passivesEmbed.append(passive.getName(), pData.getPassiveTier(passive));
            }
        }

        for (Perks perk : Perks.values()) {
            if (document.getEmbedded(Arrays.asList("perk_unlocks", perk.getName()), Boolean.class) == null) {
                unlockedPerksEmbed.append(perk.getName(), false);
            } else {
                unlockedPerksEmbed.append(perk.getName(), pData.getPerkUnlocked(perk));
            }
        }

        if (document.get("prestige") == null) document.append("prestige", 0);
        if (document.get("level") == null) document.append("level", 1);
        if (document.get("exp") == null) document.append("exp", 15);
        if (document.get("gold") == null) document.append("gold", 0.0);
        if (document.get("bounty") == null) document.append("bounty", 0);

        document.append("perk_slots", perkSlotsEmbed);
        document.append("passives", passivesEmbed);
        document.append("perk_unlocks", unlockedPerksEmbed);

        return document;
    }

    private void saveDocument(String uuid) {
        PlayerData pData = getPlayerData(uuid);
        Document pDoc = pDataCol.find(new Document("uuid", uuid)).first();
        Document perkSlotsEmbed = new Document();
        Document passivesEmbed = new Document();
        Document unlockedPerksEmbed = new Document();

        for (String slot : slots) {
            perkSlotsEmbed.append(slot, pData.getPerkAtSlot((slots.indexOf(slot) + 1)).getName());
        }

        for (Passives passive : Passives.values()) {
            passivesEmbed.append(passive.getName(), pData.getPassiveTier(passive));
        }

        for (Perks perk : Perks.values()) {
            unlockedPerksEmbed.append(perk.getName(), pData.getPerkUnlocked(perk));
        }

        pDoc.put("prestige", pData.getPrestige());
        pDoc.put("level", pData.getLevel());
        pDoc.put("exp", pData.getExp());
        pDoc.put("gold", pData.getGold());
        pDoc.put("bounty", pData.getBounty());
        pDoc.put("perk_slots", perkSlotsEmbed);
        pDoc.put("passives", passivesEmbed);
        pDoc.put("perk_unlocks", unlockedPerksEmbed);

        pDataCol.replaceOne(new Document("uuid", uuid), pDoc);
    }

    @EventHandler
    public void assignDataDocument(PlayerJoinEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        Document filter = new Document("uuid", uuid);
        Document pDoc;

        if (pDataCol.countDocuments(filter) < 1) {
            Document perkSlotsEmbed = new Document();
            Document passivesEmbed = new Document();
            Document unlockedPerksEmbed = new Document();

            for (String slot : slots) {
                perkSlotsEmbed.append(slot, "unset");
            }

            for (Passives passive : Passives.values()) {
                passivesEmbed.append(passive.getName(), 0);
            }

            for (Perks perk : Perks.values()) {
                unlockedPerksEmbed.append(perk.getName(), false);
            }

            pDataCol.insertOne(filter
                    .append("prestige", 0)
                    .append("level", 1)
                    .append("exp", 15)
                    .append("gold", 0.0)
                    .append("bounty", 0)
                    .append("perk_slots", perkSlotsEmbed)
                    .append("passives", passivesEmbed)
                    .append("perk_unlocks", unlockedPerksEmbed));

            pDoc = pDataCol.find(filter).first();

            System.out.println("Created new player data document assigned to" + uuid);
        } else {
             pDoc = pDataCol.find(filter).first();
        }

        if (!dataCheck(pDoc)) {
            playerDataMap.put(uuid, new PlayerData(pDoc));
            pDoc = updateDocument(pDoc, uuid);

            System.out.println("Successfully updated player data document assigned to " + uuid);
        }

        if (pDoc != null) {
            playerDataMap.put(uuid, new PlayerData(pDoc));
            playerUUIDList.add(uuid);
        } else {
            System.out.println("BEPIS.");
            System.out.println("Did something go wrong inserting the document?");
        }

    }

    @EventHandler
    public void saveDataDocument(PlayerQuitEvent e) {
        saveDocument(e.getPlayer().getUniqueId().toString());
        playerDataMap.remove(e.getPlayer().getUniqueId().toString());
        playerUUIDList.remove(e.getPlayer().getUniqueId().toString());
    }
}















