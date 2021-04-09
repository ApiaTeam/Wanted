package me.bertek41.wanted.arenamanager;

import org.bukkit.Material;

import me.bertek41.wanted.misc.Lang;

public enum ArenaState {
	
	WAITING(Lang.STATE_WAITING.getString(), Material.LIME_STAINED_GLASS),
	FULL(Lang.STATE_FULL.getString(), Material.RED_STAINED_GLASS),
	STARTING(Lang.STATE_STARTING.getString(), Material.CYAN_STAINED_GLASS),
	INGAME(Lang.STATE_INGAME.getString(), Material.BLACK_STAINED_GLASS),
	END(Lang.STATE_END.getString(), Material.PURPLE_STAINED_GLASS);
	
	private String name;
	private Material material;
	
	private ArenaState(String name, Material material) {
		this.name = name;
		this.material = material;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Material getMaterial() {
		return this.material;
	}
	
}
