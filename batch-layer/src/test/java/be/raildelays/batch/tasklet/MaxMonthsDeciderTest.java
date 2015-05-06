package be.raildelays.batch.tasklet;

import be.raildelays.batch.decider.MaxMonthsDecider;
import be.raildelays.domain.Sens;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;

@RunWith(BlockJUnit4ClassRunner.class)
public class MaxMonthsDeciderTest {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    // The S.U.T.
    private MaxMonthsDecider decider;
    private JobExecution jobExecution;
    private StepExecution stepExecution;
    private static final LocalDate NOW = LocalDate.now();

    @Before
    public void setUp() throws ParseException {
        jobExecution = MetaDataInstanceFactory.createJobExecution();
        stepExecution = MetaDataInstanceFactory.createStepExecution();
        decider = new MaxMonthsDecider();
        decider.setMaxNumberOfMonth(6);
    }

    private class ItemStreamItemReaderDelegator extends AbstractItemStreamItemReader<ExcelRow> {

        private ItemReader<ExcelRow> delegate;

        public ItemStreamItemReaderDelegator(ItemReader<ExcelRow> delegate) {
            this.delegate = delegate;
        }

        @Override
        public ExcelRow read() throws Exception {
            return delegate.read();
        }
    }

    @Test
    public void testCompleted() throws ParseException {
        decider.setReader(new ItemStreamItemReaderDelegator(new IteratorItemReader<>(Arrays.asList(
                new ExcelRow.Builder(Date.from(NOW
                        .minus(1, ChronoUnit.MONTHS)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant() // 1 month before Now
                ), Sens.ARRIVAL)
                        .build(false),
                new ExcelRow.Builder(null, Sens.ARRIVAL) // To test null value
                        .build(false),
                new ExcelRow.Builder(Date.from(NOW
                        .plus(1, ChronoUnit.MONTHS)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant() // 1 month after Now
                ), Sens.ARRIVAL)
                        .build(false)
        ))));

        FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);

        Assert.assertEquals(FlowExecutionStatus.COMPLETED, status);
    }


    @Test
    public void testCompletedWithMaxMonths() throws ParseException {
        decider.setReader(new ItemStreamItemReaderDelegator(new IteratorItemReader<>(Arrays.asList(
                new ExcelRow.Builder(Date.from(NOW
                        .minus(6, ChronoUnit.MONTHS)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant() // 6 month before Now
                ), Sens.ARRIVAL)
                        .build(false),
                new ExcelRow.Builder(null, Sens.ARRIVAL) // To test null value
                        .build(false),
                new ExcelRow.Builder(Date.from(NOW
                        .plus(2, ChronoUnit.MONTHS)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant() // 2 month after Now
                ), Sens.ARRIVAL)
                        .build(false)
        ))));

        FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);

        Assert.assertEquals(new FlowExecutionStatus(MaxMonthsDecider.COMPLETED_WITH_MAX_MONTHS.getExitCode()), status);
    }
}
