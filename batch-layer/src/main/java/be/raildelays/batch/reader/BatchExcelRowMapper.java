package be.raildelays.batch.reader;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.poi.RowMapper;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

/**
 * @autho Almex
 */
public class BatchExcelRowMapper implements RowMapper<BatchExcelRow> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchExcelRowMapper.class);

    private interface CellParser<T> {
        T getValue(Cell cell);
    }

    @Override
    public BatchExcelRow mapRow(Row row, int rowIndex) throws Exception {
        BatchExcelRow result = null;
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        NumberFormat numberFormat = new DecimalFormat("#");

        Train train2 = null;
        Train effectiveTrain2 = null;
        Station linkStation = null;


        if (row.getCell(2) != null && row.getCell(2).getDateCellValue() != null) {
            if (row.getCell(25) != null && row.getCell(25).getCellType() != Cell.CELL_TYPE_BLANK) {
                linkStation = new Station(row.getCell(25).getStringCellValue());
            }

            if (row.getCell(39) != null && row.getCell(39).getCellType() != Cell.CELL_TYPE_BLANK) {
                train2 = new Train(numberFormat.format(row.getCell(39).getNumericCellValue()));
            }

            if (row.getCell(51) != null && row.getCell(51).getCellType() != Cell.CELL_TYPE_BLANK) {
                effectiveTrain2 = new Train(numberFormat.format(row.getCell(51).getNumericCellValue()));
            }

            result = new BatchExcelRow.Builder(row.getCell(2).getDateCellValue(), null)
                    .departureStation(new Station(row.getCell(12).getStringCellValue()))
                    .arrivalStation(new Station(row.getCell(18).getStringCellValue()))
                    .linkStation(linkStation)
                    .expectedDepartureTime(timeFormat.parse(row.getCell(30).getStringCellValue() + ":" + row.getCell(32).getStringCellValue()))
                    .expectedArrivalTime(timeFormat.parse(row.getCell(33).getStringCellValue() + ":" + row.getCell(35).getStringCellValue()))
                    .expectedTrain1(new Train(numberFormat.format(row.getCell(36).getNumericCellValue())))
                    .expectedTrain2(train2)
                    .effectiveDepartureTime(timeFormat.parse(row.getCell(42).getStringCellValue() + ":" + row.getCell(44).getStringCellValue()))
                    .effectiveArrivalTime(timeFormat.parse(row.getCell(45).getStringCellValue() + ":" + row.getCell(47).getStringCellValue()))
                    .effectiveTrain1(new Train(numberFormat.format(row.getCell(48).getNumericCellValue())))
                    .effectiveTrain2(effectiveTrain2)
                    .delay((long) row.getCell(54).getNumericCellValue())
                    .index((long) row.getRowNum())
                    .build();
        } //-- If the first cell contains nothing we return null

        return result;
    }
}
