package be.raildelays.delays;

import javafx.beans.binding.Bindings;
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
        Assert.assertTrue(duration(between(timeDelay).and(timeDelay), is(zero())));
        Assert.assertTrue(duration(between(timeDelay).and(timeDelay), is(0L)));
    }

    @Test
    public void durationBetweenTwoTimestampIsBefore() {
        final TimeDelay timeDelay = TimeDelay.now();
        final TimeDelay timeDelay2 = timeDelay.withDelay(1L);

        Assert.assertTrue(duration(between(timeDelay2).and(timeDelay), is(before())));
        Assert.assertFalse(duration(between(timeDelay).and(timeDelay2), is(before())));
    }

    @Test
    public void durationBetweenTwoTimestampIsAfter() {
        final TimeDelay timeDelay = TimeDelay.now();
        final TimeDelay timeDelay2 = timeDelay.withDelay(1L);

        Assert.assertTrue(duration(between(timeDelay).and(timeDelay2), is(after())));
        Assert.assertFalse(duration(between(timeDelay2).and(timeDelay), is(after())));
    }

    @Test
    public void durationBetweenTwoDefaultTimestampDelayIsGreaterThanZero() {
        final TimeDelay timeDelay = TimeDelay.now();
        final TimeDelay timeDelay2 = timeDelay.withDelay(1L);

        Assert.assertTrue(duration(between(timeDelay).and(timeDelay2), is(greaterThan(0L))));
        Assert.assertFalse(duration(between(timeDelay).and(timeDelay2), is(greaterThan(1L))));
    }

    @Test
    public void durationBetweenTwoDefaultTimestampDelayIsGreaterThanOrEqualToZero() {
        final TimeDelay timeDelay = TimeDelay.now();
        final TimeDelay timeDelay2 = timeDelay.withDelay(1L);

        Assert.assertTrue(duration(between(timeDelay).and(timeDelay2), is(greaterThanOrEqual(0L))));
    }

    @Test
    public void durationBetweenTwoDefaultTimestampDelayIsLessThanZero() {
        final TimeDelay timeDelay = TimeDelay.now();
        final TimeDelay timeDelay2 = timeDelay.withDelay(1L);

        Assert.assertTrue(duration(between(timeDelay2).and(timeDelay), is(lessThan(0L))));
        Assert.assertFalse(duration(between(timeDelay2).and(timeDelay), is(lessThan(-1L))));
    }

    @Test
    public void durationBetweenTwoDefaultTimestampDelayIsLessThanOrEqualToZero() {
        final TimeDelay timeDelay = TimeDelay.now();
        final TimeDelay timeDelay2 = timeDelay.withDelay(1L);

        Assert.assertTrue(duration(between(timeDelay2).and(timeDelay), is(lessThanOrEqual(0L))));
    }

    @Test
    public void durationBetweenDateAndTimestampDelayIsEqualToZero() {
        final LocalTime time = LocalTime.now();
        final TimeDelay timeDelay = TimeDelay.of(time);

        Assert.assertTrue(duration(between(timeDelay).and(time), is(equalsTo(0L))));
        Assert.assertTrue(duration(between(time).and(timeDelay), is(equalsTo(0L))));
    }
}
