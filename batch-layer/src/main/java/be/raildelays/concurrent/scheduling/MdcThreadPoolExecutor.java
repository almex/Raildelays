package be.raildelays.concurrent.scheduling;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.*;


/**
 * A SLF4J MDC-compatible {@link ThreadPoolExecutor}.
 * <p/>
 * In general, MDC is used to store diagnostic information (e.g. a user's session id) in per-thread variables, to facilitate
 * logging. However, although MDC data is passed to thread children, this doesn't work when threads are reused in a
 * thread pool. This is a drop-in replacement for {@link ThreadPoolExecutor} sets MDC data before each task appropriately.
 * <p/>
 * Created by jlevy.
 * Date: 6/14/13
 */
public class MdcThreadPoolExecutor extends ThreadPoolExecutor {

    final private boolean useFixedContext;
    final private Map<String, Object> fixedContext;

    /**
     * Pool where task threads take MDC from the submitting thread.
     */
    public static MdcThreadPoolExecutor newWithInheritedMdc(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                                            TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                                            ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        return new MdcThreadPoolExecutor(null, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, rejectedExecutionHandler);
    }

    /**
     * Pool where task threads take fixed MDC from the thread that creates the pool.
     */
    @SuppressWarnings("unchecked")
    public static MdcThreadPoolExecutor newWithCurrentMdc(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                                          TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                                          ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        return new MdcThreadPoolExecutor(MDC.getCopyOfContextMap(), corePoolSize, maximumPoolSize, keepAliveTime, unit,
                workQueue, threadFactory, rejectedExecutionHandler);
    }

    /**
     * Pool where task threads always have a specified, fixed MDC.
     */
    public static MdcThreadPoolExecutor newWithFixedMdc(Map<String, Object> fixedContext, int corePoolSize,
                                                        int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                                        BlockingQueue<Runnable> workQueue,
                                                        ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        return new MdcThreadPoolExecutor(fixedContext, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, rejectedExecutionHandler);
    }

    private MdcThreadPoolExecutor(Map<String, Object> fixedContext, int corePoolSize, int maximumPoolSize,
                                  long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, rejectedExecutionHandler);
        this.fixedContext = fixedContext;
        useFixedContext = (fixedContext != null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getContextForTask() {
        return useFixedContext ? fixedContext : MDC.getCopyOfContextMap();
    }

    /**
     * All executions will have MDC injected. {@code ThreadPoolExecutor}'s submission methods ({@code submit()} etc.)
     * all delegate to this.
     */
    @Override
    public void execute(Runnable command) {
        super.execute(wrap(command, getContextForTask()));
    }

    public static Runnable wrap(final Runnable runnable, final Map<String, Object> context) {
        return new Runnable() {
            @Override
            public void run() {
                Map previous = MDC.getCopyOfContextMap();
                if (context == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(context);
                }
                try {
                    runnable.run();
                } finally {
                    if (previous == null) {
                        MDC.clear();
                    } else {
                        MDC.setContextMap(previous);
                    }
                }
            }
        };
    }
}