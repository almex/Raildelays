package be.raildelays.batch.service.impl;

import org.easymock.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.job.flow.FlowJob;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.easymock.EasyMock.*;

/**
 * @author Almex
 */
@RunWith(EasyMockRunner.class)
public class BatchStartAndRecoveryServiceImplTest extends EasyMockSupport {

    public static final String JOB_NAME = "foo";
    public static final long INSTANCE_ID = 1L;
    public static final long EXECUTION_ID = 1L;

    private JobExecution jobExecution;

    @Mock(type = MockType.NICE)
    private JobLauncher jobLauncher;
    @Mock(type = MockType.NICE)
    private JobRegistry jobRegistry;
    @Mock(type = MockType.NICE)
    private JobInstanceDao jobInstanceDao;
    @Mock(type = MockType.NICE)
    private JobExecutionDao jobExecutionDao;
    @Mock(type = MockType.NICE)
    private StepExecutionDao stepExecutionDao;
    @Mock(type = MockType.NICE)
    private ExecutionContextDao executionContextDao;
    @TestSubject
    private BatchStartAndRecoveryServiceImpl service = new BatchStartAndRecoveryServiceImpl(
            jobInstanceDao, jobExecutionDao, stepExecutionDao, executionContextDao
    );

    @Before
    public void setUp() throws Exception {
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
        expectGetRunningExecutions(Collections.singleton(jobExecution));
        expectFindExecutionById();
        expectUpdateJobExecution();

        replayAll();

        List<JobExecution> jobExecutions = service.stopAllRunningJobs();

        Assert.assertEquals(1, jobExecutions.size());
        Assert.assertEquals(BatchStatus.STOPPING, jobExecutions.get(0).getStatus());
    }

    @Test
    public void testMarkInconsistentJobsAsFailed() throws Exception {
        expectGetRunningExecutions(Collections.singleton(jobExecution));
        expectFindExecutionById();
        expectUpdateJobExecution();
        expectUpdateStepExecution();

        replayAll();

        List<JobExecution> jobExecutions = service.markInconsistentJobsAsFailed();

        Assert.assertEquals(1, jobExecutions.size());
        Assert.assertEquals(BatchStatus.FAILED, jobExecutions.get(0).getStatus());
    }

    @Test
    public void testRestartAllFailedJobs() throws Exception {
        jobExecution.setStatus(BatchStatus.FAILED);

        expectGetJobNames();
        expectGetJobInstances();
        expectGetStatus();
        expectStartOrRestart();

        replayAll();

        List<JobExecution> jobExecutions = service.restartAllFailedJobs();

        Assert.assertEquals(1, jobExecutions.size());
        Assert.assertEquals(BatchStatus.STARTING, jobExecutions.get(0).getStatus());
    }

    @Test
    public void testRestartAllStoppedJobs() throws Exception {
        jobExecution.setStatus(BatchStatus.STOPPED);

        expectGetJobNames();
        expectGetJobInstances();
        expectGetStatus();
        expectStartOrRestart();

        replayAll();

        List<JobExecution> jobExecutions = service.restartAllStoppedJobs();

        Assert.assertEquals(1, jobExecutions.size());
        Assert.assertEquals(BatchStatus.STARTING, jobExecutions.get(0).getStatus());
    }

    @Test(expected = NoSuchJobInstanceException.class)
    public void testGetStatus() throws Exception {
        service.getStatus(INSTANCE_ID);
    }

    @Test
    public void testStart() throws Exception {
        expectStartOrRestart();

        replayAll();

        JobExecution jobExecution = service.start(JOB_NAME, new JobParameters());

        Assert.assertEquals(BatchStatus.STARTING, jobExecution.getStatus());
    }

    @Test
    public void testStartNewInstance() throws Exception {
        expectStartOrRestart();

        replayAll();

        JobExecution jobExecution = service.startNewInstance(JOB_NAME, new JobParameters());

        Assert.assertEquals(BatchStatus.STARTING, jobExecution.getStatus());
    }

    @Test(expected = NoSuchJobException.class)
    public void testRestartNoSuchJobException() throws Exception {
        service.restart(INSTANCE_ID);
    }

    @Test(expected = NoSuchJobExecutionException.class)
    public void testRestartNoSuchJobExecutionException() throws Exception {
        expectGetJobInstance();

        replayAll();

        service.restart(INSTANCE_ID);
    }

    @Test(expected = JobRestartException.class)
    public void testRestartJobRestartException() throws Exception {
        jobExecution.setStatus(BatchStatus.COMPLETED);

        expectGetJobInstance();
        expectGetLastJobExecution();

        replayAll();

        service.restart(INSTANCE_ID);
    }

    @Test
    public void testGetJobNames() throws Exception {
        expectGetJobNames();

        replayAll();

        List<String> jobNames = service.getJobNames();

        Assert.assertEquals(1, jobNames.size());
        Assert.assertTrue(jobNames.contains(JOB_NAME));
    }

    @Test
    public void testAbandon() throws Exception {
        jobExecution.setStatus(BatchStatus.FAILED);

        expectFindExecutionById();
        expectUpdateJobExecution();

        replayAll();

        JobExecution jobExecution = service.abandon(EXECUTION_ID);

        Assert.assertNotNull(jobExecution);
    }

    @Test(expected = JobExecutionAlreadyRunningException.class)
    public void testAbandonJobExecutionAlreadyRunningException() throws Exception {
        expectFindExecutionById();
        expectUpdateJobExecution();

        replayAll();

        JobExecution jobExecution = service.abandon(EXECUTION_ID);

        Assert.assertNotNull(jobExecution);
    }

    @Test
    public void testRefresh() throws Exception {
        expectFindExecutionById();

        replayAll();

        JobExecution jobExecution = service.refresh(MetaDataInstanceFactory.createJobExecution(EXECUTION_ID));

        Assert.assertEquals(this.jobExecution, jobExecution);
    }

    @Test(expected = NoSuchJobExecutionException.class)
    public void testRefreshNoSuchJobExecutionException() throws Exception {
        service.refresh(jobExecution);
    }

    @Test(expected = JobExecutionNotRunningException.class)
    public void testStopJobExecutionNotRunningException() throws Exception {
        jobExecution.setStatus(BatchStatus.COMPLETED);

        expectFindExecutionById();
        expectUpdateJobExecution();

        replayAll();

        service.stop(EXECUTION_ID);
    }

    @Test(expected = NoSuchJobException.class)
    public void testGetRunningExecutionsNoSuchJobException() throws Exception {
        expect(jobExecutionDao.findRunningJobExecutions(JOB_NAME))
                .andStubReturn(Collections.emptySet());
        expect(jobRegistry.getJobNames())
                .andStubReturn(Collections.emptyList());

        replayAll();

        service.getRunningExecutions(JOB_NAME);
    }

    private void expectStartOrRestart() throws NoSuchJobException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        FlowJob job = new FlowJob(JOB_NAME);
        job.setJobParametersIncrementer(new RunIdIncrementer());
        expect(jobRegistry.getJob(JOB_NAME))
                .andStubReturn(job);
        expect(jobLauncher.run(anyObject(Job.class), anyObject(JobParameters.class)))
                .andStubReturn(MetaDataInstanceFactory.createJobExecution());
    }

    private void expectGetJobInstances() {
        expect(jobInstanceDao.getJobInstances(anyString(), anyInt(), anyInt()))
                .andReturn(Collections.singletonList(jobExecution.getJobInstance()))
                .andStubReturn(Collections.emptyList());
    }

    private void expectGetStatus() {
        expectGetJobInstance();
        expectGetLastJobExecution();
    }

    private void expectGetLastJobExecution() {
        expect(jobExecutionDao.getLastJobExecution(jobExecution.getJobInstance()))
                .andStubReturn(jobExecution);
    }

    private void expectGetJobInstance() {
        expect(jobInstanceDao.getJobInstance(INSTANCE_ID))
                .andStubReturn(jobExecution.getJobInstance());
    }

    private void expectUpdateJobExecution() {
        jobExecutionDao.synchronizeStatus(jobExecution);
        jobExecutionDao.updateJobExecution(jobExecution);
    }

    private void expectUpdateStepExecution() {
        StepExecution stepExecution = jobExecution.getStepExecutions().stream().findFirst().get();
        stepExecutionDao.updateStepExecution(stepExecution);
    }

    private void expectFindExecutionById() {
        StepExecution stepExecution = jobExecution.getStepExecutions().stream().findFirst().get();

        expect(jobExecutionDao.getJobExecution(EXECUTION_ID))
                .andStubReturn(jobExecution);
        expect(jobInstanceDao.getJobInstance(jobExecution))
                .andStubReturn(jobExecution.getJobInstance());
        stepExecutionDao.addStepExecutions(jobExecution);
        expect(executionContextDao.getExecutionContext(jobExecution))
                .andStubReturn(jobExecution.getExecutionContext());
        expect(executionContextDao.getExecutionContext(stepExecution))
                .andStubReturn(stepExecution.getExecutionContext());
    }

    private void expectGetRunningExecutions(Set<JobExecution> returnedValue) {
        expect(jobExecutionDao.findRunningJobExecutions(JOB_NAME))
                .andStubReturn(returnedValue);
        expectGetJobNames();
    }

    private void expectGetJobNames() {
        expect(jobRegistry.getJobNames())
                .andStubReturn(Collections.singleton(JOB_NAME));
    }
}