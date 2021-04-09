package me.bertek41.wanted.arenamanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.api.events.ArenaJoinEvent;
import me.bertek41.wanted.api.events.ArenaQuitEvent;
import me.bertek41.wanted.api.events.ArenaStateChangeEvent;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.misc.Settings;
import me.bertek41.wanted.misc.StatType;
import me.bertek41.wanted.utils.Utils;

public class Arena {
	private int id, requiredPlayers, maximumPlayers, maximumReward;
	private String name = "";
	private Location pos1, pos2, wantedLocation, lobby, spectate;
	private BoundingBox box;
	private ItemStack item;
	private List<ArenaPlayer> players;
	private Map<OfflinePlayer, Long> kills, deaths;
	private ArenaState state;
	private ArenaCountdown arenaCountdown;
	private ArenaGame arenaGame;
	private LinkedList<Location> locations;
	private Player wanted;
	private List<Sign> signs;
	
	public Arena(String name) {
		init();
		this.name = ChatColor.translateAlternateColorCodes('&', "&f" + name);
	}
	
	public Arena(int id, String name) {
		init();
		this.id = id;
		this.name = ChatColor.translateAlternateColorCodes('&', "&f" + name);
	}
	
	private void init() {
		signs = new ArrayList<>();
		setState(ArenaState.WAITING);
		arenaCountdown = new ArenaCountdown(Settings.COUNTDOWN_LOBBY.getInt(), this);
		arenaGame = new ArenaGame(this);
		item = new ItemStack(Material.AIR);
		players = new ArrayList<>();
		kills = new HashMap<>();
		deaths = new HashMap<>();
		locations = new LinkedList<>();
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getNameWithoutColors() {
		return ChatColor.stripColor(name);
	}
	
	public void setName(String name) {
		this.name = ChatColor.translateAlternateColorCodes('&', "&f" + name);
		if(item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f" + name));
			item.setItemMeta(meta);
		}
	}
	
	public Location getPos1() {
		return pos1;
	}
	
	public void setPos1(Location pos1) {
		if(pos2 != null && !pos1.getWorld().getUID().equals(pos2.getWorld().getUID()))
			return;
		this.pos1 = pos1;
		if(pos2 != null)
			setBox();
	}
	
	public Location getPos2() {
		return pos2;
	}
	
	public void setPos2(Location pos2) {
		if(pos1 != null && !pos2.getWorld().getUID().equals(pos1.getWorld().getUID()))
			return;
		this.pos2 = pos2;
		if(pos1 != null)
			setBox();
	}
	
	public Location getCenter() {
		if(box == null)
			setBox();
		if(box == null)
			return null;
		return box.getCenter().toLocation(pos1.getWorld());
	}
	
	public double getSize() {
		return box == null ? 0.0 : box.getWidthX() * box.getWidthZ();
	}
	
	public BoundingBox getBox() {
		return box;
	}
	
	public void setBox(BoundingBox box) {
		this.box = box;
	}
	
	public void setBox() {
		if(pos1 == null || pos2 == null)
			return;
		box = BoundingBox.of(pos1, pos2);
	}
	
	public List<String> getLore() {
		return item.hasItemMeta() ? item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>() : new ArrayList<>();
	}
	
	public void setLore(List<String> lore) {
		if(item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();
			meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).collect(Collectors.toList()));
			item.setItemMeta(meta);
		}
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public void setItem(ItemStack item) {
		this.item = item;
		if(!name.isEmpty() && item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
			item.setItemMeta(meta);
		}
	}
	
	public void setMaterial(Material material) {
		item = new ItemStack(material);
		if(!name.isEmpty() && item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
			item.setItemMeta(meta);
		}
	}
	
	public void setAmount(int amount) {
		item.setAmount(amount);
	}
	
	public void setDurability(short durability) {
		if(!item.hasItemMeta())
			return;
		Damageable itemMeta = (Damageable) item.getItemMeta();
		itemMeta.setDamage(durability);
		item.setItemMeta((ItemMeta) itemMeta);
	}
	
	public void broadcast(String message) {
		getOnlinePlayers().forEach(player -> player.sendMessage(Lang.PREFIX.getString() + message));
	}
	
	public void addPlayer(Player player) {
		ArenaPlayer arenaPlayer = containsPlayer(player) ? getArenaPlayer(player) : null;
		if(state == ArenaState.END || (arenaPlayer != null && !arenaPlayer.isDisconnect() && state == ArenaState.INGAME)) {
			Lang.sendMessage(player, Lang.ARENA_STARTED.getString());
			return;
		}
		if(state == ArenaState.FULL) {
			Lang.sendMessage(player, Lang.ARENA_FULL.getString());
			return;
		}
		if(arenaPlayer != null && !arenaPlayer.isDisconnect() && containsPlayer(player)) {
			Lang.sendMessage(player, Lang.ARENA_ALREADY_JOINED.getString());
			return;
		}
		arenaPlayer = addArenaPlayer(player);
		ArenaJoinEvent arenaJoinEvent = new ArenaJoinEvent(this, arenaPlayer);
		Bukkit.getPluginManager().callEvent(arenaJoinEvent);
		if(arenaJoinEvent.isCancelled()) {
			removeArenaPlayer(arenaPlayer);
			return;
		}
		if(players.size() >= maximumPlayers && state != ArenaState.INGAME)
			state = ArenaState.FULL;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Wanted.getInstance(), () -> Utils.setArenaItems(player), 1L);
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
		player.setFoodLevel(20);
		Lang.sendMessage(player, Lang.ARENA_JOINED.getString().replace("<arena>", name));
		broadcast(Lang.ARENA_PLAYER_JOINED.getString().replace("<arena>", name).replace("<player>", player.getName()).replace("<current>", players.size() + "").replace("<maximum>",
				maximumPlayers + ""));
		getOnlinePlayers().forEach(playerNew -> ArenaScoreboard.update(this, playerNew.getPlayer(), Settings.COUNTDOWN_LOBBY.getInt() + "", true));
		for(Player playerNew : Bukkit.getOnlinePlayers()) {
			if(!containsPlayer(playerNew)) {
				player.hidePlayer(Wanted.getInstance(), playerNew);
				playerNew.hidePlayer(Wanted.getInstance(), player);
			} else {
				player.showPlayer(Wanted.getInstance(), playerNew);
				playerNew.showPlayer(Wanted.getInstance(), player);
			}
		}
		if(arenaPlayer.isDisconnect() && state == ArenaState.INGAME) {
			ArenaTab.setTablist(player);
			Utils.clearItems(player);
			player.getInventory().setItem(0, arenaPlayer.getGun().getItem());
			player.getInventory().setItem(8,
					Utils.makeItem(Material.valueOf(Settings.ITEMS_GUNS_ITEM.toString()), Settings.ITEMS_GUNS_NAME.getString(), null, Settings.ITEMS_GUNS_LORE.getStringList()));
			player.teleport(getRandomLocation());
			return;
		}
		if(lobby != null)
			player.teleport(lobby);
		if(!arenaCountdown.isRunning() && players.size() >= requiredPlayers) {
			arenaCountdown.start();
		}
		updateSign();
	}
	
	public void removePlayer(OfflinePlayer player, boolean online, boolean left) {
		ArenaPlayer arenaPlayer = getArenaPlayer(player);
		ArenaQuitEvent arenaQuitEvent = new ArenaQuitEvent(this, arenaPlayer, online, left);
		Bukkit.getPluginManager().callEvent(arenaQuitEvent);
		if(arenaQuitEvent.isCancelled())
			return;
		Player plyr = player.getPlayer();
		if(isWanted(player))
			setWanted(getRandomPlayer(), true);
		ArenaScoreboard.remove(plyr);
		if(plyr != null) {
			if(plyr.getScoreboard().getObjective(plyr.getUniqueId().toString().substring(0, 16)) != null)
				plyr.getScoreboard().getObjective(plyr.getUniqueId().toString().substring(0, 16)).unregister();
			if(arenaPlayer.getBossBar() != null)
				arenaPlayer.getBossBar().removePlayer(plyr);
			arenaPlayer.setZoom(false);
			plyr.setGameMode(GameMode.SURVIVAL);
			plyr.setHealth(plyr.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
			plyr.setFoodLevel(20);
			if(wanted != null)
				getOnlinePlayers().forEach(all -> Utils.sendTeamPacket(wanted.getName(), Utils.Color.valueOf(Settings.GLOW_COLOR.toString()), false, false, "always", "always", all));
			Utils.resetWorldBorder(this, plyr);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Wanted.getInstance(), () -> Utils.setLobbyItems(plyr, online, true), 1L);
			arenaPlayer.setFreezed(false);
			Utils.unfreeze(plyr);
			if(left)
				Lang.sendMessage(plyr, Lang.ARENA_LEFT.getString());
			for(Player playerNew : Bukkit.getOnlinePlayers()) {
				if(!Wanted.getInstance().getArenaManager().containsPlayer(playerNew)) {
					plyr.showPlayer(Wanted.getInstance(), playerNew);
					playerNew.showPlayer(Wanted.getInstance(), plyr);
				}
			}
			if(Settings.BUNGEECORD_MODE.getBoolean()) {
				Utils.sendHub(plyr);
			} else
				plyr.teleport(Wanted.getInstance().getArenaManager().getSpawn());
		}
		arenaPlayer.setBossBar(null);
		if(!online && !left && state == ArenaState.INGAME)
			arenaPlayer.setDisconnect(true);
		else {
			removeArenaPlayer(player);
			if(players.size() < maximumPlayers && state == ArenaState.FULL)
				state = ArenaState.WAITING;
			broadcast(Lang.ARENA_PLAYER_LEFT.getString().replace("<arena>", name).replace("<player>", player.getName()).replace("<current>", players.size() + "").replace("<maximum>",
					maximumPlayers + ""));
		}
		updateSign();
	}
	
	public boolean containsPlayer(OfflinePlayer player) {
		return players.stream().anyMatch(playerNew -> playerNew.getOfflinePlayer().getUniqueId().equals(player.getUniqueId()));
	}
	
	public boolean containsPlayer(ArenaPlayer player) {
		return players.contains(player);
	}
	
	public List<ArenaPlayer> getPlayers() {
		return players;
	}
	
	public List<OfflinePlayer> getPlayersAsOfflinePlayer() {
		return players.stream().map(player -> player.getOfflinePlayer()).collect(Collectors.toList());
	}
	
	public Set<Player> getOnlinePlayers() {
		Set<Player> online = new HashSet<>();
		for(ArenaPlayer player : players) {
			if(player.getOfflinePlayer().isOnline())
				online.add(player.getPlayer());
		}
		return online;
	}
	
	public void setPlayers(List<ArenaPlayer> players) {
		this.players = players;
	}
	
	public Player getRandomPlayer() {
		Set<Player> onlinePlayers = getOnlinePlayers();
		if(onlinePlayers.isEmpty())
			return null;
		int plyr = ThreadLocalRandom.current().nextInt(onlinePlayers.size());
		return onlinePlayers.toArray(new Player[0])[plyr];
	}
	
	public boolean isInvincible(Player player) {
		return getArenaPlayer(player).isInvincible();
	}
	
	public void addKill(Player player) {
		kills.put(player, kills.getOrDefault(player, 0L) + 1);
	}
	
	public Long getKill(Player player) {
		return kills.getOrDefault(player, 0L);
	}
	
	public Map<OfflinePlayer, Long> getKills() {
		return kills;
	}
	
	public void setKills(HashMap<OfflinePlayer, Long> kills) {
		this.kills = kills;
	}
	
	public void addDeath(Player player) {
		deaths.put(player, deaths.getOrDefault(player, 0L) + 1);
	}
	
	public Long getDeath(Player player) {
		return deaths.getOrDefault(player, 0L);
	}
	
	public Map<OfflinePlayer, Long> getDeaths() {
		return deaths;
	}
	
	public void setDeaths(HashMap<OfflinePlayer, Long> deaths) {
		this.deaths = deaths;
	}
	
	public Player getWanted() {
		if(wanted == null)
			return null;
		return wanted;
	}
	
	public void setWanted(Player wanted, boolean haveOldWanted) {
		if(haveOldWanted && this.wanted != null) {
			this.wanted.setGlowing(false);
			getOnlinePlayers().forEach(player -> Utils.sendTeamPacket(this.wanted.getName(), Utils.Color.valueOf(Settings.GLOW_COLOR.toString()), false, false, "always", "always", player));
		}
		this.wanted = wanted;
		wanted.setHealth(wanted.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
		ArenaPlayer wantedArenaPlayer = getArenaPlayer(wanted);
		wantedArenaPlayer.setInvincible(true);
		wantedArenaPlayer.getGun().setCurrentBullet(wantedArenaPlayer.getGun().getMagazine());
		getOnlinePlayers().forEach(player -> Utils.sendTeamPacket(wanted.getName(), Utils.Color.valueOf(Settings.GLOW_COLOR.toString()), false, true, "always", "always", player));
		new BukkitRunnable() {
			int i = 5;
			
			@Override
			public void run() {
				if(i == 3)
					wantedArenaPlayer.setInvincible(false);
				if(wanted.isDead() || i <= 0 || state != ArenaState.INGAME || arenaGame.getTime() <= 0 || players.size() <= 1) {
					wantedArenaPlayer.setInvincible(false);
					cancel();
					return;
				}
				Utils.setGlow(wanted, 10);
				i--;
			}
		}.runTaskTimer(Wanted.getInstance(), 0, 20);
	}
	
	public boolean isWanted(OfflinePlayer player) {
		if(wanted == null || player == null)
			return false;
		return wanted.getUniqueId().equals(player.getUniqueId());
	}
	
	public ArenaState getState() {
		return state;
	}
	
	public void setState(ArenaState state) {
		Bukkit.getPluginManager().callEvent(new ArenaStateChangeEvent(this, state));
		this.state = state;
		updateSign();
	}
	
	public LinkedList<Location> getLocations() {
		return locations;
	}
	
	public void setLocations(LinkedList<Location> locations) {
		this.locations = locations;
	}
	
	public Location getRandomLocation() {
		if(locations.isEmpty())
			return null;
		int lctn = ThreadLocalRandom.current().nextInt(locations.size());
		Location location = locations.get(lctn);
		return location;
	}
	
	public void addLocation(Location location) {
		locations.add(location);
	}
	
	public void setLocation(Location location, int index) {
		locations.set(index, location);
	}
	
	public Location getLocation(int index) {
		if(locations.size() <= index)
			return null;
		return locations.get(index);
	}
	
	public Location getWantedLocation() {
		return wantedLocation;
	}
	
	public void setWantedLocation(Location location) {
		wantedLocation = location;
	}
	
	public Location getLobby() {
		return lobby;
	}
	
	public void setLobby(Location lobby) {
		this.lobby = lobby;
	}
	
	public Location getSpectate() {
		return spectate;
	}
	
	public void setSpectate(Location spectate) {
		this.spectate = spectate;
	}
	
	public ArenaCountdown getArenaCountdown() {
		return arenaCountdown;
	}
	
	public void setArenaCountdown(ArenaCountdown arenaCountdown) {
		this.arenaCountdown = arenaCountdown;
	}
	
	public ArenaGame getArenaGame() {
		return arenaGame;
	}
	
	public void setArenaGame(ArenaGame arenaGame) {
		this.arenaGame = arenaGame;
	}
	
	public int getRequiredPlayers() {
		return requiredPlayers;
	}
	
	public void setRequiredPlayers(int requiredPlayers) {
		this.requiredPlayers = requiredPlayers;
	}
	
	public int getMaximumPlayers() {
		return maximumPlayers;
	}
	
	public void setMaximumPlayers(int maximumPlayers) {
		this.maximumPlayers = maximumPlayers;
		if(!locations.isEmpty() && locations.size() >= maximumPlayers) {
			while(locations.size() >= maximumPlayers)
				locations.remove(locations.size() - 1);
		}
	}
	
	public int getMaximumReward() {
		return maximumReward;
	}
	
	public void setMaximumReward(int maximumReward) {
		this.maximumReward = maximumReward;
	}
	
	public List<Sign> getSigns() {
		return signs;
	}
	
	public void addSign(Sign sign) {
		signs.add(sign);
	}
	
	public void removeSign(Sign sign) {
		if(signs.contains(sign))
			signs.remove(sign);
	}
	
	public void updateSign() {
		if(signs.isEmpty())
			return;
		for(Sign sign : signs) {
			for(int i = 0; ++i < 5;) {
				sign.setLine(i, Lang.valueOf("ARENA_SIGN_" + i).getString().replace("<arena>", name).replace("<currentplayers>", players.size() + "").replace("<maximumplayers>", maximumPlayers + "")
						.replace("<state>", state.getName()));
			}
			org.bukkit.block.data.type.Sign sign2 = (org.bukkit.block.data.type.Sign) sign.getBlock().getBlockData();
			Block behind = sign.getBlock().getRelative(sign2.getRotation()).getLocation().getBlock();
			if(behind.getType() != state.getMaterial())
				behind.setType(state.getMaterial());
			sign.update();
		}
	}
	
	public boolean isCompleted() {
		return (Wanted.getInstance().getArenaManager().getSpawn() != null && lobby != null && pos1 != null && pos2 != null && !locations.isEmpty() && locations.size() >= maximumPlayers - 1
				&& maximumPlayers != 0 && requiredPlayers != 0 && wantedLocation != null && spectate != null && maximumReward != 0);
	}
	
	public String steps() {
		if(isCompleted())
			return Lang.ARENA_STEPS_COMPLETED.getString();
		String steps = new String();
		if(Wanted.getInstance().getArenaManager().getSpawn() == null)
			steps = addStep(steps, Lang.ARENA_STEPS_SPAWN.getString());
		if(lobby == null)
			steps = addStep(steps, Lang.ARENA_STEPS_LOBBY.getString());
		if(pos1 == null)
			steps = addStep(steps, Lang.ARENA_STEPS_POS1.getString());
		if(pos2 == null)
			steps = addStep(steps, Lang.ARENA_STEPS_POS2.getString());
		if(locations.isEmpty() || (locations.size() < maximumPlayers - 1) && maximumPlayers != 0)
			steps = addStep(steps, Lang.ARENA_STEPS_LOCATIONS.getString());
		if(wantedLocation == null)
			steps = addStep(steps, Lang.ARENA_STEPS_WANTED.getString());
		if(spectate == null)
			steps = addStep(steps, Lang.ARENA_STEPS_SPECTATE.getString());
		if(requiredPlayers == 0)
			steps = addStep(steps, Lang.ARENA_STEPS_MINIMUM.getString());
		if(maximumPlayers == 0)
			steps = addStep(steps, Lang.ARENA_STEPS_MAXIMUM.getString());
		if(maximumReward == 0)
			steps = addStep(steps, Lang.ARENA_STEPS_MAXIMUMREWARD.getString());
		return steps;
	}
	
	private String addStep(String steps, String step) {
		return steps.isEmpty() ? Lang.ARENA_STEPS.getString().replace("<steps>", step.replace("<arena>", getNameWithoutColors())) : steps + ", " + step.replace("<arena>", getNameWithoutColors());
	}
	
	public void reset() {
		kills.clear();
		deaths.clear();
		for(ArenaPlayer player : new ArrayList<>(players)) {
			removePlayer(player.getOfflinePlayer(), player.getOfflinePlayer().isOnline(), false);
		}
		state = ArenaState.WAITING;
		arenaCountdown = new ArenaCountdown(Settings.COUNTDOWN_LOBBY.getInt(), this);
		arenaGame = new ArenaGame(this);
		updateSign();
	}
	
	public ArenaPlayer getArenaPlayer(OfflinePlayer player) {
		for(ArenaPlayer arenaPlayer : players) {
			if(arenaPlayer.getOfflinePlayer().getUniqueId().equals(player.getUniqueId()))
				return arenaPlayer;
		}
		return null;
	}
	
	public void addCoin(Player player, int coin) {
		ArenaPlayer arenaPlayer = getArenaPlayer(player);
		arenaPlayer.addCoin(coin);
		Wanted.getInstance().getStatsManager().addStats(player, StatType.COINS, coin);
	}
	
	public void removeCoin(Player player, int coin) {
		ArenaPlayer arenaPlayer = getArenaPlayer(player);
		arenaPlayer.removeCoin(coin);
	}
	
	public int getCoin(OfflinePlayer player) {
		return getArenaPlayer(player).getCoin();
	}
	
	public List<ArenaPlayer> getArenaPlayers() {
		return players;
	}
	
	public ArenaPlayer addArenaPlayer(OfflinePlayer player) {
		if(!containsPlayer(player)) {
			ArenaPlayer arenaPlayer = new ArenaPlayer(player);
			players.add(arenaPlayer);
			return arenaPlayer;
		}
		return getArenaPlayer(player);
	}
	
	public void removeArenaPlayer(OfflinePlayer player) {
		if(containsPlayer(player))
			players.remove(getArenaPlayer(player));
	}
	
	public void removeArenaPlayer(ArenaPlayer player) {
		if(containsPlayer(player))
			players.remove(player);
	}
	
	public Set<OfflinePlayer> getHighestCoin() {
		int coin = -1;
		Set<OfflinePlayer> returnList = new HashSet<>();
		for(ArenaPlayer player : getPlayers()) {
			if(player.getCoin() > coin) {
				returnList.clear();
				coin = player.getCoin();
				returnList.add(player.getOfflinePlayer());
			} else if(player.getCoin() == coin)
				returnList.add(player.getOfflinePlayer());
		}
		return returnList;
	}
	
	public String getSign() {
		return getNameWithoutColors() + ":" + getState().getName() + ":" + getPlayers().size() + "/" + getMaximumPlayers() + ":" + getMaximumReward();
	}
	
}
