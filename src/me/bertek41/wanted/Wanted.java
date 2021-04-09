package me.bertek41.wanted;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.arenamanager.ArenaCommand;
import me.bertek41.wanted.commands.JoinCommand;
import me.bertek41.wanted.commands.LeaveCommand;
import me.bertek41.wanted.commands.WantedCommand;
import me.bertek41.wanted.listeners.BlockListener;
import me.bertek41.wanted.listeners.ChatListener;
import me.bertek41.wanted.listeners.ChunkListener;
import me.bertek41.wanted.listeners.CommandListener;
import me.bertek41.wanted.listeners.DamageListener;
import me.bertek41.wanted.listeners.DeathListener;
import me.bertek41.wanted.listeners.FoodListener;
import me.bertek41.wanted.listeners.InteractListener;
import me.bertek41.wanted.listeners.InventoryListener;
import me.bertek41.wanted.listeners.ItemListener;
import me.bertek41.wanted.listeners.JoinQuitListener;
import me.bertek41.wanted.listeners.ServerPingListener;
import me.bertek41.wanted.listeners.SignListener;
import me.bertek41.wanted.managers.ArenaManager;
import me.bertek41.wanted.managers.FileManager;
import me.bertek41.wanted.managers.HologramManager;
import me.bertek41.wanted.managers.StatsManager;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.misc.PAPIHook;
import me.bertek41.wanted.misc.Settings;
import me.bertek41.wanted.misc.StatType;
import me.bertek41.wanted.runnables.HologramUpdate;
import me.bertek41.wanted.storage.Database;
import me.bertek41.wanted.storage.MySQL;
import me.bertek41.wanted.storage.SQLite;
import me.bertek41.wanted.utils.ReflectionUtils;

public class Wanted extends JavaPlugin {
	private static Wanted instance;
	private String nmsVersion;
	private HologramUpdate hologramUpdate = new HologramUpdate(this);
	private boolean firstRun = true, useHd;
	private ArenaCommand arenaCommand;
	private ArenaManager arenaManager;
	private FileManager fileManager;
	private HologramManager hologramManager;
	private StatsManager statsManager;
	private Database database;
	
	@Override
	public void onEnable() {
		long oldTime = System.currentTimeMillis();
		instance = this;
		nmsVersion = getServer().getClass().getPackage().getName();
		nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);
		fileManager = new FileManager(this);
		fileManager.createFiles();
		Settings.setConfig(getConfig());
		Lang.setLang(fileManager.getLang());
		if(Settings.MYSQL_ENABLED.getBoolean())
			database = new MySQL(this);
		else
			database = new SQLite(this);
		if(Settings.BUNGEECORD_MODE.getBoolean())
			getServer().getMessenger().registerOutgoingPluginChannel(instance, "BungeeCord");
		arenaManager = new ArenaManager();
		hologramManager = new HologramManager(this);
		statsManager = new StatsManager();
		new PAPIHook(this).register();
		PluginManager pluginManager = getServer().getPluginManager();
		if(pluginManager.isPluginEnabled("HolographicDisplays"))
			useHd = true;
		pluginManager.registerEvents(new BlockListener(this), this);
		pluginManager.registerEvents(new ChatListener(this), this);
		pluginManager.registerEvents(new ChunkListener(this), this);
		pluginManager.registerEvents(new CommandListener(this), this);
		pluginManager.registerEvents(new DamageListener(this), this);
		pluginManager.registerEvents(new DeathListener(this), this);
		pluginManager.registerEvents(new FoodListener(this), this);
		pluginManager.registerEvents(new InteractListener(this), this);
		pluginManager.registerEvents(new InventoryListener(this), this);
		pluginManager.registerEvents(new ItemListener(this), this);
		pluginManager.registerEvents(new JoinQuitListener(this), this);
		pluginManager.registerEvents(new ServerPingListener(this), this);
		pluginManager.registerEvents(new SignListener(this), this);
		getServer().getScheduler().runTaskAsynchronously(this, () -> database.createTable());
		getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
			fileManager.loadArenas();
			fileManager.loadGuns();
			hologramManager.readHolograms();
		});
		PaperCommandManager manager = new PaperCommandManager(this);
		manager.getCommandCompletions().registerAsyncCompletion("arenas", c -> arenaManager.getArenasAsNames());
		manager.getCommandCompletions().registerAsyncCompletion("stats", c -> ReflectionUtils.getEnumAsList());
		manager.getCommandCompletions().registerAsyncCompletion("database", c -> Arrays.asList("mysql", "sqlite"));
		manager.getCommandCompletions().registerAsyncCompletion("locations", c -> Arrays.asList("wanted", "player"));
		manager.getCommandContexts().registerContext(Arena.class, c -> {
			String arg = c.popFirstArg();
			Arena arena = arenaManager.getArena(arg);
			if(arena == null) {
				throw new InvalidCommandArgument(Lang.ARENACOMMAND_NO_ARENA.getString());
			}
			return arena;
		});
		manager.getCommandContexts().registerContext(StatType.class, c -> {
			String arg = c.popFirstArg();
			StatType stat = ReflectionUtils.getEnum(arg);
			if(stat == null) {
				throw new InvalidCommandArgument(Lang.WANTED_STAT_NOT_FOUND.getString());
			}
			return stat;
		});
		arenaCommand = new ArenaCommand();
		arenaCommand.init();
		manager.registerCommand(arenaCommand);
		manager.registerCommand(new WantedCommand());
		manager.registerCommand(new JoinCommand(this));
		manager.registerCommand(new LeaveCommand(this));
		getServer().getConsoleSender().sendMessage("[Wanted] Loaded in " + (System.currentTimeMillis() - oldTime) + "ms!");
		getServer().getConsoleSender().sendMessage("[Wanted] " + ChatColor.GOLD + "Wanted " + ChatColor.YELLOW + "v" + getDescription().getVersion() + " by bertek41 enabled!");
	}
	
	@Override
	public void onDisable() {
		fileManager.saveArenas();
		hologramManager.saveHolograms();
		getServer().getConsoleSender().sendMessage("[Wanted] " + ChatColor.GOLD + "Wanted " + ChatColor.YELLOW + "v" + getDescription().getVersion() + " by bertek41 disabled!");
	}
	
	public static Wanted getInstance() {
		return instance;
	}
	
	public String getNMSVersion() {
		return nmsVersion;
	}
	
	public ArenaCommand getArenaCommand() {
		return arenaCommand;
	}
	
	public ArenaManager getArenaManager() {
		return arenaManager;
	}
	
	public FileManager getFileManager() {
		return fileManager;
	}
	
	public HologramManager getHologramManager() {
		return hologramManager;
	}
	
	public StatsManager getStatsManager() {
		return statsManager;
	}
	
	public Database getDatabase() {
		return database;
	}
	
	public boolean isUseHd() {
		return useHd;
	}
	
	public void startRunnable() {
		if(!firstRun) {
			try {
				if(getServer().getScheduler().isCurrentlyRunning(hologramUpdate.getTaskId()) || getServer().getScheduler().isQueued(hologramUpdate.getTaskId())) {
					hologramUpdate.cancel();
				}
			} catch(Exception e) {
			}
		} else
			firstRun = false;
		if(Settings.HOLOGRAMS_UPDATE.getInt() < 1) {
			firstRun = true;
			return;
		}
		int tick = 1200 * Settings.HOLOGRAMS_UPDATE.getInt();
		hologramUpdate = new HologramUpdate(this);
		hologramUpdate.runTaskTimer(this, tick, tick);
	}
	
}
