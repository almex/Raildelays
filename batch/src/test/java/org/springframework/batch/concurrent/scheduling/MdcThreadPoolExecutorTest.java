package org.springframework.batch.concurrent.scheduling;

import org.apache.log4j.MDC;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Almex
 */
public class MdcThreadPoolExecutorTest {

    private MdcThreadPoolExecutor threadPoolExecutor;
    private boolean getThroughRunnable;

    @Before
    public void setUp() throws Exception {
        threadPoolExecutor = new MdcThreadPoolExecutor(
                1,
                1,
                1000,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1),
                new MdcThreadPoolTaskExecutor(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        getThroughRunnable = false;
    }

    /**
     * We expect that the MDC is copied from the main-thread to the sub-thread.
     * Therefor, we must validate that we get through the {@code Runnable}.
     */
    @Test(timeout = 1000)
    public void testExecute() throws Exception {

        MDC.put("foo", "bar");

        threadPoolExecutor.execute(() -> {
            Assert.assertEquals("bar", MDC.get("foo"));
            getThroughRunnable = true;
        });

        Thread.sleep(500);

        Assert.assertTrue(getThroughRunnable);
    }
}