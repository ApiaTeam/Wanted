package me.bertek41.wanted.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.arenamanager.ArenaState;
import me.bertek41.wanted.utils.Utils;

public class FoodListener implements Listener {
	private Wanted instance;
	
	public FoodListener(Wanted instance) {
		this.instance = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if(!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		Arena arena = instance.getArenaManager().getArena(player);
		if(Utils.getPacks().contains(player.getUniqueId()) || (arena != null && !arena.getArenaPlayer(player).isZoom() && arena.getArenaPlayer(player).isFreezed())) {
			event.setFoodLevel(6);
			event.setCancelled(false);
			return;
		}
		event.setFoodLevel(20);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onRegen(EntityRegainHealthEvent event) {
		if(!(event.getEntity() instanceof Player) || (event.getRegainReason() != RegainReason.REGEN && event.getRegainReason() != RegainReason.SATIATED))
			return;
		Player player = (Player) event.getEntity();
		Arena arena = instance.getArenaManager().getArena(player);
		if(arena == null || arena.getState() != ArenaState.INGAME || arena.isWanted(player))
			return;
		event.setCancelled(true);
	}
	
}
