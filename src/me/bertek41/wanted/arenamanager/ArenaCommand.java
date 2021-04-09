package me.bertek41.wanted.arenamanager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.misc.Settings;
import me.bertek41.wanted.utils.Utils;
import me.bertek41.wanted.utils.Utils.Color;
import net.minecraft.server.v1_16_R3.EntityShulker;

@CommandAlias("arena|a")
public class ArenaCommand extends BaseCommand {
	@Dependency
	private Wanted instance;
	private HashMap<String, Integer> taskIds;
	private HashMap<String, LinkedHashMap<Integer, EntityShulker>> glows;
	private HashMap<String, EntityShulker> wanted;
	private HashMap<Integer, Color> tree;
	
	public void init() {
		taskIds = new HashMap<>();
		glows = new HashMap<>();
		wanted = new HashMap<>();
		tree = new HashMap<>();
		tree.put(0, Color.GREEN);
		tree.put(1, Color.YELLOW);
		tree.put(2, Color.PURPLE);
		tree.put(3, Color.AQUA);
		tree.put(4, Color.GOLD);
		tree.put(5, Color.RED);
		tree.put(6, Color.WHITE);
		tree.put(7, Color.DARK_PURPLE);
		tree.put(8, Color.GRAY);
		tree.put(9, Color.DARK_AQUA);
		tree.put(10, Color.DARK_GREEN);
		tree.put(11, Color.DARK_RED);
		tree.put(12, Color.DARK_BLUE);
		tree.put(13, Color.DARK_GRAY);
		tree.put(14, Color.BLACK);
		Color wantedColor = Color.valueOf(Settings.GLOW_COLOR.getString());
		boolean found = false;
		for(int i : new HashSet<Integer>(tree.keySet())) {
			if(tree.get(i) == wantedColor) {
				tree.remove(i);
				found = true;
				continue;
			}
			if(found) {
				tree.put(i - 1, tree.remove(i));
			}
		}
	}
	
	@Subcommand("help")
	@CommandPermission("arenacommand.help")
	@CatchUnknown
	@Default
	public void onHelp(Player player) {
		Lang.ARENACOMMAND_HELP.getStringList().forEach(message -> Utils.sendJson(player, message));
	}
	
	@Subcommand("setspawn")
	@CommandPermission("arenacommand.setspawn")
	public void onSetSpawn(Player player) {
		instance.getArenaManager().setSpawn(Utils.getLocation(player));
		Lang.sendMessage(player, Lang.ARENACOMMAND_LOCATION_SPAWN_SET.getString());
		for(Arena arenaNew : instance.getArenaManager().getArenas())
			checkArena(player, arenaNew);
	}
	
	@Subcommand("create")
	@CommandPermission("arenacommand.create")
	@Syntax("<arena>")
	public void onCreate(Player player, String arg) {
		if(instance.getArenaManager().containsArena(arg)) {
			Lang.sendMessage(player, Lang.ARENACOMMAND_ALREADY_CREATED.getString());
			return;
		}
		Arena arena = new Arena(instance.getArenaManager().getNewId(), arg);
		instance.getArenaManager().addArena(arena, false);
		Lang.sendMessage(player, Lang.ARENACOMMAND_CREATED.getString());
		Utils.sendJson(player, arena.steps());
	}
	
	@Subcommand("delete")
	@CommandPermission("arenacommand.delete")
	@Syntax("<arena>")
	@CommandCompletion("@arenas")
	public void onDelete(CommandSender sender, Arena arena) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			player.setCollidable(true);
			if(glows.containsKey(arena.getName())) {
				glows.get(arena.getName()).values().forEach(shulker -> Utils.killEntity(player, shulker));
				glows.remove(arena.getName());
			}
			if(wanted.containsKey(arena.getName())) {
				Utils.killEntity(player, wanted.get(arena.getName()));
				wanted.remove(arena.getName());
			}
		}
		arena.getPos1().getBlock().getState().update();
		arena.getPos2().getBlock().getState().update();
		instance.getArenaManager().removeArena(arena);
		Lang.sendMessage(sender, Lang.ARENACOMMAND_DELETED.getString());
	}
	
	@Subcommand("setlobby")
	@CommandPermission("arenacommand.setlobby")
	@Syntax("<arena>")
	@CommandCompletion("@arenas")
	public void onSetLobby(Player player, Arena arena) {
		arena.setLobby(Utils.getLocation(player));
		Lang.sendMessage(player, Lang.ARENACOMMAND_LOCATION_LOBBY_SET.getString());
		checkArena(player, arena);
	}
	
	@Subcommand("setpos")
	@CommandPermission("arenacommand.setpos")
	@Syntax("<arena> <1/2>")
	@CommandCompletion("@arenas @range:1-2")
	public void onSetPos(Player player, Arena arena, int pos) {
		if(pos != 1 && pos != 2) {
			Lang.sendMessage(player, Lang.ARENACOMMAND_SETPOS.getString());
			return;
		}
		Location location = Utils.getLocation(player);
		if(pos == 1 && arena.getPos1() != null)
			arena.getPos1().getBlock().getState().update();
		if(pos == 2 && arena.getPos2() != null)
			arena.getPos2().getBlock().getState().update();
		if(pos == 1)
			arena.setPos1(location);
		else
			arena.setPos2(location);
		Lang.sendMessage(player, Lang.ARENACOMMAND_SETPOS_SET.getString().replace("<pos>", pos + ""));
		checkArena(player, arena);
		if(!arena.isCompleted()) {
			Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> player.sendBlockChange(location, Material.GOLD_BLOCK.createBlockData()), 5l);
			if(taskIds.containsKey(arena.getName()))
				Bukkit.getScheduler().cancelTask(taskIds.get(arena.getName()));
			if(arena.getPos1() != null && arena.getPos2() != null)
				taskIds.put(arena.getName(), new BukkitRunnable() {
					private List<Location> points = Utils.getHollowCube(arena.getBox().getMin().toLocation(arena.getPos1().getWorld()), arena.getBox().getMax().toLocation(arena.getPos1().getWorld()));
					
					@Override
					public void run() {
						if(!instance.getArenaManager().containsArena(arena.getName()) || arena.isCompleted() || !player.isOnline()) {
							cancel();
							return;
						}
						for(Location location : points)
							player.spawnParticle(Particle.VILLAGER_HAPPY, location.getX(), location.getY(), location.getZ(), 1, 0, 0, 0);
					}
				}.runTaskTimer(instance, 0, 20).getTaskId());
		}
	}
	
	@Subcommand("setspectate")
	@CommandPermission("arenacommand.setspectate")
	@Syntax("<arena>")
	@CommandCompletion("@arenas")
	public void onSetSpectate(Player player, Arena arena) {
		arena.setSpectate(Utils.getLocation(player));
		Lang.sendMessage(player, Lang.ARENACOMMAND_LOCATION_SPECTATE_SET.getString());
		checkArena(player, arena);
	}
	
	@Subcommand("setminimum")
	@CommandPermission("arenacommand.setminimum")
	@Syntax("<arena> <number>")
	@CommandCompletion("@arenas")
	public void onSetMinimum(Player player, Arena arena, int minimum) {
		if(minimum < 2) {
			Lang.sendMessage(player, Lang.ARENACOMMAND_SETMINIMUM_HIGHER_THAN_1.getString());
			return;
		}
		if(arena.getMaximumPlayers() != 0 && minimum > arena.getMaximumPlayers()) {
			Lang.sendMessage(player, Lang.ARENACOMMAND_SETMINIMUM_HIGHER_THAN_MAXIMUM.getString());
			return;
		}
		arena.setRequiredPlayers(minimum);
		Lang.sendMessage(player, Lang.ARENACOMMAND_SETMINIMUM_SET.getString());
		checkArena(player, arena);
	}
	
	@Subcommand("setmaximum")
	@CommandPermission("arenacommand.setmaximum")
	@Syntax("<arena> <number>")
	@CommandCompletion("@arenas")
	public void onSetMaximum(Player player, Arena arena, int maximum) {
		if(maximum < 2) {
			Lang.sendMessage(player, Lang.ARENACOMMAND_SETMAXIMUM_HIGHER_THAN_1.getString());
			return;
		}
		if(arena.getRequiredPlayers() != 0 && maximum < arena.getRequiredPlayers()) {
			Lang.sendMessage(player, Lang.ARENACOMMAND_SETMAXIMUM_LOWER_THAN_MINIMUM.getString());
			return;
		}
		arena.setMaximumPlayers(maximum);
		Lang.sendMessage(player, Lang.ARENACOMMAND_SETMAXIMUM_SET.getString());
		checkArena(player, arena);
	}
	
	@Subcommand("setmaximumreward")
	@CommandPermission("arenacommand.setmaximumreward")
	@Syntax("<arena> <number>")
	@CommandCompletion("@arenas")
	public void onSetMaximumReward(Player player, Arena arena, int maximumReward) {
		if(maximumReward < 1) {
			Lang.sendMessage(player, Lang.ARENACOMMAND_SETMAXIMUMREWARD_HIGHER_THAN_0.getString());
			return;
		}
		arena.setMaximumReward(maximumReward);
		Lang.sendMessage(player, Lang.ARENACOMMAND_SETMAXIMUMREWARD_SET.getString());
		checkArena(player, arena);
	}
	
	@Subcommand("setitem")
	@CommandPermission("arenacommand.setitem")
	@Syntax("<arena>")
	@CommandCompletion("@arenas")
	public void onSetItem(Player player, Arena arena) {
		if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
			Lang.sendMessage(player, Lang.ARENACOMMAND_SETITEM_NEED_ITEM.getString());
			return;
		}
		arena.setItem(player.getInventory().getItemInMainHand());
		Lang.sendMessage(player, Lang.ARENACOMMAND_SETITEM_SET.getString());
	}
	
	@Subcommand("rename")
	@CommandPermission("arenacommand.rename")
	@Syntax("<arena> <name>")
	@CommandCompletion("@arenas")
	public void onRename(Player player, Arena arena, String arg) {
		Integer taskId = taskIds.remove(arena.getName());
		LinkedHashMap<Integer, EntityShulker> glow = glows.remove(arena.getName());
		EntityShulker wantedGlow = wanted.remove(arena.getName());
		arena.setName(arg);
		if(taskId != null)
			taskIds.put(arena.getName(), taskId);
		if(glow != null)
			glows.put(arena.getName(), glow);
		if(wantedGlow != null)
			wanted.put(arena.getName(), wantedGlow);
		Lang.sendMessage(player, Lang.ARENACOMMAND_RENAME_SET.getString().replace("<name>", arg));
	}
	
	@Subcommand("setlocation")
	@CommandPermission("arenacommand.setlocation")
	@Syntax("<arena> <player/wanted> [<index number for override old location>]")
	@CommandCompletion("@arenas @locations")
	public void onSetLocation(Player player, Arena arena, String arg, @Optional Integer index) {
		if(arena.getPos1() == null || arena.getPos2() == null || arena.getRequiredPlayers() == 0 || arena.getMaximumPlayers() == 0) {
			Lang.sendMessage(player, Lang.ARENACOMMAND_SETLOCATION_CAN_NOT_SET_NOW.getString());
			return;
		}
		if(!arg.equalsIgnoreCase("player") && !arg.equalsIgnoreCase("wanted")) {
			Lang.sendMessage(player, Lang.ARENACOMMAND_SETLOCATION.getString());
			return;
		}
		Location location = Utils.getLocation(player);
		if(!arena.getBox().contains(location.toVector())) {
			Lang.sendMessage(player, Lang.ARENACOMMAND_SETLOCATION_MUST_BETWEEN_POS1_AND_POS2.getString());
			return;
		}
		if(arg.equalsIgnoreCase("wanted")) {
			if(arena.getWantedLocation() != null && wanted.containsKey(arena.getName())) {
				Utils.killEntity(player, wanted.get(arena.getName()));
			}
			arena.setWantedLocation(location);
			Lang.sendMessage(player, Lang.ARENACOMMAND_LOCATION_WANTED_SET.getString());
			checkArena(player, arena);
			if(!arena.isCompleted())
				wanted.put(arena.getName(), Utils.sendGlowingBlock(player, Utils.getUnderBlockLocation(location), Color.valueOf(Settings.GLOW_COLOR.getString()), -1));
			return;
		}
		if(index != null) {
			index--;
			if(index < 0)
				index = 0;
		}
		if(index == null && arena.getLocations().size() == arena.getMaximumPlayers() - 1) {
			Lang.sendMessage(player, Lang.ARENACOMMAND_LOCATION_PLAYER_MAXIMUM.getString());
			return;
		}
		if(index == null)
			arena.addLocation(location);
		else if(index >= arena.getLocations().size()) {
			Lang.sendMessage(player, Lang.ARENACOMMAND_LOCATION_INDEX_CAN_NOT_BIGGER_THAN_LOCATIONS_SIZE.getString().replace("<size>", arena.getLocations().size() + ""));
			return;
		} else {
			arena.setLocation(location, index);
			if(glows.containsKey(arena.getName()) && glows.get(arena.getName()).containsKey(index))
				Utils.killEntity(player, glows.get(arena.getName()).get(index));
		}
		Lang.sendMessage(player, Lang.ARENACOMMAND_LOCATION_PLAYER_SET.getString());
		checkArena(player, arena);
		if(!arena.isCompleted())
			glows.put(arena.getName(),
					getList(arena, Utils.sendGlowingBlock(player, Utils.getUnderBlockLocation(location), tree.get(((index != null ? index : (arena.getLocations().size()) - 1) % 15)),
							(index != null ? index + 1 : arena.getLocations().size())), (index != null ? index : arena.getLocations().size() - 1)));
	}
	
	@Subcommand("steps")
	@CommandPermission("arenacommand.steps")
	@Syntax("<arena>")
	@CommandCompletion("@arenas")
	public void onSteps(Player player, Arena arena) {
		checkArena(player, arena);
	}
	
	private void checkArena(Player player, Arena arena) {
		if(arena.isCompleted()) {
			player.setCollidable(true);
			arena.getPos1().getBlock().getState().update();
			arena.getPos2().getBlock().getState().update();
			if(glows.containsKey(arena.getName())) {
				glows.get(arena.getName()).values().forEach(shulker -> Utils.killEntity(player, shulker));
				glows.remove(arena.getName());
			}
			if(wanted.containsKey(arena.getName())) {
				Utils.killEntity(player, wanted.get(arena.getName()));
				wanted.remove(arena.getName());
			}
			arena.updateSign();
			instance.getArenaManager().addArena(arena, true);
			Lang.sendMessage(player, Lang.ARENACOMMAND_COMPLETED.getString().replace("<arena>", arena.getName()));
		} else
			Utils.sendJson(player, arena.steps());
	}
	
	public LinkedHashMap<Integer, EntityShulker> getList(Arena arena, EntityShulker shulker, int index) {
		LinkedHashMap<Integer, EntityShulker> list = glows.getOrDefault(arena.getName(), new LinkedHashMap<>());
		list.put(index, shulker);
		return list;
	}
	
}
