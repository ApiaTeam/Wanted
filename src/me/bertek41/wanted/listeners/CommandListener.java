package me.bertek41.wanted.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.misc.Settings;

public class CommandListener implements Listener {
	private Wanted instance;
	
	public CommandListener(Wanted instance) {
		this.instance = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if(!instance.getArenaManager().containsPlayer(event.getPlayer()) || event.getPlayer().hasPermission("wanted.admin"))
			return;
		int i = Settings.ALLOWED_COMMANDS.getStringList().size();
		for(String command : Settings.ALLOWED_COMMANDS.getStringList()) {
			if(Settings.MAKE_ALLOWED_LIKE_BLOCKED.getBoolean()) {
				if(event.getMessage().equalsIgnoreCase(command)) {
					event.setCancelled(true);
					Lang.sendMessage(event.getPlayer(), Lang.COMMAND_BLOCKED.getString());
					return;
				}
			} else {
				if(!event.getMessage().equalsIgnoreCase(command)) {
					i--;
					break;
				}
			}
		}
		if(i != Settings.ALLOWED_COMMANDS.getStringList().size() - 1) {
			event.setCancelled(true);
			Lang.sendMessage(event.getPlayer(), Lang.COMMAND_BLOCKED.getString());
		}
	}
	
}
