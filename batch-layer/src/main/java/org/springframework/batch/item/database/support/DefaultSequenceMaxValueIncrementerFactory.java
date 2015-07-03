package org.springframework.batch.database.support;

import org.springframework.batch.item.database.support.DataFieldMaxValueIncrementerFactory;
import org.springframework.batch.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;
import org.springframework.batch.support.DatabaseType;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.HsqlSequenceMaxValueIncrementer;

import javax.sql.DataSource;

import static org.springframework.batch.support.DatabaseType.HSQL;

/**
 * This implementation prone Sequence based max values opposed to Column based max values used by the
 * {@link DefaultDataFieldMaxValueIncrementerFactory}. For instance with HSQLDB, this implementation use
 * {@code HsqlSequenceMaxValueIncrementer} instead of {@code HsqlMaxValueIncrementer}.
 */
public class DefaultSequenceMaxValueIncrementerFactory implements DataFieldMaxValueIncrementerFactory {

    private DefaultDataFieldMaxValueIncrementerFactory delegate;

    private DataSource dataSource;

    private String incrementerColumnName = "ID";

    /**
     * Public setter for the column name (defaults to "ID") in the incrementer.
     * Only used by some platforms (Derby, HSQL, MySQL, SQL Server and Sybase),
     * and should be fine for use with Spring Batch meta data as long as the
     * default batch schema hasn't been changed.
     *
     * @param incrementerColumnName the primary key column name to set
     */
    public void setIncrementerColumnName(String incrementerColumnName) {
        this.incrementerColumnName = incrementerColumnName;
    }

    public DefaultSequenceMaxValueIncrementerFactory(DataSource dataSource) {
        this.dataSource = dataSource;
        delegate = new DefaultDataFieldMaxValueIncrementerFactory(dataSource);
        delegate.setIncrementerColumnName(incrementerColumnName);
    }

    @Override
    public DataFieldMaxValueIncrementer getIncrementer(String incrementerType, String incrementerName) {
        DatabaseType databaseType = DatabaseType.valueOf(incrementerType.toUpperCase());
        DataFieldMaxValueIncrementer result;

        if (databaseType == HSQL) {
            result = new HsqlSequenceMaxValueIncrementer(dataSource, incrementerName);
        } else {
            result = delegate.getIncrementer(incrementerType, incrementerName);
        }

        return result;
    }

    @Override
    public boolean isSupportedIncrementerType(String databaseType) {
        return delegate.isSupportedIncrementerType(databaseType);
    }

    @Override
    public String[] getSupportedIncrementerTypes() {
        return delegate.getSupportedIncrementerTypes();
    }
}
