package be.raildelays.batch.support;

import org.springframework.batch.item.*;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by soumagn on 31/05/2014.
 */
public abstract class AbstractItemCountingItemStreamItemWriter<T> extends AbstractItemStreamItemWriter<T> {

    private boolean saveState = true;

    private int currentItemIndex = 0;

    private int currentItemCount = 0;

    private int maxItemCount = Integer.MAX_VALUE;

    private static final String WRITE_COUNT = "write.count";
    private static final String WRITE_COUNT_MAX = "write.count.max";

    /**
     * Write item to a certain index.
     *
     * @return true if it's a new item, false if it has replaced something
     * @throws Exception
     */
    protected abstract boolean doWrite(T item) throws Exception;

    /**
     * Open resources necessary to start writing output.
     */
    protected abstract void doOpen() throws Exception;

    /**
     * Close the resources opened in {@link #doOpen()}.
     */
    protected abstract void doClose() throws Exception;

    /**
     * Move to the given item index. Subclasses should override this method if
     * there is a more efficient way of moving to given index than re-reading
     * the input using {@link #doWrite(T)}.
     */
    protected void jumpToItem(int itemIndex) throws Exception {
        this.currentItemIndex = itemIndex;
    }

    @Override
    public void write(List<? extends T> items) throws Exception, UnexpectedInputException, ParseException {
        for (T item : items) {
            if (item instanceof ItemIndexAware) {
                Long index = ((ItemIndexAware) item).getIndex();
                if (index != null) {
                    jumpToItem(index.intValue());
                }
            }

            if (currentItemCount < maxItemCount) {
                if (doWrite(item)) {
                    currentItemCount++;
                }
                currentItemIndex++;
            }
        }
    }

    protected int getCurrentItemCount() {
        return currentItemCount;
    }

    /**
     * The index of the item to start writing to. If the
     * {@link org.springframework.batch.item.ExecutionContext} contains a key <code>[name].write.count</code>
     * (where <code>[name]</code> is the name of this component) the value from
     * the {@link org.springframework.batch.item.ExecutionContext} will be used in preference.
     *
     * @see #setName(String)
     *
     * @param itemIndex the value of the current item index
     */
    public void setCurrentItemIndex(int itemIndex) {
        this.currentItemIndex = itemIndex;
    }

    /**
     * The maximum index of the items to be write. If the
     * {@link org.springframework.batch.item.ExecutionContext} contains a key
     * <code>[name].read.count.max</code> (where <code>[name]</code> is the name
     * of this component) the value from the {@link org.springframework.batch.item.ExecutionContext} will be
     * used in preference.
     *
     * @see #setName(String)
     *
     * @param count the value of the maximum item count
     */
    public void setMaxItemCount(int count) {
        this.maxItemCount = count;
    }

    @Override
    public void close() throws ItemStreamException {
        super.close();
        currentItemCount = 0;
        try {
            doClose();
        }
        catch (Exception e) {
            throw new ItemStreamException("Error while closing item writer", e);
        }
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        super.open(executionContext);
        try {
            doOpen();
        }
        catch (Exception e) {
            throw new ItemStreamException("Failed to initialize the reader", e);
        }
        if (!isSaveState()) {
            return;
        }

        if (executionContext.containsKey(getExecutionContextKey(WRITE_COUNT_MAX))) {
            maxItemCount = executionContext.getInt(getExecutionContextKey(WRITE_COUNT_MAX));
        }

        if (executionContext.containsKey(getExecutionContextKey(WRITE_COUNT))) {
            int itemCount = executionContext.getInt(getExecutionContextKey(WRITE_COUNT));

            currentItemCount = itemCount;

            if (itemCount < maxItemCount) {
                try {
                    jumpToItem(itemCount);
                }
                catch (Exception e) {
                    throw new ItemStreamException("Could not move to stored position on restart", e);
                }
            }

        }

    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        super.update(executionContext);
        if (saveState) {
            Assert.notNull(executionContext, "ExecutionContext must not be null");
            executionContext.putInt(getExecutionContextKey(WRITE_COUNT), currentItemCount);
            if (maxItemCount < Integer.MAX_VALUE) {
                executionContext.putInt(getExecutionContextKey(WRITE_COUNT_MAX), maxItemCount);
            }
        }

    }


    /**
     * Set the flag that determines whether to save internal data for
     * {@link ExecutionContext}. Only switch this to false if you don't want to
     * save any state from this stream, and you don't need it to be restartable.
     * Always set it to false if the reader is being used in a concurrent
     * environment.
     *
     * @param saveState flag value (default true).
     */
    public void setSaveState(boolean saveState) {
        this.saveState = saveState;
    }

    /**
     * The flag that determines whether to save internal state for restarts.
     * @return true if the flag was set
     */
    public boolean isSaveState() {
        return saveState;
    }

    protected int getCurrentItemIndex() {
        return currentItemIndex;
    }

    protected int getMaxItemCount() { return maxItemCount; }
}
