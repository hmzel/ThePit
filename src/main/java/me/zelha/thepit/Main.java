package me.zelha.thepit;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.zelha.thepit.admin.commands.*;
import me.zelha.thepit.events.NPCInteractEventCaller;
import me.zelha.thepit.mainpkg.commands.OofCommand;
import me.zelha.thepit.mainpkg.commands.RespawnCommand;
import me.zelha.thepit.mainpkg.data.KillRecap;
import me.zelha.thepit.mainpkg.data.PlayerData;
import me.zelha.thepit.mainpkg.data.StorageListener;
import me.zelha.thepit.mainpkg.listeners.*;
import me.zelha.thepit.mainpkg.runnables.ParticipationRunnable;
import me.zelha.thepit.upgrades.nonpermanent.GoldenPickaxeListener;
import me.zelha.thepit.upgrades.nonpermanent.ItemsVillagerListener;
import me.zelha.thepit.upgrades.permanent.KillstreakListener;
import me.zelha.thepit.upgrades.permanent.PerkListener;
import me.zelha.thepit.upgrades.permanent.UpgradesVillagerListener;
import me.zelha.thepit.utils.ConfirmGUIHandler;
import me.zelha.thepit.utils.RunTracker;
import me.zelha.thepit.utils.ZelLogic;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {

    public List<Player> blockPriviledges = new ArrayList<>();
    private static Main instance;
    private MongoCollection<Document> playerDataCollection;
    private MongoClient mongoClient;
    private ZelLogic zelLogic;
    private StorageListener storage;
    private DeathListener deathListener;
    private KillListener killListener;
    private AssistListener assistListener;
    private AttackListener attackListener;
    private ScoreboardListener scoreboardListener;
    private ConfirmGUIHandler confirmGUIHandler;

    @Override
    public void onEnable() {
        //Plugin startup logic
        mongoClient = MongoClients.create(DatabaseLogin.DATABASE_LOGIN);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("zelhadb");
        playerDataCollection = mongoDatabase.getCollection("playerdata");

        instance = this;
        zelLogic = new ZelLogic();
        storage = new StorageListener();
        assistListener = new AssistListener();
        attackListener = new AttackListener();
        killListener = new KillListener();
        deathListener = new DeathListener();
        scoreboardListener = new ScoreboardListener();
        confirmGUIHandler = new ConfirmGUIHandler();

        getServer().getPluginManager().registerEvents(storage, this);
        getServer().getPluginManager().registerEvents(new ExpChangeListener(), this);
        getServer().getPluginManager().registerEvents(scoreboardListener, this);
        getServer().getPluginManager().registerEvents(killListener, this);
        getServer().getPluginManager().registerEvents(deathListener, this);
        getServer().getPluginManager().registerEvents(new PerkListener(), this);
        getServer().getPluginManager().registerEvents(new GeneralListener(), this);
        getServer().getPluginManager().registerEvents(attackListener, this);
        getServer().getPluginManager().registerEvents(new SpawnListener(), this);
        getServer().getPluginManager().registerEvents(new ItemsVillagerListener(), this);
        getServer().getPluginManager().registerEvents(new GoldenPickaxeListener(), this);
        getServer().getPluginManager().registerEvents(new UpgradesVillagerListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new ArmorPickupListener(), this);
        getServer().getPluginManager().registerEvents(new GoldIngotListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(assistListener, this);
        getServer().getPluginManager().registerEvents(confirmGUIHandler, this);
        getServer().getPluginManager().registerEvents(new ArrowListener(), this);
        getServer().getPluginManager().registerEvents(new NPCInteractEventCaller(), this);
        getServer().getPluginManager().registerEvents(new ActionbarListener(), this);
        getServer().getPluginManager().registerEvents(new KillstreakListener(), this);

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
        getCommand("oof").setExecutor(new OofCommand());
        getCommand("setmegastreak").setExecutor(new SetMegastreakCommand());
        getCommand("setministreak").setExecutor(new SetMinistreakCommand());
        getCommand("reset").setExecutor(new ResetCommand());

        KillRecap recap = new KillRecap();

        getServer().getPluginManager().registerEvents(recap, this);
        getCommand("killrecap").setExecutor(recap);

        storage.runDataSaver();
        scoreboardListener.startAnimation();
        new ParticipationRunnable().runTaskTimer(this, 0, 1);

        for (Player player : Bukkit.getOnlinePlayers()) {
            Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, null));
        }
    }

    @Override
    public void onDisable() {
        mongoClient.close();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:kill @e[type=armor_stand,tag=bounty]");
        scoreboardListener.clearSidebar();

        for (Team team : getServer().getScoreboardManager().getMainScoreboard().getTeams()) {
            team.unregister();
        }
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

    public DeathListener getDeathUtils() {
        return deathListener;
    }

    public KillListener getKillUtils() {
        return killListener;
    }

    public AssistListener getAssistUtils() {
        return assistListener;
    }

    public RunTracker generateRunTracker() {
        return new RunTracker();
    }

    public AttackListener getAttackUtils() {
        return attackListener;
    }

    public ConfirmGUIHandler getConfirmGUIHandler() {
        return confirmGUIHandler;
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
