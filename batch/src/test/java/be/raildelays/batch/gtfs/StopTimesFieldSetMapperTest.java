package be.raildelays.batch.gtfs;

import org.easymock.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.item.file.transform.FieldSet;

import java.util.Date;

import static org.easymock.EasyMock.expect;

/**
 * @author Almex
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class StopTimesFieldSetMapperTest extends EasyMockSupport {

    @TestSubject
    private StopTimesFieldSetMapper mapper = new StopTimesFieldSetMapper();
    @Rule
    public EasyMockRule easyMockRule = new EasyMockRule(this);
    @Mock(type = MockType.NICE)
    private FieldSet fieldSetMock;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testMapFieldSet() throws Exception {
        expect(fieldSetMock.readRawString("trip_id")).andReturn("IC466");
        expect(fieldSetMock.readDate("arrival_time", GtfsFiledSetMapper.TIME_FORMAT)).andReturn(new Date());
        expect(fieldSetMock.readDate("departure_time", GtfsFiledSetMapper.TIME_FORMAT)).andReturn(new Date());

        replayAll();
        StopTime actual = mapper.mapFieldSet(fieldSetMock);

        Assert.assertNotNull(actual);
    }
}