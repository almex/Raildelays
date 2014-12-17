package be.raildelays.batch.processor;

import be.raildelays.domain.Sens;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class FilterItemInContextProcessorTest {

    private FilterItemInContextProcessor processor;

    private ExecutionContext context;

    private static final String KEY_NAME = "foo";

    @Before
    public void setUp() throws Exception {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        context = stepExecution.getExecutionContext();
        processor = new FilterItemInContextProcessor();
        processor.setKeyName(KEY_NAME);
        processor.beforeStep(stepExecution);
    }

    @Test
    public void testFiltered() throws Exception {
        context.put(KEY_NAME, Collections.singletonMap(Sens.ARRIVAL,
                new ExcelRow.Builder(new Date(), Sens.ARRIVAL)
                        .build(false)));

        processor.setComparator(new Comparator<ExcelRow>() {
            @Override
            public int compare(ExcelRow o1, ExcelRow o2) {
                return 0;
            }
        });

        Assert.assertNull(processor.process(new ExcelRow.Builder(new Date(), Sens.DEPARTURE)
                .build(false)));
    }

    @Test
    public void testNotFiltered() throws Exception {
        context.put(KEY_NAME, Collections.singletonMap(Sens.ARRIVAL,
                new ExcelRow.Builder(new Date(), Sens.ARRIVAL)
                        .build(false)));

        processor.setComparator(new Comparator<ExcelRow>() {
            @Override
            public int compare(ExcelRow o1, ExcelRow o2) {
                return -1;
            }
        });

        Assert.assertNotNull(processor.process(new ExcelRow.Builder(new Date(), Sens.DEPARTURE)
                .build(false)));
    }
}