package me.bertek41.wanted.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.google.common.collect.ImmutableCollection;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.arenamanager.Arena;

public class ChatListener implements Listener {
	private Wanted instance;
	
	public ChatListener(Wanted instance) {
		this.instance = instance;
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		if(event.getRecipients() instanceof ImmutableCollection)
			return;
		event.getRecipients().clear();
		if(instance.getArenaManager().containsPlayer(event.getPlayer())) {
			Arena arena = instance.getArenaManager().getArena(event.getPlayer());
			arena.getOnlinePlayers().forEach(event.getRecipients()::add);
		} else {
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(!instance.getArenaManager().containsPlayer(player)) {
					event.getRecipients().add(player);
					
				}
			}
		}
	}
	
}
