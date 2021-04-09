package me.bertek41.wanted.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.arenamanager.ArenaState;

public class ArenaStateChangeEvent extends Event {
	private final static HandlerList HANDLERS = new HandlerList();
	private final Arena arena;
	private final ArenaState arenaState;
	
	public ArenaStateChangeEvent(Arena arena, ArenaState arenaState) {
		this.arena = arena;
		this.arenaState = arenaState;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public ArenaState getArenaState() {
		return arenaState;
	}
	
}