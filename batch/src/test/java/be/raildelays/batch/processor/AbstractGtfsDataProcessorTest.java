package be.raildelays.batch.processor;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.item.ItemStreamReader;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertNotNull;

/**
 * @author Almex
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class AbstractGtfsDataProcessorTest {

    private ItemStreamReader<?> reader;

    @Before
    public void setUp() throws Exception {
        reader = EasyMock.createMock(ItemStreamReader.class);
    }

    @Test
    public void testReadAll() throws Exception {

        reader.open(EasyMock.anyObject());
        EasyMock.expectLastCall();
        expect(reader.read()).andReturn(new Object());
        expect(reader.read()).andReturn(new Object());
        expect(reader.read()).andReturn(new Object());
        expect(reader.read()).andReturn(null);
        reader.close();
        EasyMock.expectLastCall();

        replay(reader);

        assertNotNull(AbstractGtfsDataProcessor.readAll(reader));
    }

    @Test(expected = IllegalStateException.class)
    public void testReadAllWithException() throws Exception {

        reader.open(EasyMock.anyObject());
        EasyMock.expectLastCall();
        expect(reader.read()).andThrow(new Exception());
        reader.close();
        EasyMock.expectLastCall();

        replay(reader);

        AbstractGtfsDataProcessor.readAll(reader);
    }
}