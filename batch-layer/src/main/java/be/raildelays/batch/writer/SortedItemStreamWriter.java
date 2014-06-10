package be.raildelays.batch.writer;

import be.raildelays.batch.support.ItemIndexAware;
import org.apache.commons.lang.Validate;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemCountAware;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Write items into a certain order using a {@link Comparator}.</br>
 * <br/>
 * To achieve that goal this writer during {@link SortedItemStreamWriter#write(List)}:
 * <ul>
 * <li>Read entirely content of the resource</li>
 * <li>Add new items</li>
 * <li>Sort all items in memory</li>
 * <li>Write all items in a temporary resource</li>
 * <li>Delete the original resource and rename the temporary resource as the original resource</li>
 * </ul>
 * <br/>
 * This writer is on itself restartable and therefor is not a real {@link ItemStream}.
 * Only the {@link ItemStream#open(ExecutionContext)} method is used to have a
 * reference to the {@link ExecutionContext} during {@link SortedItemStreamWriter#write(List)}.
 */
public class SortedItemStreamWriter<T> implements ResourceAwareItemWriterItemStream<T>, InitializingBean {

    protected ResourceAwareItemWriterItemStream<T> writer;

    protected ResourceAwareItemReaderItemStream<T> reader;

    protected Resource resource;

    private Resource tempResource;

    private ExecutionContext executionContext;

    protected Comparator<? super T> comparator;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(writer,
                "You must provide a writer before using this bean");
        Validate.notNull(reader,
                "You must provide a reader before using this bean");
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
    }

    @Override
    public void write(List<? extends T> items) throws Exception {
        initializeStreams();

        try {
            List<T> allItems = replaceItems(readContent(), items);
            sort(allItems);
            writeAll(allItems);

            commit();
        } catch (Exception e) {
            rollback();
            throw e;
        }
    }

    @Override
    public void close() throws ItemStreamException {

    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    private void sort(List<T> allItems) {
        Collections.sort(allItems, comparator);
        indexItems(allItems);
    }

    private List<T> replaceItems(List<T> content, List<? extends T> items) {
        List<T> allItems = new ArrayList<>();

        allItems.addAll(content);

        for (T item : items) {
            if (item instanceof ItemIndexAware) {
                Long index = ((ItemIndexAware) item).getIndex();

                if (index != null) {
                    /**
                     * We know here that expect to replace a item and we must do it before sorting.
                     */
                    allItems.remove(index.intValue());
                    allItems.add(index.intValue(), item);
                } else {
                    allItems.add(item);
                }
            } else {
                allItems.add(item);
            }
        }

        return allItems;
    }

    private void indexItems(List<T> items) {
        for (T item : items) {
            if (item instanceof ItemCountAware) {
                ((ItemCountAware) item).setItemCount(items.indexOf(item));
            } else {
                /**
                 * Optimization: do not need to loop over all items if one of them is not of the good type
                 */
                break;
            }
        }
    }

    private List<T> readContent() throws Exception {
        List<T> result = new ArrayList<>();

        try {
            reader.open(executionContext);

            for (T item = reader.read(); item != null; item = reader.read()) {
                result.add(item);
            }
        } finally {
            reader.close();
        }

        return result;
    }

    private void initializeStreams() throws Exception {
        tempResource = resource.createRelative(resource.getFilename() + ".tmp");

        reader.setResource(resource);
        writer.setResource(tempResource);
    }

    private void writeAll(List<? extends T> items) throws Exception {
        try {
            writer.open(executionContext);
            writer.write(items);
        } finally {
            writer.close();
        }
    }

    private void commit() throws Exception {
        File tempFile = tempResource.getFile();
        File outputFile = resource.getFile();
        File directory = outputFile.getParentFile();
        File backupFile = new File(directory, resource.getFilename() + ".bak");

        if (outputFile.renameTo(backupFile)) {
            if (tempFile.renameTo(outputFile)) {
                if (!backupFile.delete()) {
                    backupFile.renameTo(outputFile);
                    throw new IllegalStateException("Commit failure: we were not able to delete the original file");
                }
            } else {
                throw new IllegalStateException("Commit failure: we were not able to rename the temporary file");
            }
        } else {
            throw new IllegalStateException("Commit failure: we were not able to rename the original file");
        }
    }

    private void rollback() throws Exception {
        if (!tempResource.getFile().delete()) {
            throw new IllegalStateException("Rollback failure: we were not able to delete the temporary file");
        }
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setWriter(ResourceAwareItemWriterItemStream<T> writer) {
        this.writer = writer;
    }

    public void setReader(ResourceAwareItemReaderItemStream<T> reader) {
        this.reader = reader;
    }
}
