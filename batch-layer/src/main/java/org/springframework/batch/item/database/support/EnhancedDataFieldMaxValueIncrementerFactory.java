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

package org.springframework.batch.item.database.support;

import org.springframework.batch.support.DatabaseType;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.HsqlSequenceMaxValueIncrementer;

import javax.sql.DataSource;

import static org.springframework.batch.support.DatabaseType.HSQL;

/**
 * Allow to use {@link org.springframework.jdbc.support.incrementer.HsqlSequenceMaxValueIncrementer} instead of
 * {@link org.springframework.jdbc.support.incrementer.HsqlMaxValueIncrementer} for HSQLDB.
 */
public class EnhancedDataFieldMaxValueIncrementerFactory implements DataFieldMaxValueIncrementerFactory {

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

    public EnhancedDataFieldMaxValueIncrementerFactory(DataSource dataSource) {
        this.dataSource = dataSource;
        delegate = new DefaultDataFieldMaxValueIncrementerFactory(dataSource);
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
