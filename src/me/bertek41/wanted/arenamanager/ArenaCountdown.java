package me.bertek41.wanted.arenamanager;

import org.bukkit.scheduler.BukkitRunnable;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.misc.Settings;

public class ArenaCountdown extends BukkitRunnable {
	private int time;
	private Arena arena;
	
	public ArenaCountdown(int time, Arena arena) {
		this.time = time;
		this.arena = arena;
	}
	
	public void start() {
		arena.setState(ArenaState.STARTING);
		runTaskTimer(Wanted.getInstance(), 0L, 20L);
	}
	
	@Override
	public void run() {
		if(arena.getPlayers().size() < arena.getRequiredPlayers()) {
			cancel();
			arena.setState(ArenaState.WAITING);
			arena.broadcast(Lang.ARENA_COUNTDOWN_TOO_FEW_PLAYERS.getString());
			arena.getOnlinePlayers().forEach(player -> ArenaScoreboard.update(arena, player, Settings.COUNTDOWN_LOBBY.getInt() + "", true));
			arena.setArenaCountdown(new ArenaCountdown(Settings.COUNTDOWN_LOBBY.getInt(), arena));
			return;
		}
		if(time == 0) {
			cancel();
			arena.getArenaGame().start();
			return;
		}
		if(arena.getState() == ArenaState.WAITING)
			arena.setState(ArenaState.STARTING);
		if(time % 15 == 0 || time == 10 || time <= 5) {
			if(time != 1)
				arena.broadcast(Lang.ARENA_COUNTDOWN_PLURAL.getString().replace("<time>", time + ""));
			else
				arena.broadcast(Lang.ARENA_COUNTDOWN_SINGULAR.getString().replace("<time>", time + ""));
		}
		arena.getOnlinePlayers().forEach(player -> ArenaScoreboard.update(arena, player, time + "", true));
		time--;
	}
	
	public boolean isRunning() {
		return arena.getState() == ArenaState.STARTING;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public Arena getArena() {
		return arena;
	}
	
}
