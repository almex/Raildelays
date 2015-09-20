package org.springframework.batch.item.support;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.*;

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
public class SortedItemStreamWriter<T extends Comparable<T>>
        extends ItemStreamSupport
        implements ResourceAwareItemWriterItemStream<T>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SortedItemStreamWriter.class);
    protected ResourceAwareItemWriterItemStream<? super T> writer;
    protected ResourceAwareItemReaderItemStream<? extends T> reader;
    protected Resource resource;
    protected Comparator<? super T> comparator = Comparator.<T>naturalOrder();
    private Resource outputResource;
    private ExecutionContext executionContext;
    private boolean useTemporaryFile = false;

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
        } catch (Exception e) {
            rollback(e);
        }

        commit();
    }

    private void sort(List<T> allItems) {
        Collections.sort(allItems, comparator);
        indexItems(allItems);

        LOGGER.trace("Items sorted={}", allItems.size());
    }

    private List<T> replaceItems(Map<Long, T> content, List<? extends T> items) {
        List<T> allItems = new ArrayList<>();

        allItems.addAll(content.values());

        for (T item : items) {
            Long index = null;

            if (item instanceof ItemIndexAware) {
                index = ((ItemIndexAware) item).getIndex();
            }

            if (index != null) {
                /**
                 * We know here that expect to replace an item and we must do it before sorting.
                 */
                if (content.containsKey(index)) {
                    allItems.remove(content.get(index));

                    LOGGER.debug("Removed item on index={}", index);
                }
            }

            allItems.add(item);

            LOGGER.trace("Item added: {}", item);
        }

        return allItems;
    }

    private void indexItems(List<T> items) {
        for (T item : items) {
            if (item instanceof ItemCountAware) {
                final int index = items.indexOf(item);

                ((ItemCountAware) item).setItemCount(index);

                LOGGER.trace("Indexed item to={}", index);
            } else {
                /**
                 * Optimization: do not need to loop over all items if one of them is not of the good type
                 */
                break;
            }
        }
    }

    private Map<Long, T> readContent() throws Exception {
        Map<Long, T> result = new HashMap<>();

        try {
            reader.open(executionContext);

            long i = 0;
            for (T item = reader.read(); item != null; item = reader.read(), i++) {
                Long index = null;

                if (item instanceof ItemIndexAware) {
                    index = ((ItemIndexAware) item).getIndex();

                    LOGGER.trace("Retrieving existing index={}", index);
                }

                if (index == null) {
                    index = i;

                    LOGGER.trace("Setting new index={}", index);
                }

                result.put(index, item);

                LOGGER.debug("Read content : line={}, item={}", index, item);
            }
        } finally {
            reader.close();
        }

        return result;
    }

    private void initializeStreams() throws Exception {
        if (useTemporaryFile) {
            outputResource = new FileSystemResource(File.createTempFile(resource.getFilename(), ".tmp", resource.getFile().getParentFile()));
        } else {
            outputResource = resource;
        }

        reader.setResource(resource);
        writer.setResource(outputResource);
    }

    private void writeAll(List<? extends T> items) throws Exception {
        try {
            writer.open(executionContext);
            writer.write(items);

            LOGGER.debug("Written {} items", items.size());
        } finally {
            writer.close();
        }
    }

    private void commit() throws Exception {
        if (useTemporaryFile) {
            File tempFile = outputResource.getFile();
            File outputFile = resource.getFile();
            File directory = outputFile.getParentFile();
            File backupFile = new File(directory, resource.getFilename() + ".bak");

            if (!outputFile.renameTo(backupFile)
                    || !tempFile.renameTo(outputFile)
                    || !backupFile.delete()
                    ) {
                boolean renamed = backupFile.renameTo(outputFile);

                LOGGER.error("Commit failure: we were not able to delete the original file (renamed={})", renamed);
            }
        }
    }

    private void rollback(Exception e) throws Exception {
        if (useTemporaryFile && !outputResource.getFile().delete()) {
            throw new IllegalStateException("Rollback failure: we were not able to delete the temporary file", e);
        } else {
            throw e;
        }
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setWriter(ResourceAwareItemWriterItemStream<? super T> writer) {
        this.writer = writer;
    }

    public void setReader(ResourceAwareItemReaderItemStream<? extends T> reader) {
        this.reader = reader;
    }

    /**
     * @param comparator specify the comparison used to sort all items. By default we use the
     *                   {@linkplain Comparator#naturalOrder() natural order}.
     */
    public void setComparator(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    public void setUseTemporaryFile(boolean useTemporaryFile) {
        this.useTemporaryFile = useTemporaryFile;
    }
}
