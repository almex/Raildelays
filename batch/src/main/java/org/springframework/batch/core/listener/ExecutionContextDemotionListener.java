package org.springframework.batch.core.listener;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * This class can be used to automatically demote items from the {@link org.springframework.batch.core.Job}
 * {@link org.springframework.batch.item.ExecutionContext} to the {@link org.springframework.batch.core.Step}
 * {@link org.springframework.batch.item.ExecutionContext} at the
 * end of a step. A list of keys should be provided that correspond to the items
 * in the {@link org.springframework.batch.core.Step} {@link org.springframework.batch.item.ExecutionContext}
 * that should be demoted.
 *
 * @author Almex
 * @since 1.2
 */
public class ExecutionContextDemotionListener extends StepExecutionListenerSupport implements InitializingBean {
    private String[] keys = null;

    private boolean strict = false;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext stepContext = stepExecution.getExecutionContext();
        ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();

        for (String key : keys) {
            if (jobContext.containsKey(key)) {
                stepContext.put(key, jobContext.get(key));
            } else {
                if (strict) {
                    throw new IllegalArgumentException("The key [" + key
                            + "] was not found in the Step's ExecutionContext.");
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.keys, "The 'keys' property must be provided");
        Assert.notEmpty(this.keys, "The 'keys' property must not be empty");
    }

    /**
     * @param keys A list of keys corresponding to items in the {@link org.springframework.batch.core.Step}
     * {@link ExecutionContext} that must be promoted.
     */
    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    /**
     * If set to TRUE, the listener will throw an exception if any 'key' is not
     * found in the Step {@link ExecutionContext}. FALSE by default.
     *
     * @param strict
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }
}
