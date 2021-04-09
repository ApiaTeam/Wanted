package me.bertek41.wanted.misc;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public enum Lang {
	
	PREFIX("prefix"),
	NO_PERMISSION("no_permission"),
	WANTED_RELOADED("wanted_reloaded"),
	WANTED_STAT_NOT_FOUND("wanted_stat_not_found"),
	WANTED_ADDSTATS("wanted_addstats"),
	WANTED_ADDSTATS_ENTER_NUMBER("wanted_addstats_enter_number"),
	WANTED_ADDSTATS_SET("wanted_addstats_set"),
	WANTED_SETSTATS("wanted_setstats"),
	WANTED_SETSTATS_ENTER_NUMBER("wanted_setstats_enter_number"),
	WANTED_SETSTATS_SET("wanted_setstats_set"),
	WANTED_SETHOLOGRAM("wanted_sethologram"),
	WANTED_SETHOLOGRAM_SET("wanted_sethologram_set"),
	WANTED_DELETEHOLOGRAM_NO_HOLOGRAMS_FOUND("wanted_deletehologram_no_holograms_found"),
	WANTED_DELETEHOLOGRAM_DELETED("wanted_deletehologram_deleted"),
	WANTED_CONVERT("wanted_convert"),
	WANTED_CONVERT_IS_NOT_CONNECTED("wanted_convert_is_not_connected"),
	WANTED_CONVERT_IS_NOT_CORRECT_DATABASE("wanted_convert_is_not_correct_database"),
	WANTED_CONVERT_DONE("wanted_convert_done"),
	WANTED_HELP("wanted_help"),
	HOLOGRAM_FORMAT("hologram_format"),
	RESOURCE_PACK_REQUIRED("resource_pack_required"),
	ACCEPT_RESOURCE_PACK("accept_resource_pack"),
	COMMAND_BLOCKED("command_blocked"),
	RESPAWNING_TITLE("respawning_title"),
	RESPAWNING_SUBTITLE("respawning_subtitle"),
	RESPAWNING_ACTIONBAR("respawning_actionbar"),
	GUNS_GUI_TITLE("guns_gui_title"),
	GUNS_GUI_GUN_LORE("guns_gui_gun_lore"),
	GUNS_GUI_GUN_LORE_COIN("guns_gui_gun_lore_coin"),
	GUNS_NOT_ENOUGH_COIN("guns_not_enough_coin"),
	GUNS_GOT("guns_got"),
	GUN_HIT_ACTIONBAR("gun_hit_actionbar"),
	GUN_HEADSHOT_ACTIONBAR("gun_headshot_actionbar"),
	GUN_SHOT_ACTIONBAR("gun_shot_actionbar"),
	PLAYER_KILLED_BY_WANTED("player_killed_by_wanted"),
	WANTED_KILLED_BY_PLAYER("wanted_killed_by_player"),
	ARENAS_GUI_TITLE("arenas_gui_title"),
	ARENAS_GUI_ARENA_LORE("arenas_gui_arena_lore"),
	AUTOJOIN_SIGN_PLACED("autojoin_sign_placed"),
	AUTOJOIN_SIGN_REMOVED("autojoin_sign_removed"),
	ARENA_NOT_FOUND("arena_not_found"),
	ARENA_NOT_FOUND_SIGN_1("arena_not_found_sign_1"),
	ARENA_NOT_FOUND_SIGN_2("arena_not_found_sign_2"),
	ARENA_NOT_FOUND_SIGN_3("arena_not_found_sign_3"),
	ARENA_NOT_FOUND_SIGN_4("arena_not_found_sign_4"),
	JOIN_SIGN_PLACED("join_sign_placed"),
	JOIN_SIGN_REMOVED("join_sign_removed"),
	INGAME_ONLY("ingame_only"),
	ARENACOMMAND_NO_ARENA("arenacommand_no_arena"),
	ARENACOMMAND_LOCATION_SPAWN_SET("arenacommand_location_spawn_set"),
	ARENACOMMAND_CREATE("arenacommand_create"),
	ARENACOMMAND_ALREADY_CREATED("arenacommand_already_created"),
	ARENACOMMAND_CREATED("arenacommand_created"),
	ARENACOMMAND_DELETE("arenacommand_delete"),
	ARENACOMMAND_DELETED("arenacommand_deleted"),
	ARENACOMMAND_SETLOBBY("arenacommand_setlobby"),
	ARENACOMMAND_LOCATION_LOBBY_SET("arenacommand_location_lobby_set"),
	ARENACOMMAND_SETPOS("arenacommand_setpos"),
	ARENACOMMAND_SETPOS_SET("arenacommand_setpos_set"),
	ARENACOMMAND_SETLOCATION("arenacommand_setlocation"),
	ARENACOMMAND_SETLOCATION_CAN_NOT_SET_NOW("arenacommand_setlocation_can_not_set_now"),
	ARENACOMMAND_SETLOCATION_MUST_BETWEEN_POS1_AND_POS2("arenacommand_setlocation_must_between_pos1_and_pos2"),
	ARENACOMMAND_LOCATION_WANTED_SET("arenacommand_location_wanted_set"),
	ARENACOMMAND_LOCATION_PLAYER_SET("arenacommand_location_player_set"),
	ARENACOMMAND_LOCATION_PLAYER_MAXIMUM("arenacommand_location_player_maximum"),
	ARENACOMMAND_LOCATION_INDEX_CAN_NOT_BIGGER_THAN_LOCATIONS_SIZE("arenacommand_location_index_can_not_bigger_than_locations_size"),
	ARENACOMMAND_SETSPECTATE("arenacommand_setspectate"),
	ARENACOMMAND_LOCATION_SPECTATE_SET("arenacommand_location_spectate_set"),
	ARENACOMMAND_SETMINIMUM("arenacommand_setminimum"),
	ARENACOMMAND_SETMINIMUM_ENTER_NUMBER("arenacommand_setminimum_enter_number"),
	ARENACOMMAND_SETMINIMUM_HIGHER_THAN_1("arenacommand_setminimum_higher_than_1"),
	ARENACOMMAND_SETMINIMUM_HIGHER_THAN_MAXIMUM("arenacommand_setminimum_higher_than_maximum"),
	ARENACOMMAND_SETMINIMUM_SET("arenacommand_setminimum_set"),
	ARENACOMMAND_SETMAXIMUM("arenacommand_setmaximum"),
	ARENACOMMAND_SETMAXIMUM_ENTER_NUMBER("arenacommand_setmaximum_enter_number"),
	ARENACOMMAND_SETMAXIMUM_HIGHER_THAN_1("arenacommand_setmaximum_higher_than_1"),
	ARENACOMMAND_SETMAXIMUM_LOWER_THAN_MINIMUM("arenacommand_setmaximum_lower_than_minimum"),
	ARENACOMMAND_SETMAXIMUM_SET("arenacommand_setmaximum_set"),
	ARENACOMMAND_SETMAXIMUMREWARD("arenacommand_setmaximumreward"),
	ARENACOMMAND_SETMAXIMUMREWARD_ENTER_NUMBER("arenacommand_setmaximumreward_enter_number"),
	ARENACOMMAND_SETMAXIMUMREWARD_HIGHER_THAN_0("arenacommand_setmaximumreward_higher_than_0"),
	ARENACOMMAND_SETMAXIMUMREWARD_SET("arenacommand_setmaximum_set"),
	ARENACOMMAND_SETITEM("arenacommand_setitem"),
	ARENACOMMAND_SETITEM_NEED_ITEM("arenacommand_setitem_need_item"),
	ARENACOMMAND_SETITEM_SET("arenacommand_setitem_set"),
	ARENACOMMAND_RENAME("arenacommand_rename"),
	ARENACOMMAND_RENAME_SET("arenacommand_rename_set"),
	ARENACOMMAND_STEPS("arenacommand_steps"),
	ARENACOMMAND_COMPLETED("arenacommand_completed"),
	JOIN_ARENA_NOT_COMPLETED("join_arena_not_completed"),
	YOU_DID_NOT_JOINED_ARENA("you_did_not_joined_arena"),
	ARENACOMMAND_HELP("arenacommand_help"),
	ARENA_STARTED("arena_started"),
	ARENA_FULL("arena_full"),
	ARENA_ALREADY_JOINED("arena_already_joined"),
	ARENA_JOINED("arena_joined"),
	ARENA_PLAYER_JOINED("arena_player_joined"),
	ARENA_LEFT("arena_left"),
	ARENA_PLAYER_LEFT("arena_player_left"),
	ARENA_SIGN_1("arena_sign_1"),
	ARENA_SIGN_2("arena_sign_2"),
	ARENA_SIGN_3("arena_sign_3"),
	ARENA_SIGN_4("arena_sign_4"),
	ARENA_STEPS("arena_steps"),
	ARENA_STEPS_COMPLETED("arena_steps_completed"),
	ARENA_STEPS_SPAWN("arena_steps_spawn"),
	ARENA_STEPS_LOBBY("arena_steps_lobby"),
	ARENA_STEPS_POS1("arena_steps_pos1"),
	ARENA_STEPS_POS2("arena_steps_pos2"),
	ARENA_STEPS_LOCATIONS("arena_steps_locations"),
	ARENA_STEPS_WANTED("arena_steps_wanted"),
	ARENA_STEPS_SPECTATE("arena_steps_spectate"),
	ARENA_STEPS_MINIMUM("arena_steps_minimum"),
	ARENA_STEPS_MAXIMUM("arena_steps_maximum"),
	ARENA_STEPS_MAXIMUMREWARD("arena_steps_maximumreward"),
	ARENA_COUNTDOWN_SINGULAR("arena_countdown_singular"),
	ARENA_COUNTDOWN_PLURAL("arena_countdown_plural"),
	ARENA_COUNTDOWN_TOO_FEW_PLAYERS("arena_countdown_too_few_players"),
	ARENA_GAME_END("arena_game_end"),
	ARENA_GAME_DRAW("arena_game_draw"),
	ARENA_GAME_END_NO_WINNER("arena_game_end_no_winner"),
	ARENA_AUTOJOIN_SIGN_1("arena_autojoin_sign_1"),
	ARENA_AUTOJOIN_SIGN_2("arena_autojoin_sign_2"),
	ARENA_AUTOJOIN_SIGN_3("arena_autojoin_sign_3"),
	ARENA_AUTOJOIN_SIGN_4("arena_autojoin_sign_4"),
	ARENA_TAB_TITLE("arena_tab_title"),
	ARENA_TAB_SUBTITLE("arena_tab_subtitle"),
	SCOREBOARD_LOBBY_TITLE("scoreboard_lobby_title"),
	SCOREBOARD_LOBBY("scoreboard_lobby"),
	SCOREBOARD_GAME_TITLE("scoreboard_game_title"),
	SCOREBOARD_GAME("scoreboard_game"),
	STATE_WAITING("state_waiting"),
	STATE_FULL("state_full"),
	STATE_STARTING("state_starting"),
	STATE_INGAME("state_ingame"),
	STATE_END("state_end");
	
	private static FileConfiguration lang;
	private final String path;
	
	private Lang(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	public static void setLang(FileConfiguration lang) {
		if(Lang.lang == null)
			Lang.lang = lang;
	}
	
	public static void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(Lang.PREFIX.getString() + message);
	}
	
	public static void sendMessage(Player player, String message) {
		player.sendMessage(Lang.PREFIX.getString() + message);
	}
	
	public static void sendMessages(CommandSender sender, List<String> messages) {
		messages.forEach(message -> sender.sendMessage(Lang.PREFIX.getString() + message));
	}
	
	public static void sendMessages(Player player, List<String> messages) {
		messages.forEach(message -> player.sendMessage(Lang.PREFIX.getString() + message));
	}
	
	public Boolean getBoolean() {
		return lang == null ? null : lang.getBoolean(path);
	}
	
	public Double getDouble() {
		return lang == null ? null : lang.getDouble(path);
	}
	
	public Integer getInt() {
		return lang == null ? null : lang.getInt(path);
	}
	
	public String getString() {
		return lang == null ? null : ChatColor.translateAlternateColorCodes('&', lang.getString(path));
	}
	
	public List<String> getStringList() {
		return lang == null ? null : lang.getStringList(path).stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
	}
	
	@Override
	public String toString() {
		return lang == null ? null : lang.getString(path);
	}
	
}
