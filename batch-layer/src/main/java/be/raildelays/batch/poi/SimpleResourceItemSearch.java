package be.raildelays.batch.poi;

import be.raildelays.batch.support.IndexedResourceAwareItemStreamReader;
import be.raildelays.batch.support.ResourceItemSearch;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;

import java.util.Comparator;

/**
 * @author Almex
 */
public class SimpleResourceItemSearch<T extends Comparable<? super T>> implements ResourceItemSearch<T> {

    protected Comparator<? super T> comparator = new Comparator<T>() {
        @Override
        public int compare(T o1, T o2) {
            int result = 0;

            if (o1 != null) {
                result = o1.compareTo(o2);
            } else {
                result = o2 != null ? 1 : 0;
            }

            return result;
        }
    };
    private IndexedResourceAwareItemStreamReader<? extends T> reader;

    public SimpleResourceItemSearch() {
    }

    public int indexOf(T item, Resource resource) throws Exception {
        int result = -1;

        this.reader.setResource(resource);
        reader.open(new ExecutionContext());

        try {
            T object = null;

            do {
                object = reader.read();

                if (item == null ? object == null : comparator.compare(item, object) == 0) {
                    result = reader.getCurrentIndex();
                    break;
                }
            } while (object != null);

        } finally {
            reader.close();
        }

        return result;
    }

    public void setReader(IndexedResourceAwareItemStreamReader<T> reader) {
        this.reader = reader;
    }

    public void setComparator(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }
}
