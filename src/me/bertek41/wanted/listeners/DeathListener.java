package me.bertek41.wanted.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.api.events.GunHitEvent;
import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.arenamanager.ArenaPlayer;
import me.bertek41.wanted.arenamanager.ArenaState;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.misc.Settings;
import me.bertek41.wanted.misc.StatType;
import me.bertek41.wanted.utils.Utils;

public class DeathListener implements Listener {
	private Wanted instance;
	
	public DeathListener(Wanted instance) {
		this.instance = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent event) {
		Player victim = event.getEntity();
		event.setDeathMessage(null);
		event.getDrops().clear();
		event.setDroppedExp(0);
		Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
			if(instance.getArenaManager().containsPlayer(victim)) {
				Arena arena = instance.getArenaManager().getArena(victim);
				ArenaPlayer arenaPlayer = arena.getArenaPlayer(victim);
				if(arenaPlayer.isZoom())
					arenaPlayer.setZoom(false);
				if(arenaPlayer.getCooldown() != 0)
					arenaPlayer.setCooldown(0);
				victim.spigot().respawn();
				if(arena.getState() != ArenaState.INGAME) {
					victim.teleport(arena.getLobby());
					Utils.setArenaItems(victim);
					return;
				}
				s: if(event.getEntity().getLastDamageCause() != null && event.getEntity().getLastDamageCause() instanceof GunHitEvent
						&& ((GunHitEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Player) {
					Player attacker = (Player) ((GunHitEvent) event.getEntity().getLastDamageCause()).getDamager();
					instance.getStatsManager().addStats(victim, StatType.DEATHS, 1);
					instance.getStatsManager().addStats(attacker, StatType.KILLS, 1);
					arena.addCoin(attacker, Settings.PRICE_PER_KILL.getInt());
					arena.addDeath(victim);
					arena.addKill(attacker);
					if(victim.getGameMode() == GameMode.SPECTATOR)
						break s;
					if(arena.isWanted(victim)) {
						arena.broadcast(Lang.WANTED_KILLED_BY_PLAYER.getString().replace("<wanted>", victim.getName()).replace("<player>", attacker.getName()));
						arena.setWanted(attacker, true);
					} else
						arena.broadcast(Lang.PLAYER_KILLED_BY_WANTED.getString().replace("<wanted>", attacker.getName()).replace("<player>", victim.getName()));
				}
				victim.getInventory().clear();
				respawnPlayer(arena, victim, arenaPlayer);
			} else {
				Utils.setLobbyItems(victim, true, true);
				victim.spigot().respawn();
				victim.teleport(instance.getArenaManager().getSpawn());
			}
		}, 1L);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
		player.setFoodLevel(20);
	}
	
	private synchronized void respawnPlayer(Arena arena, Player player, ArenaPlayer arenaPlayer) {
		player.setGameMode(GameMode.SPECTATOR);
		player.setAllowFlight(true);
		player.teleport(arena.getSpectate());
		arenaPlayer.setFreezed(true);
		arenaPlayer.setInvincible(true);
		Utils.freeze(player);
		arenaPlayer.getGun().setCurrentBullet(arenaPlayer.getGun().getMagazine());
		new BukkitRunnable() {
			int i = 3;
			
			@Override
			public void run() {
				if(i <= 0) {
					cancel();
					player.setGameMode(GameMode.SURVIVAL);
					player.setFoodLevel(20);
					player.setAllowFlight(false);
					if(arena.getState() == ArenaState.INGAME) {
						player.teleport(arena.getRandomLocation());
						new BukkitRunnable() {
							@Override
							public void run() {
								if(arenaPlayer != null)
									arenaPlayer.setInvincible(false);
							}
						}.runTaskLater(instance, 40);
						if(arenaPlayer.getNextCoin() != 0 && arenaPlayer.getCoin() >= arenaPlayer.getNextCoin()) {
							arenaPlayer.removeCoin(arenaPlayer.getNextCoin());
						}
						player.getInventory().addItem(arenaPlayer.getNextGun().getItem());
						player.getInventory().setItem(8,
								Utils.makeItem(Material.valueOf(Settings.ITEMS_GUNS_ITEM.toString()), Settings.ITEMS_GUNS_NAME.getString(), null, Settings.ITEMS_GUNS_LORE.getStringList()));
						Utils.sendWorldBorder(arena, player);
					} else {
						player.teleport(arena.getLobby());
						Utils.setLobbyItems(player, true, true);
					}
					arenaPlayer.setFreezed(false);
					Utils.unfreeze(player);
					Utils.sendTitle(player, 0, 0, 0, "", "");
					Utils.sendActionBar(player, "");
					return;
				}
				Utils.sendTitle(player, 0, 40, 0, Lang.RESPAWNING_TITLE.getString().replace("<second>", i + ""), Lang.RESPAWNING_SUBTITLE.getString().replace("<second>", i + ""));
				Utils.sendActionBar(player, Lang.RESPAWNING_ACTIONBAR.getString().replace("<second>", i + ""));
				i--;
			}
		}.runTaskTimer(instance, 0, 20);
	}
	
}
