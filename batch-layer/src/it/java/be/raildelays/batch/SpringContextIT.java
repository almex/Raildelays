package be.raildelays.batch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = {
        "classpath:spring/batch/raildelays-batch-integration-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringContextIT {

    @Test
    public void testLoading() {
        assertTrue("The test should fail before this line", true);
    }

}
