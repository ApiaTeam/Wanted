package me.bertek41.wanted.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.arenamanager.ArenaPlayer;

public class ArenaQuitEvent extends Event implements Cancellable {
	private final static HandlerList HANDLERS = new HandlerList();
	private final Arena arena;
	private final ArenaPlayer arenaPlayer;
	private boolean online, left, isCancelled = false;
	
	public ArenaQuitEvent(Arena arena, ArenaPlayer arenaPlayer, boolean online, boolean left) {
		this.arena = arena;
		this.arenaPlayer = arenaPlayer;
		this.online = online;
		this.left = left;
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
	
	public boolean isOnline() {
		return online;
	}
	
	public boolean isLeft() {
		return left;
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