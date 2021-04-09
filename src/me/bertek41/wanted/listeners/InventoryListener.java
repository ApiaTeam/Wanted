package me.bertek41.wanted.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.arenamanager.ArenaPlayer;
import me.bertek41.wanted.gun.Gun;
import me.bertek41.wanted.gun.GunManager;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.misc.Settings;

public class InventoryListener implements Listener {
	private Wanted instance;
	
	public InventoryListener(Wanted instance) {
		this.instance = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if(!(event.getWhoClicked() instanceof Player))
			return;
		if(event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || event.getCurrentItem().getType() == Material.AIR)
			return;
		Player player = (Player) event.getWhoClicked();
		if(event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.PLAYER) {
			if(event.getCurrentItem().getType() == Material.valueOf(Settings.ITEMS_RETURN_LOBBY_ITEM.toString()))
				event.setResult(Result.DENY);
			if(event.getCurrentItem().getType() == Material.valueOf(Settings.ITEMS_JOIN_ARENA_ITEM.toString()))
				event.setResult(Result.DENY);
			if(event.getCurrentItem().getType() == Material.valueOf(Settings.ITEMS_GUNS_ITEM.toString()))
				event.setResult(Result.DENY);
			if(event.getCurrentItem().getType() == Material.PUMPKIN)
				event.setResult(Result.DENY);
			return;
		}
		if(event.getView().getTitle().equals(Lang.ARENAS_GUI_TITLE.getString())) {
			event.setCancelled(true);
			if(event.getCurrentItem().getItemMeta().hasLocalizedName()) {
				Arena arena = instance.getArenaManager().getArena(event.getCurrentItem().getItemMeta().getLocalizedName());
				if(arena == null)
					return;
				arena.addPlayer(player);
			}
		} else if(event.getView().getTitle().equals(Lang.GUNS_GUI_TITLE.getString())) {
			event.setCancelled(true);
			if(event.getCurrentItem().getItemMeta().hasLocalizedName()) {
				Gun gun = GunManager.getGun(event.getCurrentItem().getItemMeta().getLocalizedName());
				Arena arena = instance.getArenaManager().getArena(player);
				ArenaPlayer arenaPlayer = arena.getArenaPlayer(player);
				if(gun == null || arena == null)
					return;
				if(!arenaPlayer.getGuns().contains(gun.getId()) && gun.hasCoin() && arena.getCoin(player) >= gun.getCoin()) {
					arenaPlayer.setNextGun(gun);
					Lang.sendMessage(player, Lang.GUNS_GOT.getString().replace("<gun>", gun.getName()));
				} else {
					if(arenaPlayer.getGuns().contains(gun.getId()) || !gun.hasCoin()) {
						arenaPlayer.setNextGun(gun);
						Lang.sendMessage(player, Lang.GUNS_GOT.getString().replace("<gun>", gun.getName()));
					} else
						Lang.sendMessage(player, Lang.GUNS_NOT_ENOUGH_COIN.getString());
				}
				player.closeInventory();
			}
		}
	}
	
}
