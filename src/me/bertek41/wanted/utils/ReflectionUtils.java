package me.bertek41.wanted.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.misc.StatType;

public class ReflectionUtils {
	
	public static StatType getEnum(String arg) {
		for(StatType stat : StatType.values()) {
			if(stat.getName().equalsIgnoreCase(arg))
				return stat;
		}
		return null;
	}
	
	public static List<String> getEnumAsList() {
		List<String> list = new ArrayList<>();
		for(StatType stat : StatType.values()) {
			list.add(stat.getName());
		}
		return list;
	}
	
	public static Object getSound(Object sound) {
		if(sound == null)
			return null;
		if(sound instanceof Sound)
			return sound;
		for(Sound s : Sound.values()) {
			if(s.name().equalsIgnoreCase((String) sound))
				return s;
		}
		return sound;
	}
	
	public static void sendPacket(Player player, Object packet) {
		try {
			Object getHandle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
			Object playerConnection = getHandle.getClass().getField("playerConnection").get(getHandle);
			playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Object getEntityPlayer(Player player) {
		try {
			Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + Wanted.getInstance().getNMSVersion() + ".entity.CraftPlayer");
			Method getHandle = craftPlayer.getMethod("getHandle");
			return getHandle.invoke(player);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Class<?> getNMSClass(String name) {
		try {
			return Class.forName("net.minecraft.server." + Wanted.getInstance().getNMSVersion() + "." + name);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Constructor<?> getConstructor(Class<?> clazz, int parameterCount) {
		Constructor<?>[] arrayOfConstructor;
		int j = (arrayOfConstructor = clazz.getDeclaredConstructors()).length;
		for(int i = 0; i < j; i++) {
			Constructor<?> c = arrayOfConstructor[i];
			if(c.getParameterCount() == parameterCount) {
				return c;
			}
		}
		return null;
	}
	
	public static Field getField(Class<?> clazz, boolean declared, String fieldName) throws NoSuchFieldException, SecurityException {
		Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
		field.setAccessible(true);
		return field;
	}
	
}
