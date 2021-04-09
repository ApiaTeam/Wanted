package me.bertek41.wanted.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.arenamanager.ArenaPlayer;

public class ArenaJoinEvent extends Event implements Cancellable {
	private final static HandlerList HANDLERS = new HandlerList();
	private final Arena arena;
	private final ArenaPlayer arenaPlayer;
	private boolean isCancelled = false;
	
	public ArenaJoinEvent(Arena arena, ArenaPlayer arenaPlayer) {
		this.arena = arena;
		this.arenaPlayer = arenaPlayer;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public ArenaPlayer getArenaPlayer() {
		return arenaPlayer;
	}
	
	@Override
	public boolean isCancelled() {
		return isCancelled;
	}
	
	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}
	
}