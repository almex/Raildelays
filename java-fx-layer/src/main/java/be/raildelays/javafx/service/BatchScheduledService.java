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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;

/**
 * {@link ScheduledService} to do unsynchronized call to {@link BatchStartAndRecoveryService}.
 *
 * @author Almex
 * @since 1.2
 */
public class BatchScheduledService extends ScheduledService<Integer> {
    private IntegerProperty count = new SimpleIntegerProperty();
    private BatchStartAndRecoveryService service;
    private JobExecution jobExecution;
    private String jobName;
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchScheduledService.class);

    public BatchScheduledService() {
        this.jobExecution = null;
    }

    public void start(String jobName, JobParameters jobParameters) {
        if (!isStarted()) {
            try {
                jobExecution = service.start(jobName, jobParameters);
            } catch (Exception e) {
                LOGGER.error("Error when starting the job: ", e);
            }
        }

        start();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    protected Task<Integer> createTask() {
        return new Task<Integer>() {
            protected Integer call() {
                final int counter = getCount();

                try {
                    jobExecution = service.refresh(jobExecution);
                } catch (Exception e) {
                    LOGGER.error("Error when retrieving last status of the job execution: ", e);
                }

                count.set(counter + 1);

                return counter;
            }
        };
    }

    @Override
    public void reset() {
        super.reset();
        count.set(0);
        jobExecution = null;
    }

    @Override
    public void restart() {
        try {
            if (isStarted()) {
                jobExecution = service.restart(jobExecution.getId());
            }
        } catch (Exception e) {
            LOGGER.error("Error when restarting the job execution!", e);
        }

        super.restart();
    }

    public boolean stop() {
        boolean result = false;

        try {
            if (isStarted()) {
                jobExecution = service.stop(jobExecution.getId());
                result = jobExecution.isStopping();
            }
        } catch (Exception e) {
            LOGGER.error("Error when stopping the job execution!", e);
        }

        cancel();

        return result;
    }

    public boolean abandon() {
        boolean result = false;

        try {
            if (isStarted()) {
                jobExecution = service.abandon(jobExecution.getId());
                result = jobExecution.getStatus().equals(BatchStatus.ABANDONED);
            }
        } catch (Exception e) {
            LOGGER.error("Error when stopping the job execution!", e);
        }

        cancel();

        return result;
    }

    public boolean isStarted() {
        return jobExecution != null;
    }

    public final Integer getCount() {
        return count.get();
    }

    public final JobExecution getJobExecution() {
        return jobExecution;
    }

    public final IntegerProperty countProperty() {
        return count;
    }

    public void setService(BatchStartAndRecoveryService service) {
        this.service = service;
    }
}