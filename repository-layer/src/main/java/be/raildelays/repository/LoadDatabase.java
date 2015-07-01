/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package be.raildelays.repository;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    @PostConstruct
    public void startUp() throws InstantiationException,
            IllegalAccessException, ClassNotFoundException, SQLException {

        initDatabase(dataSource.getConnection());
    }

    @PreDestroy
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
        } finally {
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
            } finally {
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
