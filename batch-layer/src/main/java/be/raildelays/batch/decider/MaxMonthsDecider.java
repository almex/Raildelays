package be.raildelays.batch.decider;

import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContextRepeatCallback;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.support.RepeatTemplate;
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
public class MaxMonthsDecider implements JobExecutionDecider, InitializingBean, Tasklet {

    public static final ExitStatus COMPLETED_WITH_MAX_MONTHS = new ExitStatus("COMPLETED_WITH_MAX_MONTHS");
    private ItemStreamReader<ExcelRow> reader;
    private long maxNumberOfMonth;
    private Date firstDate;
    private boolean opened;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.maxNumberOfMonth, "The 'maxNumberOfMonth' property must be provided");
        Assert.notNull(this.reader, "The 'reader' property must be provided");
        opened = false;
    }

    @Override
    public FlowExecutionStatus decide(final JobExecution jobExecution, final StepExecution stepExecution) {
        StepContribution contribution = stepExecution.createStepContribution();

        new RepeatTemplate().iterate(
                new StepContextRepeatCallback(stepExecution) {
                    @Override
                    public RepeatStatus doInChunkContext(RepeatContext context, ChunkContext chunkContext) throws Exception {
                        return execute(contribution, chunkContext);
                    }
                });

        stepExecution.apply(contribution);

        return new FlowExecutionStatus(stepExecution.getExitStatus().getExitCode());
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        ExecutionContext context = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
        RepeatStatus result = RepeatStatus.CONTINUABLE;

        if (!opened) {
            reader.open(context);
            opened = true;
        }

        ExcelRow item = reader.read();

        reader.update(context);

        if (item != null) {
            Date thresholdDate = getMaxMonthsThresholdDate(item);

            if (thresholdDate != null) {
                // We store the threshold date in the context
                context.put("threshold.date", thresholdDate);

                result = finished(contribution, COMPLETED_WITH_MAX_MONTHS);
            }
        } else {
            result = finished(contribution, ExitStatus.COMPLETED);
        }

        reader.update(context);

        return result;
    }

    private Date getMaxMonthsThresholdDate(ExcelRow item) {
        Date result = null;

        if (item.getDate() != null) {
            if (firstDate == null || firstDate.after(item.getDate())) {
                firstDate = item.getDate();
            }

            LocalDate startDate = firstDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = LocalDate.now();
            Period period = Period.between(startDate, endDate);

            /**
             * If the period between the first date and today is greater or equal to maxNumberOfMonth
             */
            if (period.get(ChronoUnit.MONTHS) >= maxNumberOfMonth) {
                result = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
        }

        return result;
    }

    public RepeatStatus finished(StepContribution contribution, ExitStatus status) {
        contribution.setExitStatus(status);
        reader.close();
        opened = false;

        return RepeatStatus.FINISHED;
    }

    public void setReader(ItemStreamReader<ExcelRow> reader) {
        this.reader = reader;
    }

    public void setMaxNumberOfMonth(long maxNumberOfMonth) {
        this.maxNumberOfMonth = maxNumberOfMonth;
    }
}

