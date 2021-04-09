package me.bertek41.wanted.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.api.events.GunHitEvent;
import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.arenamanager.ArenaState;
import me.bertek41.wanted.utils.Utils;

public class DamageListener implements Listener {
	private Wanted instance;
	
	public DamageListener(Wanted instance) {
		this.instance = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player && (Utils.getPacks().contains(event.getEntity().getUniqueId()) || event.getCause() == DamageCause.FALL))
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player && !((event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) || event.getDamager() instanceof Player)
				&& instance.getArenaManager().containsPlayer((Player) event.getEntity())) {
			event.setCancelled(true);
			return;
		}
		if(!(event.getEntity() instanceof Player && ((event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) || event.getDamager() instanceof Player)))
			return;
		Player player = event.getDamager() instanceof Arrow ? (Player) ((Arrow) event.getDamager()).getShooter() : (Player) event.getDamager();
		Arena arena = instance.getArenaManager().getArena(player);
		if(arena == null || arena.getState() != ArenaState.INGAME)
			event.setCancelled(true);
		else if(arena.getState() == ArenaState.INGAME && !(event instanceof GunHitEvent)) {
			event.setCancelled(true);
		}
	}
	
}
