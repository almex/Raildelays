package be.raildelays.batch;

import org.junit.runner.RunWith;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * Parent class of all Integration Tests.
 *
 * @implNote We cannot use {@code @Transactional} because Spring Batch does not allow to open a transaction outside its
 * scope
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/batch/raildelays-batch-integration-test-context.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        StepScopeTestExecutionListener.class,
//        TransactionalTestExecutionListener.class
})
//@Transactional(transactionManager = "raildelaysTransactionManager")
//@Rollback(true)
public abstract class AbstractContextIT {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    public JobLauncherTestUtils getJobLauncherTestUtils() {
        return jobLauncherTestUtils;
    }
}
