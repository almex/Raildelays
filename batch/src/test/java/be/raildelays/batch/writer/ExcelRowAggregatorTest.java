package be.raildelays.batch.writer;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@RunWith(BlockJUnit4ClassRunner.class)
public class ExcelRowAggregatorTest {


    public static final long DELAY = 10L;
    public static final Long TRAIN1 = 466L;
    public static final Long TRAIN2 = 516L;
    public static final String SHEET_NAME = "new sheet";
    public static final int SHEET_INDEX = 0;
    public static final int ROW_INDEX = 0;

    private ExcelRowAggregator aggregator;

    private BatchExcelRow item;

    private Row row;

    private Workbook workbook;

    @Before
    public void setUp() throws Exception {
        workbook = new HSSFWorkbook();
        row = workbook.createSheet(SHEET_NAME).createRow(ROW_INDEX);

        initRow(row);

        item = new BatchExcelRow.Builder(LocalDate.parse("2000-01-01"), null) //
                .departureStation(new Station("BRUXELLES-CENTRAL")) //
                .arrivalStation(new Station("LIEGE-GUILLEMINS")) //
                .expectedDepartureTime(LocalTime.parse("14:00")) //
                .expectedArrivalTime(LocalTime.parse("15:00")) //
                .expectedTrain1(new TrainLine.Builder(TRAIN1).build()) //
                .expectedTrain2(new TrainLine.Builder(TRAIN2).build()) //
                .effectiveDepartureTime(LocalTime.parse("14:05")) //
                .effectiveArrivalTime(LocalTime.parse("15:15")) //
                .effectiveTrain1(new TrainLine.Builder(TRAIN1).build()) //
                .effectiveTrain2(new TrainLine.Builder(TRAIN2).build()) //
                .delay(DELAY) //
                .build();

        aggregator = new ExcelRowAggregator();
    }

    private static void initRow(Row row) {
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
    }


    @Test
    public void testAggregateRow() throws Exception {
        aggregator.aggregate(item, workbook, SHEET_INDEX, ROW_INDEX);

        Row row = workbook.getSheetAt(SHEET_INDEX).getRow(ROW_INDEX);
        Assert.assertNotNull(row);
        Assert.assertEquals(TRAIN1.toString(), row.getCell(36).getStringCellValue());
        Assert.assertEquals(TRAIN1.toString(), row.getCell(48).getStringCellValue());
    }

    /**
     * We expect to test the '*.xlsx' format.
     */
    @Test
    public void testXSSFWorkbook() throws Exception {
        workbook = new XSSFWorkbook();
        row = workbook.createSheet().createRow(ROW_INDEX);

        initRow(row);

        Assert.assertNotNull(aggregator.aggregate(item, workbook, SHEET_INDEX, ROW_INDEX));

        Row row = workbook.getSheetAt(SHEET_INDEX).getRow(ROW_INDEX);
        Assert.assertNotNull(row);
        Assert.assertEquals(TRAIN1.toString(), row.getCell(36).getStringCellValue());
        Assert.assertEquals(TRAIN1.toString(), row.getCell(48).getStringCellValue());
    }

    /**
     * We expect that if we use a {@link Workbook} sub-type which is not supported we do not aggregate the row.
     * We should not throw any Exception.
     */
    @Test
    public void testInvalidFormatException() throws Exception {
        workbook = new SXSSFWorkbook();
        row = workbook.createSheet().createRow(ROW_INDEX);

        initRow(row);

        Assert.assertNotNull(aggregator.aggregate(item, workbook, SHEET_INDEX, ROW_INDEX));

        Row row = workbook.getSheetAt(SHEET_INDEX).getRow(ROW_INDEX);
        Assert.assertNotNull(row);
        Assert.assertNotEquals(DELAY, row.getCell(54).getNumericCellValue());
    }

    /**
     * We expect that if we set a non numerical as a {@link TrainLine} then we should get a {@link java.text.ParseException}.
     * Which will be interpreted as an error in the log. We should not throw any Exception.
     */
    @Test
    public void testInvalidNumericFormat() throws Exception {
        item = new BatchExcelRow
                .Builder(LocalDate.now(), Sens.DEPARTURE)
                .expectedTrain1(new TrainLine.Builder(466L).build())
                .build(false);

        row.createCell(36).setCellValue(1717);

        Assert.assertNotNull(aggregator.aggregate(item, workbook, SHEET_INDEX, ROW_INDEX));

        Row row = workbook.getSheetAt(SHEET_INDEX).getRow(ROW_INDEX);
        Assert.assertNotNull(row);
        Assert.assertNotEquals(1717, row.getCell(36).getNumericCellValue());
    }


    /**
     * We expect that if aggregate an empty we return {@code null}.
     * We should not throw any Exception.
     */
    @Test
    public void testEmptyRow() throws Exception {
        workbook.createSheet().createRow(2);

        Assert.assertNull(aggregator.aggregate(item, workbook, SHEET_INDEX, 2));
    }

    /**
     * We expect that if we map a row which does not exist we do not create a new one.
     * We should not throw any Exception.
     */
    @Test
    public void testCannotMapRowIndex() throws Exception {
        Assert.assertNull(aggregator.aggregate(item, workbook, SHEET_INDEX, 2));
        Assert.assertNull(workbook.getSheetAt(SHEET_INDEX).getRow(2));
    }

    @Test
    public void testRoundTrip() throws Exception {
        BatchExcelRow batchExcelRow = new BatchExcelRowMapper().mapRow(workbook.getSheetAt(SHEET_INDEX).getRow(ROW_INDEX), ROW_INDEX);
        ExcelRow previousRow = aggregator.aggregate(item, workbook, SHEET_INDEX, ROW_INDEX);

        Assert.assertNotNull(previousRow);
        Assert.assertNotEquals(item, previousRow);
        Assert.assertNotNull(batchExcelRow);
        Assert.assertEquals(previousRow, batchExcelRow);
    }
}