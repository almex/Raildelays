package be.raildelays.repository.impl;

import com.excilys.ebi.spring.dbunit.test.RollbackTransactionalDataSetTestExecutionListener;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * Optimization to load only one time the Spring context for all integration tests.
 *
 * @author Almex
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring/repository/raildelays-repository-integration-test-context.xml",
        "classpath:spring/test/raildelays-tx-context.xml"})
@Rollback
@Transactional(transactionManager = "raildelaysTransactionManager")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        RollbackTransactionalDataSetTestExecutionListener.class})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD, hierarchyMode = DirtiesContext.HierarchyMode.EXHAUSTIVE) //Cannot any other solution when loading an in-memory database with Spring
public abstract class AbstractIT {
}
