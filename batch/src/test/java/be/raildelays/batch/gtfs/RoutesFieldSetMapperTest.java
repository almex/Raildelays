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
public class RoutesFieldSetMapperTest extends EasyMockSupport {

    @TestSubject
    private RoutesFieldSetMapper mapper = new RoutesFieldSetMapper();
    @Rule
    public EasyMockRule easyMockRule = new EasyMockRule(this);
    @Mock(type = MockType.NICE)
    private FieldSet fieldSetMock;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testMapFieldSet() throws Exception {
        expect(fieldSetMock.readRawString("route_id")).andReturn("route:IC466");
        expect(fieldSetMock.readRawString("route_short_name")).andReturn("IC466");
        expect(fieldSetMock.readRawString("route_long_name")).andReturn("Liège-Guillemins - Bruxelles Central");

        replayAll();

        Route actual = mapper.mapFieldSet(fieldSetMock);

        Assert.assertNotNull(actual);
    }
}