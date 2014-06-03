package be.raildelays.batch.poi;

import be.raildelays.batch.reader.ExcelSheetItemReader;
import be.raildelays.batch.writer.ExcelSheetItemWriter;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.InitializingBean;

/**
* @author Almex
*/
public class WorkbookSearch<T extends Comparable<T>> implements InitializingBean {

    private ExcelSheetItemReader<T> reader;

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
