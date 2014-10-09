package be.raildelays.batch.processor;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Comparator;

/**
 * @author Almex
 * @since 1.2
 */
public class FilterItemInContextProcessor<T> implements ItemProcessor<T, T>, InitializingBean {

    private String keyName;
    private ExecutionContext executionContext;
    private Comparator<T> comparator;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.keyName, "The 'keyName' property must be provided");
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.executionContext = stepExecution.getExecutionContext();
    }

    @Override
    public T process(T item) throws Exception {
        T result = item;

        if (executionContext.containsKey(keyName)) {
            if (comparator.compare((T) executionContext.get(keyName), item) == 0) {
                result = null;
            }
        }

        return result;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }
}