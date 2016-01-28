package be.raildelays.batch.reader;

import be.raildelays.domain.entities.TrainLine;
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
public class TrainFieldSetMapperTest extends EasyMockSupport {

    @TestSubject
    private TrainFieldSetMapper mapper = new TrainFieldSetMapper();
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

        TrainLine actual = mapper.mapFieldSet(fieldSetMock);

        Assert.assertNotNull(actual);
    }
}