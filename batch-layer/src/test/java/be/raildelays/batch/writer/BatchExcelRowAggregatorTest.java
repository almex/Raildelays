package be.raildelays.batch.writer;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(BlockJUnit4ClassRunner.class)
public class BatchExcelRowAggregatorTest {


    public static final long DELAY = 10L;
    public static final String TRAIN1 = "466";
    public static final String TRAIN2 = "516";
    public static final String SHEET_NAME = "new sheet";
    public static final int SHEET_INDEX = 0;
    public static final int ROW_INDEX = 0;

    private BatchExcelRowAggregator aggregator;

    private BatchExcelRow item;

    private Row row;

    private Workbook workbook;

    @Before
    public void setUp() throws Exception {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        workbook = new HSSFWorkbook();
        row = workbook.createSheet(SHEET_NAME).createRow(ROW_INDEX);
        row.createCell(2).setCellValue(new Date());
        row.createCell(12).setCellValue("Liège-Guillemins");
        row.createCell(18).setCellValue("Bruxelles-Central");
        row.createCell(24).setCellValue("Leuven");
        row.createCell(30).setCellValue("08");
        row.createCell(32).setCellValue("01");
        row.createCell(33).setCellValue("08");
        row.createCell(35).setCellValue("58");
        row.createCell(36).setCellValue("1717");
        row.createCell(39).setCellValue("477");
        row.createCell(42).setCellValue("08");
        row.createCell(44).setCellValue("05");
        row.createCell(45).setCellValue("09");
        row.createCell(47).setCellValue("18");
        row.createCell(48).setCellValue("1717");
        row.createCell(51).setCellValue("477");
        row.createCell(54).setCellValue(17L);

        item = new BatchExcelRow.Builder(dateFormat.parse("01/01/2000"), null) //
                .departureStation(new Station("BRUXELLES-CENTRAL")) //
                .arrivalStation(new Station("LIEGE-GUILLEMINS")) //
                .expectedDepartureTime(timeFormat.parse("14:00")) //
                .expectedArrivalTime(timeFormat.parse("15:00")) //
                .expectedTrain1(new Train(TRAIN1)) //
                .expectedTrain2(new Train(TRAIN2)) //
                .effectiveDepartureTime(timeFormat.parse("14:05")) //
                .effectiveArrivalTime(timeFormat.parse("15:15")) //
                .effectiveTrain1(new Train(TRAIN1)) //
                .effectiveTrain2(new Train(TRAIN2)) //
                .delay(DELAY) //
                .build();

        aggregator = new BatchExcelRowAggregator();
    }


    @Test
    public void testAggregateRow() throws Exception {
        aggregator.aggregate(item, workbook, SHEET_INDEX, ROW_INDEX);

        Row row = workbook.getSheetAt(SHEET_INDEX).getRow(ROW_INDEX);
        Assert.assertNotNull(row);
        Assert.assertEquals(TRAIN1, row.getCell(36).getStringCellValue());
        Assert.assertEquals(TRAIN1, row.getCell(48).getStringCellValue());
    }

    @Test
    public void testRoundTrip() throws Exception {
        BatchExcelRow batchExcelRow = new BatchExcelRowMapper().mapRow(workbook.getSheetAt(SHEET_INDEX).getRow(ROW_INDEX), ROW_INDEX);
        BatchExcelRow previousRow = aggregator.aggregate(item, workbook, SHEET_INDEX, ROW_INDEX);

        Assert.assertNotNull(previousRow);
        Assert.assertNotEquals(item, previousRow);
        Assert.assertNotNull(batchExcelRow);
        Assert.assertEquals(previousRow, batchExcelRow);
    }
}