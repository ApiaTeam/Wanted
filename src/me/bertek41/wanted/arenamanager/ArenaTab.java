package me.bertek41.wanted.arenamanager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.utils.ReflectionUtils;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.IScoreboardCriteria.EnumScoreboardHealthDisplay;
import net.minecraft.server.v1_16_R3.ScoreboardServer;

public class ArenaTab {
	private static Class<?> PacketPlayOutScoreboardObjective, PacketPlayOutScoreboardDisplayObjective, PacketPlayOutScoreboardScore;
	private static Constructor<?> newPacketPlayOutScoreboardObjective, newPacketPlayOutScoreboardDisplayObjective, newPacketPlayOutScoreboardScore_4, titleConstructor;
	private static Field PacketPlayOutScoreboardObjective_OBJECTIVENAME, PacketPlayOutScoreboardObjective_TITLE, PacketPlayOutScoreboardObjective_DISPLAYTYPE, PacketPlayOutScoreboardObjective_ACTION,
			PacketPlayOutScoreboardDisplayObjective_POSITION, PacketPlayOutScoreboardDisplayObjective_OBJECTIVENAME;
	private static Object tabHeader;
	static {
		try {
			tabHeader = Class.forName("net.minecraft.server." + Wanted.getInstance().getNMSVersion() + ".IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class });
			PacketPlayOutScoreboardObjective = ReflectionUtils.getNMSClass("PacketPlayOutScoreboardObjective");
			newPacketPlayOutScoreboardObjective = PacketPlayOutScoreboardObjective.getConstructor(new Class[0]);
			(PacketPlayOutScoreboardObjective_OBJECTIVENAME = PacketPlayOutScoreboardObjective.getDeclaredField("a")).setAccessible(true);
			(PacketPlayOutScoreboardObjective_TITLE = PacketPlayOutScoreboardObjective.getDeclaredField("b")).setAccessible(true);
			(PacketPlayOutScoreboardObjective_DISPLAYTYPE = PacketPlayOutScoreboardObjective.getDeclaredField("c")).setAccessible(true);
			(PacketPlayOutScoreboardObjective_ACTION = PacketPlayOutScoreboardObjective.getDeclaredField("d")).setAccessible(true);
			PacketPlayOutScoreboardDisplayObjective = ReflectionUtils.getNMSClass("PacketPlayOutScoreboardDisplayObjective");
			newPacketPlayOutScoreboardDisplayObjective = PacketPlayOutScoreboardDisplayObjective.getConstructor(new Class[0]);
			(PacketPlayOutScoreboardDisplayObjective_POSITION = PacketPlayOutScoreboardDisplayObjective.getDeclaredField("a")).setAccessible(true);
			(PacketPlayOutScoreboardDisplayObjective_OBJECTIVENAME = PacketPlayOutScoreboardDisplayObjective.getDeclaredField("b")).setAccessible(true);
			PacketPlayOutScoreboardScore = ReflectionUtils.getNMSClass("PacketPlayOutScoreboardScore");
			newPacketPlayOutScoreboardScore_4 = ReflectionUtils.getConstructor(PacketPlayOutScoreboardScore, 4);
			titleConstructor = Class.forName("net.minecraft.server." + Wanted.getInstance().getNMSVersion() + ".PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[0]);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setTablist(Player player) {
		try {
			Object header = ((Method) tabHeader).invoke(null, new Object[] { "{\"text\":\"" + String.join("\n", Lang.ARENA_TAB_TITLE.getStringList()) + "\"}" });
			Object footer = ((Method) tabHeader).invoke(null, new Object[] { "{\"text\":\"" + String.join("\n", Lang.ARENA_TAB_SUBTITLE.getStringList()) + "\"}" });
			Object packet = titleConstructor.newInstance(new Object[0]);
			Field a = packet.getClass().getDeclaredField("header");
			a.setAccessible(true);
			a.set(packet, header);
			Field b = packet.getClass().getDeclaredField("footer");
			b.setAccessible(true);
			b.set(packet, footer);
			ReflectionUtils.sendPacket(player, packet);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setObjective(Player player, int action) {
		try {
			String title = "ms";
			Object packet = newPacketPlayOutScoreboardObjective.newInstance(new Object[0]);
			PacketPlayOutScoreboardObjective_OBJECTIVENAME.set(packet, "TabObjective");
			
			PacketPlayOutScoreboardObjective_TITLE.set(packet, ChatSerializer.a("{\"text\":\"" + title + "\"}"));
			PacketPlayOutScoreboardObjective_DISPLAYTYPE.set(packet, EnumScoreboardHealthDisplay.INTEGER);
			PacketPlayOutScoreboardObjective_ACTION.set(packet, action);
			ReflectionUtils.sendPacket(player, packet);
			
			if(action != 0)
				return;
			
			Object packet1 = newPacketPlayOutScoreboardDisplayObjective.newInstance(new Object[0]);
			PacketPlayOutScoreboardDisplayObjective_POSITION.set(packet1, 0);
			PacketPlayOutScoreboardDisplayObjective_OBJECTIVENAME.set(packet1, "TabObjective");
			ReflectionUtils.sendPacket(player, packet1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateCoin(Arena arena) {
		for(Player player : arena.getOnlinePlayers()) {
			for(Player all : arena.getOnlinePlayers()) {
				updateCoin(player, all.getName(), arena.getArenaPlayer(all).getCoin());
			}
		}
	}
	
	private static void updateCoin(Player player, String all, int coin) {
		try {
			Object packet2 = newPacketPlayOutScoreboardScore_4.newInstance(new Object[] { ScoreboardServer.Action.CHANGE, "TabObjective", all, coin });
			ReflectionUtils.sendPacket(player, packet2);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
