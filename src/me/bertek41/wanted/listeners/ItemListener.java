package me.bertek41.wanted.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.arenamanager.ArenaPlayer;
import me.bertek41.wanted.gun.Gun;
import me.bertek41.wanted.gun.GunManager;
import me.bertek41.wanted.misc.Settings;

public class ItemListener implements Listener {
	private Wanted instance;
	
	public ItemListener(Wanted instance) {
		this.instance = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		if(event.getItemDrop().getItemStack().getType() == Material.valueOf(Settings.ITEMS_RETURN_LOBBY_ITEM.toString()))
			event.setCancelled(true);
		if(event.getItemDrop().getItemStack().getType() == Material.valueOf(Settings.ITEMS_JOIN_ARENA_ITEM.toString()))
			event.setCancelled(true);
		if(event.getItemDrop().getItemStack().getType() == Material.valueOf(Settings.ITEMS_GUNS_ITEM.toString()))
			event.setCancelled(true);
		if(instance.getArenaManager().containsPlayer(event.getPlayer()) && event.getItemDrop().getItemStack().hasItemMeta() && event.getItemDrop().getItemStack().getItemMeta().hasDisplayName()
				&& GunManager.getGun(event.getItemDrop().getItemStack().getItemMeta().getDisplayName()) != null)
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onHandChange(PlayerSwapHandItemsEvent event) {
		event.setCancelled(true);
		ItemStack item = event.getOffHandItem();
		if(item == null || item.getItemMeta() == null || !item.getItemMeta().hasDisplayName() || GunManager.getGun(item.getItemMeta().getDisplayName()) == null)
			return;
		Player player = event.getPlayer();
		Arena arena = instance.getArenaManager().getArena(player);
		if(arena == null)
			return;
		ArenaPlayer arenaPlayer = arena.getArenaPlayer(player);
		if(arenaPlayer == null)
			return;
		Gun gun = arenaPlayer.getGun();
		if(gun != null && !gun.isReloading())
			gun.reload(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSlotChange(PlayerItemHeldEvent event) {
		if(!instance.getArenaManager().containsPlayer(event.getPlayer()))
			return;
		Arena arena = instance.getArenaManager().getArena(event.getPlayer());
		ArenaPlayer arenaPlayer = arena.getArenaPlayer(event.getPlayer());
		if(!arenaPlayer.isZoom())
			return;
		ItemStack item = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
		if(item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && GunManager.getGun(item.getItemMeta().getDisplayName()) != null)
			arenaPlayer.setZoom(false);
	}
	
}
