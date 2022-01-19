package me.zelha.thepit.mainpkg.data;

import com.mongodb.client.MongoCollection;
import me.zelha.thepit.Main;
import me.zelha.thepit.zelenums.Passives;
import me.zelha.thepit.zelenums.Perks;
import org.bson.Document;
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

    public PlayerData getPlayerData(String uuid) {
        return playerDataMap.get(uuid);
    }

    public void runDataSaver() {
        new SaveDataPeriodically().runTaskTimerAsynchronously(Main.getInstance(),0, 1200);
        System.out.println("ThePit: Successfully started data saver");
    }

    private boolean dataCheck(Document document) {

        for (int i = 1; i <= 4; i++) {
            if (document.get("perk_slots." + i) == null) {
                return false;
            }
        }

        for (Passives passive : Passives.values()) {
            if (document.get("passives." + passive.getName()) == null) {
                return false;
            }
        }

        for (Perks perk : Perks.values()) {
            if (document.get("perk_unlocks." + perk.getName()) == null) {
                return false;
            }
        }

        return document.get("prestige") != null
                && document.get("level") != null
                && document.get("exp") != null
                && document.get("gold") != null
                && document.get("bounty") != null;
    }

    private Document updateDocument(Document document) {
        if (document.get("prestige") == null) document.append("prestige", 0);
        if (document.get("level") == null) document.append("level", 1);
        if (document.get("exp") == null) document.append("exp", 15);
        if (document.get("gold") == null) document.append("gold", 0.0);
        if (document.get("bounty") == null) document.append("bounty", 0);

        for (int i = 1; i <= 4; i++) {
            if (document.get("perk_slots." + i) == null) {
                document.append("perk_slots." + i, "unset");
            }
        }

        for (Passives passive : Passives.values()) {
            if (document.get("passives." + passive.getName()) == null) {
                document.append("passives." + passive.getName(), 0);
            }
        }

        for (Perks perk : Perks.values()) {
            if (document.get("perk_unlocks." + perk.getName()) == null) {
                document.append("perk_unlocks." + perk.getName(), false);
            }
        }

        return document;
    }

    @EventHandler
    public void assignDataDocument(PlayerJoinEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        Document filter = new Document("uuid", uuid);
        Document pDoc = pDataCol.find(filter).first();

        if (pDataCol.countDocuments(filter) < 1) {
            Document perkSlotsEmbed = new Document();
            Document passivesEmbed = new Document();
            Document unlockedPerksEmbed = new Document();

            for (int i = 1; i <= 4; i++) {
                perkSlotsEmbed.append(String.valueOf(i), "unset");
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
        } else if (!dataCheck(pDoc)) {
            pDoc = updateDocument(pDoc);

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
        String uuid = e.getPlayer().getUniqueId().toString();
        PlayerData pData = getPlayerData(uuid);
        Document pDoc = pDataCol.find(new Document("uuid", uuid)).first();

        pDoc.put("prestige", pData.getPrestige());
        pDoc.put("level", pData.getLevel());
        pDoc.put("exp", pData.getExp());
        pDoc.put("gold", pData.getGold());
        pDoc.put("bounty", pData.getBounty());

        for (int i = 1; i <= 4; i++) {
            pDoc.put("perk_slots." + i, pData.getPerkAtSlot(i).getName());
        }

        for (Passives passive : Passives.values()) {
            pDoc.put("passives." + passive.getName(), pData.getPassiveTier(passive));
        }

        for (Perks perk : Perks.values()) {
            pDoc.put("perk_unlocks." + perk.getName(), pData.getPerkUnlocked(perk));
        }

        pDataCol.replaceOne(new Document("uuid", uuid), pDoc);

        playerDataMap.remove(uuid);
        playerUUIDList.remove(uuid);
    }


    private class SaveDataPeriodically extends BukkitRunnable {

        @Override
        public void run() {

            for (String uuid : playerUUIDList) {
                PlayerData pData = getPlayerData(uuid);
                Document pDoc = pDataCol.find(new Document("uuid", uuid)).first();

                pDoc.put("prestige", pData.getPrestige());
                pDoc.put("level", pData.getLevel());
                pDoc.put("exp", pData.getExp());
                pDoc.put("gold", pData.getGold());
                pDoc.put("bounty", pData.getBounty());

                for (int i = 1; i <= 4; i++) {
                    pDoc.put("perk_slots." + i, pData.getPerkAtSlot(i).getName());
                }

                for (Passives passive : Passives.values()) {
                    pDoc.put("passives." + passive.getName(), pData.getPassiveTier(passive));
                }

                for (Perks perk : Perks.values()) {
                    pDoc.put("perk_unlocks." + perk.getName(), pData.getPerkUnlocked(perk));
                }

                pDataCol.replaceOne(new Document("uuid", uuid), pDoc);
            }
        }
    }
}















