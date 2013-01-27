package be.raildelays.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

public class LoadDatabase {

	static final private Logger LOGGER = LoggerFactory
			.getLogger(LoadDatabase.class);

	private EmbeddedDatabase db;

	private Connection connexion;

	private String initScriptPath;

	private String databaseName;

	public void startUp() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		Properties properties = new Properties();
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();

		connexion = DriverManager.getConnection("jdbc:derby:" + databaseName
				+ ";create=true", properties);
		connexion.setAutoCommit(false);
		initDatabase();
	}

	public void shutdown() throws SQLException {
		if (db != null) {
			DriverManager.getConnection("jdbc:derby:" + databaseName
					+ ";shutdown=true");
		}
	}

	public DataSource getDataSource() {
		return new SingleConnectionDataSource(connexion, true);
	}

	/**
	 * Hook to initialize the embedded database. Subclasses may call to force
	 * initialization. After calling this method, {@link #getDataSource()}
	 * returns the DataSource providing connectivity to the db.
	 */
	protected void initDatabase() {
		if (StringUtils.isNotBlank(initScriptPath)) {
			ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
			databasePopulator.addScript(new ClassPathResource(initScriptPath));
			try {
				DatabasePopulatorUtils.execute(databasePopulator,
						getDataSource());
			} catch (DataAccessResourceFailureException e) {
				LOGGER.warn(
						"Exception occured during database initilization : {}",
						e.getMessage());
			}
		}
	}

	public void setInitScriptPath(String initScriptPath) {
		this.initScriptPath = initScriptPath;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

}
