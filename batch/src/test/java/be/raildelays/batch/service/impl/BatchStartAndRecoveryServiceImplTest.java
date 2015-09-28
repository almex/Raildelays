package be.raildelays.batch.service.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
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
    private JobExplorer jobExplorer;
    private JobLauncher jobLauncher;
    private JobRegistry jobRegistry;
    private JobRepository jobRepository;
    private JobExecution jobExecution;

    @Before
    public void setUp() throws Exception {
        service = new BatchStartAndRecoveryServiceImpl();

        jobExplorer = EasyMock.createMock(JobExplorer.class);
        jobLauncher = EasyMock.createMock(JobLauncher.class);
        jobRegistry = EasyMock.createMock(JobRegistry.class);
        jobRepository = EasyMock.createMock(JobRepository.class);

        service.setJobExplorer(jobExplorer);
        service.setJobLauncher(jobLauncher);
        service.setJobRegistry(jobRegistry);
        service.setJobRepository(jobRepository);

        jobExecution = MetaDataInstanceFactory.createJobExecution(JOB_NAME, INSTANCE_ID, EXECUTION_ID);
    }

    @Test
    public void testStopAllRunningJobs() throws Exception {
        EasyMock.expect(jobRegistry.getJobNames())
                .andReturn(Collections.singleton(JOB_NAME))
                .anyTimes();
        EasyMock.expect(jobExplorer.findRunningJobExecutions(JOB_NAME))
                .andReturn(Collections.singleton(jobExecution));
        EasyMock.expect(jobExplorer.getJobExecution(EXECUTION_ID))
                .andReturn(jobExecution);
        //EasyMock.expect(jobRepository.update(jobExecution)).andVoid();
        EasyMock.replay(jobRegistry, jobExplorer);

        List<JobExecution> jobExecutions = service.stopAllRunningJobs();

        Assert.assertEquals(1, jobExecutions.size());
        Assert.assertEquals(BatchStatus.STOPPING, jobExecutions.get(0).getStatus());

        EasyMock.verify(jobRegistry, jobExplorer);
    }

    @Test
    public void testMarkInconsistentJobsAsFailed() throws Exception {
        EasyMock.expect(jobRegistry.getJobNames())
                .andReturn(Collections.singleton(JOB_NAME))
                .anyTimes();
        EasyMock.expect(jobExplorer.findRunningJobExecutions(JOB_NAME))
                .andReturn(Collections.singleton(jobExecution));
        EasyMock.expect(jobExplorer.getJobExecution(EXECUTION_ID))
                .andReturn(jobExecution);
        EasyMock.replay(jobRegistry, jobExplorer);

        List<JobExecution> jobExecutions = service.markInconsistentJobsAsFailed();

        Assert.assertEquals(1, jobExecutions.size());
        Assert.assertEquals(BatchStatus.FAILED, jobExecutions.get(0).getStatus());

        EasyMock.verify(jobRegistry, jobExplorer);
    }

    @Test
    @Ignore
    public void testRestartAllFailedJobs() throws Exception {
        jobExecution.setStatus(BatchStatus.FAILED);

        EasyMock.expect(jobRegistry.getJobNames())
                .andReturn(Collections.singleton(JOB_NAME))
                .anyTimes();
        EasyMock.expect(jobExplorer.getJobInstances(EasyMock.anyString(), EasyMock.anyInt(), EasyMock.anyInt()))
                .andReturn(Collections.singletonList(jobExecution.getJobInstance()))
                .once();
        EasyMock.expect(jobExplorer.getJobInstances(EasyMock.anyString(), EasyMock.anyInt(), EasyMock.anyInt()))
                .andReturn(Collections.emptyList())
                .once();
        EasyMock.expect(jobExplorer.getJobInstance(INSTANCE_ID))
                .andReturn(jobExecution.getJobInstance())
                .anyTimes();
        EasyMock.expect(jobExplorer.getJobExecutions(jobExecution.getJobInstance()))
                .andReturn(Collections.singletonList(jobExecution))
                .anyTimes();
//        EasyMock.expect(jobExplorer.getJobExecution(EXECUTION_ID))
//                .andReturn(jobExecution);
        EasyMock.replay(jobRegistry, jobExplorer);

        List<JobExecution> jobExecutions = service.restartAllFailedJobs();

        Assert.assertEquals(1, jobExecutions.size());
        Assert.assertEquals(BatchStatus.STARTED, jobExecutions.get(0).getStatus());

        EasyMock.verify(jobRegistry, jobExplorer);
    }

    @Test
    public void testRestartAllStoppedJobs() throws Exception {

    }

    @Test
    public void testRestartJobs() throws Exception {

    }

    @Test
    public void testGetStatus() throws Exception {

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