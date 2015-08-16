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

package be.raildelays.concurrent.scheduling;

import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

import java.util.concurrent.*;

/**
 * @author Almex
 */
public class MdcThreadPoolTaskExecutor extends ExecutorConfigurationSupport
        implements AsyncListenableTaskExecutor, SchedulingTaskExecutor {

    private final Object poolSizeMonitor = new Object();

    private int corePoolSize = 1;

    private int maxPoolSize = Integer.MAX_VALUE;

    private int keepAliveSeconds = 60;

    private boolean allowCoreThreadTimeOut = false;

    private int queueCapacity = Integer.MAX_VALUE;
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * Return the ThreadPoolExecutor's core pool size.
     */
    public int getCorePoolSize() {
        synchronized (this.poolSizeMonitor) {
            return this.corePoolSize;
        }
    }

    /**
     * Set the ThreadPoolExecutor's core pool size.
     * Default is 1.
     * <p><b>This setting can be modified at runtime, for example through JMX.</b>
     */
    public void setCorePoolSize(int corePoolSize) {
        synchronized (this.poolSizeMonitor) {
            this.corePoolSize = corePoolSize;
            if (this.threadPoolExecutor != null) {
                this.threadPoolExecutor.setCorePoolSize(corePoolSize);
            }
        }
    }

    /**
     * Return the ThreadPoolExecutor's maximum pool size.
     */
    public int getMaxPoolSize() {
        synchronized (this.poolSizeMonitor) {
            return this.maxPoolSize;
        }
    }

    /**
     * Set the ThreadPoolExecutor's maximum pool size.
     * Default is {@code Integer.MAX_VALUE}.
     * <p><b>This setting can be modified at runtime, for example through JMX.</b>
     */
    public void setMaxPoolSize(int maxPoolSize) {
        synchronized (this.poolSizeMonitor) {
            this.maxPoolSize = maxPoolSize;
            if (this.threadPoolExecutor != null) {
                this.threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
            }
        }
    }

    /**
     * Return the ThreadPoolExecutor's keep-alive seconds.
     */
    public int getKeepAliveSeconds() {
        synchronized (this.poolSizeMonitor) {
            return this.keepAliveSeconds;
        }
    }

    /**
     * Set the ThreadPoolExecutor's keep-alive seconds.
     * Default is 60.
     * <p><b>This setting can be modified at runtime, for example through JMX.</b>
     */
    public void setKeepAliveSeconds(int keepAliveSeconds) {
        synchronized (this.poolSizeMonitor) {
            this.keepAliveSeconds = keepAliveSeconds;
            if (this.threadPoolExecutor != null) {
                this.threadPoolExecutor.setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * Specify whether to allow core threads to time out. This enables dynamic
     * growing and shrinking even in combination with a non-zero queue (since
     * the max pool size will only grow once the queue is full).
     * <p>Default is "false".
     *
     * @see java.util.concurrent.ThreadPoolExecutor#allowCoreThreadTimeOut(boolean)
     */
    public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }

    /**
     * Set the capacity for the ThreadPoolExecutor's BlockingQueue.
     * Default is {@code Integer.MAX_VALUE}.
     * <p>Any positive value will lead to a LinkedBlockingQueue instance;
     * any other value will lead to a SynchronousQueue instance.
     *
     * @see java.util.concurrent.LinkedBlockingQueue
     * @see java.util.concurrent.SynchronousQueue
     */
    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    /**
     * Create the BlockingQueue to use for the ThreadPoolExecutor.
     * <p>A LinkedBlockingQueue instance will be created for a positive
     * capacity value; a SynchronousQueue else.
     *
     * @param queueCapacity the specified queue capacity
     * @return the BlockingQueue instance
     * @see java.util.concurrent.LinkedBlockingQueue
     * @see java.util.concurrent.SynchronousQueue
     */
    protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
        if (queueCapacity > 0) {
            return new LinkedBlockingQueue<Runnable>(queueCapacity);
        } else {
            return new SynchronousQueue<Runnable>();
        }
    }

    /**
     * Return the current pool size.
     *
     * @see java.util.concurrent.ThreadPoolExecutor#getPoolSize()
     */
    public int getPoolSize() {
        if (this.threadPoolExecutor == null) {
            // Not initialized yet: assume core pool size.
            return this.corePoolSize;
        }
        return this.threadPoolExecutor.getPoolSize();
    }

    /**
     * Return the number of currently active threads.
     *
     * @see java.util.concurrent.ThreadPoolExecutor#getActiveCount()
     */
    public int getActiveCount() {
        if (this.threadPoolExecutor == null) {
            // Not initialized yet: assume no active threads.
            return 0;
        }
        return this.threadPoolExecutor.getActiveCount();
    }

    @Override
    public void execute(Runnable task) {
        Executor executor = getThreadPoolExecutor();
        try {
            executor.execute(task);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        execute(task);
    }

    @Override
    public Future<?> submit(Runnable task) {
        ExecutorService executor = getThreadPoolExecutor();
        try {
            return executor.submit(task);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        ExecutorService executor = getThreadPoolExecutor();
        try {
            return executor.submit(task);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        ExecutorService executor = getThreadPoolExecutor();
        try {
            ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
            executor.execute(future);
            return future;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ExecutorService executor = getThreadPoolExecutor();
        try {
            ListenableFutureTask<T> future = new ListenableFutureTask<T>(task);
            executor.execute(future);
            return future;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    /**
     * This task executor prefers short-lived work units.
     */
    @Override
    public boolean prefersShortLivedTasks() {
        return true;
    }

    @Override
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        BlockingQueue<Runnable> queue = createQueue(this.queueCapacity);
        ThreadPoolExecutor executor = MdcThreadPoolExecutor.newWithInheritedMdc(
                this.corePoolSize, this.maxPoolSize, this.keepAliveSeconds, TimeUnit.SECONDS,
                queue, threadFactory, rejectedExecutionHandler);
        if (this.allowCoreThreadTimeOut) {
            executor.allowCoreThreadTimeOut(true);
        }

        this.threadPoolExecutor = executor;
        return executor;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() throws IllegalStateException {
        Assert.state(this.threadPoolExecutor != null, "ThreadPoolTaskExecutor not initialized");
        return this.threadPoolExecutor;
    }
}
