package be.raildelays.delays;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static be.raildelays.delays.DelayMatcher.*;

public class DelayMatcherTest {

    @Test
    public void durationBetweenTwoDefaultTimestampDelayIsEqualToZero() {
        final TimestampDelay timestampDelay = TimestampDelay.now();

        Assert.assertTrue(duration(between(timestampDelay).and(timestampDelay), is(equalTo(0L))));
        Assert.assertFalse(duration(between(timestampDelay).and(timestampDelay), is(equalTo(1L))));
    }

    @Test
    public void durationBetweenTwoDefaultTimestampDelayIsGreaterThanZero() {
        final TimestampDelay timestampDelay = TimestampDelay.now();
        final TimestampDelay timestampDelay2 = TimestampDelay.from(timestampDelay, 1L);

        Assert.assertTrue(duration(between(timestampDelay).and(timestampDelay2), is(greaterThan(0L))));
        Assert.assertFalse(duration(between(timestampDelay).and(timestampDelay2), is(greaterThan(1L))));
    }

    @Test
    public void durationBetweenDateAndTimestampDelayIsEqualToZero() {
        final Date date = new Date();
        final TimestampDelay timestampDelay = TimestampDelay.of(date);

        Assert.assertTrue(duration(between(timestampDelay).and(date), is(equalTo(0L))));
        Assert.assertTrue(duration(between(date).and(timestampDelay), is(equalTo(0L))));
    }
}
