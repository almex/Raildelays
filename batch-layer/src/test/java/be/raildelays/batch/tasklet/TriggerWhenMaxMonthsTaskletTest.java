package be.raildelays.batch.tasklet;

import be.raildelays.domain.Sens;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@RunWith(BlockJUnit4ClassRunner.class)
public class TriggerWhenMaxMonthsTaskletTest {

    private TriggerWhenMaxMonthsTasklet tasklet;

    @Before
    public void setUp() throws ParseException {
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
        tasklet = new TriggerWhenMaxMonthsTasklet();
        tasklet.setReader(new IteratorItemReader<>(Arrays.asList(new ExcelRow
                        .Builder(formater.parse("01/01/2000"), Sens.ARRIVAL)
                        .build(false),
                new ExcelRow
                        .Builder(formater.parse("01/07/2000"), Sens.ARRIVAL)
                        .build(false))));
        tasklet.setMaxNumberOfMonth(6);
        tasklet.setKeyName("foo");
    }

    @Test
    public void testExecute() {
        final StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        RepeatStatus status = new RepeatTemplate().iterate(new RepeatCallback() {
            @Override
            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                return tasklet.execute(new StepContribution(stepExecution),
                        new ChunkContext(new StepContext(stepExecution)));
            }
        });

        Assert.assertEquals(RepeatStatus.FINISHED, status);
    }


    @Test
    public void testExecutionContext() {
        final StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        RepeatStatus status = new RepeatTemplate().iterate(new RepeatCallback() {
            @Override
            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                return tasklet.execute(new StepContribution(stepExecution),
                        new ChunkContext(new StepContext(stepExecution)));
            }
        });

        Assert.assertTrue(stepExecution.getExecutionContext().containsKey("foo"));
    }
}
