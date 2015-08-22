package be.raildelays.delays;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class TimeDelayTest {

    @Test
    public void testOf1() throws Exception {
        TimeDelay timeDelay = TimeDelay.of(LocalTime.parse("18:00"));

        Assert.assertEquals(LocalTime.parse("18:00"), timeDelay.getEffectiveTime());
    }

    @Test
    public void testOf2() throws Exception {
        TimeDelay timeDelay = TimeDelay.of(LocalTime.parse("18:00"), 30L * 1000 * 60);

        Assert.assertEquals(LocalTime.parse("18:30"), timeDelay.getEffectiveTime());
    }

    @Test
    public void testOf3() throws Exception {
        TimeDelay timeDelay = TimeDelay.of(LocalTime.parse("18:00"), 30L, ChronoUnit.MINUTES);

        Assert.assertEquals(LocalTime.parse("18:30"), timeDelay.getEffectiveTime());
    }

    @Test
    public void testComputeFrom() {
        LocalTime expectedTime = LocalTime.parse("18:00");
        LocalTime effectiveTime = LocalTime.parse("18:30");
        TimeDelay expected = TimeDelay.of(LocalTime.parse("18:00"), 30L, ChronoUnit.MINUTES);
        TimeDelay actual = expected.computeFrom(expectedTime, effectiveTime);

        Assert.assertEquals(expected, actual);
        Assert.assertEquals(expectedTime, actual.getExpectedTime());
        Assert.assertEquals(effectiveTime, actual.getEffectiveTime());
    }

    @Test
    public void testCompareToNotEquals() throws Exception {
        TimeDelay timeDelay1 = TimeDelay.of(LocalTime.parse("18:00"), 30L);
        TimeDelay timeDelay2 = TimeDelay.of(LocalTime.parse("18:00"), 45L);

        Assert.assertThat(timeDelay1.compareTo(timeDelay2), is(equalTo(-1)));
        Assert.assertThat(timeDelay2.compareTo(timeDelay1), is(equalTo(1)));
    }

    @Test
    public void testCompareToEquals() throws Exception {
        TimeDelay timeDelay1 = TimeDelay.of(LocalTime.parse("18:00"), 30L, ChronoUnit.MINUTES);
        TimeDelay timeDelay2 = TimeDelay.of(LocalTime.parse("18:30"));

        Assert.assertThat(timeDelay1.compareTo(timeDelay2), is(equalTo(0)));
        Assert.assertEquals(timeDelay2.getEffectiveTime(), timeDelay1.getEffectiveTime());
    }

    @Test
    public void testIsBefore() throws Exception {
        TimeDelay timeDelay1 = TimeDelay.of(LocalTime.parse("18:00"), 30L, ChronoUnit.MINUTES);
        TimeDelay timeDelay2 = TimeDelay.of(LocalTime.parse("18:00"), 45L, ChronoUnit.MINUTES);

        Assert.assertTrue(timeDelay1.isBefore(timeDelay2));
        Assert.assertFalse(timeDelay2.isBefore(timeDelay1));
    }

    @Test
    public void testIsAfter() throws Exception {
        TimeDelay timeDelay1 = TimeDelay.of(LocalTime.parse("18:00"), 30L, ChronoUnit.MINUTES);
        TimeDelay timeDelay2 = TimeDelay.of(LocalTime.parse("18:00"), 45L, ChronoUnit.MINUTES);

        Assert.assertFalse(timeDelay1.isAfter(timeDelay2));
        Assert.assertTrue(timeDelay2.isAfter(timeDelay1));
    }

    @Test
    public void testGetExpectedTime() throws Exception {
        LocalTime time = LocalTime.now();
        TimeDelay timeDelay = TimeDelay.of(time);

        Assert.assertEquals(time, timeDelay.getExpectedTime());
    }

    @Test
    public void testGetDelay() throws Exception {
        TimeDelay timestamp = TimeDelay.now();

        Assert.assertEquals(0L, timestamp.getDelay());
    }

    @Test
    public void testToLocalTime() throws Exception {
        LocalTime localTime = LocalTime.now();
        TimeDelay timeDelay = TimeDelay.of(localTime);

        Assert.assertEquals(localTime, timeDelay.getEffectiveTime());
    }

    @Test
    public void testAtDate() throws Exception {
        LocalDateTime dateTime = LocalDateTime.now();
        LocalTime time = dateTime.toLocalTime();
        TimeDelay timeDelay = TimeDelay.of(time);

        Assert.assertEquals(dateTime, timeDelay.atDate(dateTime.toLocalDate()));
    }

    @Test
    public void testFromTimeDelay() throws Exception {
        TimeDelay now = TimeDelay.of(LocalTime.now(), 15L);
        TimeDelay timestamp = TimeDelay.from(now);

        Assert.assertEquals(15L, timestamp.getDelay());
    }
}