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

package org.springframework.batch.concurrent.scheduling;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.*;


/**
 * A SLF4J Mapped Diagnostic Context (MDC) compatible with {@link ThreadPoolExecutor} inspired by the source code
 * published by jlevy on
 * <a href="http://stackoverflow.com/questions/6073019/how-to-use-mdc-with-thread-pools">StackOverflow</a>
 * (Source code date: 6/14/2013).
 * <p>
 *     The goal of this implementation is to allow sub-threads to share the same {@link MDC} as the main-thread.
 *     Therefor, when you use Spring Batch partitioning, you allow sub-steps to communicate with your {@code Logger} via
 *     the {@code ThreadContext} (another word for MDC).
 * </p>
 *
 * @author Almex
 * @since 1.2
 */
public class MdcThreadPoolExecutor extends ThreadPoolExecutor {

    public MdcThreadPoolExecutor(int corePoolSize,
                                 int maximumPoolSize,
                                 long keepAliveTime,
                                 TimeUnit unit,
                                 BlockingQueue<Runnable> workQueue,
                                 ThreadFactory threadFactory,
                                 RejectedExecutionHandler rejectedExecutionHandler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, rejectedExecutionHandler);
    }

    /**
     * All executions will have MDC injected. {@code ThreadPoolExecutor}'s submission methods ({@code submit()} etc.)
     * all delegate to this.
     */
    @Override
    public void execute(Runnable command) {
        super.execute(wrap(command, MDC.getCopyOfContextMap()));
    }

    public static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();

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
        };
    }
}