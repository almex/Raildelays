package be.raildelays.batch.reader;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.service.RaildelaysService;
import org.easymock.EasyMock;
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
    private ByTrainIdAndDateLineStopReader reader;
    private Date now;
    private ExecutionContext context;

    private RaildelaysService service;

    private LineStop expected;

    @Before
    public void setUp() throws Exception {
        now = new Date();
        context = MetaDataInstanceFactory.createStepExecution()
                .getExecutionContext();
        reader = new ByTrainIdAndDateLineStopReader();
        service = EasyMock.createMock(RaildelaysService.class);
        expected = new LineStop.Builder()
                .id(1l)
                .train(new Train("bar"))
                .date(now)
                .station(new Station("bar"))
                .build();
        reader.setDate(now);
        reader.setService(service);
        reader.setTrainId(TRAIN_ID);
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
        EasyMock.replay(service);

        reader.read();
        assertEquals(null, reader.read());


        EasyMock.verify(service);
    }

    @Test
    public void testEmptyContext() throws Exception {
        EasyMock.expect(service.searchLineStopByTrain(null, now)).andReturn(null);
        EasyMock.replay(service);

        reader.setTrainId(null);

        assertEquals(null, reader.read());

        EasyMock.verify(service);
    }
}