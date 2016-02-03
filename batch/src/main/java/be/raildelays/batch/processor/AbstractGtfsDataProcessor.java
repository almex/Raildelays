package be.raildelays.batch.processor;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;

import java.util.*;

/**
 * Group some useful static methods for all {@link ItemProcessor} dealing with GTFS data.
 *
 * @author Almex
 * @since 2.0
 */
public abstract class AbstractGtfsDataProcessor<I, O> implements ItemProcessor<I, O> {

    private static Map<ItemStreamReader<?>, List<?>> cache = new WeakHashMap<>(3);

    @SuppressWarnings("unchecked") // The caching doesn't have to know the type
    protected static <O> List<O> readAll(ItemStreamReader<O> reader) {
        List<O> result = (List<O>) cache.get(reader);

        // Cache Manager
        if (result == null) {
            result = doReadAll(reader);
            cache.put(reader, result);
        }

        return result;
    }

    protected static <O> List<O> doReadAll(ItemStreamReader<O> reader) {
        List<O> result = Collections.synchronizedList(new ArrayList<>());

        reader.open(new ExecutionContext());

        try {
            for (O actual = reader.read(); actual != null; actual = reader.read()) {
                result.add(actual);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Exception occurred when reading from a GTFS file", e);
        } finally {
            reader.close();
        }

        return result;
    }
}
