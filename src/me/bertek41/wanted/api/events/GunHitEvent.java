package me.bertek41.wanted.api.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.bertek41.wanted.gun.Gun;

public class GunHitEvent extends EntityDamageByEntityEvent implements Cancellable {
	private final static HandlerList HANDLERS = new HandlerList();
	private Gun gun;
	private boolean isHeadshot;
	private boolean isCancelled = false;
	
	public GunHitEvent(Entity damager, Entity damagee, DamageCause cause, double damage, Gun gun, boolean isHeadshot) {
		super(damager, damagee, cause, damage);
		this.gun = gun;
		this.isHeadshot = isHeadshot;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	public Gun getGun() {
		return gun;
	}
	
	public void setGun(Gun gun) {
		this.gun = gun;
	}
	
	public boolean isHeadshot() {
		return isHeadshot;
	}
	
	public void setHeadshot(boolean isHeadshot) {
		this.isHeadshot = isHeadshot;
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
