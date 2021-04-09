package me.bertek41.wanted.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.bertek41.wanted.gun.Gun;

public class GunFireEvent extends Event implements Cancellable {
	private final static HandlerList HANDLERS = new HandlerList();
	private final Player player;
	private Gun gun;
	private float yaw, pitch;
	private boolean isCancelled;
	
	public GunFireEvent(Player player, Gun gun, float yaw, float pitch) {
		this.player = player;
		this.gun = gun;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Gun getGun() {
		return gun;
	}
	
	public void setGun(Gun gun) {
		this.gun = gun;
	}
	
	@Override
	public boolean isCancelled() {
		return isCancelled;
	}
	
	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
}
