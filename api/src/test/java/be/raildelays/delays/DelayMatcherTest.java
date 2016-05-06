package be.raildelays.delays;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalTime;

import static be.raildelays.delays.DelayMatcher.*;

/**
 * @author Almex
 */
public class DelayMatcherTest {

    @Test
    public void durationBetweenTwoDefaultTimestampDelayIsEqualToZero() {
        final TimeDelay timeDelay = TimeDelay.now();

        Assert.assertTrue(duration(between(timeDelay).and(timeDelay), is(equalsTo(0L))));
        Assert.assertFalse(duration(between(timeDelay).and(timeDelay), is(equalsTo(1L))));
    }

    @Test
    public void durationBetweenTwoDefaultTimestampDelayIsGreaterThanZero() {
        final TimeDelay timeDelay = TimeDelay.now();
        final TimeDelay timeDelay2 = timeDelay.withDelay(1L);

        Assert.assertTrue(duration(between(timeDelay).and(timeDelay2), is(greaterThan(0L))));
        Assert.assertFalse(duration(between(timeDelay).and(timeDelay2), is(greaterThan(1L))));
    }

    @Test
    public void durationBetweenDateAndTimestampDelayIsEqualToZero() {
        final LocalTime time = LocalTime.now();
        final TimeDelay timeDelay = TimeDelay.of(time);

        Assert.assertTrue(duration(between(timeDelay).and(time), is(equalsTo(0L))));
        Assert.assertTrue(duration(between(time).and(timeDelay), is(equalsTo(0L))));
    }
}
