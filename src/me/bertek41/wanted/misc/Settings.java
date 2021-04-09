package me.bertek41.wanted.misc;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public enum Settings {
	
	USE_RESOURCE_PACK("wanted.use_resource_pack"),
	RESOURCE_PACK_REQUIRED("wanted.resource_pack_required"),
	RESOURCE_PACK_LINK("wanted.resource_pack_link"),
	RESOURCE_PACK_HASH("wanted.resource_pack_hash"),
	SET_RESOURCE_PACK_AFTER("wanted.set_resource_pack_after"),
	HOLOGRAMS_UPDATE("wanted.holograms_update"),
	BUNGEECORD_MODE("bungeecord.bungeecord_mode"),
	STATE_AS_MOTD("bungeecord.state_as_motd"),
	HUB_SERVER("bungeecord.hub_server"),
	COUNTDOWN_LOBBY("countdown.lobby"),
	COUNTDOWN_GAME("countdown.game"),
	GLOW_COLOR("game.glow_color"),
	BORDER_COLOR("game.border_color"),
	PRICE_PER_KILL("game.price_per_kill"),
	WIN_COMMANDS("game.win_commands"),
	DRAW_COMMANDS("game.draw_commands"),
	LOSE_COMMANDS("game.lose_commands"),
	MAKE_ALLOWED_LIKE_BLOCKED("game.make_allowed_like_blocked"),
	ALLOWED_COMMANDS("game.allowed_commands"),
	GAME_GLOW_DURATION("game.glow_duration"),
	GAME_GLOW_DURATION_COUNTDOWN("game.glow_duration_countdown"),
	BULLET_HEIGHT("game.bullet_height"),
	BULLET_HEIGHT_WHILE_SNEAKING("game.bullet_height_while_sneaking"),
	HEAD_HEIGHT("game.head_height"),
	HEAD_HEIGHT_WHILE_SNEAKING("game.head_height_while_sneaking"),
	ITEMS_JOIN_ARENA_ITEM("items.join_arena_item"),
	ITEMS_JOIN_ARENA_NAME("items.join_arena_name"),
	ITEMS_JOIN_ARENA_LORE("items.join_arena_lore"),
	ITEMS_RETURN_LOBBY_ITEM("items.return_lobby_item"),
	ITEMS_RETURN_LOBBY_NAME("items.return_lobby_name"),
	ITEMS_RETURN_LOBBY_LORE("items.return_lobby_lore"),
	ITEMS_GUNS_ITEM("items.guns_item"),
	ITEMS_GUNS_NAME("items.guns_name"),
	ITEMS_GUNS_LORE("items.guns_lore"),
	MYSQL_ENABLED("MySQL.enabled"),
	MYSQL_HOST("MySQL.host"),
	MYSQL_DATABASE("MySQL.database"),
	MYSQL_PORT("MySQL.port"),
	MYSQL_USER("MySQL.user"),
	MYSQL_PASSWORD("MySQL.password"),
	VERSION("version"),
	LANG_VERSION("lang_version"),
	GUNS_VERSION("guns_version"),
	ARENAS_VERSION("arenas_version");
	
	private static FileConfiguration config;
	private final String path;
	
	private Settings(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	public static void setConfig(FileConfiguration config) {
		Settings.config = config;
	}
	
	public Boolean getBoolean() {
		return config == null ? null : config.getBoolean(path);
	}
	
	public Double getDouble() {
		return config == null ? null : config.getDouble(path);
	}
	
	public Integer getInt() {
		return config == null ? null : config.getInt(path);
	}
	
	public String getString() {
		return config == null ? null : ChatColor.translateAlternateColorCodes('&', config.getString(path));
	}
	
	public List<String> getStringList() {
		return config == null ? null : config.getStringList(path).stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
	}
	
	@Override
	public String toString() {
		return config == null ? null : config.getString(path);
	}
	
}
