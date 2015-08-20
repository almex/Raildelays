package be.raildelays.delays;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeDelayTest {

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

        Assert.assertEquals(localTime, timeDelay.toLocalTime());
    }

    @Test
    public void testToDate() throws Exception {
        Date date = new Date();
        LocalTime time = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalTime();
        TimeDelay timeDelay = TimeDelay.of(time);

        Assert.assertEquals(date, timeDelay.toDate());
    }

    @Test
    public void testFromTimeDelay() throws Exception {
        TimeDelay now = TimeDelay.of(LocalTime.now(), 15L);
        TimeDelay timestamp = TimeDelay.from(now);

        Assert.assertEquals(15L, timestamp.getDelay());
    }
}