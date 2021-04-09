package me.bertek41.wanted.commands;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.utils.Utils;

@CommandAlias("join|j")
public class JoinCommand extends BaseCommand {
	private Wanted instance;
	
	public JoinCommand(Wanted instance) {
		this.instance = instance;
	}
	
	@CatchUnknown
	@Default
	public void onMain(Player player, String[] args) {
		if(!Utils.loadedResourcePack(player)) {
			Lang.sendMessage(player, Lang.ACCEPT_RESOURCE_PACK.getString());
			return;
		}
		if(args.length == 0) {
			Utils.arenasGUI(player);
			return;
		}
		if(!instance.getArenaManager().containsArena(args[0])) {
			Lang.sendMessage(player, Lang.ARENACOMMAND_NO_ARENA.getString());
			return;
		}
		Arena arena = instance.getArenaManager().getArena(args[0]);
		if(arena.isCompleted()) {
			arena.addPlayer(player);
		} else {
			Lang.sendMessage(player, Lang.JOIN_ARENA_NOT_COMPLETED.getString());
		}
	}
	
}
