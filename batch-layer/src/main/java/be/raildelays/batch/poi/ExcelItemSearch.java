package be.raildelays.batch.poi;

import be.raildelays.batch.reader.ExcelSheetItemReader;
import be.raildelays.batch.support.ItemSearch;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * @author Almex
 */
public class ExcelItemSearch<T extends Comparable<? super T>> implements ItemSearch<T> {

    private ResourceAwareItemReaderItemStream<? extends T> reader;

    public ExcelItemSearch() {
    }

    public int indexOf(T item, Resource resource) throws Exception {
        int result = -1;

        this.reader.setResource(resource);
        reader.open(new ExecutionContext());

        try {

            for (T object = reader.read() ; object != null ; object = reader.read() ) {
                if (item == null ? object == null : item.compareTo(object) == 0) {
                    if (reader instanceof ExcelSheetItemReader) {
                        result =  ((ExcelSheetItemReader) reader).getRowIndex();
                    }
                    break;
                }
            }

        } finally {
            reader.close();
        }

        return result;
    }

    public void setReader(ExcelSheetItemReader<T> reader) {
        this.reader = reader;
    }
}
