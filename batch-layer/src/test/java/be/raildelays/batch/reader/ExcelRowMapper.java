package be.raildelays.batch.reader;

import be.raildelays.batch.poi.RowMapper;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.poi.ss.usermodel.Row;

import java.text.SimpleDateFormat;

/**
* Created by soumagn on 30/05/2014.
*/
class ExcelRowMapper implements RowMapper<ExcelRow> {
    @Override
    public ExcelRow mapRow(Row row, int lineNumber) throws Exception {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Train train2 = null;
        Train effectiveTrain2 = null;
        Station linkStation = null;

        if (row.getCell(24) != null) {
            linkStation = new Station(row.getCell(24).getStringCellValue());
        }

        if (row.getCell(39) != null) {
            train2 = new Train(row.getCell(39).getStringCellValue());
        }

        if (row.getCell(51) != null) {
            effectiveTrain2 = new Train(row.getCell(51).getStringCellValue());
        }

        return new ExcelRow.Builder(row.getCell(2).getDateCellValue(), null)
                .departureStation(new Station(row.getCell(12).getStringCellValue()))
                .arrivalStation(new Station(row.getCell(18).getStringCellValue()))
                .linkStation(linkStation)
                .expectedDepartureTime(timeFormat.parse(row.getCell(30).getStringCellValue() + ":" + row.getCell(32).getStringCellValue()))
                .expectedArrivalTime(timeFormat.parse(row.getCell(33).getStringCellValue() + ":" + row.getCell(35).getStringCellValue()))
                .expectedTrain1(new Train(row.getCell(36).getStringCellValue()))
                .expectedTrain2(train2)
                .effectiveDepartureTime(timeFormat.parse(row.getCell(42).getStringCellValue() + ":" + row.getCell(44).getStringCellValue()))
                .effectiveDepartureTime(timeFormat.parse(row.getCell(45).getStringCellValue() + ":" + row.getCell(47).getStringCellValue()))
                .effectiveTrain1(new Train(row.getCell(48).getStringCellValue()))
                .effectiveTrain2(effectiveTrain2)
                .delay((long) row.getCell(53).getNumericCellValue())
                .build();
    }
}
