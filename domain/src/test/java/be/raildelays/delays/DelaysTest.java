package be.raildelays.delays;

import be.raildelays.delays.Delays;
import be.raildelays.delays.TimeDelay;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;


public class DelaysTest {

    @Test
    public void testCompareTimeAndDelay() throws Exception {
        LocalTime departureA = LocalTime.now();
        TimeDelay departureB = TimeDelay.of(departureA, 0L);

        Assert.assertThat(Delays.compareTimeAndDelay(departureA, departureB), is(equalTo(0L)));
    }

    @Test
    public void testCompareTimeAndDelayWithLeftIsNull() throws Exception {
        TimeDelay departureA = null;
        TimeDelay departureB = TimeDelay.now();

        Assert.assertThat(Delays.compareTimeAndDelay(departureA, departureB), is(lessThan(0L)));
    }

    @Test
    public void testCompareTimeAndDelayWithRightIsNull() throws Exception {
        TimeDelay departureA = TimeDelay.now();
        TimeDelay departureB = null;

        Assert.assertThat(Delays.compareTimeAndDelay(departureA, departureB), is(greaterThan(0L)));
    }

    @Test
    public void testCompareTimeAndDelayEquals() throws Exception {
        LocalTime departureA = LocalTime.parse("15:05");
        TimeDelay departureB = TimeDelay.of(LocalTime.parse("15:00"), 5L, ChronoUnit.MINUTES);

        Assert.assertThat(Delays.compareTimeAndDelay(departureA, departureB), is(equalTo(0L)));
    }

    @Test
    public void testRevertedCompareTimeAndDelayEquals() throws Exception {
        LocalTime departureA = LocalTime.parse("15:05");
        TimeDelay departureB = TimeDelay.of(LocalTime.parse("15:00"), 5L, ChronoUnit.MINUTES);

        Assert.assertThat(Delays.compareTimeAndDelay(departureB, departureA), is(equalTo(0L)));
    }

    @Test
    public void testCompareTimeAndNullDelay() throws Exception {
        LocalTime departureA = LocalTime.now();
        TimeDelay departureB = TimeDelay.of(departureA, null);

        Assert.assertThat(Delays.compareTimeAndDelay(departureA, departureB), is(equalTo(0L)));
    }

    @Test
    public void testCompareNullTimeAndDelay() throws Exception {
        LocalTime departureA = LocalTime.now();
        TimeDelay departureB = TimeDelay.of(null, 5L);

        Assert.assertThat(Delays.compareTimeAndDelay(departureA, departureB), is(greaterThan(0L)));
    }

    @Test
    public void testCompareTimeAndDelayGreater() throws Exception {
        LocalTime departureA = LocalTime.parse("15:00");
        TimeDelay departureB = TimeDelay.of(LocalTime.parse("14:00"), 0L);

        Assert.assertThat(Delays.compareTimeAndDelay(departureA, departureB), is(greaterThan(0L)));
    }

    @Test
    public void testCompareTimeAndDelayLess() throws Exception {
        LocalTime departureA = LocalTime.parse("15:00");
        TimeDelay departureB = TimeDelay.of(departureA, 5L);

        Assert.assertThat(Delays.compareTimeAndDelay(departureA, departureB), is(lessThan(0L)));
    }

    @Test
    public void testCompareTimeWithoutDelaysEquals() throws Exception {
        LocalTime departureA = LocalTime.parse("15:05");
        TimeDelay departureB = TimeDelay.of(LocalTime.parse("15:00"), 5L * 60 * 1000);

        Assert.assertThat(Delays.compareTimeAndDelay(departureA, departureB), is(equalTo(0L)));
    }

    @Test
    public void testCompareTime() throws Exception {
        LocalTime departureA = LocalTime.now();
        TimeDelay departureB = TimeDelay.of(departureA, 15L);

        Assert.assertThat(Delays.compareTime(departureA, departureB), is(equalTo(0L)));
    }

    @Test
    public void testRevertedCompareTimeGreater() throws Exception {
        LocalTime departureA = LocalTime.parse("14:00");
        TimeDelay departureB = TimeDelay.of(LocalTime.parse("15:00"), 15L);

        Assert.assertThat(Delays.compareTime(departureB, departureA), is(equalTo(3600L * 1000)));
    }

    @Test
    public void testCompareNullTime() throws Exception {
        LocalTime departureA = LocalTime.now();
        TimeDelay departureB = TimeDelay.of(null, 0L);

        Assert.assertThat(Delays.compareTime(departureA, departureB), is(greaterThan(0L)));
    }

    @Test
    public void testCompareTimeEquals() throws Exception {
        TimeDelay departureA = TimeDelay.of(LocalTime.parse("15:00"), 15L);
        TimeDelay departureB = TimeDelay.of(LocalTime.parse("15:00"), 5L);

        Assert.assertThat(Delays.compareTime(departureA, departureB), is(equalTo(0L)));
    }

    @Test
    public void testCompareTimeGreater() throws Exception {
        LocalTime departureA = LocalTime.parse("15:00");
        TimeDelay departureB = TimeDelay.of(LocalTime.parse("14:00"), 0L);

        Assert.assertThat(Delays.compareTime(departureA, departureB), is(equalTo(3600L * 1000)));
    }

    @Test
    public void testCompareTimeLess() throws Exception {
        LocalTime departureA = LocalTime.parse("14:00");
        TimeDelay departureB = TimeDelay.of(LocalTime.parse("15:00"), 0L);

        Assert.assertThat(Delays.compareTime(departureA, departureB), is(equalTo(-3600L * 1000)));
    }

    @Test
    public void testCompareTimeWithTwoEqualsDates() throws Exception {
        LocalTime departureA = LocalTime.now();

        Assert.assertThat(Delays.compareTime(departureA, departureA), is(equalTo(0L)));
    }

    @Test
    public void testComputeZeroDelay() throws Exception {
        LocalTime departureA = LocalTime.now();
        LocalTime departureB = departureA;

        Assert.assertThat(Delays.computeDelay(departureA, departureB), is(equalTo(0L)));
    }

    @Test
    public void testComputeNonZeroDelay() throws Exception {
        LocalTime departureA = LocalTime.now();
        LocalTime departureB = TimeDelay.of(departureA, 15L).getEffectiveTime();

        Assert.assertThat(Delays.computeDelay(departureA, departureB), is(equalTo(15L)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToLocalTimeWithSqlDate() {
        Delays.toLocalTime(Date.valueOf("2000-01-01"));
    }

    @Test
    public void testToLocalTimeWithSqlTime() {
        Assert.assertEquals(LocalTime.of(12, 0, 0),
                Delays.toLocalTime(Time.valueOf("12:00:00")));
    }

    @Test
    public void testToLocalTimeWithSqlTimestamp() {
        Assert.assertEquals(LocalTime.of(12, 0, 0),
                Delays.toLocalTime(Timestamp.valueOf("2000-01-01 12:00:00")));
    }

    @Test
    public void testToLocalTimeWithUtilDate() throws ParseException {
        Assert.assertEquals(LocalTime.of(12, 0, 0),
                Delays.toLocalTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2000-01-01 12:00:00")));
    }

    @Test
    public void testToMinutes() {
        Assert.assertEquals(15, (long) Delays.toMinutes(15L * 1000 * 60));
    }

    @Test
    public void testToMillis() {
        Assert.assertEquals(15 * 1000 * 60, (long) Delays.toMillis(15L));
    }
}