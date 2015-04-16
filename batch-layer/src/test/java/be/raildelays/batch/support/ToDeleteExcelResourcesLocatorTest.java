package be.raildelays.batch.support;

import be.raildelays.batch.AbstractFileTest;
import org.junit.*;
import org.springframework.core.io.Resource;

/**
 * Created by xbmc on 16-04-15.
 */
public class ToDeleteExcelResourcesLocatorTest extends AbstractFileTest {

    private ToDeleteExcelResourcesLocator resourcesLocator;

    @Before
    public void setUp() throws Exception {
        resourcesLocator = new ToDeleteExcelResourcesLocator();
        resourcesLocator.setDestination(null);
        resourcesLocator.setSource(null);
    }

    @Test
    @Ignore
    public void testGetResources() throws Exception {
        Resource[] result = resourcesLocator.getResources();

        Assert.assertEquals(3, result.length);
    }

    @After
    public void tearDown() throws Exception {

    }
}