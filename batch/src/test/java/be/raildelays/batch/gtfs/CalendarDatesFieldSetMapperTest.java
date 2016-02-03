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
public class CalendarDatesFieldSetMapperTest extends EasyMockSupport {

    @TestSubject
    private CalendarDatesFieldSetMapper mapper = new CalendarDatesFieldSetMapper();
    @Rule
    public EasyMockRule easyMockRule = new EasyMockRule(this);
    @Mock(type = MockType.NICE)
    private FieldSet fieldSetMock;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testMapFieldSet() throws Exception {
        expect(fieldSetMock.readDate("date", GtfsFiledSetMapper.DATE_FORMAT)).andReturn(new Date());
        expect(fieldSetMock.readString("service_id")).andReturn("1");
        expect(fieldSetMock.readInt("exception_type")).andReturn(0);

        replayAll();
        CalendarDate actual = mapper.mapFieldSet(fieldSetMock);

        Assert.assertNotNull(actual);
        verifyAll();
    }
}