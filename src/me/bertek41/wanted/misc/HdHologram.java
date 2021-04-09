package me.bertek41.wanted.misc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.bertek41.wanted.Wanted;

public class HdHologram {
	private Hologram hologram;
	private Location location;
	private List<String> lines;
	private StatType statType;
	
	public HdHologram(Location location) {
		this.location = location;
		this.lines = new ArrayList<>();
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
		if(hologram != null) {
			hologram.clearLines();
			lines.forEach(line -> hologram.appendTextLine(line));
		}
	}
	
	public StatType getStatType() {
		return statType;
	}
	
	public void setStatType(StatType statType) {
		this.statType = statType;
	}
	
	public void spawn() {
		hologram = HologramsAPI.createHologram(Wanted.getInstance(), location);
		lines.forEach(line -> hologram.appendTextLine(line));
	}
	
	public void remove() {
		if(hologram != null) {
			hologram.delete();
			return;
		}
		for(Hologram holo : HologramsAPI.getHolograms(Wanted.getInstance())) {
			if(holo.getLocation().equals(location))
				holo.delete();
		}
	}
	
}