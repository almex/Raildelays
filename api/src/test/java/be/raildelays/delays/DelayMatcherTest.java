package be.raildelays.delays;

import org.junit.Assert;
import org.junit.Test;

import static be.raildelays.delays.DelayMatcher.*;

public class DelayMatcherTest {

    @Test
    public void differenceBetweenTwoDefaultTimestampDelayIsEqualToZero() {
        Assert.assertTrue(difference(between(new TimestampDelay(), new TimestampDelay()), is(equalTo(0L))));
        Assert.assertFalse(difference(between(new TimestampDelay(), new TimestampDelay()), is(equalTo(1L))));
    }
}