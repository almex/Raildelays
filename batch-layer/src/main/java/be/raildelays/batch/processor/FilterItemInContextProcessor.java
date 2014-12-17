package be.raildelays.batch.processor;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.Map;

/**
 * If the input item match the one retrieved from the {@link org.springframework.batch.item.ExecutionContext} then it's
 * filtered.
 *
 * @author Almex
 * @since 1.2
 * @param <T> type of the Input/Output of this {@link org.springframework.batch.item.ItemProcessor}
 */
public class FilterItemInContextProcessor<T extends Comparable<T>> implements ItemProcessor<T, T>, InitializingBean {

    private String keyName;
    private ExecutionContext executionContext;
    // By default we use the natural order
    private Comparator<T> comparator = new Comparator<T>() {
        @Override
        public int compare(T lho, T rho) {
            return lho.compareTo(rho);
        }
    };

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.keyName, "The 'keyName' property must be provided");
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.executionContext = stepExecution.getExecutionContext();
    }

    @Override
    @SuppressWarnings("unchecked") // ClassCastException must be thrown if the value in the context is of the wrong type
    public T process(T item) throws Exception {
        T result = item;

        if (executionContext.containsKey(keyName)) {
            for (T object : ((Map<?, T>) executionContext.get(keyName)).values()) {
                if (comparator.compare(object, item) == 0) {
                    result = null;
                }
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
