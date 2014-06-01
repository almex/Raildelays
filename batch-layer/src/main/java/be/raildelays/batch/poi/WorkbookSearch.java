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

        if (item != null) {
            try {
                T object = reader.read();
                while (object != null) {
                    if (item.compareTo(object) == 0) {
                        result = reader.getRowIndex();
                        break;
                    }
                    object = reader.read();
                }
            } finally {
                reader.close();
            }
        }

        return result;
    }

    public void setReader(ExcelSheetItemReader<T> reader) {
        this.reader = reader;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        reader.afterPropertiesSet();
        reader.open(executionContext);
    }
}
