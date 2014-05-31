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

/**
* @author Almex
*/
public class WorkbookSearch<T extends Comparable<T>> {

    private ExcelSheetItemReader<T> reader;

    public WorkbookSearch(ExecutionContext executionContext) throws Exception {
        reader.afterPropertiesSet();
        reader.open(executionContext);
    }

    public int indexOf(T item) throws Exception {
        int result = -1;

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

        return result;
    }

//    private static int compare(Row row, ExcelRow excelRow) {
//        int result = 0;
//        Cell dateCell = null;
//        Cell stationFromCell = null;
//        Cell stationToCell = null;
//
//        if (row != null) {
//            dateCell = row.getCell(2);
//            stationFromCell = row.getCell(12);
//            stationToCell = row.getCell(18);
//        }
//
//        if (dateCell != null && stationFromCell != null && stationToCell != null) {
//            result = new CompareToBuilder()
//                    .append(dateCell.getDateCellValue(), excelRow == null ? null : excelRow.getDate())
//                    .append(stationFromCell.getStringCellValue(), excelRow == null ? "" : getStationName(excelRow.getDepartureStation()))
//                    .append(stationToCell.getStringCellValue(), excelRow == null ? "" : getStationName(excelRow.getArrivalStation()))
//                    .toComparison();
//        } else if (excelRow != null) {
//            result = 1;
//        }
//
//        return result;
//    }

    public void setReader(ExcelSheetItemReader<T> reader) {
        this.reader = reader;
    }
}
