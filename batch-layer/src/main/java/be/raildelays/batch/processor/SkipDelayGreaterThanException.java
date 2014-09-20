package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;

/**
 * @author Almex
 * @since 1.2
 */
public class SkipDelayGreaterThanException extends Exception {

    private BatchExcelRow skippedItem;

    private Long delayThreshold;

    @Override
    public String getMessage() {
        return "The item " + (skippedItem != null ? skippedItem.toString()  : "null")
                + " has been skipped because its delay was greater than "
                + (delayThreshold != null ? delayThreshold : "unknown");
    }

    public SkipDelayGreaterThanException(BatchExcelRow skippedItem, Long delayThreshold) {
        this.delayThreshold = delayThreshold;
        this.skippedItem = skippedItem;
    }

    public void setSkippedItem(BatchExcelRow skippedItem) {
        this.skippedItem = skippedItem;
    }

    public void setDelayThreshold(Long delayThreshold) {
        this.delayThreshold = delayThreshold;
    }
}
