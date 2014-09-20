package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Language;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Train;
import be.raildelays.test.RaildelaysTestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.Date;

public class SkipDelayGreaterThanProcessorTest {

    private static String KEY_NAME = "key";
    private static Language language = Language.EN;
    /*
     * The System Under Test.
     */
    private SkipDelayGreaterThanProcessor processor;
    private StepExecution stepbExecution;

    private BatchExcelRow input;

    @Before
    public void setUp() throws Exception {
        stepbExecution = MetaDataInstanceFactory.createStepExecution();

        processor = new SkipDelayGreaterThanProcessor();
        processor.setThreshold(60L);

        input = new BatchExcelRow.Builder(new Date(), Sens.DEPARTURE)
                .expectedTrain1(RaildelaysTestUtils.generateTrain("dummy", Language.EN))
                .build();
    }

    @Test(expected = SkipDelayGreaterThanException.class)
    public void testThatItemIsSkipped() throws Exception {
        input.setDelay(61L);
        processor.process(input);
    }

    @Test
    public void testThatItemIsNotSkipped() throws Exception {
        input.setDelay(59L);

        Assert.assertNotNull(processor.process(input));
    }
}