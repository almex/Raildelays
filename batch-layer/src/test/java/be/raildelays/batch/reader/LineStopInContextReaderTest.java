package be.raildelays.batch.reader;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.repository.LineStopDao;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(BlockJUnit4ClassRunner.class)
public class LineStopInContextReaderTest {

    private static Long LINE_STOP_ID = 1L;
    private LineStopInContextReader reader;

    private LineStopDao lineStopDao;

    private LineStop expected;

    @Before
    public void setUp() throws Exception {
        reader = new LineStopInContextReader();
        lineStopDao = EasyMock.createMock(LineStopDao.class);
        expected = new LineStop.Builder()
                .id(LINE_STOP_ID)
                .train(new Train("bar"))
                .date(new Date())
                .station(new Station("bar"))
                .build();
        reader.setLineStopIds(Arrays.asList(new Long[]{LINE_STOP_ID}));
        reader.setLineStopDao(lineStopDao);
        reader.open(MetaDataInstanceFactory.createStepExecution().getExecutionContext());
    }

    @After
    public void teadrDown() {
        reader.close();
    }


    @Test
    public void testOneRead() throws Exception {
        EasyMock.expect(lineStopDao.findOne(LINE_STOP_ID)).andReturn(expected);
        EasyMock.replay(lineStopDao);

        assertEquals(expected, reader.read());

        EasyMock.verify(lineStopDao);
    }

    @Test
    public void testTwoRead() throws Exception {
        EasyMock.expect(lineStopDao.findOne(LINE_STOP_ID)).andReturn(expected);
        EasyMock.replay(lineStopDao);

        reader.read();
        assertEquals(null, reader.read());


        EasyMock.verify(lineStopDao);
    }

    @Test
    public void testEmptyContext() throws Exception {
        EasyMock.expect(lineStopDao.findOne(LINE_STOP_ID)).andReturn(expected);
        EasyMock.replay(lineStopDao);

        reader.setLineStopIds(null);
        reader.open(MetaDataInstanceFactory.createStepExecution().getExecutionContext());

        assertEquals(null, reader.read());
    }
}