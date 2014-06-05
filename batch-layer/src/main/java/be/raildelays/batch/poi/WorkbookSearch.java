package be.raildelays.batch.poi;

import be.raildelays.batch.reader.ExcelSheetItemReader;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Almex
 */
public class WorkbookSearch<T extends Comparable<? super T>> implements InitializingBean {

    private ExcelSheetItemReader<? extends T> reader;

    private ExecutionContext executionContext;

    public WorkbookSearch(ExecutionContext executionContext) throws Exception {
        this.executionContext = executionContext;
    }

    public int indexOf(T item) throws Exception {
        int result = -1;

        reader.open(executionContext);

        try {
            T object = null;
            do {
                object = reader.read();

                if (item == null ? object == null : item.compareTo(object) == 0) {
                    result = reader.getRowIndex();
                    break;
                }
            } while (object != null);


        } finally {
            reader.close();
        }

        return result;
    }

    public void setReader(ExcelSheetItemReader<T> reader) {
        this.reader = reader;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        reader.afterPropertiesSet();
    }
}
