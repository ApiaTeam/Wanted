package me.bertek41.wanted.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.bertek41.wanted.Wanted;

public abstract class Database {
	protected Wanted instance;
	protected Connection connection;
	
	public Database(Wanted instance) {
		this.instance = instance;
	}
	
	public abstract Connection connect();
	
	protected Connection getConnection() {
		return isConnected() ? connection : connect();
	}
	
	public boolean isConnected() {
		try {
			if(connection != null && !connection.isClosed())
				return true;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void createTable() {
		if(!isConnected())
			getConnection();
		update("CREATE TABLE IF NOT EXISTS Stats (Uuid VARCHAR(36) NOT NULL PRIMARY KEY, Coins INT DEFAULT 0, GamesPlayed INT DEFAULT 0, Kills INT DEFAULT 0, Deaths INT DEFAULT 0, Shots INT DEFAULT 0, ShotsOnTarget INT DEFAULT 0, Headshots INT DEFAULT 0, Wins INT DEFAULT 0, Draws INT DEFAULT 0, Defeats INT DEFAULT 0);");
	}
	
	public void close() {
		if(isConnected()) {
			try {
				connection.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ResultSet query(String query) {
		if(!isConnected())
			return null;
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();
			return resultSet;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void update(String query) {
		if(!isConnected())
			return;
		try {
			Statement statement = connection.createStatement();
			statement.execute(query);
			statement.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
}
