package me.bertek41.wanted.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.bertek41.wanted.Wanted;

public class BungeeCordManager /* implements PluginMessageListener */ {
	
	public BungeeCordManager() {
		if(/* is bungee mode && */!Bukkit.getServer().getMessenger().getIncomingChannels().contains("BungeeCord")) {
			Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Wanted.getInstance(), "BungeeCord");
		}
	}
	
	/*
	 * @Override
	 * public void onPluginMessageReceived(String channel, Player player, byte[] message) {
	 * if(!channel.equals("BungeeCord") || !is bungee mode) return;
	 * }
	 */
	
	// public void updateBungeeSigns() {}
	
	public static void sendHub(Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ConnectOther");
		out.writeUTF(player.getName());
		out.writeUTF("Hub");
		player.sendPluginMessage(Wanted.getInstance(), "BungeeCord", out.toByteArray());
	}
	
}
