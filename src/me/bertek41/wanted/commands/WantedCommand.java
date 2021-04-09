package me.bertek41.wanted.commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.arenamanager.ArenaTab;
import me.bertek41.wanted.misc.HdHologram;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.misc.Settings;
import me.bertek41.wanted.misc.StatType;
import me.bertek41.wanted.storage.Database;
import me.bertek41.wanted.storage.MySQL;
import me.bertek41.wanted.storage.SQLite;
import me.bertek41.wanted.utils.Utils;

@CommandAlias("wanted")
public class WantedCommand extends BaseCommand {
	@Dependency
	private Wanted instance;
	
	@Subcommand("help")
	@CommandPermission("wanted.help")
	@CatchUnknown
	@Default
	public void onHelp(CommandSender sender) {
		if(sender instanceof Player && !sender.hasPermission("wanted.help")) {
			Lang.sendMessage(sender, Lang.NO_PERMISSION.getString());
			return;
		}
		Lang.sendMessages(sender, Lang.WANTED_HELP.getStringList());
	}
	
	@Subcommand("reload")
	@CommandPermission("wanted.reload")
	public void onReload(CommandSender sender) {
		instance.reloadConfig();
		Settings.setConfig(instance.getConfig());
		instance.getFileManager().saveGuns();
		instance.getFileManager().saveLang();
		instance.getFileManager().loadGuns();
		Lang.setLang(instance.getFileManager().getLang());
		instance.startRunnable();
		instance.getArenaCommand().init();
		Bukkit.getOnlinePlayers().forEach(player -> ArenaTab.setTablist(player));
		Lang.sendMessage(sender, Lang.WANTED_RELOADED.getString());
	}
	
	@Subcommand("pack")
	@CommandPermission("wanted.pack")
	public void onPack(CommandSender sender) {
		Utils.setPack((Player) sender, false);
	}
	
	@Subcommand("addstats")
	@CommandPermission("wanted.addstats")
	@Syntax("&7/wanted addstats <player> <coins/gamesplayed/kills/deaths/shots/shotsontarget/headshots/wins/draws/defeats> <amount>")
	@CommandCompletion("@players @stats")
	public void onAddStats(CommandSender sender, OnlinePlayer target, StatType stat, int amount) {
		instance.getStatsManager().addStats(target.getPlayer(), stat, amount);
		Lang.sendMessage(sender, Lang.WANTED_ADDSTATS_SET.getString().replace("<amount>", amount + "").replace("<stat>", stat.getName()).replace("<player>", target.getPlayer().getName()));
	}
	
	@Subcommand("setstats")
	@CommandPermission("wanted.setstats")
	@Syntax("&7/wanted setstats <player> <coins/gamesplayed/kills/deaths/shots/shotsontarget/headshots/wins/draws/defeats> <amount>")
	@CommandCompletion("@players @stats")
	public void onSetStats(CommandSender sender, OnlinePlayer target, StatType stat, int amount) {
		instance.getStatsManager().setStats(target.getPlayer(), stat, amount);
		Lang.sendMessage(sender, Lang.WANTED_SETSTATS_SET.getString().replace("<amount>", amount + "").replace("<stat>", stat.getName()).replace("<player>", target.getPlayer().getName()));
	}
	
	@Subcommand("sethologram")
	@CommandPermission("wanted.sethologram")
	@Syntax("&7/wanted sethologram <coins/gamesplayed/kills/deaths/shots/shotsontarget/headshots/wins/draws/defeats>")
	@CommandCompletion("@stats")
	public void onSetHologram(Player player, StatType stat) {
		HdHologram hdHologram = new HdHologram(Utils.getLocation(player));
		Utils.setLines(instance, hdHologram, stat);
		hdHologram.setStatType(stat);
		hdHologram.spawn();
		instance.getHologramManager().addHologram(hdHologram);
	}
	
	@Subcommand("deletehologram")
	@CommandPermission("wanted.deletehologram")
	@Syntax("&7/wanted deletehologram <radius>")
	public void onDeleteHologram(Player player, int radius) {
		int amount = instance.getHologramManager().deleteHologramsByRadius(Utils.getLocation(player), radius);
		if(amount == 0)
			Lang.sendMessage(player, Lang.WANTED_DELETEHOLOGRAM_NO_HOLOGRAMS_FOUND.getString().replace("<radius>", radius + ""));
		else
			Lang.sendMessage(player, Lang.WANTED_DELETEHOLOGRAM_DELETED.getString().replace("<amount>", amount + "").replace("<radius>", radius + ""));
	}
	
	@Subcommand("convert")
	@CommandPermission("wanted.convert")
	@Syntax("&7/wanted convert <mysql/sqlite>")
	@CommandCompletion("@database")
	public void onConvert(CommandSender sender, String arg) {
		if(!arg.equalsIgnoreCase("mysql") && !arg.equalsIgnoreCase("sqlite")) {
			Lang.sendMessage(sender, Lang.WANTED_CONVERT.getString());
			return;
		}
		boolean mysql = arg.equalsIgnoreCase("mysql");
		if(!instance.getDatabase().isConnected()) {
			Lang.sendMessage(sender, Lang.WANTED_CONVERT_IS_NOT_CONNECTED.getString());
			return;
		}
		if(!(mysql && instance.getDatabase() instanceof MySQL) && !(!mysql && instance.getDatabase() instanceof SQLite)) {
			Lang.sendMessage(sender, Lang.WANTED_CONVERT_IS_NOT_CORRECT_DATABASE.getString());
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			Database old = mysql ? new SQLite(instance) : new MySQL(instance);
			old.connect();
			ResultSet resultSet = old.query("SELECT * FROM Stats");
			try {
				while(resultSet.next()) {
					instance.getDatabase()
							.update("INSERT INTO Stats(Uuid, Coins, GamesPlayed, Kills, Deaths, Shots, ShotsOnTarget, Headshots, Wins, Draws, Defeats) VALUES(" + "\'" + resultSet.getString("Uuid")
									+ "\', \'" + resultSet.getLong("Coins") + "\', \'" + resultSet.getLong("GamesPlayed") + "\', \'" + resultSet.getLong("Kills") + "\', \'"
									+ resultSet.getLong("Deaths") + "\', \'" + resultSet.getLong("Shots") + "\', \'" + resultSet.getLong("ShotsOnTarget") + "\', \'" + resultSet.getLong("Headshots")
									+ "\', \'" + resultSet.getLong("Wins") + "\', \'" + resultSet.getLong("Draws") + "\', \'" + resultSet.getLong("Defeats") + "\')");
				}
				resultSet.close();
				old.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
			Lang.sendMessage(sender, Lang.WANTED_CONVERT_DONE.getString());
		});
	}
	
}
