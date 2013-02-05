package be.raildelays.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.Assert;

public class LoadDatabase implements InitializingBean {

	static final private Logger LOGGER = LoggerFactory
			.getLogger(LoadDatabase.class);

	
	private String initScriptPath;

	private String databaseName;
	
	private DataSource dataSource;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(dataSource);
	}

	public void startUp() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {

			initDatabase(dataSource.getConnection());
	}

	public void shutdown() throws SQLException {
		LOGGER.debug("Shutting down database...");
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:derby:" + databaseName
					+ ";shutdown=true");
		} catch (SQLException e) {
			// The message say erroCode=08006 but e.getErrorCode() return 45000 don't know why...
			// So I do it dirty and swallow the exception instead of filtering  the right error code
			LOGGER.debug("erroCode={}", e.getErrorCode());
			LOGGER.debug("Database '{}' shutdown!", databaseName);
		} finally{
			if (connection != null) {
				connection.close();
			}
		}

	}

	/**
	 * Hook to initialize the embedded database. Subclasses may call to force
	 * initialization. After calling this method, {@link #getDataSource()}
	 * returns the DataSource providing connectivity to the db.
	 * 
	 * @throws SQLException
	 */
	protected void initDatabase(final Connection connection)
			throws SQLException {
		if (StringUtils.isNotBlank(initScriptPath)) {
			ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
			databasePopulator.addScript(new ClassPathResource(initScriptPath));

			try {
				LOGGER.debug("Loading script={} ...", initScriptPath);
				databasePopulator.populate(connection);
			} catch (Exception e) {
				LOGGER.warn(
						"Exception occured during database initilization : {}",
						e.getMessage());
			} finally{
				if (connection != null) {
					connection.close();
				}
			}
		}
	}

	public void setInitScriptPath(String initScriptPath) {
		this.initScriptPath = initScriptPath;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
