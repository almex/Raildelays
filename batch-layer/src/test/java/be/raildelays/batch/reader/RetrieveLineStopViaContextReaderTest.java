package be.raildelays.batch.reader;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.service.RaildelaysService;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class RetrieveLineStopViaContextReaderTest {

    private static String KEY_NAME = "foo";
    private static Long TRAIN_ID = 1L;
    private RetrieveLineStopViaContextReader reader;
    private Date now;
    private ExecutionContext context;

    private RaildelaysService service;

    private LineStop expected;

    @Before
    public void setUp() throws Exception {
        now = new Date();
        context = MetaDataInstanceFactory.createStepExecution()
                .getExecutionContext();
        reader = new RetrieveLineStopViaContextReader();
        service = EasyMock.createMock(RaildelaysService.class);
        expected = new LineStop.Builder()
                .id(1l)
                .train(new Train("bar"))
                .date(now)
                .station(new Station("bar"))
                .build();
        reader.setDate(now);
        reader.setKeyName(KEY_NAME);
        reader.setService(service);

        context.putLong(KEY_NAME, TRAIN_ID);

        JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution();
        jobExecution.setExecutionContext(context);
        reader.beforeJob(jobExecution);
    }

    @Test
    public void testOneRead() throws Exception {
        EasyMock.expect(service.searchLineStopByTrain(TRAIN_ID, now)).andReturn(expected);
        EasyMock.replay(service);

        assertEquals(expected, reader.read());

        EasyMock.verify(service);
    }

    @Test
    public void testTwoRead() throws Exception {
        EasyMock.expect(service.searchLineStopByTrain(TRAIN_ID, now)).andReturn(expected);
        EasyMock.expect(service.searchLineStopByTrain(null, now)).andReturn(null);
        EasyMock.replay(service);

        reader.read();
        assertEquals(null, reader.read());


        EasyMock.verify(service);
    }

    @Test
    public void testEmptyContext() throws Exception {
        EasyMock.expect(service.searchLineStopByTrain(null, now)).andReturn(null);
        EasyMock.replay(service);

        context.remove(KEY_NAME);
        assertEquals(null, reader.read());

        EasyMock.verify(service);
    }
}