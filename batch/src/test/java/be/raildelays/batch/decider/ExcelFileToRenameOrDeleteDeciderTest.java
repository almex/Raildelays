package be.raildelays.batch.decider;

import be.raildelays.batch.AbstractFileTest;
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
import org.springframework.core.io.FileSystemResource;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@RunWith(BlockJUnit4ClassRunner.class)
public class ExcelFileToRenameOrDeleteDeciderTest extends AbstractFileTest {

    private static final LocalDate NOW = LocalDate.now();
    private ExcelFileToRenameOrDeleteDecider decider;
    private JobExecution jobExecution;
    private StepExecution stepExecution;

    @Before
    public void setUp() throws Exception {
        jobExecution = MetaDataInstanceFactory.createJobExecution();
        stepExecution = MetaDataInstanceFactory.createStepExecution();
        decider = new ExcelFileToRenameOrDeleteDecider();
        decider.setContextKey("foo");
        decider.setFileNamePrefix("bar");
        decider.setDirectory(new FileSystemResource(CURRENT_PATH + EXCEL_FILE_NAME));
    }

    @Test
    public void testRename() throws Exception {
        decider.setReader(new ItemStreamItemReaderDelegator<>(new IteratorItemReader<>(Arrays.asList(
                new ExcelRow.Builder(NOW.minus(1, ChronoUnit.MONTHS) // 1 month before Now
                        , Sens.ARRIVAL)
                        .build(false),
                new ExcelRow.Builder(null, null) // To test null value
                        .build(false),
                new ExcelRow.Builder(NOW
                        .plus(1, ChronoUnit.MONTHS) // 1 month after Now
                        , Sens.ARRIVAL)
                        .build(false)
        ))));

        FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);

        Assert.assertEquals(new FlowExecutionStatus(ExcelFileToRenameOrDeleteDecider.RENAME.getExitCode()), status);
    }

    @Test
    public void testDelete() throws Exception {
        decider.setReader(new ItemStreamItemReaderDelegator<>(new IteratorItemReader<>(Arrays.asList(
                new ExcelRow.Builder(null, null) // To test null value
                        .build(false),
                new ExcelRow.Builder(NOW
                        .minus(1, ChronoUnit.MONTHS) // 1 month before Now
                        , Sens.ARRIVAL)
                        .build(false),
                new ExcelRow.Builder(NOW
                        .plus(1, ChronoUnit.MONTHS) // 1 month after Now
                        , Sens.ARRIVAL)
                        .build(false)
        ))));

        FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);

        Assert.assertEquals(new FlowExecutionStatus(ExcelFileToRenameOrDeleteDecider.DELETE.getExitCode()), status);

    }
}