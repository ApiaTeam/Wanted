package me.bertek41.wanted.titles;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.bertek41.wanted.Wanted;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Titles extends JavaPlugin implements Listener {
	
	public static void sendTitle(Player player, int fadeIn, int stay, int fadeOut, String title, String subtitle) {
		player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
	}
	
	public static void clearTitle(Player player) {
		sendTitle(player, 0, 0, 0, "", "");
	}
	
	public static void sendActionBar(Player player, String message) {
		if(!player.isOnline() || message == null)
			return;
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
	}
	
	public static void sendActionBar(Player player, final String message, int duration) {
		if(!player.isOnline() || message == null)
			return;
		sendActionBar(player, message);
		if(duration >= 0) {
			new BukkitRunnable() {
				@Override
				public void run() {
					sendActionBar(player, "");
				}
			}.runTaskLater(Wanted.getInstance(), duration + 1);
		}
		while(duration > 60) {
			duration -= 60;
			int sched = duration % 60;
			new BukkitRunnable() {
				@Override
				public void run() {
					sendActionBar(player, message);
				}
			}.runTaskLater(Wanted.getInstance(), sched);
		}
	}
	
	public static void sendActionBarToAllPlayers(String message) {
		sendActionBarToAllPlayers(message, 3);
	}
	
	public static void sendActionBarToAllPlayers(String message, int duration) {
		for(Player player : Bukkit.getOnlinePlayers())
			sendActionBar(player, message, duration);
	}
}
