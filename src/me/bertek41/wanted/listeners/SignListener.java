package me.bertek41.wanted.listeners;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.utils.Utils;

public class SignListener implements Listener {
	private Wanted instance;
	
	public SignListener(Wanted instance) {
		this.instance = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		if(!event.getPlayer().hasPermission("wanted.admin") || event.getLine(0) == null || !event.getLine(0).equals("[WANTED]") || event.getLine(1) == null)
			return;
		event.setCancelled(true);
		Sign sign = (Sign) event.getBlock().getState();
		if(event.getLine(1).equalsIgnoreCase("autojoin")) {
			instance.getArenaManager().addAutoJoinSign(sign);
			Lang.sendMessage(event.getPlayer(), Lang.AUTOJOIN_SIGN_PLACED.getString());
			return;
		}
		if(!instance.getArenaManager().containsArena(event.getLine(1))) {
			for(int i = -1; ++i < 4;) {
				sign.setLine(i, Lang.valueOf("ARENA_NOT_FOUND_SIGN_" + i).getString());
			}
			sign.update();
			Lang.sendMessage(event.getPlayer(), Lang.ARENA_NOT_FOUND.getString());
			return;
		}
		Arena arena = instance.getArenaManager().getArena(event.getLine(1));
		arena.addSign(sign);
		arena.updateSign();
		Lang.sendMessage(event.getPlayer(), Lang.JOIN_SIGN_PLACED.getString().replace("<arena>", arena.getName()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSignBreak(BlockBreakEvent event) {
		if(!event.getPlayer().hasPermission("wanted.admin") || !(event.getBlock().getState() instanceof Sign))
			return;
		Sign sign = (Sign) event.getBlock().getState();
		if(instance.getArenaManager().containsSign(sign)) {
			instance.getArenaManager().removeSign(sign);
			Lang.sendMessage(event.getPlayer(), Lang.AUTOJOIN_SIGN_REMOVED.getString());
			return;
		}
		if(instance.getArenaManager().getArena(sign) == null)
			return;
		Arena arena = instance.getArenaManager().getArena(sign);
		arena.removeSign(sign);
		Lang.sendMessage(event.getPlayer(), Lang.JOIN_SIGN_REMOVED.getString().replace("<arena>", arena.getName()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSignClick(PlayerInteractEvent event) {
		if(event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null || !(event.getClickedBlock().getState() instanceof Sign))
			return;
		if(Utils.getPacks().contains(event.getPlayer().getUniqueId()))
			return;
		Sign sign = (Sign) event.getClickedBlock().getState();
		if(instance.getArenaManager().containsSign(sign)) {
			instance.getArenaManager().addRandomly(event.getPlayer());
			return;
		}
		if(instance.getArenaManager().getArena(sign) == null)
			return;
		Arena arena = instance.getArenaManager().getArena(sign);
		arena.addPlayer(event.getPlayer());
	}
	
}
