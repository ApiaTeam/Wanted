package me.bertek41.wanted.misc;

public enum StatType {
	COINS("Coins"),
	GAMES_PLAYED("GamesPlayed"),
	KILLS("Kills"),
	DEATHS("Deaths"),
	SHOTS("Shots"),
	SHOTS_ON_TARGET("ShotsOnTarget"),
	HEADSHOTS("Headshots"),
	WINS("Wins"),
	DRAWS("Draws"),
	DEFEATS("Defeats");
	
	private String name;
	
	private StatType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
