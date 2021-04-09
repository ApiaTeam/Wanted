package me.bertek41.wanted.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.misc.Settings;

public class ServerPingListener implements Listener {
	private Wanted instance;
	
	public ServerPingListener(Wanted instance) {
		this.instance = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onServerPing(ServerListPingEvent event) {
		if(Settings.BUNGEECORD_MODE.getBoolean() && Settings.STATE_AS_MOTD.getBoolean()) {
			Arena arena = instance.getArenaManager().getBestArena();
			if(arena == null || !arena.isCompleted())
				event.setMotd("null");
			else
				event.setMotd(arena.getSign());
		}
	}
	
}
