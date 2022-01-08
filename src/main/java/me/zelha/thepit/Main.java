package me.zelha.thepit;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.zelha.thepit.admin.commands.*;
import me.zelha.thepit.mainpkg.HologramCheckClass;
import me.zelha.thepit.mainpkg.NPCCheckClass;
import me.zelha.thepit.mainpkg.commands.RespawnCommand;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.mainpkg.data.StorageListener;
import me.zelha.thepit.mainpkg.listeners.*;
import me.zelha.thepit.mainpkg.runnables.ParticipationRunnable;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    public ZelLogic zelLogic;
    public StorageListener storage;
    public DeathListener deathListener;
    public SpawnListener spawnListener;

    public MongoCollection<Document> playerDataCollection;
    public MongoClient mongoClient;

    public List<Player> blockPriviledges = new ArrayList<>();

    public static Main instance;


    @Override
    public void onEnable() {
        //Plugin startup logic
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:kick @a");

        instance = this;
        zelLogic = new ZelLogic();
        storage = new StorageListener();
        deathListener = new DeathListener();
        spawnListener = new SpawnListener();


        new HologramCheckClass().HologramCheck();
        new NPCCheckClass().NPCCheck();

        getServer().getPluginManager().registerEvents(new StorageListener(), this);
        getServer().getPluginManager().registerEvents(new LevelUpListener(), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
        getServer().getPluginManager().registerEvents(new KillListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new AntiVanillaListener(), this);
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(), this);
        getServer().getPluginManager().registerEvents(new ItemsVillagerListener(), this);
        getServer().getPluginManager().registerEvents(new GoldenPickaxeListener(), this);

        getCommand("setprestige").setExecutor(new SetPrestigeCommand());
        getCommand("setlevel").setExecutor(new SetLevelCommand());
        getCommand("setgold").setExecutor(new SetGoldCommand());
        getCommand("setexp").setExecutor(new SetExpCommand());
        getCommand("setstatus").setExecutor(new SetStatusCommand());
        getCommand("setbounty").setExecutor(new SetBountyCommand());
        getCommand("setstreak").setExecutor(new SetStreakCommand());
        getCommand("letmeplaceblocksplease").setExecutor(new AllowBlockEventsCommand());
        getCommand("respawn").setExecutor(new RespawnCommand());

        mongoClient = MongoClients.create("mongodb+srv://zelhagodis:KuroHanaRokuNiSanRei1019@zelcluster.epcte.mongodb.net/endmysuffering?retryWrites=true&w=majority");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("zelhadb");
        playerDataCollection = mongoDatabase.getCollection("playerdata");

        storage.runDataSaver();
        new ParticipationRunnable().runTaskTimerAsynchronously(this, 0, 1);
    }


    @Override
    public void onDisable() {mongoClient.close();}


    public static Main getInstance() {return instance;}

    public MongoCollection<Document> getPlayerDataCollection() {return playerDataCollection;}

    public ZelLogic getZelLogic() {return zelLogic;}
    public StorageListener getStorage() {return storage;}
    public PlayerData getPlayerData(Document document) {return new PlayerData(document);}
    public RunMethods getRunMethods() {return new RunMethods();}
}
