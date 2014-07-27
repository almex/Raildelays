package be.raildelays.delays;

import org.junit.Assert;
import org.junit.Test;

public class TimestampDelayTest {

    @Test
    public void testGetExpected() throws Exception {
        TimestampDelay timestamp = new TimestampDelay();

        Assert.assertNotNull(timestamp.getExpected());
    }

    @Test
    public void testGetDelay() throws Exception {
        TimestampDelay timestamp = new TimestampDelay();

        Assert.assertEquals(0L, (long) timestamp.getDelay());
    }
}