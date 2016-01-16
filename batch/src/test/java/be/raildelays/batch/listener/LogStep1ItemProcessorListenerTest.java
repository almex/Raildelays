package be.raildelays.batch.listener;

import be.raildelays.delays.TimeDelay;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import org.apache.logging.log4j.junit.LoggerContextRule;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.time.LocalDate;

public class LogStep1ItemProcessorListenerTest {

    @ClassRule
    public static LoggerContextRule init = new LoggerContextRule("log4j2.xml");
    private LogStep1ItemProcessorListener listener;
    private ListAppender appender;

    @Before
    public void setUp() throws Exception {
        listener = new LogStep1ItemProcessorListener();
        /**
         * We retrieve the Appender in order to express some assertion on it
         */
        appender = init.getListAppender("List");
    }

    @Test
    public void testInfoInputTwoDirections() throws Exception {
        listener.beforeProcess(new LineStop
                .Builder()
                .date(LocalDate.now())
                .station(new Station("foo"))
                .train(new be.raildelays.domain.entities.Train("bar"))
                .departureTime(TimeDelay.now())
                .arrivalTime(TimeDelay.now().withDelay(15L))
                .build());

        Assert.assertTrue(
                appender.getMessages()
                        .stream()
                        .allMatch(message -> message != null)
        );
    }

    @Test
    public void testInfoInputObject() throws Exception {
        listener.beforeProcess(new Object());

        Assert.assertTrue(
                appender.getMessages()
                        .stream()
                        .allMatch(message -> message == null)
        );
    }

    @Test
    public void testInfoOutputLineStop() throws Exception {
        listener.afterProcess(null, new LineStop
                        .Builder()
                        .date(LocalDate.now())
                        .station(new Station("foo"))
                        .train(new be.raildelays.domain.entities.Train("bar"))
                        .departureTime(TimeDelay.now())
                        .arrivalTime(TimeDelay.now().withDelay(15L))
                        .build()
        );

        Assert.assertTrue(
                appender.getMessages()
                        .stream()
                        .allMatch(message -> message != null)
        );
    }

    @Test
    public void testInfoOutputObject() throws Exception {
        listener.afterProcess(null, new Object());

        Assert.assertTrue(
                appender.getMessages()
                        .stream()
                        .allMatch(message -> message == null)
        );
    }
}
