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
 * Read all {@link be.raildelays.domain.xls.ExcelRow} given in {@link ItemReader} and store the start date and the end date.
 * If the difference between start and end is greater or equal to the {@code maxNumberOfMonth} then we return the status
 * {@link #COMPLETED_WITH_MAX_MONTHS} on which you can branch another flow.
 *
 * The threshold {@link Date} is stored in the {@code jobExecutionContext} via the key {@code 'threshold.date'}.
 *
 * @author Almex
 * @since 1.2
 */
public class MaxMonthsDecider implements JobExecutionDecider, InitializingBean {

    public static final FlowExecutionStatus COMPLETED_WITH_MAX_MONTHS = new FlowExecutionStatus("COMPLETED_WITH_MAX_MONTHS");
    private ItemReader<ExcelRow> reader;
    private Date minimum;
    private Date maximum;
    private long maxNumberOfMonth;
    // Must be a field to be modifiable inside a lambda expression
    private FlowExecutionStatus finalStatus = FlowExecutionStatus.COMPLETED;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.maxNumberOfMonth, "The 'maxNumberOfMonth' property must be provided");
        Assert.notNull(this.reader, "The 'reader' property must be provided");
    }

    @Override
    public FlowExecutionStatus decide(final JobExecution jobExecution, final StepExecution stepExecution) {
        new RepeatTemplate().iterate((context) -> {
            RepeatStatus result = RepeatStatus.CONTINUABLE;
            ExcelRow item = reader.read();

            if (item != null) {
                if (maximum == null || maximum.before(item.getDate())) {
                    maximum = item.getDate();
                }

                if (minimum == null || minimum.after(item.getDate())) {
                    minimum = item.getDate();
                }

                LocalDate startDate = minimum.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endDate = maximum.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Period period = Period.between(startDate, endDate);

                if (period.get(ChronoUnit.MONTHS) >= maxNumberOfMonth) {
                    this.finalStatus = COMPLETED_WITH_MAX_MONTHS;
                    result = RepeatStatus.FINISHED;
                    jobExecution.getExecutionContext().put("threshold.date", maximum);
                }
            } else {
                result = RepeatStatus.FINISHED;
            }

            return result;
        });

        return this.finalStatus;
    }

    public void setReader(ItemReader<ExcelRow> reader) {
        this.reader = reader;
    }

    public void setMaxNumberOfMonth(long maxNumberOfMonth) {
        this.maxNumberOfMonth = maxNumberOfMonth;
    }

}
