package be.raildelays.delays;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;


public class UtilsDelayTest {

    @Test
    public void testCompareTimeAndDelay() throws Exception {
        Date departureA = new Date();
        TimestampDelay departureB = TimestampDelay.of(departureA, 0L);

        Assert.assertThat(UtilsDelay.compareTimeAndDelay(departureA, departureB), is(equalTo(0L)));
    }

    @Test
    public void testCompareTimeAndDelayEquals() throws Exception {
        Date departureA = new SimpleDateFormat("hh:mm").parse("15:05");
        TimestampDelay departureB = TimestampDelay.of(new SimpleDateFormat("hh:mm").parse("15:00"), 5L * 60 * 1000);

        Assert.assertThat(UtilsDelay.compareTimeAndDelay(departureA, departureB), is(equalTo(0L)));
    }

    @Test
    public void testRevertedCompareTimeAndDelayEquals() throws Exception {
        Date departureA = new SimpleDateFormat("hh:mm").parse("15:05");
        TimestampDelay departureB = TimestampDelay.of(new SimpleDateFormat("hh:mm").parse("15:00"), 5L * 60 * 1000);

        Assert.assertThat(UtilsDelay.compareTimeAndDelay(departureB, departureA), is(equalTo(0L)));
    }

    @Test
    public void testCompareTimeAndNullDelay() throws Exception {
        Date departureA = new Date();
        TimestampDelay departureB = TimestampDelay.of(departureA, null);

        Assert.assertThat(UtilsDelay.compareTimeAndDelay(departureA, departureB), is(equalTo(0L)));
    }

    @Test
    public void testCompareNullTimeAndDelay() throws Exception {
        Date departureA = new Date();
        TimestampDelay departureB = TimestampDelay.of(null, 5L);

        Assert.assertThat(UtilsDelay.compareTimeAndDelay(departureA, departureB), is(greaterThan(0L)));
    }

    @Test
    public void testCompareTimeAndDelayGreater() throws Exception {
        Date departureA = new SimpleDateFormat("hh:mm").parse("15:00");
        TimestampDelay departureB = TimestampDelay.of(new SimpleDateFormat("hh:mm").parse("14:00"), 0L);

        Assert.assertThat(UtilsDelay.compareTimeAndDelay(departureA, departureB), is(greaterThan(0L)));
    }

    @Test
    public void testCompareTimeAndDelayLess() throws Exception {
        Date departureA = new SimpleDateFormat("hh:mm").parse("15:00");
        TimestampDelay departureB = TimestampDelay.of(departureA, 5L);

        Assert.assertThat(UtilsDelay.compareTimeAndDelay(departureA, departureB), is(lessThan(0L)));
    }

    @Test
    public void testCompareTimeWithoutDelaysEquals() throws Exception {
        Date departureA = new SimpleDateFormat("hh:mm").parse("15:05");
        TimestampDelay departureB = TimestampDelay.of(new SimpleDateFormat("hh:mm").parse("15:00"), 5L * 60 * 1000);

        Assert.assertThat(UtilsDelay.compareTimeAndDelay(departureA, departureB), is(equalTo(0L)));
    }

    @Test
    public void testCompareTime() throws Exception {
        Date departureA = new Date();
        TimestampDelay departureB = TimestampDelay.of(departureA, 0L);

        Assert.assertThat(UtilsDelay.compareTime(departureA, departureB), is(equalTo(0L)));
    }

    @Test
    public void testRevertedCompareTimeGreater() throws Exception {
        Date departureA = new SimpleDateFormat("hh:mm").parse("14:00");
        TimestampDelay departureB = TimestampDelay.of(new SimpleDateFormat("hh:mm").parse("15:00"), 0L);

        Assert.assertThat(UtilsDelay.compareTime(departureB, departureA), is(greaterThan(0L)));
    }

    @Test
    public void testCompareNullTime() throws Exception {
        Date departureA = new Date();
        TimestampDelay departureB = TimestampDelay.of(null, 0L);

        Assert.assertThat(UtilsDelay.compareTime(departureA, departureB), is(greaterThan(0L)));
    }

    @Test
    public void testCompareTimeEquals() throws Exception {
        TimestampDelay departureA = TimestampDelay.of(new SimpleDateFormat("hh:mm").parse("15:00"), 15L);
        TimestampDelay departureB = TimestampDelay.of(new SimpleDateFormat("hh:mm").parse("15:00"), 5L);

        Assert.assertThat(UtilsDelay.compareTime(departureA, departureB), is(equalTo(0L)));
    }

    @Test
    public void testCompareTimeGreater() throws Exception {
        Date departureA = new SimpleDateFormat("hh:mm").parse("15:00");
        TimestampDelay departureB = TimestampDelay.of(new SimpleDateFormat("hh:mm").parse("14:00"), 0L);

        Assert.assertThat(UtilsDelay.compareTime(departureA, departureB), is(greaterThan(0L)));
    }

    @Test
    public void testCompareTimeLess() throws Exception {
        Date departureA = new SimpleDateFormat("hh:mm").parse("14:00");
        TimestampDelay departureB = TimestampDelay.of(new SimpleDateFormat("hh:mm").parse("15:00"), 0L);

        Assert.assertThat(UtilsDelay.compareTime(departureA, departureB), is(lessThan(0L)));
    }
}