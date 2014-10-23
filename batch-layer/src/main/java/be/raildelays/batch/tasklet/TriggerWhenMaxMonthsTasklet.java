package be.raildelays.batch.tasklet;

import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Read all {@link be.raildelays.domain.xls.ExcelRow} given in input and store the start date and the end date.
 * If the difference between start and end is greater or equal to the {@code maxNumberOfMonth} then we store in the
 * {@link org.springframework.batch.item.ExecutionContext} the value {@code true} with the given {@code keyName}.
 *
 * @author Almex
 * @since 1.2
 */
public class TriggerWhenMaxMonthsTasklet implements Tasklet {

    private ItemReader<ExcelRow> reader;
    private String keyName;
    private Date minimum;
    private Date maximum;
    private long maxNumberOfMonth;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        final ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
        ExcelRow item = reader.read();

        if (maximum == null || maximum.before(item.getDate())) {
            maximum = item.getDate();
        }

        if (minimum == null || minimum.after(item.getDate())) {
            minimum = item.getDate();
        }

        if (minimum.toInstant().until(maximum.toInstant(), ChronoUnit.MONTHS) >= maxNumberOfMonth ) {
            executionContext.putString(keyName, "true");
        } else {
            executionContext.remove(keyName);
        }

        return item != null ? RepeatStatus.CONTINUABLE : RepeatStatus.FINISHED;
    }

    public void setReader(ItemReader<ExcelRow> reader) {
        this.reader = reader;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setMaxNumberOfMonth(long maxNumberOfMonth) {
        this.maxNumberOfMonth = maxNumberOfMonth;
    }
}
