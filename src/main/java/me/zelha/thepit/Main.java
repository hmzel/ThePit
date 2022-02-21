package me.zelha.thepit;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.zelha.thepit.admin.commands.*;
import me.zelha.thepit.admin.commands.HologramCheckCommand;
import me.zelha.thepit.admin.commands.NPCCheckCommand;
import me.zelha.thepit.mainpkg.commands.RespawnCommand;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.mainpkg.data.StorageListener;
import me.zelha.thepit.mainpkg.listeners.*;
import me.zelha.thepit.mainpkg.runnables.ParticipationRunnable;
import me.zelha.thepit.mainpkg.listeners.BlockListener;
import me.zelha.thepit.upgrades.nonpermanent.GoldenPickaxeListener;
import me.zelha.thepit.upgrades.nonpermanent.villager.ItemsVillagerListener;
import me.zelha.thepit.upgrades.permanent.perks.PerkListenersAndUtils;
import me.zelha.thepit.upgrades.permanent.villager.UpgradesVillagerListener;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {

    private ZelLogic zelLogic;
    private PerkListenersAndUtils perkUtils;
    private StorageListener storage;
    private DeathListener deathListener;

    private MongoCollection<Document> playerDataCollection;
    private MongoClient mongoClient;

    public List<Player> blockPriviledges = new ArrayList<>();

    private static Main instance;


    @Override
    public void onEnable() {
        //Plugin startup logic
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:kick @a");
        mongoClient = MongoClients.create(DatabaseLogin.DATABASE_LOGIN);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("zelhadb");
        playerDataCollection = mongoDatabase.getCollection("playerdata");

        instance = this;
        zelLogic = new ZelLogic();
        storage = new StorageListener();
        perkUtils = new PerkListenersAndUtils();
        deathListener = new DeathListener();

        getServer().getPluginManager().registerEvents(storage, this);
        getServer().getPluginManager().registerEvents(new LevelUpListener(), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
        getServer().getPluginManager().registerEvents(new KillListener(), this);
        getServer().getPluginManager().registerEvents(perkUtils, this);
        getServer().getPluginManager().registerEvents(deathListener, this);
        getServer().getPluginManager().registerEvents(new AntiVanillaListener(), this);
        getServer().getPluginManager().registerEvents(new AttackListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(), this);
        getServer().getPluginManager().registerEvents(new ItemsVillagerListener(), this);
        getServer().getPluginManager().registerEvents(new GoldenPickaxeListener(), this);
        getServer().getPluginManager().registerEvents(new UpgradesVillagerListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new ArmorPickupListener(), this);
        getServer().getPluginManager().registerEvents(new GoldIngotListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new AssistListener(), this);

        getCommand("setprestige").setExecutor(new SetPrestigeCommand());
        getCommand("setlevel").setExecutor(new SetLevelCommand());
        getCommand("setgold").setExecutor(new SetGoldCommand());
        getCommand("setexp").setExecutor(new SetExpCommand());
        getCommand("setstatus").setExecutor(new SetStatusCommand());
        getCommand("setbounty").setExecutor(new SetBountyCommand());
        getCommand("setstreak").setExecutor(new SetStreakCommand());
        getCommand("letmeplaceblocksplease").setExecutor(new AllowBlockEventsCommand());
        getCommand("respawn").setExecutor(new RespawnCommand());
        getCommand("setpassive").setExecutor(new SetPassiveCommand());
        getCommand("npccheck").setExecutor(new NPCCheckCommand());
        getCommand("hologramcheck").setExecutor(new HologramCheckCommand());
        getCommand("setperk").setExecutor(new SetPerkCommand());

        storage.runDataSaver();
        new ParticipationRunnable().runTaskTimerAsynchronously(this, 0, 1);
    }


    @Override
    public void onDisable() {
        mongoClient.close();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:kill @e[type=armor_stand,tag=bounty]");
    }


    public static Main getInstance() {
        return instance;
    }

    public MongoCollection<Document> getPlayerDataCollection() {
        return playerDataCollection;
    }

    public ZelLogic getZelLogic() {
        return zelLogic;
    }
    public PerkListenersAndUtils getPerkUtils() {
        return perkUtils;
    }
    public DeathListener getDeathListener() {
        return deathListener;
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
