package me.bertek41.wanted.misc;

import java.util.HashMap;
import java.util.UUID;

public class Stats {
	private UUID player;
	private HashMap<StatType, Long> stats;
	
	public Stats(UUID player) {
		this.player = player;
		stats = new HashMap<>();
	}
	
	public Stats(UUID player, long coins, long gamesPlayed, long kills, long deaths, long shots, long shotsOnTarget, long headshot, long wins, long draws, long defeats) {
		this(player);
		stats.put(StatType.COINS, coins);
		stats.put(StatType.GAMES_PLAYED, gamesPlayed);
		stats.put(StatType.KILLS, kills);
		stats.put(StatType.DEATHS, deaths);
		stats.put(StatType.SHOTS, shots);
		stats.put(StatType.SHOTS_ON_TARGET, shotsOnTarget);
		stats.put(StatType.HEADSHOTS, headshot);
		stats.put(StatType.WINS, wins);
		stats.put(StatType.DRAWS, draws);
		stats.put(StatType.DEFEATS, defeats);
	}
	
	public UUID getPlayer() {
		return player;
	}
	
	public void setPlayer(UUID player) {
		this.player = player;
	}
	
	public HashMap<StatType, Long> getStats() {
		return stats;
	}
	
	public void setStats(HashMap<StatType, Long> stats) {
		this.stats = stats;
	}
	
	public long getStat(StatType type) {
		return stats.getOrDefault(type, 0l);
	}
	
	public void addStats(StatType type, long number) {
		stats.put(type, getStat(type) + number);
	}
	
	public void setStats(StatType type, long number) {
		stats.put(type, number);
	}
	
	public String saveQuery() {
		return "INSERT INTO Stats(Uuid, Coins, GamesPlayed, Kills, Deaths, Shots, ShotsOnTarget, Headshots, Wins, Draws, Defeats) VALUES(\'" + player.toString() + "\', \'"
				+ stats.getOrDefault(StatType.COINS, 0l) + "\', \'" + stats.getOrDefault(StatType.GAMES_PLAYED, 0l) + "\', \'" + stats.getOrDefault(StatType.KILLS, 0l) + "\', \'"
				+ stats.getOrDefault(StatType.DEATHS, 0l) + "\', \'" + stats.getOrDefault(StatType.SHOTS, 0l) + "\', \'" + stats.getOrDefault(StatType.SHOTS_ON_TARGET, 0l) + "\', \'"
				+ stats.getOrDefault(StatType.HEADSHOTS, 0l) + "\', \'" + stats.getOrDefault(StatType.DRAWS, 0l) + "\', \'" + stats.getOrDefault(StatType.WINS, 0l) + "\', \'"
				+ stats.getOrDefault(StatType.DEFEATS, 0l) + "\') ON CONFLICT(Uuid) DO UPDATE SET Coins=\'" + stats.getOrDefault(StatType.COINS, 0l) + "\', GamesPlayed=\'"
				+ stats.getOrDefault(StatType.GAMES_PLAYED, 0l) + "\', Kills=\'" + stats.getOrDefault(StatType.KILLS, 0l) + "\', Deaths=\'" + stats.getOrDefault(StatType.DEATHS, 0l) + "\', Shots=\'"
				+ stats.getOrDefault(StatType.SHOTS, 0l) + "\', ShotsOnTarget=\'" + stats.getOrDefault(StatType.SHOTS_ON_TARGET, 0l) + "\', Headshots=\'" + stats.getOrDefault(StatType.HEADSHOTS, 0l)
				+ "\', Wins=\'" + stats.getOrDefault(StatType.WINS, 0l) + "\', Draws=\'" + stats.getOrDefault(StatType.DRAWS, 0l) + "\', Defeats=\'" + stats.getOrDefault(StatType.DEFEATS, 0l) + "\';";
	}
	
}
