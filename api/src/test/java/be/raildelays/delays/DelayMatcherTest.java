package be.raildelays.delays;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static be.raildelays.delays.DelayMatcher.*;

public class DelayMatcherTest {

    @Test
    public void differenceBetweenTwoDefaultTimestampDelayIsEqualToZero() {
        final TimestampDelay timestampDelay = new TimestampDelay();

        Assert.assertTrue(difference(between(timestampDelay).and(timestampDelay), is(equalTo(0L))));
        Assert.assertFalse(difference(between(timestampDelay).and(timestampDelay), is(equalTo(1L))));
    }

    @Test
    public void differenceBetweenDateAndTimestampDelayIsEqualToZero() {
        final Date date = new Date();
        final TimestampDelay timestampDelay = new TimestampDelay(date);

        Assert.assertTrue(difference(between(timestampDelay).and(date), is(equalTo(0L))));
        Assert.assertTrue(difference(between(date).and(timestampDelay), is(equalTo(0L))));
    }
}
