package me.bertek41.wanted.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.arenamanager.ArenaPlayer;
import me.bertek41.wanted.arenamanager.ArenaState;
import me.bertek41.wanted.arenamanager.ArenaTab;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.misc.Settings;
import me.bertek41.wanted.misc.Stats;
import me.bertek41.wanted.utils.Utils;

public class JoinQuitListener implements Listener {
	private Wanted instance;
	
	public JoinQuitListener(Wanted instance) {
		this.instance = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		event.setJoinMessage(null);
		player.setGameMode(GameMode.SURVIVAL);
		ArenaTab.setTablist(player);
		for(Player playerNew : Bukkit.getOnlinePlayers()) {
			if(instance.getArenaManager().containsPlayer(playerNew)) {
				player.hidePlayer(instance, playerNew);
				playerNew.hidePlayer(instance, player);
			} else {
				player.showPlayer(instance, playerNew);
				playerNew.showPlayer(instance, player);
			}
		}
		if(Settings.BUNGEECORD_MODE.getBoolean())
			instance.getArenaManager().addRandomly(player);
		else if(instance.getArenaManager().getSpawn() != null)
			player.teleport(instance.getArenaManager().getSpawn());
		Utils.clearItems(player);
		if(!Settings.USE_RESOURCE_PACK.getBoolean()) {
			join(player);
			return;
		}
		Utils.setPack(player, true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPackRequest(PlayerResourcePackStatusEvent event) {
		if(instance.getArenaManager().containsPlayer(event.getPlayer()))
			return;
		if(event.getStatus() == Status.SUCCESSFULLY_LOADED) {
			join(event.getPlayer());
		} else if(event.getStatus() != Status.ACCEPTED) {
			if(Settings.USE_RESOURCE_PACK.getBoolean() && Settings.RESOURCE_PACK_REQUIRED.getBoolean()) {
				if(Utils.getPacks().contains(event.getPlayer().getUniqueId()))
					Utils.getPacks().remove(event.getPlayer().getUniqueId());
				event.getPlayer().kickPlayer(Lang.RESOURCE_PACK_REQUIRED.getString());
			} else
				join(event.getPlayer());
		}
	}
	
	private void join(Player player) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			ResultSet resultSet = instance.getDatabase().query("SELECT * FROM Stats WHERE Uuid=\'" + player.getUniqueId().toString() + "\'");
			try {
				while(resultSet.next()) {
					instance.getStatsManager().addStats(player.getUniqueId(),
							new Stats(player.getUniqueId(), resultSet.getLong("Coins"), resultSet.getLong("GamesPlayed"), resultSet.getLong("Kills"), resultSet.getLong("Deaths"),
									resultSet.getLong("Shots"), resultSet.getLong("ShotsOnTarget"), resultSet.getLong("Headshots"), resultSet.getLong("Wins"), resultSet.getLong("Draws"),
									resultSet.getLong("Defeats")));
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		});
		Utils.unfreeze(player);
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		if(Settings.BUNGEECORD_MODE.getBoolean()) {
			Arena arena = instance.getArenaManager().getBestArena();
			if(arena == null || !arena.isCompleted() || arena.getState() == ArenaState.FULL || arena.getState() == ArenaState.INGAME)
				Utils.sendHub(player);
			else
				arena.addPlayer(player);
		} else
			Utils.setLobbyItems(player, true, Utils.getPacks().contains(player.getUniqueId()));
		if(Utils.getPacks().contains(player.getUniqueId()))
			Utils.getPacks().remove(player.getUniqueId());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		event.setQuitMessage(null);
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			Stats stats = instance.getStatsManager().getStats(player);
			if(stats == null)
				return;
			instance.getDatabase().update(stats.saveQuery());
			instance.getStatsManager().removeStats(player.getUniqueId());
		});
		if(instance.getArenaManager().containsPlayer(player)) {
			Arena arena = instance.getArenaManager().getArena(player);
			ArenaPlayer arenaPlayer = arena.getArenaPlayer(player);
			if(arenaPlayer.isZoom())
				arenaPlayer.setZoom(false);
			arena.removePlayer(player, false, false);
		}
	}
	
}
