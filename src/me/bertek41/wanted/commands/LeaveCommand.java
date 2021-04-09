package me.bertek41.wanted.commands;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.misc.Lang;

@CommandAlias("leave|l")
public class LeaveCommand extends BaseCommand {
	private Wanted instance;
	
	public LeaveCommand(Wanted instance) {
		this.instance = instance;
	}
	
	@CatchUnknown
	@Default
	public void onMain(Player player, String[] args) {
		if(instance.getArenaManager().containsPlayer(player)) {
			instance.getArenaManager().removePlayer(player, true);
			if(instance.getArenaManager().getSpawn() != null)
				player.teleport(instance.getArenaManager().getSpawn());
		} else {
			Lang.sendMessage(player, Lang.YOU_DID_NOT_JOINED_ARENA.getString());
		}
	}
	
}
