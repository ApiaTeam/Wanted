package me.bertek41.wanted.misc;

import org.bukkit.entity.Player;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.utils.ReflectionUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPIHook extends PlaceholderExpansion {
	private Wanted instance;
	
	public PAPIHook(Wanted instance) {
		this.instance = instance;
	}
	
	@Override
	public boolean persist() {
		return true;
	}
	
	@Override
	public String getAuthor() {
		return "bertek41";
	}
	
	@Override
	public String getIdentifier() {
		return "wanted";
	}
	
	@Override
	public String getVersion() {
		return "1.0";
	}
	
	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		StatType stat = ReflectionUtils.getEnum(identifier);
		if(stat != null) {
			return instance.getStatsManager().getStats(player, stat) + "";
		}
		return "";
		
	}
	
}
