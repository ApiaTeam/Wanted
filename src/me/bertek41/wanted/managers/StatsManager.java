package me.bertek41.wanted.managers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import me.bertek41.wanted.misc.StatType;
import me.bertek41.wanted.misc.Stats;

public class StatsManager {
	private HashMap<UUID, Stats> stats;
	
	public StatsManager() {
		this.stats = new HashMap<>();
	}
	
	public HashMap<UUID, Stats> getStats() {
		return stats;
	}
	
	public void setStats(HashMap<UUID, Stats> stats) {
		this.stats = stats;
	}
	
	public Stats getStats(OfflinePlayer player) {
		return getStats(player.getUniqueId());
	}
	
	public long getStats(OfflinePlayer player, StatType stat) {
		return getStats(player.getUniqueId(), stat);
	}
	
	public Stats getStats(UUID player) {
		Stats stat = stats.getOrDefault(player, new Stats(player));
		if(!stats.containsKey(player))
			stats.put(player, stat);
		return stat;
	}
	
	public long getStats(UUID player, StatType stat) {
		return getStats(player).getStats().getOrDefault(player, 0l);
	}
	
	public void addStats(OfflinePlayer player, StatType stat, int number) {
		addStats(player.getUniqueId(), stat, number);
	}
	
	public void setStats(OfflinePlayer player, StatType stat, int number) {
		setStats(player.getUniqueId(), stat, number);
	}
	
	public void addStats(UUID player, StatType stat, int number) {
		getStats(player).addStats(stat, number);
	}
	
	public void setStats(UUID player, StatType stat, int number) {
		getStats(player).setStats(stat, number);
	}
	
	public void addStats(UUID player, Stats stat) {
		stats.put(player, stat);
	}
	
	public void removeStats(UUID player) {
		stats.remove(player);
	}
	
}
