/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package be.raildelays.javafx.service;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
import be.raildelays.javafx.test.JavaFXThreadingRule;
import be.raildelays.test.GraphicalTest;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.concurrent.ExecutionException;

/**
 * @author Almex
 * @since 1.2
 */
@Category(GraphicalTest.class)
@RunWith(BlockJUnit4ClassRunner.class)
public class BatchScheduledServiceIT {

    private BatchScheduledService service;
    private BatchStartAndRecoveryService startAndRecoveryService;
    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setUp() throws Exception {
        JobExecution expected = MetaDataInstanceFactory.createJobExecution();
        startAndRecoveryService = EasyMock.createMock(BatchStartAndRecoveryService.class);

        service = new BatchScheduledService();
        service.setService(startAndRecoveryService);
        service.setDelay(Duration.seconds(1));
        service.setPeriod(Duration.seconds(1));

        EasyMock.expect(startAndRecoveryService.refresh(expected)).andReturn(expected);
    }

    @Test
    public void testStart() throws Exception {
        JobExecution expected = MetaDataInstanceFactory.createJobExecution();

        EasyMock.expect(startAndRecoveryService.startNewInstance(
                EasyMock.anyString(),
                EasyMock.anyObject(JobParameters.class)
        )).andReturn(expected);

        EasyMock.replay(startAndRecoveryService);

        service.start("foo", new JobParameters());

        Assert.assertEquals(expected, service.getJobExecution());
        assertOnTask();
    }

    @Test
    public void testRestart() throws Exception {
        JobExecution expected1 = MetaDataInstanceFactory.createJobExecution();
        JobExecution expected2 = MetaDataInstanceFactory.createJobExecution();

        EasyMock.expect(startAndRecoveryService.startNewInstance(
                EasyMock.anyString(),
                EasyMock.anyObject(JobParameters.class)
        )).andReturn(expected1);
        EasyMock.expect(startAndRecoveryService.restart(EasyMock.anyLong())).andReturn(expected2);
        EasyMock.replay(startAndRecoveryService);

        service.start("foo", new JobParameters());
        service.cancel();
        service.restart();

        Assert.assertEquals(expected2, service.getJobExecution());
        assertOnTask();
    }

    @Test
    public void testStop() throws Exception {
        JobExecution expected = MetaDataInstanceFactory.createJobExecution();

        EasyMock.expect(startAndRecoveryService.startNewInstance(
                EasyMock.anyString(),
                EasyMock.anyObject(JobParameters.class)
        )).andReturn(expected);
        EasyMock.expect(startAndRecoveryService.stop(EasyMock.anyLong())).andReturn(expected);

        EasyMock.replay(startAndRecoveryService);

        service.start("foo", new JobParameters());
        service.stop();

        Assert.assertEquals(expected, service.getJobExecution());
        assertOnTask();
    }

    @Test
    public void testAbandon() throws Exception {
        JobExecution expected = MetaDataInstanceFactory.createJobExecution();

        EasyMock.expect(startAndRecoveryService.startNewInstance(
                EasyMock.anyString(),
                EasyMock.anyObject(JobParameters.class)
        )).andReturn(expected);
        EasyMock.expect(startAndRecoveryService.abandon(EasyMock.anyLong())).andReturn(expected);

        EasyMock.replay(startAndRecoveryService);

        service.start("foo", new JobParameters());
        service.abandon();

        Assert.assertEquals(expected, service.getJobExecution());
        assertOnTask();

    }

    @Test
    public void testIsStarted() throws Exception {
        JobExecution expected = MetaDataInstanceFactory.createJobExecution();

        EasyMock.expect(startAndRecoveryService.startNewInstance(
                EasyMock.anyString(),
                EasyMock.anyObject(JobParameters.class)
        )).andReturn(expected);

        EasyMock.replay(startAndRecoveryService);

        service.start("foo", new JobParameters());

        Assert.assertTrue(service.isStarted());
        assertOnTask();
    }

    private void assertOnTask() throws InterruptedException, ExecutionException {
        Task<Integer> task = service.createTask();

        task.run();

        Assert.assertEquals(service.countProperty().get(), (int) task.get());
    }
}