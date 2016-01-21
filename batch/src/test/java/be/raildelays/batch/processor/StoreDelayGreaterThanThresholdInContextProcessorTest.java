package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.delays.Delays;
import be.raildelays.domain.Language;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.TrainLine;
import be.raildelays.test.RaildelaysTestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.time.LocalDate;
import java.util.Map;

public class StoreDelayGreaterThanThresholdInContextProcessorTest {

    private static String KEY_NAME = "key";
    private static Language language = Language.EN;
    /*
     * The System Under Test.
     */
    private StoreDelayGreaterThanThresholdInContextProcessor processor;
    private StepExecution stepbExecution;

    private BatchExcelRow input;

    @Before
    public void setUp() throws Exception {
        TrainLine trainLine = new TrainLine("dummy");

        stepbExecution = MetaDataInstanceFactory.createStepExecution();

        processor = new StoreDelayGreaterThanThresholdInContextProcessor();
        processor.setKeyName(KEY_NAME);
        processor.setThreshold(60L);
        processor.beforeStep(stepbExecution);
        processor.afterPropertiesSet();

        input = new BatchExcelRow
                .Builder(LocalDate.now(), Sens.DEPARTURE)
                .expectedTrain1(RaildelaysTestUtils.generateTrain("dummy", Language.EN))
                .build(false);
    }

    @Test
    public void testThatItemIsNotFiltered() throws Exception {
        input.setDelay(Delays.toMillis(59L));

        Assert.assertNotNull(processor.process(input));
    }

    @Test
    public void testThatTrainIdIsInTheContext() throws Exception {
        input.setDelay(Delays.toMillis(61L));
        processor.process(input);

        Assert.assertEquals(input, ((Map) stepbExecution.getExecutionContext().get(KEY_NAME)).get(Sens.DEPARTURE));
    }

    @Test
    public void testThat2TrainIdsAreInTheContext() throws Exception {
        processor.process(new BatchExcelRow
                .Builder(LocalDate.now(), Sens.DEPARTURE)
                .expectedTrain1(RaildelaysTestUtils.generateTrain("dummy", Language.EN))
                .delay(Delays.toMillis(75L))
                .build(false));
        processor.process(new BatchExcelRow
                .Builder(LocalDate.now(), Sens.ARRIVAL)
                .expectedTrain1(RaildelaysTestUtils.generateTrain("dummy", Language.EN))
                .delay(Delays.toMillis(66L))
                .build(false));

        Assert.assertNotNull(((Map) stepbExecution.getExecutionContext().get(KEY_NAME)).get(Sens.DEPARTURE));
        Assert.assertNotNull(((Map) stepbExecution.getExecutionContext().get(KEY_NAME)).get(Sens.ARRIVAL));
    }
}