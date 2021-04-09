package me.bertek41.wanted.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.arenamanager.ArenaPlayer;
import me.bertek41.wanted.gun.Gun;
import me.bertek41.wanted.gun.GunManager;
import me.bertek41.wanted.misc.HdHologram;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.misc.Settings;
import me.bertek41.wanted.misc.StatType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityPig;
import net.minecraft.server.v1_16_R3.EntityShulker;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumChatFormat;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutMount;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction;
import net.minecraft.server.v1_16_R3.WorldBorder;

public class Utils {
	private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("(<[a-zA-Z ]+:[^>]*>|<reset>)");
	private static final PotionEffect JUMP, SLOW;
	private static final WorldBorder WORLD_BORDER;
	private static List<UUID> packs = new ArrayList<>();
	
	static {
		JUMP = new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 250, false, false);
		SLOW = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 250, false, false);
		
		WORLD_BORDER = new WorldBorder();
		WORLD_BORDER.setWarningDistance(0);
		WORLD_BORDER.setWarningTime(0);
	}
	
	public static void setLines(Wanted instance, HdHologram hdHologram, StatType statType) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			List<String> lines = new ArrayList<>();
			ResultSet resultSet = instance.getDatabase().query("SELECT * FROM Stats ORDER BY " + statType.getName() + " DESC LIMIT 10;");
			try {
				int i = 1;
				while(resultSet.next()) {
					lines.add(Lang.HOLOGRAM_FORMAT.getString().replace("<rank>", "" + i++).replace("<stat>", statType.getName())
							.replace("<player>", Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("Uuid"))).getName()).replace("<number>", resultSet.getLong(statType.getName()) + ""));
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
			Bukkit.getScheduler().runTask(Wanted.getInstance(), () -> hdHologram.setLines(lines));
		});
	}
	
	public static List<Location> getHollowCube(Location corner1, Location corner2) {
		List<Location> result = new ArrayList<Location>();
		World world = corner1.getWorld();
		double minX = Math.min(corner1.getX(), corner2.getX());
		double minY = Math.min(corner1.getY(), corner2.getY());
		double minZ = Math.min(corner1.getZ(), corner2.getZ());
		double maxX = Math.max(corner1.getX(), corner2.getX()) + 1;
		double maxY = Math.max(corner1.getY(), corner2.getY()) + 1;
		double maxZ = Math.max(corner1.getZ(), corner2.getZ()) + 1;
		for(double x = minX; x <= maxX; x += 1) {
			for(double y = minY; y <= maxY; y += 1) {
				for(double z = minZ; z <= maxZ; z += 1) {
					int components = 0;
					if(x == minX || x == maxX)
						components++;
					if(y == minY || y == maxY)
						components++;
					if(z == minZ || z == maxZ)
						components++;
					if(components >= 2) {
						result.add(new Location(world, x, y, z));
					}
				}
			}
		}
		return result;
	}
	
	public static EntityShulker sendGlowingBlock(final Player player, final Location loc, Color color, int which) {
		player.setCollidable(false);
		final EntityArmorStand armorStand = new EntityArmorStand(EntityTypes.ARMOR_STAND, ((CraftPlayer) player).getHandle().world);
		armorStand.setPosition(loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5);
		armorStand.setFlag(5, true);
		armorStand.setNoGravity(true);
		armorStand.setInvulnerable(true);
		armorStand.setMarker(true);
		armorStand.setSilent(true);
		armorStand.collides = false;
		armorStand.setCustomName(CraftChatMessage.fromStringOrNull("§" + (which == -1 ? color.colorCode + "WANTED" : color.colorCode + which + ".")));
		armorStand.setCustomNameVisible(true);
		
		PacketPlayOutSpawnEntityLiving spawnEntityLiving = new PacketPlayOutSpawnEntityLiving(armorStand);
		ReflectionUtils.sendPacket(player, spawnEntityLiving);
		
		final EntityPig pig = new EntityPig(EntityTypes.PIG, ((CraftPlayer) player).getHandle().world);
		pig.setPosition(loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5);
		pig.setFlag(5, true);
		pig.setInvulnerable(true);
		pig.setSilent(true);
		pig.setNoAI(true);
		pig.collides = false;
		
		PacketPlayOutSpawnEntityLiving spawnEntityLiving2 = new PacketPlayOutSpawnEntityLiving(pig);
		ReflectionUtils.sendPacket(player, spawnEntityLiving2);
		
		final EntityShulker shulker = new EntityShulker(EntityTypes.SHULKER, ((CraftPlayer) player).getHandle().world);
		shulker.setPosition(loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5);
		shulker.setFlag(6, true);
		shulker.setFlag(5, true);
		shulker.setInvulnerable(true);
		shulker.setNoAI(true);
		shulker.setSilent(true);
		shulker.collides = false;
		
		PacketPlayOutSpawnEntityLiving spawnEntityLiving3 = new PacketPlayOutSpawnEntityLiving(shulker);
		ReflectionUtils.sendPacket(player, spawnEntityLiving3);
		
		armorStand.startRiding(pig);
		
		PacketPlayOutMount mountPacket = new PacketPlayOutMount(pig);
		ReflectionUtils.sendPacket(player, mountPacket);
		
		pig.startRiding(shulker);
		
		PacketPlayOutMount mountPacket2 = new PacketPlayOutMount(shulker);
		ReflectionUtils.sendPacket(player, mountPacket2);
		
		PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
		ReflectionUtils.sendPacket(player, metaPacket);
		PacketPlayOutEntityMetadata metaPacket2 = new PacketPlayOutEntityMetadata(pig.getId(), pig.getDataWatcher(), true);
		ReflectionUtils.sendPacket(player, metaPacket2);
		PacketPlayOutEntityMetadata metaPacket3 = new PacketPlayOutEntityMetadata(shulker.getId(), shulker.getDataWatcher(), true);
		ReflectionUtils.sendPacket(player, metaPacket3);
		
		sendTeamPacket(null, color, true, false, "always", "never", player);
		sendTeamPacket(shulker.getUniqueID().toString(), color, false, true, "always", "never", player);
		return shulker;
	}
	
	public static void killEntity(Player player, Entity entity) {
		entity.getAllPassengers().forEach(entity2 -> Utils.killEntity(player, entity2));
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getId());
		ReflectionUtils.sendPacket(player, packet);
	}
	
	public static void sendTeamPacket(String entity, Color color, boolean createNewTeam/* If true, we don't add any entities */, boolean addEntity/* true->add the entity, false->remove the entity */,
			String tagVisibility, String push, Player receiver) {
		try {
			PacketPlayOutScoreboardTeam packetScoreboardTeam = new PacketPlayOutScoreboardTeam();
			ReflectionUtils.getField(packetScoreboardTeam.getClass(), true, "i").set(packetScoreboardTeam, createNewTeam ? 0 : addEntity ? 3 : 4);
			ReflectionUtils.getField(packetScoreboardTeam.getClass(), true, "a").set(packetScoreboardTeam, color.getTeamName());
			ReflectionUtils.getField(packetScoreboardTeam.getClass(), true, "e").set(packetScoreboardTeam, tagVisibility);
			ReflectionUtils.getField(packetScoreboardTeam.getClass(), true, "f").set(packetScoreboardTeam, push);
			
			if(createNewTeam) {
				ReflectionUtils.getField(packetScoreboardTeam.getClass(), true, "g").set(packetScoreboardTeam, color.packetValue);
				
				ChatComponentText prefix = new ChatComponentText("§" + color.colorCode);
				ChatComponentText displayName = new ChatComponentText(color.getTeamName());
				ChatComponentText suffix = new ChatComponentText("");
				
				ReflectionUtils.getField(packetScoreboardTeam.getClass(), true, "c").set(packetScoreboardTeam, prefix);
				ReflectionUtils.getField(packetScoreboardTeam.getClass(), true, "b").set(packetScoreboardTeam, displayName);
				ReflectionUtils.getField(packetScoreboardTeam.getClass(), true, "d").set(packetScoreboardTeam, suffix);
				ReflectionUtils.getField(packetScoreboardTeam.getClass(), true, "j").set(packetScoreboardTeam, 0);
			}
			
			if(!createNewTeam) {
				@SuppressWarnings("unchecked")
				Collection<String> collection = ((Collection<String>) ReflectionUtils.getField(packetScoreboardTeam.getClass(), true, "h").get(packetScoreboardTeam));
				collection.add(entity);
			}
			
			ReflectionUtils.sendPacket(receiver, packetScoreboardTeam);
		} catch(ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void sendHub(Player player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(Settings.HUB_SERVER.toString());
		player.sendPluginMessage(Wanted.getInstance(), "BungeeCord", out.toByteArray());
	}
	
	public static void setPack(Player player, boolean join) {
		if(join) {
			if(Settings.USE_RESOURCE_PACK.getBoolean() && Settings.RESOURCE_PACK_REQUIRED.getBoolean())
				packs.add(player.getUniqueId());
			freeze(player);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if(Settings.RESOURCE_PACK_HASH.toString() != null && !Settings.RESOURCE_PACK_HASH.toString().isEmpty())
					((CraftPlayer) player).getHandle().setResourcePack(Settings.RESOURCE_PACK_LINK.toString(), Settings.RESOURCE_PACK_HASH.toString());
				else
					player.setResourcePack(Settings.RESOURCE_PACK_LINK.toString());
			}
		}.runTaskLater(Wanted.getInstance(), Settings.SET_RESOURCE_PACK_AFTER.getInt());
	}
	
	public static boolean loadedResourcePack(Player player) {
		return !packs.contains(player.getUniqueId());
	}
	
	public static List<UUID> getPacks() {
		return packs;
	}
	
	public static void setPacks(List<UUID> packs) {
		Utils.packs = packs;
	}
	
	public static void sendJson(Player player, String message) {
		player.spigot().sendMessage(convertListToArray(parseMessage(message).getComponents()));
	}
	
	private static TextComponent[] convertListToArray(List<TextComponent> list) {
		return list.toArray(new TextComponent[list.size()]);
	}
	
	private static TextComponentBuilder parseMessage(String message) {
		TextComponentBuilder compBuilder = new TextComponentBuilder();
		Matcher matcher = ATTRIBUTE_PATTERN.matcher(message);
		int lastIndex = 0;
		StringBuilder curStr = new StringBuilder();
		while(matcher.find()) {
			if(matcher.start() != 0) {
				curStr.append(message, lastIndex, matcher.start());
				TextComponent current = new TextComponent(TextComponent.fromLegacyText(curStr.toString()));
				compBuilder.add(current);
				curStr.delete(0, curStr.length());
			}
			lastIndex = matcher.end();
			if(matcher.group().equals("<reset>")) {
				compBuilder.setNextHoverEvent(null);
				compBuilder.setNextClickEvent(null);
			} else {
				Object event = parseEvent(matcher.group());
				if(event != null) {
					if(event instanceof HoverEvent)
						compBuilder.setNextHoverEvent((HoverEvent) event);
					else if(event instanceof ClickEvent)
						compBuilder.setNextClickEvent((ClickEvent) event);
				}
			}
		}
		if(lastIndex < message.length()) {
			curStr.append(message, lastIndex, message.length());
			TextComponent current = new TextComponent(TextComponent.fromLegacyText(curStr.toString()));
			compBuilder.add(current);
		}
		return compBuilder;
	}
	
	@SuppressWarnings("deprecation")
	private static Object parseEvent(String attribute) {
		String trimmed = attribute.replaceFirst("<", "").substring(0, attribute.length() - 2);
		String[] attributes = trimmed.split(":", 2);
		String value = attributes[1];
		if(attributes[0] == null)
			return null;
		switch(attributes[0]) {
		case "tooltip":
		case "show text":
			return new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(value));
		case "suggest command":
		case "suggest cmd":
		case "suggest":
			return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, value);
		case "run command":
		case "command":
		case "cmd":
			return new ClickEvent(ClickEvent.Action.RUN_COMMAND, value);
		case "open url":
		case "url":
		case "link":
			return new ClickEvent(ClickEvent.Action.OPEN_URL, value);
		}
		return null;
	}
	
	public static void arenasGUI(Player player) {
		Inventory arenasGUI = Bukkit.createInventory(null, 54, Lang.ARENAS_GUI_TITLE.getString());
		player.openInventory(arenasGUI);
		for(Arena arena : Wanted.getInstance().getArenaManager().getArenas()) {
			arenasGUI.addItem(makeItem(arena));
		}
	}
	
	public static Location getLocation(Player player) {
		Location location = player.getLocation();
		return new Location(player.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw(), location.getPitch());
	}
	
	public static Location getUnderBlockLocation(Location location) {
		return location.getBlock().getRelative(BlockFace.DOWN).getLocation();
	}
	
	public static void freeze(Player player) {
		player.setFlySpeed(0.0F);
		player.setWalkSpeed(0.0F);
		player.removePotionEffect(PotionEffectType.JUMP);
		player.removePotionEffect(PotionEffectType.SLOW);
		player.addPotionEffect(JUMP);
		player.addPotionEffect(SLOW);
		player.setFoodLevel(6);
	}
	
	public static void unfreeze(Player player) {
		player.setFlySpeed(0.1F);
		player.setWalkSpeed(0.2F);
		player.removePotionEffect(PotionEffectType.JUMP);
		player.removePotionEffect(PotionEffectType.SLOW);
		player.setFoodLevel(20);
	}
	
	public static void setArenaItems(Player player) {
		clearItems(player);
		player.getInventory().setItem(8,
				Utils.makeItem(Material.valueOf(Settings.ITEMS_RETURN_LOBBY_ITEM.toString()), Settings.ITEMS_RETURN_LOBBY_NAME.getString(), null, Settings.ITEMS_RETURN_LOBBY_LORE.getStringList()));
	}
	
	public static void setLobbyItems(Player player, boolean online, boolean clear) {
		if(clear)
			clearItems(player);
		if(online)
			player.getInventory().setItem(0,
					makeItem(Material.valueOf(Settings.ITEMS_JOIN_ARENA_ITEM.toString()), Settings.ITEMS_JOIN_ARENA_NAME.getString(), null, Settings.ITEMS_JOIN_ARENA_LORE.getStringList()));
	}
	
	public static void clearItems(Player player) {
		player.getActivePotionEffects().clear();
		player.getInventory().clear();
		player.getEquipment().clear();
	}
	
	public static void setGlow(Player player, int time) {
		player.setGlowing(true);
		new BukkitRunnable() {
			@Override
			public void run() {
				player.setGlowing(false);
			}
		}.runTaskLater(Wanted.getInstance(), time);
	}
	
	public static String convertUnicode(String text) {
		return text.replace("<coinsymbol>", Character.toString((char) 0x26C3)).replace("<heartsymbol>", Character.toString((char) 0x2764)).replace("<headsymbol>", "☹").replace("<bulletsymbol>",
				Character.toString((char) 0x204D));
	}
	
	public static List<String> convertUnicode(List<String> list) {
		List<String> newList = new ArrayList<>();
		for(String string : list) {
			newList.add(string.replace("<coinsymbol>", Character.toString((char) 0x26C3)).replace("<heartsymbol>", Character.toString((char) 0x2764)).replace("<headsymbol>", "☹")
					.replace("<bulletsymbol>", Character.toString((char) 0x204D)));
		}
		return newList;
	}
	
	public static ItemStack makeItem(Gun gun, boolean own) {
		ItemStack item = gun.getItem().clone();
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>(gun.getLore());
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', gun.getName()));
		List<String> guiLore = new ArrayList<>();
		guiLore = convertUnicode(own ? Lang.GUNS_GUI_GUN_LORE.getStringList() : Lang.GUNS_GUI_GUN_LORE_COIN.getStringList());
		guiLore.forEach(l -> lore.add(l.replace("<coin>", gun.getCoin() + "")));
		meta.setLocalizedName(gun.getName());
		meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).collect(Collectors.toList()));
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack makeItem(Material material, String name, String localName, List<String> lore) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		if(name != null)
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		if(localName != null)
			meta.setLocalizedName(ChatColor.translateAlternateColorCodes('&', localName));
		if(lore != null && !lore.isEmpty())
			meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).collect(Collectors.toList()));
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack makeItem(Arena arena) {
		ItemStack oldItem = arena.getItem();
		String name = arena.getName();
		ItemStack item = new ItemStack(oldItem.getType() != Material.AIR ? oldItem : new ItemStack(Material.EMERALD_BLOCK));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLocalizedName(name);
		meta.setLore(Lang.ARENAS_GUI_ARENA_LORE.getStringList().stream().map(l -> l.replace("<online>", arena.getPlayers().size() + "").replace("<max>", arena.getMaximumPlayers() + ""))
				.collect(Collectors.toList()));
		item.setItemMeta(meta);
		return item;
	}
	
	public static void gunGUI(ArenaPlayer arenaPlayer) {
		Inventory gunGUI = Bukkit.createInventory(null, 54, Lang.GUNS_GUI_TITLE.getString());
		for(Gun gun : new ArrayList<>(GunManager.getGuns())) {
			gunGUI.addItem(makeItem(gun, arenaPlayer.getGuns().contains(gun.getId())));
		}
		arenaPlayer.getOfflinePlayer().getPlayer().openInventory(gunGUI);
	}
	
	public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
		if(title != null)
			player.sendTitle(title, null, fadeIn, stay, fadeOut);
		if(subtitle != null)
			player.sendTitle(null, subtitle, fadeIn, stay, fadeOut);
	}
	
	public static void sendActionBar(Player player, String message) {
		if(message == null)
			return;
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
	}
	
	public static void sendWorldBorder(Arena arena, Player player) {
		WORLD_BORDER.world = ((CraftWorld) arena.getPos1().getWorld()).getHandle();
		WORLD_BORDER.setCenter(arena.getCenter().getBlockX() + 0.5, arena.getCenter().getBlockZ() + 0.5);
		double size = arena.getBox().getWidthX() > arena.getBox().getWidthZ() ? arena.getBox().getWidthX() : arena.getBox().getWidthZ();
		WORLD_BORDER.setSize(size);
		if(Settings.BORDER_COLOR.toString().equalsIgnoreCase("RED"))
			WORLD_BORDER.transitionSizeBetween(size, size - 0.1D, 20000000L);
		else if(Settings.BORDER_COLOR.toString().equalsIgnoreCase("GREEN"))
			WORLD_BORDER.transitionSizeBetween(size - 0.1D, size, 20000000L);
		ReflectionUtils.sendPacket(player, new PacketPlayOutWorldBorder(WORLD_BORDER, EnumWorldBorderAction.INITIALIZE));
	}
	
	public static void resetWorldBorder(Arena arena, Player player) {
		ReflectionUtils.sendPacket(player, new PacketPlayOutWorldBorder(((CraftWorld) arena.getPos1().getWorld()).getHandle().getWorldBorder(), EnumWorldBorderAction.INITIALIZE));
	}
	
	public enum Color {
		BLACK(0, "0"),
		DARK_BLUE(1, "1"),
		DARK_GREEN(2, "2"),
		DARK_AQUA(3, "3"),
		DARK_RED(4, "4"),
		DARK_PURPLE(5, "5"),
		GOLD(6, "6"),
		GRAY(7, "7"),
		DARK_GRAY(8, "8"),
		BLUE(9, "9"),
		GREEN(10, "a"),
		AQUA(11, "b"),
		RED(12, "c"),
		PURPLE(13, "d"),
		YELLOW(14, "e"),
		WHITE(15, "f"),
		NONE(-1, "");
		
		Object packetValue;
		String colorCode;
		
		Color(int packetValue, String colorCode) {
			this.packetValue = EnumChatFormat.a(packetValue);
			this.colorCode = colorCode;
		}
		
		String getTeamName() {
			String name = String.format("WANTED#%s", name());
			if(name.length() > 16) {
				name = name.substring(0, 16);
			}
			return name;
		}
	}
	
}
