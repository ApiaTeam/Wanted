package me.bertek41.wanted.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.misc.Lang;

public class ArenaManager {
	private Map<Arena, Boolean> arenas;
	private List<Sign> signs;
	private Location spawn;
	
	public ArenaManager() {
		arenas = new HashMap<>();
		signs = new ArrayList<>();
	}
	
	public void addArena(Arena arena, boolean active) {
		arenas.put(arena, active);
	}
	
	public void addRandomly(Player player) {
		Arena arena = getBestArena();
		if(arena != null && arena.getName() != null)
			arena.addPlayer(player);
	}
	
	public void removeArena(Arena arena) {
		if(arenas.containsKey(arena))
			arenas.remove(arena);
	}
	
	public int getNewId() {
		int i = 0;
		if(arenas.isEmpty())
			return i;
		for(Arena arena : arenas.keySet()) {
			if(arena.getId() >= i)
				i = arena.getId();
		}
		return i++;
	}
	
	public Arena getArena(int id) {
		if(arenas.isEmpty())
			return null;
		for(Arena arena : arenas.keySet()) {
			if(arena.getId() == id)
				return arena;
		}
		return null;
	}
	
	public Arena getArena(String name) {
		if(arenas.isEmpty())
			return null;
		for(Arena arena : arenas.keySet()) {
			if(arena.getName().equals(name) || arena.getNameWithoutColors().equals(name))
				return arena;
		}
		return null;
	}
	
	public Arena getArena(Player player) {
		for(Arena arena : arenas.keySet()) {
			if(arena.containsPlayer(player))
				return arena;
		}
		return null;
	}
	
	public Arena getArena(Sign sign) {
		if(arenas.isEmpty())
			return null;
		for(Arena arena : arenas.keySet()) {
			if(arena.getSigns() == null || arena.getSigns().isEmpty())
				continue;
			if(arena.getSigns().contains(sign))
				return arena;
		}
		return null;
	}
	
	public Arena getBestArena() {
		if(arenas.isEmpty())
			return null;
		Arena arena = arenas.keySet().iterator().next();
		for(Arena arenaNew : arenas.keySet()) {
			if(arenas.get(arenaNew) && arenaNew.getPlayers().size() >= arena.getPlayers().size())
				arena = arenaNew;
		}
		return arena;
	}
	
	public boolean containsArena(String string) {
		if(arenas.isEmpty())
			return false;
		for(Arena arena : arenas.keySet()) {
			if(arena.getName().equals(string))
				return true;
		}
		return false;
	}
	
	public boolean containsPlayer(Player player) {
		if(arenas.isEmpty())
			return false;
		for(Arena arena : arenas.keySet()) {
			if(arena.containsPlayer(player))
				return true;
		}
		return false;
	}
	
	public void removePlayer(Player player, boolean left) {
		if(arenas.isEmpty())
			return;
		for(Arena arena : arenas.keySet()) {
			if(arena.containsPlayer(player))
				arena.removePlayer(player, true, left);
		}
	}
	
	public Set<Arena> getArenas() {
		Set<Arena> list = Sets.newHashSet();
		for(Arena arena : arenas.keySet()) {
			if(arenas.get(arena))
				list.add(arena);
		}
		return list;
	}
	
	public List<String> getArenasAsNames() {
		List<String> list = new ArrayList<>();
		for(Arena arena : arenas.keySet()) {
			list.add(arena.getNameWithoutColors());
		}
		return list;
	}
	
	public boolean containsSign(Sign sign) {
		if(signs.isEmpty())
			return false;
		for(Sign signNew : signs) {
			if(signNew.equals(sign))
				return true;
		}
		return false;
	}
	
	public void addAutoJoinSign(Sign sign) {
		sign.setLine(0, Lang.ARENA_AUTOJOIN_SIGN_1.getString());
		sign.setLine(1, Lang.ARENA_AUTOJOIN_SIGN_2.getString());
		sign.setLine(2, Lang.ARENA_AUTOJOIN_SIGN_3.getString());
		sign.setLine(3, Lang.ARENA_AUTOJOIN_SIGN_4.getString());
		sign.update();
		signs.add(sign);
	}
	
	public void removeSign(Sign sign) {
		signs.remove(sign);
	}
	
	public List<Sign> getSigns() {
		return signs;
	}
	
	public void setSigns(List<Sign> signs) {
		this.signs = signs;
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	public void setSpawn(Location newSpawn) {
		spawn = newSpawn;
	}
	
}
