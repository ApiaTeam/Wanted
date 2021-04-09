package me.bertek41.wanted.storage;

import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.misc.Settings;

public class MySQL extends Database {
	
	public MySQL(Wanted instance) {
		super(instance);
	}
	
	@Override
	public Connection connect() {
		try {
			if(connection != null && !connection.isClosed())
				return connection;
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUser(Settings.MYSQL_USER.toString());
			dataSource.setPassword(Settings.MYSQL_PASSWORD.toString());
			dataSource.setServerName(Settings.MYSQL_HOST.toString());
			dataSource.setDatabaseName(Settings.MYSQL_DATABASE.toString());
			dataSource.setPort(Settings.MYSQL_PORT.getInt());
			dataSource.setUseUnicode(true);
			dataSource.setCharacterEncoding("UTF-8");
			dataSource.setAutoReconnect(true);
			dataSource.setFailOverReadOnly(false);
			dataSource.setMaxReconnects(10);
			dataSource.setVerifyServerCertificate(false);
			dataSource.setUseSSL(false);
			connection = dataSource.getConnection();
			return connection;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
