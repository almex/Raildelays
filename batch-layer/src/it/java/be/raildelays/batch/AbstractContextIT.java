package be.raildelays.batch;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"/spring/batch/raildelays-batch-integration-context.xml" })
@TransactionConfiguration(defaultRollback = false)
@Transactional
public abstract class AbstractContextIT extends
		AbstractJUnit4SpringContextTests {

}
