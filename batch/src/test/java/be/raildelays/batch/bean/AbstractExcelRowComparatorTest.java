package be.raildelays.batch.bean;

import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.Comparator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;

@RunWith(BlockJUnit4ClassRunner.class)
public abstract class AbstractExcelRowComparatorTest {

    protected Comparator<ExcelRow> comparator;


    @Before
    public abstract void setUp() throws Exception;

    @Test
    public final void testSameReference() throws Exception {
        BatchExcelRow row = new BatchExcelRow.Builder(null, null).build(false);

        Assert.assertThat(comparator.compare(row, row), equalTo(0));
    }

    @Test
    public final void testCompareNull() throws Exception {
        Assert.assertThat(comparator.compare(null, null), is(equalTo(0)));
    }
}
