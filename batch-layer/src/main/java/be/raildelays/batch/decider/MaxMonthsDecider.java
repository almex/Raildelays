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

package be.raildelays.batch.decider;

import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Read all {@link be.raildelays.domain.xls.ExcelRow} given in {@link ItemReader} and compare the first date with today.
 * If the difference between those two dates is greater or equal to the {@code maxNumberOfMonth} then we return the status
 * {@link #COMPLETED_WITH_MAX_MONTHS} on which you can branch another flow.
 * <p>
 * The threshold {@link Date} is stored in the {@code jobExecutionContext} via the key {@code 'threshold.date'}.
 * <p>
 * You can use this class either as a {@link JobExecutionDecider} or a {@link Tasklet}.
 *
 * @author Almex
 * @since 1.2
 */
public class MaxMonthsDecider extends AbstractReadAndDecideTasklet<ExcelRow> implements InitializingBean {

    public static final ExitStatus COMPLETED_WITH_MAX_MONTHS = new ExitStatus("COMPLETED_WITH_MAX_MONTHS");
    private long maxNumberOfMonth;
    private Date firstDate;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(this.maxNumberOfMonth, "The 'maxNumberOfMonth' property must be provided");
        Assert.notNull(this.reader, "The 'reader' property must be provided");
    }

    @Override
    protected ExitStatus doRead(StepContribution contribution, ExecutionContext context, ExcelRow item) {
        ExitStatus result = ExitStatus.EXECUTING;
        Date thresholdDate = getMaxMonthsThresholdDate(item);

        if (thresholdDate != null) {
            // We store the threshold date in the context
            context.put("threshold.date", thresholdDate);
            // We keep trace that we've stored something
            contribution.incrementWriteCount(1);
            result = COMPLETED_WITH_MAX_MONTHS;
        }

        return result;
    }

    private Date getMaxMonthsThresholdDate(ExcelRow item) {
        Date result = null;

        if (item.getDate() != null) {
            if (firstDate == null || firstDate.after(item.getDate())) {
                firstDate = item.getDate();
            }

            final LocalDate startDate = firstDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            final LocalDate endDate = LocalDate.now();
            final Period period = Period.between(startDate, endDate);

            /**
             * If the period between the first date and today is greater or equal to maxNumberOfMonth
             */
            if (period.get(ChronoUnit.MONTHS) >= maxNumberOfMonth) {
                result = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
        }

        return result;
    }

    public void setMaxNumberOfMonth(long maxNumberOfMonth) {
        this.maxNumberOfMonth = maxNumberOfMonth;
    }
}

