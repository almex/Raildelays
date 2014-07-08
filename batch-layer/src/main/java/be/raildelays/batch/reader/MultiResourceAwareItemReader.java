package be.raildelays.batch.reader;

import be.raildelays.batch.support.ResourceLocator;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.core.io.Resource;

/**
 * This implementation make the job not restartable
 *
 * @author Almex
 */
public class MultiResourceAwareItemReader<T> extends AbstractItemStreamItemReader<T> implements ResourceAwareItemReaderItemStream<T> {

    private ResourceLocator resourceLocator;

    private ResourceAwareItemReaderItemStream<? extends T> delegate;

    private ExecutionContext executionContext;

    private Resource previousResource;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        super.open(executionContext);
        this.executionContext = executionContext;
        previousResource = null;
    }

    @Override
    public T read() throws Exception {
        final Resource currentResource = resourceLocator.getResource(executionContext);

        if (previousResource == null || !previousResource.equals(currentResource)) {
            delegate.close();
            delegate.setResource(currentResource);
            delegate.open(executionContext);
            previousResource = currentResource;
        }

        return delegate.read();
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }


    public void setResourceLocator(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }

    public void setDelegate(ResourceAwareItemReaderItemStream<? extends T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setResource(Resource resource) {
        resourceLocator.setResource(resource);
    }
}
