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

public class StorageListener implements Listener {

    private final MongoCollection<Document> pDataCol = Main.getInstance().getPlayerDataCollection();
    private final Map<String, PlayerData> playerDataMap = new HashMap<>();
    private final List<String> playerUUIDList = new ArrayList<>();
    private final List<String> slots = Arrays.asList("one", "two", "three", "four");

    public PlayerData getPlayerData(String uuid) {
        return playerDataMap.get(uuid);
    }

    public void runDataSaver() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (String uuid : new ArrayList<>(playerUUIDList)) saveDocument(uuid);
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0, 1200);

        System.out.println("ThePit: Successfully started data saver");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void assignDataDocument(PlayerJoinEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        Document filter = new Document("uuid", uuid);
        Document pDoc;

        if (pDataCol.countDocuments(filter) < 1) {
            Document perkSlotsEmbed = new Document();
            Document ministreakSlotsEmbed = new Document();
            Document passivesEmbed = new Document();
            Document unlockedPerksEmbed = new Document();
            Document unlockedMegastreaksEmbed = new Document();
            Document unlockedMinistreaksEmbed = new Document();

            for (String slot : slots) perkSlotsEmbed.append(slot, "unset");
            for (int i = 0; i < 3; i++) ministreakSlotsEmbed.append(slots.get(i), "unset");
            for (Passives passive : Passives.values()) passivesEmbed.append(passive.name().toLowerCase(), 0);
            for (Perks perk : Perks.values()) unlockedPerksEmbed.append(perk.name().toLowerCase(), false);

            for (Megastreaks mega : Megastreaks.values()) {
                if (mega == Megastreaks.OVERDRIVE) {
                    unlockedMegastreaksEmbed.append(mega.name().toLowerCase(), true);
                } else {
                    unlockedMegastreaksEmbed.append(mega.name().toLowerCase(), false);
                }
            }

            for (Ministreaks mini : Ministreaks.values()) unlockedMinistreaksEmbed.append(mini.name().toLowerCase(), false);

            pDataCol.insertOne(filter
                    .append("prestige", 0)
                    .append("level", 1)
                    .append("exp", 15)
                    .append("gold", 0.0)
                    .append("bounty", 0)
                    .append("megastreaks", "overdrive")
                    .append("perk_slots", perkSlotsEmbed)
                    .append("ministreak_slots", ministreakSlotsEmbed)
                    .append("passives", passivesEmbed)
                    .append("perk_unlocks", unlockedPerksEmbed)
                    .append("megastreak_unlocks", unlockedMegastreaksEmbed)
                    .append("ministreak_unlocks", unlockedMinistreaksEmbed)
                    .append("combat_logged", false));

            pDoc = pDataCol.find(filter).first();

            System.out.println("Created new player data document assigned to " + uuid);
        } else {
             pDoc = pDataCol.find(filter).first();
        }

        if (!dataCheck(pDoc)) {
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void saveDataDocument(PlayerQuitEvent e) {
        saveDocument(e.getPlayer().getUniqueId().toString());
        playerDataMap.remove(e.getPlayer().getUniqueId().toString());
        playerUUIDList.remove(e.getPlayer().getUniqueId().toString());
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

        for (String slot : slots) perkSlotsEmbed.append(slot, pData.getPerkAtSlot((slots.indexOf(slot) + 1)).name().toLowerCase());
        for (int i = 0; i < 3; i++) ministreakSlotsEmbed.append(slots.get(i), pData.getMinistreakAtSlot(i + 1).name().toLowerCase());
        for (Passives passive : Passives.values()) passivesEmbed.append(passive.name().toLowerCase(), pData.getPassiveTier(passive));
        for (Perks perk : Perks.values()) unlockedPerksEmbed.append(perk.name().toLowerCase(), pData.getPerkUnlockStatus(perk));
        for (Megastreaks mega : Megastreaks.values()) unlockedMegastreaksEmbed.append(mega.name().toLowerCase(), pData.getMegastreakUnlockStatus(mega));
        for (Ministreaks mini : Ministreaks.values()) unlockedMinistreaksEmbed.append(mini.name().toLowerCase(), pData.getMinistreakUnlockStatus(mini));

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
        pDoc.put("combat_logged", pData.getCombatLogged());

        pDataCol.replaceOne(new Document("uuid", uuid), pDoc);
    }

    private Document updateDocument(Document document) {
        Document perkSlotsEmbed = new Document();
        Document ministreakSlotsEmbed = new Document();
        Document passivesEmbed = new Document();
        Document unlockedPerksEmbed = new Document();
        Document unlockedMegastreaksEmbed = new Document();
        Document unlockedMinistreaksEmbed = new Document();

        for (String slot : slots) {
            if (document.getEmbedded(Arrays.asList("perk_slots", slot), String.class) == null) {
                perkSlotsEmbed.append(slot, "unset");
            } else {
                perkSlotsEmbed.append(slot, document.getEmbedded(Arrays.asList("perk_slots", slot), String.class));
            }
        }

        for (int i = 0; i < 3; i++) {
            if (document.getEmbedded(Arrays.asList("ministreak_slots", slots.get(i)), String.class) == null) {
                ministreakSlotsEmbed.append(slots.get(i), "unset");
            } else {
                ministreakSlotsEmbed.append(slots.get(i), document.getEmbedded(Arrays.asList("ministreak_slots", slots.get(i)), String.class));
            }
        }

        for (Passives passive : Passives.values()) {
            if (document.getEmbedded(Arrays.asList("passives", passive.name().toLowerCase()), Integer.class) == null) {
                passivesEmbed.append(passive.name().toLowerCase(), 0);
            } else {
                passivesEmbed.append(passive.name().toLowerCase(), document.getEmbedded(Arrays.asList("passives", passive.name().toLowerCase()), Integer.class));
            }
        }

        for (Perks perk : Perks.values()) {
            if (document.getEmbedded(Arrays.asList("perk_unlocks", perk.name().toLowerCase()), Boolean.class) == null) {
                unlockedPerksEmbed.append(perk.name().toLowerCase(), false);
            } else {
                unlockedPerksEmbed.append(perk.name().toLowerCase(), document.getEmbedded(Arrays.asList("perk_unlocks", perk.name().toLowerCase()), Boolean.class));
            }
        }

        for (Megastreaks mega : Megastreaks.values()) {
            if (document.getEmbedded(Arrays.asList("megastreak_unlocks", mega.name().toLowerCase()), Boolean.class) == null) {
                unlockedMegastreaksEmbed.append(mega.name().toLowerCase(), false);
            } else {
                unlockedMegastreaksEmbed.append(mega.name().toLowerCase(), document.getEmbedded(Arrays.asList("megastreak_unlocks", mega.name().toLowerCase()), Boolean.class));
            }
        }

        for (Ministreaks mini : Ministreaks.values()) {
            if (document.getEmbedded(Arrays.asList("ministreak_unlocks", mini.name().toLowerCase()), Boolean.class) == null) {
                unlockedMinistreaksEmbed.append(mini.name().toLowerCase(), false);
            } else {
                unlockedMinistreaksEmbed.append(mini.name().toLowerCase(), document.getEmbedded(Arrays.asList("ministreak_unlocks", mini.name().toLowerCase()), Boolean.class));
            }
        }

        if (document.get("prestige") == null) document.append("prestige", 0);
        if (document.get("level") == null) document.append("level", 1);
        if (document.get("exp") == null) document.append("exp", 15);
        if (document.get("gold") == null) document.append("gold", 0.0);
        if (document.get("bounty") == null) document.append("bounty", 0);
        if (document.get("megastreak") == null) document.append("megastreak", Megastreaks.OVERDRIVE.name().toLowerCase());

        document.append("perk_slots", perkSlotsEmbed);
        document.append("ministreak_slots", ministreakSlotsEmbed);
        document.append("passives", passivesEmbed);
        document.append("perk_unlocks", unlockedPerksEmbed);
        document.append("megastreak_unlocks", unlockedMegastreaksEmbed);
        document.append("ministreak_unlocks", unlockedMinistreaksEmbed);

        if (document.get("combat_logged") == null) document.append("combat_logged", false);

        return document;
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
                && document.get("combat_logged") != null;
    }
}















