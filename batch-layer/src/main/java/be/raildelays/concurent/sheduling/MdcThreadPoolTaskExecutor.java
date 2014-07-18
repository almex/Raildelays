package be.raildelays.concurent.sheduling;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import java.util.concurrent.*;

/**
 * @author Almex
 */
public class MdcThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        BlockingQueue<Runnable> queue = createQueue(Integer.MAX_VALUE);
        ThreadPoolExecutor executor  = MdcThreadPoolExecutor.newWithCurrentMdc(
                this.getCorePoolSize(), this.getMaxPoolSize(), this.getKeepAliveSeconds(), TimeUnit.SECONDS,
                queue);

        this.threadPoolExecutor = executor;
        return executor;
    }

    @Override
    public ThreadPoolExecutor getThreadPoolExecutor() throws IllegalStateException {
        Assert.state(this.threadPoolExecutor != null, "ThreadPoolTaskExecutor not initialized");
        return this.threadPoolExecutor;
    }
}
