package be.raildelays.batch.decider;

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
import org.springframework.batch.item.support.ItemStreamItemReaderDelegator;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * @author Almex
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class MaxMonthsDeciderTest {

    private static final LocalDate NOW = LocalDate.now();
    private static final FlowExecutionStatus COMPLETED_WITH_MAX_MONTHS =
            new FlowExecutionStatus(MaxMonthsDecider.COMPLETED_WITH_MAX_MONTHS.getExitCode());
    private MaxMonthsDecider decider;
    private JobExecution jobExecution;
    private StepExecution stepExecution;

    @Before
    public void setUp() throws Exception {
        jobExecution = MetaDataInstanceFactory.createJobExecution();
        stepExecution = MetaDataInstanceFactory.createStepExecution();
        decider = new MaxMonthsDecider();
        decider.setMaxNumberOfMonth(6);
    }

    /**
     * We only expect that it decide COMPLETED.
     */
    @Test
    public void testCompleted() throws Exception {
        decider.setReader(new ItemStreamItemReaderDelegator<>(new IteratorItemReader<>(Arrays.asList(
                new ExcelRow
                        .Builder(NOW.minus(1, ChronoUnit.MONTHS), Sens.ARRIVAL) // 1 month before Now
                        .build(false),
                new ExcelRow
                        .Builder(null, Sens.ARRIVAL) // To test null value
                        .build(false),
                new ExcelRow
                        .Builder(NOW.plus(1, ChronoUnit.MONTHS), Sens.ARRIVAL) // 1 month after Now
                        .build(false)
        ))));
        decider.afterPropertiesSet();

        FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);

        Assert.assertEquals(FlowExecutionStatus.COMPLETED, status);
    }


    /**
     * We expect to get COMPLETED_WITH_MAX_MONTHS and 'threshold.date' in the stepExecutionContext.
     */
    @Test
    public void testCompletedWithMaxMonths() throws Exception {
        decider.setReader(new ItemStreamItemReaderDelegator<>(new IteratorItemReader<>(Arrays.asList(
                new ExcelRow
                        .Builder(NOW.minus(6, ChronoUnit.MONTHS), Sens.ARRIVAL) // 6 month before Now
                        .build(false),
                new ExcelRow
                        .Builder(null, Sens.ARRIVAL) // To test null value
                        .build(false),
                new ExcelRow
                        .Builder(NOW.plus(2, ChronoUnit.MONTHS), Sens.ARRIVAL) // 2 month after Now
                        .build(false)
        ))));
        decider.afterPropertiesSet();

        FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);

        Assert.assertTrue(stepExecution.getExecutionContext()
                .entrySet()
                .stream()
                .anyMatch(entry -> entry.getKey().equals("threshold.date")));
        Assert.assertEquals(COMPLETED_WITH_MAX_MONTHS, status);
    }
}
