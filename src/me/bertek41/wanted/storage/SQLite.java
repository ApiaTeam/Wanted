package me.bertek41.wanted.storage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.bertek41.wanted.Wanted;

public class SQLite extends Database {
	
	public SQLite(Wanted instance) {
		super(instance);
	}
	
	@Override
	public Connection connect() {
		try {
			if(connection != null && !connection.isClosed())
				return connection;
			connection = DriverManager.getConnection("jdbc:sqlite:" + getDatabaseFile());
			return connection;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public File getDatabaseFile() {
		File file = new File(instance.getDataFolder(), "stats.db");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
}
