package me.zelha.thepit;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.zelha.thepit.admin.commands.*;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.mainpkg.data.StorageListener;
import me.zelha.thepit.mainpkg.listeners.DeathListener;
import me.zelha.thepit.mainpkg.listeners.KillListener;
import me.zelha.thepit.mainpkg.listeners.LevelUpListener;
import me.zelha.thepit.mainpkg.listeners.ScoreboardListener;
import org.bson.Document;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private ZelLogic zelUtils;
    private StorageListener storage;

    private static MongoCollection<Document> playerDataCollection;
    private MongoClient mongoClient;

    public static Main instance;


    @Override
    public void onEnable() {
        //Plugin startup logic
        instance = this;
        zelUtils = new ZelLogic();
        storage = new StorageListener();

        getServer().getPluginManager().registerEvents(new StorageListener(), this);
        getServer().getPluginManager().registerEvents(new LevelUpListener(), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
        getServer().getPluginManager().registerEvents(new KillListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);

        getCommand("setprestige").setExecutor(new SetPrestigeCommand());
        getCommand("setlevel").setExecutor(new SetLevelCommand());
        getCommand("setgold").setExecutor(new SetGoldCommand());
        getCommand("setexp").setExecutor(new SetExpCommand());
        getCommand("setstatus").setExecutor(new SetStatusCommand());

        mongoClient = MongoClients.create("mongodb+srv://zelhagodis:KuroHanaRokuNiSanRei1019@zelcluster.epcte.mongodb.net/endmysuffering?retryWrites=true&w=majority");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("zelhadb");
        playerDataCollection = mongoDatabase.getCollection("playerdata");

        storage.runDataSaver();
    }


    @Override
    public void onDisable() {mongoClient.close();}


    public static Main getInstance() {
        return instance;
    }

    public MongoCollection<Document> getPlayerDataCollection() {return playerDataCollection;}

    public ZelLogic getZelLogic() {
        return zelUtils;
    }
    public StorageListener getStorage() {
        return storage;
    }
    public PlayerData getPlayerData(Document document) {return new PlayerData(document);}
    public RunMethods getRunMethods() {return new RunMethods();}
}
