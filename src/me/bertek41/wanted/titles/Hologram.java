package me.bertek41.wanted.titles;

import org.bukkit.Location;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.misc.StatType;

import java.util.List;

public class Hologram {
	private Location location;
	private List<String> lines;
	private StatType statType;
	
	public Hologram(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public List<String> getLines() {
		return lines;
	}
	
	public void setLines(List<String> lines) {
		this.lines = lines;
	}
	
	public StatType getStatType() {
		return statType;
	}
	
	public void setStatType(StatType statType) {
		this.statType = statType;
	}
	
	public void spawn() {
		com.gmail.filoghost.holographicdisplays.api.Hologram hologram = HologramsAPI.createHologram(Wanted.getInstance(), location);
		lines.forEach(line -> hologram.appendTextLine(line));
	}
	
	public void remove() {
		for(com.gmail.filoghost.holographicdisplays.api.Hologram hologram : HologramsAPI.getHolograms(Wanted.getInstance())) {
			if(hologram.getLocation().equals(location))
				hologram.delete();
		}
	}
	
}