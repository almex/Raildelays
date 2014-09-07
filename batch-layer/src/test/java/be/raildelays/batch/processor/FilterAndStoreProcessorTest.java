package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Language;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Train;
import be.raildelays.test.RaildelaysTestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.Date;

public class FilterAndStoreProcessorTest {

    /*
     * The System Under Test.
     */
    private FilterAndStoreProcessor processor;

    private static String KEY_NAME = "key";

    private static Language language = Language.EN;

    private JobExecution jobExecution;

    private BatchExcelRow input;

    @Before
    public void setUp() throws Exception {
        Train train = new Train("dummy");

        jobExecution = MetaDataInstanceFactory.createJobExecution();

        processor = new FilterAndStoreProcessor();
        processor.setKeyName(KEY_NAME);
        processor.setThreshold(60L);
        processor.beforeJob(jobExecution);

        input = new BatchExcelRow.Builder(new Date(), Sens.DEPARTURE)
                .effectiveTrain1(RaildelaysTestUtils.generateTrain("dummy", Language.EN))
                .delay(61L)
                .build();
    }

    @Test
    public void testThatItemIsFiltered() throws Exception {
        Assert.assertNull(processor.process(input));
    }

    @Test
    public void testThatItemIsNotFiltered() throws Exception {
        input.setDelay(59L);

        Assert.assertNotNull(processor.process(input));
    }

    @Test
    public void testThatTrainIdIsInTheContext() throws Exception {
        processor.process(input);
        Assert.assertNotNull(jobExecution.getExecutionContext().getLong(KEY_NAME));
    }
}