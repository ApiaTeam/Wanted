package me.bertek41.wanted.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import me.bertek41.wanted.Wanted;

public class BlockListener implements Listener {
	private Wanted instance;
	
	public BlockListener(Wanted instance) {
		this.instance = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!event.getPlayer().hasPermission("wanted.admin") || instance.getArenaManager().containsPlayer(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if(!event.getPlayer().hasPermission("wanted.admin") || instance.getArenaManager().containsPlayer(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
	}
	
}
