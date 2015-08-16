package be.raildelays.batch.reader;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.writer.BatchExcelRowAggregator;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
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
public class BatchExcelRowMapperTest {

    public static final long DELAY = 10L;
    public static final long TRAIN1 = 466l;
    public static final long TRAIN2 = 516L;
    public static final String SHEET_NAME = "new sheet";
    public static final int SHEET_INDEX = 0;
    public static final int ROW_INDEX = 0;

    private BatchExcelRowMapper mapper;

    private Row row;

    private Workbook workbook;

    @Before
    public void setUp() {
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
        row.createCell(36).setCellValue(TRAIN1);
        row.createCell(39).setCellValue(TRAIN2);
        row.createCell(42).setCellValue("08");
        row.createCell(44).setCellValue("05");
        row.createCell(45).setCellValue("09");
        row.createCell(47).setCellValue("18");
        row.createCell(48).setCellValue(TRAIN1);
        row.createCell(51).setCellValue(TRAIN2);
        row.createCell(54).setCellValue(DELAY);

        mapper = new BatchExcelRowMapper();
    }


    @Test
    public void testMapRow() throws Exception {
        NumberFormat format = new DecimalFormat("#");

        BatchExcelRow batchExcelRow = mapper.mapRow(row, 0);

        Assert.assertNotNull(batchExcelRow);
        Assert.assertEquals(DELAY, batchExcelRow.getDelay().longValue());
        Assert.assertEquals(format.format(TRAIN1), batchExcelRow.getEffectiveTrain1().getEnglishName());
    }

    @Test
    public void testRoundTrip() throws Exception {
        final DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        BatchExcelRow expected = new BatchExcelRow.Builder(dateFormat.parse("01/01/2000"), null) //
                .departureStation(new Station("BRUXELLES-CENTRAL")) //
                .arrivalStation(new Station("LIEGE-GUILLEMINS")) //
                .expectedDepartureTime(timeFormat.parse("14:00")) //
                .expectedArrivalTime(timeFormat.parse("15:00")) //
                .expectedTrain1(new Train("529")) //
                .expectedTrain2(new Train("516")) //
                .effectiveDepartureTime(timeFormat.parse("14:05")) //
                .effectiveArrivalTime(timeFormat.parse("15:15")) //
                .effectiveTrain1(new Train("529")) //
                .effectiveTrain2(new Train("516")) //
                .delay(10L) //
                .build();


        BatchExcelRow previousRow = new BatchExcelRowAggregator().aggregate(expected, workbook, SHEET_INDEX, ROW_INDEX);
        BatchExcelRow batchExcelRow = mapper.mapRow(workbook.getSheetAt(SHEET_INDEX).getRow(ROW_INDEX), ROW_INDEX);

        Assert.assertNotNull(previousRow);
        Assert.assertNotEquals(expected, previousRow);
        Assert.assertNotNull(batchExcelRow);
        Assert.assertNotEquals(batchExcelRow, previousRow);
        Assert.assertEquals(expected, batchExcelRow);
    }
}