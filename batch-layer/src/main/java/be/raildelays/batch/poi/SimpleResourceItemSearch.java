package be.raildelays.batch.poi;

import be.raildelays.batch.support.IndexedResourceAwareItemStreamReader;
import be.raildelays.batch.support.ResourceItemSearch;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;

/**
 * @author Almex
 */
public class SimpleResourceItemSearch<T extends Comparable<? super T>> implements ResourceItemSearch<T> {

    private IndexedResourceAwareItemStreamReader<? extends T> reader;

    public SimpleResourceItemSearch() {
    }

    public int indexOf(T item, Resource resource) throws Exception {
        int result = -1;

        this.reader.setResource(resource);
        reader.open(new ExecutionContext());

        try {

            for (T object = reader.read() ; object != null ; object = reader.read() ) {
                if (item == null ? object == null : item.compareTo(object) == 0) {
                    result =  reader.getCurrentIndex();
                    break;
                }
            }

        } finally {
            reader.close();
        }

        return result;
    }

    public void setReader(IndexedResourceAwareItemStreamReader<T> reader) {
        this.reader = reader;
    }
}
