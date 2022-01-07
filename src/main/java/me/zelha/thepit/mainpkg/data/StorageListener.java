package me.zelha.thepit.mainpkg.data;

import com.mongodb.client.MongoCollection;
import me.zelha.thepit.Main;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.List;

public class StorageListener implements Listener {

    private final static Map<String, PlayerData> playerDataMap = new HashMap<>();
    private final static List<String> playerUUIDList = new ArrayList<>();

    public PlayerData getPlayerData(String uuid) {
        return playerDataMap.get(uuid);
    }

    public List<String> getPlayerUUIDList() {return playerUUIDList;}

    public void runDataSaver() {
        new SaveDataPeriodically().runTaskTimerAsynchronously(Main.getInstance(),0, 1200);
        System.out.println("ThePit: Successfully started data saver");
    }

    boolean dataCheck(Document document) {
        return
        document.get("prestige")     != null
        && document.get("level")     != null
        && document.get("exp")       != null
        && document.get("gold")      != null
        && document.get("bounty")    != null;
    }

    Document updateDocument(Document document) {
        if (document.get("prestige") == null)  {document.append("prestige", 0);}
        if (document.get("level") == null)     {document.append("level", 1);}
        if (document.get("exp") == null)       {document.append("exp", 15);}
        if (document.get("gold") == null)      {document.append("gold", 0.0);}
        if (document.get("bounty") == null)    {document.append("bounty", 0);}

        return document;
    }

    @EventHandler
    public void assignDataDocument(PlayerJoinEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        String uuidString = uuid.toString();
        Document pDoc = Main.getInstance().getPlayerDataCollection().find(new Document("uuid", uuidString)).first();

        if (Main.getInstance().getPlayerDataCollection().countDocuments(new Document("uuid", uuidString)) < 1) {
            Main.getInstance().getPlayerDataCollection().insertOne(new Document("uuid", uuidString)
                    .append("prestige", 0)
                    .append("level", 1)
                    .append("exp", 15)
                    .append("gold", 0.0)
                    .append("bounty", 0));

            pDoc = Main.getInstance().getPlayerDataCollection().find(new Document("uuid", uuidString)).first();

            System.out.println("Created new player data document assigned to" + uuidString);
        }else if (!dataCheck(pDoc)) {
            pDoc = updateDocument(pDoc);

            System.out.println("Successfully updated player data document assigned to " + uuidString);
        }

        if (pDoc != null) {
            playerDataMap.put(uuidString, Main.getInstance().getPlayerData(pDoc));
            playerUUIDList.add(uuidString);
        }else {
            System.out.println(ChatColor.DARK_RED + "HALL OF FAILURE.");
            System.out.println("Did something go wrong inserting the document?");
        }

    }

    @EventHandler
    public void unassignDataDocument(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();

        MongoCollection<Document> pDataCol = Main.getInstance().getPlayerDataCollection();
        PlayerData pData = Main.getInstance().getStorage().getPlayerData(uuid.toString());
        Document pDoc = pDataCol.find(new Document("uuid", uuid.toString())).first();

        pDoc.put("prestige", pData.getPrestige());
        pDoc.put("level", pData.getLevel());
        pDoc.put("exp", pData.getExp());
        pDoc.put("gold", pData.getGold());
        pDoc.put("bounty", pData.getBounty());

        pDataCol.replaceOne(new Document("uuid", uuid.toString()), pDoc);

        playerDataMap.remove(uuid.toString());
        playerUUIDList.remove(uuid.toString());
    }
}


class SaveDataPeriodically extends BukkitRunnable {

    MongoCollection<Document> pDataCol = Main.getInstance().getPlayerDataCollection();

    public SaveDataPeriodically() {}

    @Override
    public void run() {

        List<String> pUUIDList = Main.getInstance().getStorage().getPlayerUUIDList();

        for (String uuid : pUUIDList) {
            PlayerData pData = Main.getInstance().getStorage().getPlayerData(uuid);
            Document pDoc = pDataCol.find(new Document("uuid", uuid)).first();

            pDoc.put("prestige", pData.getPrestige());
            pDoc.put("level", pData.getLevel());
            pDoc.put("exp", pData.getExp());
            pDoc.put("gold", pData.getGold());
            pDoc.put("bounty", pData.getBounty());

            pDataCol.replaceOne(new Document("uuid", uuid), pDoc);
        }
    }
}












