package me.bertek41.wanted.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.misc.HdHologram;

public class ChunkListener implements Listener {
	private Wanted instance;
	
	public ChunkListener(Wanted instance) {
		this.instance = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChunkLoad(ChunkLoadEvent event) {
		if(event.getChunk().isLoaded()) {
			List<HdHologram> hdHolograms = instance.getHologramManager().getHologramByChunk(event.getChunk().getX(), event.getChunk().getZ());
			if(!hdHolograms.isEmpty())
				hdHolograms.forEach(hologram -> hologram.spawn());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChunkUnload(ChunkUnloadEvent event) {
		List<HdHologram> hdHolograms = instance.getHologramManager().getHologramByChunk(event.getChunk().getX(), event.getChunk().getZ());
		if(!hdHolograms.isEmpty())
			hdHolograms.forEach(hologram -> hologram.remove());
	}
	
}
