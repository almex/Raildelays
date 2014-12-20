package be.raildelays.batch.tasklet;

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
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

@RunWith(BlockJUnit4ClassRunner.class)
public class MaxMonthsDeciderTest {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    // The S.U.T.
    private MaxMonthsDecider decider;
    private JobExecution jobExecution;
    private StepExecution stepExecution;

    @Before
    public void setUp() throws ParseException {
        jobExecution = MetaDataInstanceFactory.createJobExecution();
        stepExecution = MetaDataInstanceFactory.createStepExecution();
        decider = new MaxMonthsDecider();
        decider.setMaxNumberOfMonth(6);
    }

    @Test
    public void testCompleted() throws ParseException {
        decider.setReader(new IteratorItemReader<>(Arrays.asList(
                new ExcelRow
                        .Builder(FORMAT.parse("01/01/2000"), Sens.ARRIVAL)
                        .build(false),
                new ExcelRow
                        .Builder(FORMAT.parse("31/01/2000"), Sens.ARRIVAL)
                        .build(false)
        )));

        FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);

        Assert.assertEquals(FlowExecutionStatus.COMPLETED, status);
    }


    @Test
    public void testCompletedWithMaxMonths() throws ParseException {
        decider.setReader(new IteratorItemReader<>(Arrays.asList(
                new ExcelRow
                        .Builder(FORMAT.parse("01/01/2000"), Sens.ARRIVAL)
                        .build(false),
                new ExcelRow
                        .Builder(FORMAT.parse("01/07/2000"), Sens.ARRIVAL)
                        .build(false)
        )));

        FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);

        Assert.assertEquals(MaxMonthsDecider.COMPLETED_WITH_MAX_MONTHS, status);
    }
}
