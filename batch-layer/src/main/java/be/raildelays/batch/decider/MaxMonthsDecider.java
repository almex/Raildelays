package be.raildelays.batch.decider;

import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.item.ItemReader;
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
 * <p/>
 * The threshold {@link Date} is stored in the {@code jobExecutionContext} via the key {@code 'threshold.date'}.
 *
 * @author Almex
 * @since 1.2
 */
public class MaxMonthsDecider implements JobExecutionDecider, InitializingBean {

    public static final FlowExecutionStatus COMPLETED_WITH_MAX_MONTHS = new FlowExecutionStatus("COMPLETED_WITH_MAX_MONTHS");
    private ItemReader<ExcelRow> reader;
    private long maxNumberOfMonth;
    private FlowExecutionStatus finalStatus; // To be accessible by the Lambda expression
    private Date firstDate; // To be accessible by the Lambda expression

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.maxNumberOfMonth, "The 'maxNumberOfMonth' property must be provided");
        Assert.notNull(this.reader, "The 'reader' property must be provided");
    }

    @Override
    public FlowExecutionStatus decide(final JobExecution jobExecution, final StepExecution stepExecution) {
        final LocalDate now = LocalDate.now();

        finalStatus = FlowExecutionStatus.COMPLETED; // By default it's COMPLETED

        new RepeatTemplate().iterate((context) -> {
            RepeatStatus result = RepeatStatus.CONTINUABLE;
            ExcelRow item = reader.read();

            if (item != null) {
                if (firstDate == null || firstDate.after(item.getDate())) {
                    firstDate = item.getDate();
                }

                LocalDate startDate = firstDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endDate = now;
                Period period = Period.between(startDate, endDate);

                /**
                 * If the period between the first date and today is greater or equal to maxNumberOfMonth
                 */
                if (period.get(ChronoUnit.MONTHS) >= maxNumberOfMonth) {
                    finalStatus = COMPLETED_WITH_MAX_MONTHS;
                    result = RepeatStatus.FINISHED;
                    // We store the threshold date in the context
                    jobExecution.getExecutionContext().put("threshold.date", Date.from(
                                    now.atStartOfDay(ZoneId.systemDefault()).toInstant()
                            )
                    );
                }
            } else {
                result = RepeatStatus.FINISHED;
            }

            return result;
        });

        return finalStatus;
    }

    public void setReader(ItemReader<ExcelRow> reader) {
        this.reader = reader;
    }

    public void setMaxNumberOfMonth(long maxNumberOfMonth) {
        this.maxNumberOfMonth = maxNumberOfMonth;
    }

}

