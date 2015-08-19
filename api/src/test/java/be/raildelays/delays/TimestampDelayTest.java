package be.raildelays.delays;

import org.junit.Assert;
import org.junit.Test;

public class TimestampDelayTest {

    @Test
    public void testGetExpected() throws Exception {
        TimestampDelay timestamp = TimestampDelay.now();

        Assert.assertNotNull(timestamp.getExpectedTime());
    }

    @Test
    public void testGetDelay() throws Exception {
        TimestampDelay timestamp = TimestampDelay.now();

        Assert.assertEquals(0L, (long) timestamp.getDelay());
    }
}