package be.raildelays.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class LoadDatabase {
	
	private EmbeddedDatabase db;
	
	private Connection connexion;
	
	public void startUp() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
//		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder(new DefaultResourceLoader());
//		String tmpDir = System.getProperty("java.io.tmpdir");
//	    db = builder.setType(EmbeddedDatabaseType.DERBY).addScript("spring-batch-schema-derby.ddl").build();
		Properties properties = new Properties();		
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
				
		connexion = DriverManager.getConnection("jdbc:derby:derbyDB;create=true", properties);
		
	}
	
	public void shutdown() throws SQLException {
		if (db != null) {
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
		}
	}
	
	public DataSource getDataSource() {
		return new SingleConnectionDataSource(connexion, true);
	}

}
