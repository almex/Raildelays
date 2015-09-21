package org.springframework.batch.database.support;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;
import org.springframework.batch.support.DatabaseType;

import javax.sql.DataSource;

/**
 * @author Almex
 */
public class DefaultSequenceMaxValueIncrementerFactoryTest {

    private DefaultSequenceMaxValueIncrementerFactory sequenceMaxValueIncrementerFactory;
    private DataSource dataSource;

    @Before
    public void setUp() throws Exception {
        dataSource = EasyMock.createMock(DataSource.class);

        sequenceMaxValueIncrementerFactory = new DefaultSequenceMaxValueIncrementerFactory(dataSource);
        sequenceMaxValueIncrementerFactory.setIncrementerColumnName("foo");
    }

    @Test
    public void testGetIncrementer() throws Exception {
        for (DatabaseType type : DatabaseType.values()) {
            Assert.assertNotNull(sequenceMaxValueIncrementerFactory.getIncrementer(type.name(), "bar"));
        }
    }

    @Test
    public void testIsSupportedIncrementerType() throws Exception {
        for (DatabaseType type : DatabaseType.values()) {
            Assert.assertTrue(sequenceMaxValueIncrementerFactory.isSupportedIncrementerType(type.name()));
        }
    }

    @Test
    public void testGetSupportedIncrementerTypes() throws Exception {
        String[] actual = sequenceMaxValueIncrementerFactory.getSupportedIncrementerTypes();
        String[] expected = new DefaultDataFieldMaxValueIncrementerFactory(dataSource).getSupportedIncrementerTypes();

        Assert.assertArrayEquals(expected, actual);
    }
}