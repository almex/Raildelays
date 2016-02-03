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
public class StopsFieldSetMapperTest extends EasyMockSupport {

    @TestSubject
    private StopsFieldSetMapper mapper = new StopsFieldSetMapper();
    @Rule
    public EasyMockRule easyMockRule = new EasyMockRule(this);
    @Mock(type = MockType.NICE)
    private FieldSet fieldSetMock;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testMapFieldSet() throws Exception {
        expect(fieldSetMock.readString("stop_id")).andReturn("stops:007015400");
        expect(fieldSetMock.readString("stop_name")).andReturn("London Saint Pancras International");
        expect(fieldSetMock.readString("stop_lat")).andReturn("51.5310399");
        expect(fieldSetMock.readString("stop_lon")).andReturn("-0.1260606");
        expect(fieldSetMock.readString("platform_code")).andReturn("");
        expect(fieldSetMock.readInt("location_type")).andReturn(1);
        expect(fieldSetMock.readString("parent_station")).andReturn("");

        replayAll();
        Stop actual = mapper.mapFieldSet(fieldSetMock);

        Assert.assertNotNull(actual);
        verifyAll();
    }
}