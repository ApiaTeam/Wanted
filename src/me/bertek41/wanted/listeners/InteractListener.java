package me.bertek41.wanted.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.arenamanager.ArenaPlayer;
import me.bertek41.wanted.gun.Gun;
import me.bertek41.wanted.gun.GunFire;
import me.bertek41.wanted.gun.GunManager;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.misc.Settings;
import me.bertek41.wanted.utils.Utils;

public class InteractListener implements Listener {
	private Wanted instance;
	
	public InteractListener(Wanted instance) {
		this.instance = instance;
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onClick(PlayerInteractEvent event) {
		if(event.getHand() != EquipmentSlot.HAND || event.getItem() == null || event.getItem().getItemMeta() == null || event.getAction() == Action.PHYSICAL)
			return;
		Player player = event.getPlayer();
		if(event.getItem().getItemMeta().hasDisplayName() && GunManager.getGun(event.getItem().getItemMeta().getDisplayName()) != null && instance.getArenaManager().containsPlayer(player)) {
			Arena arena = instance.getArenaManager().getArena(player);
			if(arena == null)
				return;
			ArenaPlayer arenaPlayer = arena.getArenaPlayer(player);
			event.setCancelled(true);
			if(arenaPlayer.isFreezed() && !arenaPlayer.isZoom())
				return;
			Gun gun = arenaPlayer.getGun();
			if(!gun.isReloading() && arenaPlayer.canShoot()) {
				boolean zoomed = false;
				if(gun.isHasZoom() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
					arenaPlayer.setZoom(!arenaPlayer.isZoom());
				} else {
					if(arenaPlayer.isZoom()) {
						zoomed = true;
						arenaPlayer.setZoom(false);
					} else if(gun.isZoomRequired())
						return;
					GunFire.fire(arena, arenaPlayer, player, gun, event.getItem(), zoomed);
				}
			}
		} else if(event.getItem().getType() == Material.valueOf(Settings.ITEMS_RETURN_LOBBY_ITEM.toString()) && instance.getArenaManager().containsPlayer(player)) {
			event.setCancelled(true);
			instance.getArenaManager().removePlayer(player, true);
			if(instance.getArenaManager().getSpawn() != null)
				player.teleport(instance.getArenaManager().getSpawn());
		} else if(event.getItem().getType() == Material.valueOf(Settings.ITEMS_JOIN_ARENA_ITEM.toString())) {
			event.setCancelled(true);
			if(Utils.getPacks().contains(player.getUniqueId())) {
				Lang.sendMessage(player, Lang.ACCEPT_RESOURCE_PACK.getString());
				return;
			}
			Utils.arenasGUI(player);
		} else if(event.getItem().getType() == Material.valueOf(Settings.ITEMS_GUNS_ITEM.toString())) {
			event.setCancelled(true);
			Arena arena = instance.getArenaManager().getArena(player);
			if(arena == null)
				return;
			Utils.gunGUI(arena.getArenaPlayer(player));
		}
	}
	
}
