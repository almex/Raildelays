package be.raildelays.batch.retry;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;

/**
 * @author Almex
 * @since 1.2
 */
public class WaitRetryPolicy implements RetryPolicy {

    private long timer = 1000;

    private static class TimerRetryContext extends RetryContextSupport {
        public TimerRetryContext(RetryContext parent) {
            super(parent);
        }
    }

    @Override
    public boolean canRetry(RetryContext context) {
        try {
            Thread.sleep(timer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public RetryContext open(RetryContext parent) {
        return new TimerRetryContext(parent);
    }

    @Override
    public void close(RetryContext context) {

    }

    @Override
    public void registerThrowable(RetryContext context, Throwable throwable) {

    }

    public void setTimer(long timer) {
        this.timer = timer;
    }
}
