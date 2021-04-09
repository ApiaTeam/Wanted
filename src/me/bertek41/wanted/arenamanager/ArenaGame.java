package me.bertek41.wanted.arenamanager;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.misc.Settings;
import me.bertek41.wanted.misc.StatType;
import me.bertek41.wanted.utils.Utils;

public class ArenaGame extends BukkitRunnable {
	private Arena arena;
	private int time = Settings.COUNTDOWN_GAME.getInt();
	private int wait = 3;
	
	public ArenaGame(Arena arena) {
		this.arena = arena;
	}
	
	public void start() {
		arena.setState(ArenaState.INGAME);
		Player wanted = arena.getRandomPlayer();
		int index = 0;
		for(ArenaPlayer arenaPlayer : arena.getPlayers()) {
			if(arenaPlayer.getOfflinePlayer().isOnline()) {
				Player player = arenaPlayer.getPlayer();
				player.setGameMode(GameMode.SURVIVAL);
				player.setFoodLevel(20);
				Utils.clearItems(player);
				player.getInventory().setItem(0, arenaPlayer.getGun().getItem());
				player.getInventory().setItem(8,
						Utils.makeItem(Material.valueOf(Settings.ITEMS_GUNS_ITEM.toString()), Settings.ITEMS_GUNS_NAME.getString(), null, Settings.ITEMS_GUNS_LORE.getStringList()));
				arenaPlayer.setFreezed(true);
				Utils.freeze(player);
				Utils.sendTeamPacket(null, Utils.Color.valueOf(Settings.GLOW_COLOR.toString()), true, false, "always", "always", player);
				if(player.getUniqueId().equals(wanted.getUniqueId()))
					continue;
				player.teleport(arena.getLocation(index++));
				Utils.sendWorldBorder(arena, player);
			}
		}
		arena.setWanted(wanted, false);
		arena.getWanted().teleport(arena.getWantedLocation());
		Utils.sendWorldBorder(arena, arena.getWanted());
		runTaskTimer(Wanted.getInstance(), 0, 20);
		new BukkitRunnable() {
			@Override
			public void run() {
				if(arena.getState() != ArenaState.INGAME || time <= 0 || arena.getPlayers().size() <= 1) {
					cancel();
					return;
				}
				Utils.setGlow(arena.getWanted(), (Settings.GAME_GLOW_DURATION.getInt() * 20));
			}
		}.runTaskTimer(Wanted.getInstance(), 0, (Settings.GAME_GLOW_DURATION_COUNTDOWN.getInt() * 20));
	}
	
	@Override
	public void run() {
		for(Player player : arena.getOnlinePlayers()) {
			ArenaScoreboard.update(arena, player, convertMinute(time), false);
			ArenaTab.updateCoin(arena);
			if(wait == 0) {
				Utils.sendTitle(player, 0, 20, 0, "§a" + wait, "§a" + wait);
				arena.getArenaPlayer(player).setFreezed(false);
				Utils.unfreeze(player);
			} else if(wait > 0)
				Utils.sendTitle(player, 0, 20, 0, "§a" + wait, "§a" + wait);
		}
		if(wait >= 0)
			wait--;
		if(time <= 0 || arena.getOnlinePlayers().size() < 2 || arena.getPlayers().stream().filter(player -> player.getCoin() >= arena.getMaximumReward()).findAny().isPresent()) {
			arena.setState(ArenaState.END);
			cancel();
			Set<OfflinePlayer> winners = arena.getHighestCoin();
			if(winners != null) {
				if(arena.getPlayers().size() != 1 && winners.size() == arena.getPlayers().size()) {
					arena.broadcast(Lang.ARENA_GAME_DRAW.getString());
					for(OfflinePlayer player : arena.getPlayersAsOfflinePlayer()) {
						Wanted.getInstance().getStatsManager().addStats(player, StatType.DRAWS, 1);
						Settings.DRAW_COMMANDS.getStringList().forEach(command -> Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("<player>", player.getName())));
					}
				} else {
					arena.broadcast(Lang.ARENA_GAME_END.getString().replace("<player>", winners.stream().map(OfflinePlayer::getName).collect(Collectors.joining(", "))));
					for(OfflinePlayer winner : winners) {
						Settings.WIN_COMMANDS.getStringList().forEach(command -> Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("<player>", winner.getName())));
						Wanted.getInstance().getStatsManager().addStats(winner, StatType.WINS, 1);
					}
					for(OfflinePlayer player : arena.getPlayersAsOfflinePlayer()) {
						if(winners != null && winners.contains(player))
							continue;
						Wanted.getInstance().getStatsManager().addStats(player, StatType.DEFEATS, 1);
						Settings.LOSE_COMMANDS.getStringList().forEach(command -> Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("<player>", player.getName())));
					}
				}
			} else
				arena.broadcast(Lang.ARENA_GAME_END_NO_WINNER.getString());
			arena.getPlayers().forEach(player -> Wanted.getInstance().getStatsManager().addStats(player.getOfflinePlayer(), StatType.GAMES_PLAYED, 1));
			arena.reset();
			return;
		}
		time--;
	}
	
	public String convertMinute(int time) {
		int minutes = time / 60;
		int seconds = time % 60;
		String min = (minutes < 10 ? "0" : "") + minutes;
		String sec = (seconds < 10 ? "0" : "") + seconds;
		return min + ":" + sec;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
}
