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
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.batch.test.ItemStreamItemReaderDelegator;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@RunWith(BlockJUnit4ClassRunner.class)
public class MaxMonthsDeciderTest {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final LocalDate NOW = LocalDate.now();
    // The S.U.T.
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

    @Test
    public void testCompleted() throws Exception {
        decider.setReader(new ItemStreamItemReaderDelegator<>(new IteratorItemReader<>(Arrays.asList(
                new ExcelRow.Builder(NOW.minus(1, ChronoUnit.MONTHS) // 1 month before Now
                        , Sens.ARRIVAL)
                        .build(false),
                new ExcelRow.Builder(null, Sens.ARRIVAL) // To test null value
                        .build(false),
                new ExcelRow.Builder(NOW.plus(1, ChronoUnit.MONTHS) // 1 month after Now
                        , Sens.ARRIVAL)
                        .build(false)
        ))));
        decider.afterPropertiesSet();

        FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);

        Assert.assertEquals(FlowExecutionStatus.COMPLETED, status);
    }


    @Test
    public void testCompletedWithMaxMonths() throws Exception {
        decider.setReader(new ItemStreamItemReaderDelegator<>(new IteratorItemReader<>(Arrays.asList(
                new ExcelRow.Builder(NOW.minus(6, ChronoUnit.MONTHS) // 6 month before Now
                        , Sens.ARRIVAL)
                        .build(false),
                new ExcelRow.Builder(null, Sens.ARRIVAL) // To test null value
                        .build(false),
                new ExcelRow.Builder(NOW.plus(2, ChronoUnit.MONTHS) // 2 month after Now
                        , Sens.ARRIVAL)
                        .build(false)
        ))));
        decider.afterPropertiesSet();

        FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);

        Assert.assertTrue(stepExecution.getExecutionContext().entrySet().stream().anyMatch(stringObjectEntry -> stringObjectEntry.getKey().equals("threshold.date")));
        Assert.assertEquals(new FlowExecutionStatus(MaxMonthsDecider.COMPLETED_WITH_MAX_MONTHS.getExitCode()), status);
    }
}
