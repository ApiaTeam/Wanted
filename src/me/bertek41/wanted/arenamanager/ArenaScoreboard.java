package me.bertek41.wanted.arenamanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import me.bertek41.wanted.misc.Lang;
import me.clip.placeholderapi.PlaceholderAPI;

public class ArenaScoreboard {
	
	public static void update(Arena arena, Player player, String time, boolean inLobby) {
		final Map<String, Integer> lines = new HashMap<String, Integer>();
		final List<String> list = inLobby ? Lang.SCOREBOARD_LOBBY.getStringList() : Lang.SCOREBOARD_GAME.getStringList();
		int size = list.size();
		for(final String line : list) {
			final String placeholders = setPlaceholders(arena, player, time, line);
			lines.put(placeholders, size--);
		}
		displayScoreboard(player, setPlaceholders(arena, player, time, inLobby ? Lang.SCOREBOARD_LOBBY_TITLE.getString() : Lang.SCOREBOARD_GAME_TITLE.getString()), lines);
	}
	
	private static String setPlaceholders(Arena arena, Player player, String time, String text) {
		return PlaceholderAPI.setPlaceholders(player, text).replace("<player>", player.getName()).replace("<time>", time).replace("<currentplayers>", arena.getPlayers().size() + "")
				.replace("<maximumplayers>", arena.getMaximumPlayers() + "").replace("<wanted>", arena.getWanted() == null ? "" : arena.getWanted().getName())
				.replace("<coin>", arena.getCoin(player) + "").replace("<kills>", arena.getKill(player) + "").replace("<deaths>", arena.getDeath(player) + "")
				.replace("<coinsymbol>", Character.toString((char) 0x26C3)).replace("<heartsymbol>", Character.toString((char) 0x2764)).replace("<headsymbol>", "☹")
				.replace("<bulletsymbol>", Character.toString((char) 0x204D));
	}
	
	private static void displayScoreboard(final Player player, String title, final Map<String, Integer> elements) {
		if(title.length() > 32) {
			title = title.substring(0, 32);
		}
		while(elements.size() > 15) {
			String minimumKey = (String) elements.keySet().toArray()[0];
			int minimum = elements.get(minimumKey);
			for(final String string : elements.keySet()) {
				if(elements.get(string) < minimum || (elements.get(string) == minimum && string.compareTo(minimumKey) < 0)) {
					minimumKey = string;
					minimum = elements.get(string);
				}
			}
			elements.remove(minimumKey);
		}
		for(final String string2 : new ArrayList<String>(elements.keySet())) {
			if(string2 != null && string2.length() > 40) {
				final int value = elements.get(string2);
				elements.remove(string2);
				elements.put(string2.substring(0, 40), value);
			}
		}
		if(player.getScoreboard().getObjective(player.getUniqueId().toString().substring(0, 16)) == null) {
			player.getScoreboard().registerNewObjective(player.getUniqueId().toString().substring(0, 16), "dummy", title);
			player.getScoreboard().getObjective(player.getUniqueId().toString().substring(0, 16)).setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		Scoreboard board = player.getScoreboard();
		for(final String string2 : elements.keySet()) {
			if(board.getObjective(DisplaySlot.SIDEBAR).getScore(string2).getScore() != elements.get(string2)) {
				board.getObjective(DisplaySlot.SIDEBAR).getScore(string2).setScore(elements.get(string2));
			}
		}
		for(final String string2 : new ArrayList<String>(player.getScoreboard().getEntries())) {
			if(!elements.keySet().contains(string2)) {
				player.getScoreboard().resetScores(string2);
			}
		}
		ArenaTab.setObjective(player, 0);
	}
	
	public static void remove(Player player) {
		ArenaTab.setObjective(player, 1);
		player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
	}
	
}
