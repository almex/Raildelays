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
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class LoadDatabase {

	static final private Logger LOGGER = LoggerFactory
			.getLogger(LoadDatabase.class);

	private Connection connection;

	private String initScriptPath;

	private String databaseName;
	
	private DataSource datasource; 

	public void startUp() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		Properties properties = new Properties();
		
		LOGGER.debug("Loading database...");
		
		properties.put("hibernate.dialect", "org.hibernate.dialect.DerbyTenSevenDialect");
		
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		
		LOGGER.debug("Driver loaded!");

		try {
			DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_SUPPORTS);
			transactionDefinition
					.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
			transactionDefinition.setReadOnly(false);
			
			connection = DriverManager.getConnection("jdbc:derby:"
					+ databaseName + ";create=true", properties);
			connection.setAutoCommit(true);
			
			datasource = new SingleConnectionDataSource(connection, true);
			
			DataSourceUtils.prepareConnectionForTransaction(connection,
					transactionDefinition);			

			LOGGER.debug("Connexion created!");
			
			initDatabase();
		} finally  {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
		
		
	}

	public void shutdown() throws SQLException {
		LOGGER.debug("Shutting down database...");
		DriverManager.getConnection("jdbc:derby:" + databaseName
				+ ";shutdown=true");
	}

	public DataSource getDataSource() {
		return datasource;
	}

	/**
	 * Hook to initialize the embedded database. Subclasses may call to force
	 * initialization. After calling this method, {@link #getDataSource()}
	 * returns the DataSource providing connectivity to the db.
	 * @throws SQLException 
	 */
	protected void initDatabase() throws SQLException {
		if (StringUtils.isNotBlank(initScriptPath)) {
			ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
			databasePopulator.addScript(new ClassPathResource(initScriptPath));
			
			
			try {
				LOGGER.debug("Loading script={} ...", initScriptPath);
//				DatabasePopulatorUtils.execute(databasePopulator,
//						getDataSource());
				databasePopulator.populate(connection);
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
