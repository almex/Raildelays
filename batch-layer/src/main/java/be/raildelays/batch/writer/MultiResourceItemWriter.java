package be.raildelays.batch.writer;

import be.raildelays.batch.support.AbstractItemCountingItemStreamItemWriter;
import be.raildelays.batch.support.ResourceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Almex
 */
public class MultiResourceItemWriter<T> extends AbstractItemCountingItemStreamItemWriter<T> {

    protected ResourceLocator resourceLocator;

    private ResourceAwareItemWriterItemStream<? super T> delegate;

    private ExecutionContext executionContext;

    final static private String RESOURCE_INDEX_KEY = "resource.index";

    private int resourceIndex = 0;

    private boolean opened = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiResourceItemWriter.class);


    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
        super.open(executionContext);
    }

    @Override
    protected boolean doWrite(T item) throws Exception {
        List<T> items = new ArrayList<>();
        if (!opened) {
            File file = setResourceToDelegate();
            // create only if write is called
            file.createNewFile();
            resourceIndex++;
            Assert.state(file.canWrite(), "Output resource " + file.getAbsolutePath() + " must be writable");
            delegate.open(executionContext);
            opened = true;
        }

        int beforeITemCount = -1;
        if (delegate instanceof AbstractItemCountingItemStreamItemWriter) {
            beforeITemCount = ((AbstractItemCountingItemStreamItemWriter) delegate).getCurrentItemCount();
        }

        items.add(item);
        delegate.write(items);

        int afterITemCount = 0;
        if (delegate instanceof AbstractItemCountingItemStreamItemWriter) {
            afterITemCount = ((AbstractItemCountingItemStreamItemWriter) delegate).getCurrentItemCount();
        }

        if (delegate instanceof AbstractItemCountingItemStreamItemWriter ) {
            if (((AbstractItemCountingItemStreamItemWriter) delegate).getCurrentItemCount() >=
                    ((AbstractItemCountingItemStreamItemWriter) delegate).getMaxItemCount()) {
                delegate.close();
                delegate.update(executionContext);
                opened = false;
            }
        }



        return beforeITemCount < afterITemCount;
    }

    /**
     * Create output resource (if necessary) and point the delegate to it.
     */
    private File setResourceToDelegate() throws IOException {
        Resource localResource = resourceLocator.getResource(executionContext);

        delegate.setResource(localResource);

        return localResource.getFile();
    }

    @Override
    protected void doOpen() throws Exception {
        resourceIndex = executionContext.getInt(getExecutionContextKey(RESOURCE_INDEX_KEY), 0);

        if (executionContext.containsKey(getExecutionContextKey(RESOURCE_INDEX_KEY))) {
            // It's a restart
            delegate.open(executionContext);
            // We don't have to create the resource
            opened = true;
        }
        else {
            opened = false;
        }
    }

    @Override
    protected void doClose() throws Exception {
        resourceIndex = 0;
        setCurrentItemIndex(0);
        if (opened) {
            delegate.close();
            opened = false;
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        if (isSaveState()) {
            if (opened) {
                delegate.update(executionContext);
            }
            executionContext.putInt(getExecutionContextKey(RESOURCE_INDEX_KEY), resourceIndex);
        }
    }

    public void setResourceLocator(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }

    public void setDelegate(ResourceAwareItemWriterItemStream<? super T> delegate) {
        this.delegate = delegate;
    }
}
