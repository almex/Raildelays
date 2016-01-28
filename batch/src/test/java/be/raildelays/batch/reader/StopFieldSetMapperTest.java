package be.raildelays.batch.reader;

import be.raildelays.domain.entities.LineStop;
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
public class StopFieldSetMapperTest extends EasyMockSupport {

    @TestSubject
    private StopFieldSetMapper mapper = new StopFieldSetMapper();
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
        expect(fieldSetMock.readDate("arrival_time", StopFieldSetMapper.TIME_FORMAT)).andReturn(new Date());
        expect(fieldSetMock.readDate("departure_time", StopFieldSetMapper.TIME_FORMAT)).andReturn(new Date());

        replayAll();

        LineStop actual = mapper.mapFieldSet(fieldSetMock);

        Assert.assertNotNull(actual);
    }
}