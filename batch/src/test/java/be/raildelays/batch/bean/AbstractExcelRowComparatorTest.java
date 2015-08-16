package be.raildelays.batch.bean;

import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.Comparator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@RunWith(BlockJUnit4ClassRunner.class)
public abstract class AbstractExcelRowComparatorTest {

    protected Comparator<ExcelRow> comparator;


    @Before
    public abstract void setUp();

    @Test
    public final void testSameReference() throws Exception {
        BatchExcelRow row = new BatchExcelRow.Builder(null, null).build(false);

        Assert.assertThat(comparator.compare(row, row), equalTo(0));
    }

    @Test
    public final void testCompareNull() throws Exception {
        Assert.assertThat(comparator.compare(new BatchExcelRow.Builder(null, null)
                                .build(false),
                        null),
                greaterThan(0));
    }
}
