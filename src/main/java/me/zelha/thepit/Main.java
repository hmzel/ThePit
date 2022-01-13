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
import me.zelha.thepit.upgrades.villager.UpgradesVillagerListener;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {

    private ZelLogic zelLogic;
    private StorageListener storage;
    private DeathListener deathListener;
    private SpawnListener spawnListener;

    private MongoCollection<Document> playerDataCollection;
    private MongoClient mongoClient;

    public List<Player> blockPriviledges = new ArrayList<>();

    private static Main instance;


    @Override
    public void onEnable() {
        //Plugin startup logic
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:kick @a");
        mongoClient = MongoClients.create(DatabaseLogin.DatabaseLogin);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("zelhadb");
        playerDataCollection = mongoDatabase.getCollection("playerdata");

        instance = this;
        zelLogic = new ZelLogic();
        storage = new StorageListener();
        deathListener = new DeathListener();
        spawnListener = new SpawnListener();

        new HologramCheckClass().hologramCheck();
        new NPCCheckClass().npcCheck();

        getServer().getPluginManager().registerEvents(storage, this);
        getServer().getPluginManager().registerEvents(new LevelUpListener(), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
        getServer().getPluginManager().registerEvents(new KillListener(), this);
        getServer().getPluginManager().registerEvents(deathListener, this);
        getServer().getPluginManager().registerEvents(new AntiVanillaListener(), this);
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        getServer().getPluginManager().registerEvents(spawnListener, this);
        getServer().getPluginManager().registerEvents(new ItemsVillagerListener(), this);
        getServer().getPluginManager().registerEvents(new GoldenPickaxeListener(), this);
        getServer().getPluginManager().registerEvents(new UpgradesVillagerListener(), this);

        getCommand("setprestige").setExecutor(new SetPrestigeCommand());
        getCommand("setlevel").setExecutor(new SetLevelCommand());
        getCommand("setgold").setExecutor(new SetGoldCommand());
        getCommand("setexp").setExecutor(new SetExpCommand());
        getCommand("setstatus").setExecutor(new SetStatusCommand());
        getCommand("setbounty").setExecutor(new SetBountyCommand());
        getCommand("setstreak").setExecutor(new SetStreakCommand());
        getCommand("letmeplaceblocksplease").setExecutor(new AllowBlockEventsCommand());
        getCommand("respawn").setExecutor(new RespawnCommand());

        storage.runDataSaver();
        new ParticipationRunnable().runTaskTimerAsynchronously(this, 0, 1);
    }


    @Override
    public void onDisable() {
        mongoClient.close();
    }


    public static Main getInstance() {
        return instance;
    }

    public MongoCollection<Document> getPlayerDataCollection() {//i should clean this up later
        return playerDataCollection;
    }

    public ZelLogic getZelLogic() {
        return zelLogic;
    }
    public DeathListener getDeathListener() {
        return deathListener;
    }
    public SpawnListener getSpawnListener() {
        return spawnListener;
    }
    public RunMethods generateRunMethods() {
        return new RunMethods();
    }

    public PlayerData getPlayerData(String uuid) {
        return storage.getPlayerData(uuid);
    }
    public PlayerData getPlayerData(UUID uuid) {
        return storage.getPlayerData(uuid.toString());
    }
    public PlayerData getPlayerData(Player player) {
        return storage.getPlayerData(player.getUniqueId().toString());
    }
}