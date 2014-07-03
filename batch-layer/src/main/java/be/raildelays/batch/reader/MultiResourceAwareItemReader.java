package be.raildelays.batch.reader;

import be.raildelays.batch.support.ResourceLocator;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @author Almex
 */
public class MultiResourceAwareItemReader<T> extends AbstractItemStreamItemReader<T> implements ResourceAwareItemReaderItemStream<T> {

    private ResourceLocator resourceLocator;

    private ResourceAwareItemReaderItemStream<? extends T> delegate;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        super.open(executionContext);

        try {
            delegate.setResource(resourceLocator.getResource(executionContext));
        } catch (IOException e) {
            throw new ItemStreamException("Couldn't assign resource", e);
        }

        delegate.open(executionContext);
    }

    @Override
    public T read() throws Exception {
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
