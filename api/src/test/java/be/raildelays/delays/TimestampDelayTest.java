package be.raildelays.delays;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

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

    @Test
    public void testToDate() throws Exception {
        Date date = new Date();
        TimestampDelay timestamp = TimestampDelay.of(date);

        Assert.assertEquals(date, timestamp.toDate());
    }
}