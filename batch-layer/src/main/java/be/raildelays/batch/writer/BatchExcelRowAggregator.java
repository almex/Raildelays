package be.raildelays.batch.writer;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.poi.RowAggregator;
import be.raildelays.batch.poi.WorkbookAction;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Almex
 */
public class BatchExcelRowAggregator implements RowAggregator<BatchExcelRow> {

    @Override
    public BatchExcelRow aggregate(BatchExcelRow item, Workbook workbook, int sheetIndex, int rowIndex) throws Exception {
        SimpleDateFormat hh = new SimpleDateFormat("HH");
        SimpleDateFormat mm = new SimpleDateFormat("mm");
        NumberFormat numberFormat = new DecimalFormat("#");
        final Row row = workbook.getSheetAt(sheetIndex).getRow(rowIndex);
        BatchExcelRow previousRow = null;

        if (row != null && row.getCell(2) != null) {
            previousRow = new BatchExcelRowMapper().mapRow(row, rowIndex);
            String departureStation = getStationName(item.getDepartureStation());
            String arrivalStation = getStationName(item.getArrivalStation());

            row.getCell(2).setCellValue(item.getDate());
            row.getCell(12).setCellValue(departureStation);
            row.getCell(18).setCellValue(arrivalStation);
            if (item.getLinkStation() != null) {
                row.getCell(24).setCellValue(getStationName(item.getLinkStation()));
            } else {
                row.getCell(39).setCellType(Cell.CELL_TYPE_BLANK);
            }
            row.getCell(30).setCellValue(
                    hh.format(item.getExpectedDepartureTime()));
            row.getCell(32).setCellValue(
                    mm.format(item.getExpectedDepartureTime()));
            row.getCell(33).setCellValue(
                    hh.format(item.getExpectedArrivalTime()));
            row.getCell(35).setCellValue(
                    mm.format(item.getExpectedArrivalTime()));
            row.getCell(36).setCellValue(numberFormat.parse(
                    item.getExpectedTrain1().getEnglishName()).longValue());
            if (item.getExpectedTrain2() != null) {
                row.getCell(39).setCellValue(numberFormat.parse(
                        item.getExpectedTrain2().getEnglishName()).longValue());
            } else {
                row.getCell(39).setCellType(Cell.CELL_TYPE_BLANK);
            }
            row.getCell(42).setCellValue(
                    hh.format(item.getEffectiveDepartureTime()));
            row.getCell(44).setCellValue(
                    mm.format(item.getEffectiveDepartureTime()));
            row.getCell(45).setCellValue(
                    hh.format(item.getEffectiveArrivalTime()));
            row.getCell(47).setCellValue(
                    mm.format(item.getEffectiveArrivalTime()));
            row.getCell(48).setCellValue(numberFormat.parse(
                    item.getEffectiveTrain1().getEnglishName()).longValue());
            if (item.getExpectedTrain2() != null) {
                row.getCell(51).setCellValue(numberFormat.parse(
                        item.getEffectiveTrain2().getEnglishName()).longValue());
            } else {
                row.getCell(39).setCellType(Cell.CELL_TYPE_BLANK);
            }

            FormulaEvaluator evaluator = new WorkbookAction<FormulaEvaluator>(workbook) {

                @Override
                protected FormulaEvaluator doWithHSSFWorkbook(HSSFWorkbook workbook) {
                    return new HSSFFormulaEvaluator(workbook);
                }

                @Override
                protected FormulaEvaluator doWithXSSFWorkbook(XSSFWorkbook workbook) {
                    return new XSSFFormulaEvaluator(workbook);
                }
            }.execute();

            evaluator.evaluateFormulaCell(row.getCell(56));
            evaluator.evaluateFormulaCell(row.getCell(55));
            evaluator.evaluateFormulaCell(row.getCell(54));
        }

        return previousRow;
    }

    private static String getStationName(Station station) {
        String result = "";

        if (station != null) {
            String stationName = station.getEnglishName();

            if (StringUtils.isNotBlank(stationName)) {
                result = StringUtils.stripAccents(stationName.toUpperCase(Locale.UK));
            }
        }

        return result;
    }
}
