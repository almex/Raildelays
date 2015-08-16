package be.raildelays.repository.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring/repository/raildelays-repository-integration-context.xml"})
public class SpringContextIT {

    @Test
    public void testLoading() {
        assertTrue("The test should fail before this line", true);
    }

}
