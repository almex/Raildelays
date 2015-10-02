package be.raildelays.batch.service.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.job.flow.FlowJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.Collections;
import java.util.List;

/**
 * @author Almex
 */
public class BatchStartAndRecoveryServiceImplTest {

    public static final String JOB_NAME = "foo";
    public static final long INSTANCE_ID = 1L;
    public static final long EXECUTION_ID = 1L;
    private BatchStartAndRecoveryServiceImpl service;
    //private JobExplorer jobExplorer;
    private JobLauncher jobLauncher;
    private JobRegistry jobRegistry;
    //private JobRepository jobRepository;
    private JobExecution jobExecution;
    private JobInstanceDao jobInstanceDao;
    private JobExecutionDao jobExecutionDao;
    private StepExecutionDao stepExecutionDao;
    private ExecutionContextDao executionContextDao;

    @Before
    public void setUp() throws Exception {
        service = new BatchStartAndRecoveryServiceImpl();

        jobLauncher = EasyMock.createMock(JobLauncher.class);
        jobRegistry = EasyMock.createMock(JobRegistry.class);
        jobInstanceDao = EasyMock.createMock(JobInstanceDao.class);
        jobExecutionDao = EasyMock.createMock(JobExecutionDao.class);
        stepExecutionDao = EasyMock.createMock(StepExecutionDao.class);
        executionContextDao = EasyMock.createMock(ExecutionContextDao.class);

        service.setJobLauncher(jobLauncher);
        service.setJobRegistry(jobRegistry);
        service.setJobInstanceDao(jobInstanceDao);
        service.setJobExecutionDao(jobExecutionDao);
        service.setStepExecutionDao(stepExecutionDao);
        service.setExecutionContextDao(executionContextDao);

        jobExecution = MetaDataInstanceFactory.createJobExecution(JOB_NAME, INSTANCE_ID, EXECUTION_ID);
        jobExecution.addStepExecutions(Collections.singletonList(MetaDataInstanceFactory.createStepExecution()));
    }

    @Test
    public void testStopAllRunningJobs() throws Exception {

        EasyMock.expect(jobRegistry.getJobNames())
                .andReturn(Collections.singleton(JOB_NAME))
                .anyTimes();
        EasyMock.expect(jobExecutionDao.findRunningJobExecutions(JOB_NAME))
                .andReturn(Collections.singleton(jobExecution));
        expectFindExecutionById(jobExecution);
        EasyMock.replay(jobRegistry, jobInstanceDao, jobExecutionDao, stepExecutionDao, executionContextDao);

        List<JobExecution> jobExecutions = service.stopAllRunningJobs();

        Assert.assertEquals(1, jobExecutions.size());
        Assert.assertEquals(BatchStatus.STOPPING, jobExecutions.get(0).getStatus());
    }

    private void expectFindExecutionById(JobExecution jobExecution) {
        StepExecution stepExecution = jobExecution.getStepExecutions().stream().findFirst().get();

        EasyMock.expect(jobExecutionDao.getJobExecution(EXECUTION_ID))
                .andReturn(jobExecution);
        EasyMock.expect(jobInstanceDao.getJobInstance(jobExecution))
                .andReturn(jobExecution.getJobInstance());
        stepExecutionDao.addStepExecutions(jobExecution);
        EasyMock.expectLastCall();
        jobExecutionDao.synchronizeStatus(jobExecution);
        EasyMock.expectLastCall();
        jobExecutionDao.updateJobExecution(jobExecution);
        EasyMock.expectLastCall();
        stepExecutionDao.updateStepExecution(stepExecution);
        EasyMock.expectLastCall();
        EasyMock.expect(executionContextDao.getExecutionContext(jobExecution))
                .andReturn(jobExecution.getExecutionContext());
        EasyMock.expect(executionContextDao.getExecutionContext(stepExecution))
                .andReturn(stepExecution.getExecutionContext())
                .anyTimes();
    }

    @Test
    public void testMarkInconsistentJobsAsFailed() throws Exception {
        EasyMock.expect(jobRegistry.getJobNames())
                .andReturn(Collections.singleton(JOB_NAME))
                .anyTimes();
        EasyMock.expect(jobExecutionDao.findRunningJobExecutions(JOB_NAME))
                .andReturn(Collections.singleton(jobExecution));
        expectFindExecutionById(jobExecution);
        EasyMock.replay(jobRegistry, jobInstanceDao, jobExecutionDao, stepExecutionDao, executionContextDao);

        List<JobExecution> jobExecutions = service.markInconsistentJobsAsFailed();

        Assert.assertEquals(1, jobExecutions.size());
        Assert.assertEquals(BatchStatus.FAILED, jobExecutions.get(0).getStatus());
    }

    @Test
    public void testRestartAllFailedJobs() throws Exception {
        FlowJob job = new FlowJob(JOB_NAME);

        jobExecution.setStatus(BatchStatus.FAILED);

        EasyMock.expect(jobRegistry.getJobNames())
                .andReturn(Collections.singleton(JOB_NAME))
                .anyTimes();
        EasyMock.expect(jobRegistry.getJob(JOB_NAME))
                .andReturn(job)
                .anyTimes();
        EasyMock.expect(jobInstanceDao.getJobInstances(EasyMock.anyString(), EasyMock.anyInt(), EasyMock.anyInt()))
                .andReturn(Collections.singletonList(jobExecution.getJobInstance()))
                .once();
        EasyMock.expect(jobInstanceDao.getJobInstances(EasyMock.anyString(), EasyMock.anyInt(), EasyMock.anyInt()))
                .andReturn(Collections.emptyList())
                .once();
        EasyMock.expect(jobInstanceDao.getJobInstance(INSTANCE_ID))
                .andReturn(jobExecution.getJobInstance())
                .anyTimes();
        EasyMock.expect(jobExecutionDao.getLastJobExecution(jobExecution.getJobInstance()))
                .andReturn(jobExecution)
                .anyTimes();
        EasyMock.expect(jobLauncher.run(EasyMock.anyObject(Job.class), EasyMock.anyObject(JobParameters.class)))
                .andReturn(MetaDataInstanceFactory.createJobExecution())
                .anyTimes();
        EasyMock.replay(jobRegistry, jobLauncher, jobInstanceDao, jobExecutionDao, stepExecutionDao, executionContextDao);

        List<JobExecution> jobExecutions = service.restartAllFailedJobs();

        Assert.assertEquals(1, jobExecutions.size());
        Assert.assertEquals(BatchStatus.STARTING, jobExecutions.get(0).getStatus());
    }

    @Test
    public void testRestartAllStoppedJobs() throws Exception {
        FlowJob job = new FlowJob(JOB_NAME);

        jobExecution.setStatus(BatchStatus.STOPPED);

        EasyMock.expect(jobRegistry.getJobNames())
                .andReturn(Collections.singleton(JOB_NAME))
                .anyTimes();
        EasyMock.expect(jobRegistry.getJob(JOB_NAME))
                .andReturn(job)
                .anyTimes();
        EasyMock.expect(jobInstanceDao.getJobInstances(EasyMock.anyString(), EasyMock.anyInt(), EasyMock.anyInt()))
                .andReturn(Collections.singletonList(jobExecution.getJobInstance()))
                .once();
        EasyMock.expect(jobInstanceDao.getJobInstances(EasyMock.anyString(), EasyMock.anyInt(), EasyMock.anyInt()))
                .andReturn(Collections.emptyList())
                .once();
        EasyMock.expect(jobInstanceDao.getJobInstance(INSTANCE_ID))
                .andReturn(jobExecution.getJobInstance())
                .anyTimes();
        EasyMock.expect(jobExecutionDao.getLastJobExecution(jobExecution.getJobInstance()))
                .andReturn(jobExecution)
                .anyTimes();
        EasyMock.expect(jobLauncher.run(EasyMock.anyObject(Job.class), EasyMock.anyObject(JobParameters.class)))
                .andReturn(MetaDataInstanceFactory.createJobExecution())
                .anyTimes();
        EasyMock.replay(jobRegistry, jobLauncher, jobInstanceDao, jobExecutionDao, stepExecutionDao, executionContextDao);

        List<JobExecution> jobExecutions = service.restartAllStoppedJobs();

        Assert.assertEquals(1, jobExecutions.size());
        Assert.assertEquals(BatchStatus.STARTING, jobExecutions.get(0).getStatus());
    }

    @Test(expected = NoSuchJobInstanceException.class)
    public void testGetStatus() throws Exception {

        EasyMock.expect(jobInstanceDao.getJobInstance(INSTANCE_ID))
                .andReturn(null);

        EasyMock.replay(jobRegistry, jobLauncher, jobInstanceDao, jobExecutionDao, stepExecutionDao, executionContextDao);

        service.getStatus(INSTANCE_ID);
    }

    @Test
    public void testStart() throws Exception {

    }

    @Test
    public void testStart1() throws Exception {

    }

    @Test
    public void testRestart() throws Exception {

    }

    @Test
    public void testRestart1() throws Exception {

    }

    @Test
    public void testStartNewInstance() throws Exception {

    }

    @Test
    public void testStop() throws Exception {

    }

    @Test
    public void testGetJobNames() throws Exception {

    }

    @Test
    public void testRefresh() throws Exception {

    }

    @Test
    public void testAbandon() throws Exception {

    }
}