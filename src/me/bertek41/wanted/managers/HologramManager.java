package me.bertek41.wanted.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.misc.HdHologram;
import me.bertek41.wanted.misc.StatType;
import me.bertek41.wanted.utils.ReflectionUtils;
import me.bertek41.wanted.utils.Utils;

public class HologramManager {
	private Wanted instance;
	private List<HdHologram> hdHolograms = new ArrayList<>();
	
	public HologramManager(Wanted instance) {
		this.instance = instance;
	}
	
	public void addHologram(HdHologram hdHologram) {
		hdHolograms.add(hdHologram);
	}
	
	public List<HdHologram> getHolograms(int x, int z) {
		List<HdHologram> list = new ArrayList<>();
		if(hdHolograms.isEmpty())
			return list;
		for(HdHologram hdHologram : hdHolograms) {
			if(hdHologram.getLocation().getBlockX() == x && hdHologram.getLocation().getBlockZ() == z)
				list.add(hdHologram);
		}
		return list;
	}
	
	public List<HdHologram> getHologramByChunk(int chunkX, int chunkZ) {
		List<HdHologram> list = new ArrayList<>();
		if(hdHolograms.isEmpty())
			return list;
		for(HdHologram hdHologram : hdHolograms) {
			if(hdHologram.getLocation().getChunk().getX() == chunkX && hdHologram.getLocation().getChunk().getZ() == chunkZ)
				list.add(hdHologram);
		}
		return list;
	}
	
	public List<HdHologram> getHolograms() {
		return hdHolograms;
	}
	
	public int deleteHologramsByRadius(Location location, int radius) {
		int amount = 0;
		if(hdHolograms.isEmpty())
			return amount;
		for(int x = location.getBlockX() - radius; x < location.getBlockX() + radius; x++) {
			for(int z = location.getBlockZ() - radius; z < location.getBlockZ() + radius; z++) {
				List<HdHologram> holograms = getHolograms(x, z);
				if(!holograms.isEmpty())
					holograms.forEach(holo -> holo.remove());
				amount += holograms.size();
				hdHolograms.removeAll(holograms);
			}
		}
		return amount;
	}
	
	public void readHolograms() {
		if(!instance.getFileManager().getADatabase().isSet("Holograms"))
			return;
		for(String string : instance.getFileManager().getADatabase().getConfigurationSection("Holograms").getKeys(false)) {
			Location location = instance.getFileManager().getLocationFromString(instance.getFileManager().getADatabase().getString("Holograms." + string + ".Location"));
			HdHologram hdHologram = new HdHologram(location);
			if(instance.getFileManager().getADatabase().isSet("Holograms." + string + ".Lines"))
				hdHologram.setLines(instance.getFileManager().getADatabase().getStringList("Holograms." + string + ".Lines"));
			else if(instance.getFileManager().getADatabase().isSet("Holograms." + string + ".Stat")) {
				StatType statType = ReflectionUtils.getEnum(instance.getFileManager().getADatabase().getString("Holograms." + string + ".Stat"));
				hdHologram.setStatType(statType);
				Utils.setLines(instance, hdHologram, statType);
			}
			hdHolograms.add(hdHologram);
			if(location.getChunk().isLoaded())
				hdHologram.spawn();
		}
	}
	
	public void saveHolograms() {
		if(hdHolograms.isEmpty())
			return;
		for(int i = 0; i < hdHolograms.size(); i++) {
			HdHologram hdHologram = hdHolograms.get(i);
			instance.getFileManager().getADatabase().set("Holograms." + i + ".Location", instance.getFileManager().getStringFromLocation(hdHologram.getLocation()));
			if(hdHologram.getStatType() == null)
				instance.getFileManager().getADatabase().set("Holograms." + i + ".Lines", hdHologram.getLines());
			else
				instance.getFileManager().getADatabase().set("Holograms." + i + ".Stat", hdHologram.getStatType().getName());
			hdHologram.remove();
		}
	}
	
}
