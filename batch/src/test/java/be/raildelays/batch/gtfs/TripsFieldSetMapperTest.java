package be.raildelays.batch.gtfs;

import org.easymock.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.item.file.transform.FieldSet;

import static org.easymock.EasyMock.expect;

/**
 * @author Almex
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TripsFieldSetMapperTest extends EasyMockSupport {

    @TestSubject
    private TripsFieldSetMapper mapper = new TripsFieldSetMapper();
    @Rule
    public EasyMockRule easyMockRule = new EasyMockRule(this);
    @Mock(type = MockType.NICE)
    private FieldSet fieldSetMock;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testMapFieldSet() throws Exception {
        expect(fieldSetMock.readRawString("route_id")).andReturn("routes:IC106");
        expect(fieldSetMock.readRawString("service_id")).andReturn("1");
        expect(fieldSetMock.readRawString("trip_id")).andReturn("IC466");

        replayAll();
        Trip actual = mapper.mapFieldSet(fieldSetMock);

        Assert.assertNotNull(actual);
        verifyAll();
    }
}