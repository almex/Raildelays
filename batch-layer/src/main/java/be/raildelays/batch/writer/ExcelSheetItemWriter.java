package be.raildelays.batch.writer;

import be.raildelays.domain.xls.ExcelRow;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExcelSheetItemWriter extends
		AbstractItemStreamItemWriter<List<ExcelRow>> implements
		InitializingBean {

	private String input;

	private String output;

	private OutputStream outputStream;

	private XSSFWorkbook workbook;

	private static final int FIRST_ROW_INDEX = 21;

	private static final int MAX_ROW_PER_SHEET = 40;

	private static final String READ_COUNT = "read.count";

	private int currentItemCount = 0;

	@Override
	public void afterPropertiesSet() throws Exception {
		Validate.notNull(input,
				"You must provide an input before using this bean");
		Validate.notNull(output,
				"You must provide an output before using this bean");

		setExecutionContextName(ExcelSheetItemWriter.class.getCanonicalName());
	}

	@Override
	public void open(ExecutionContext executionContext)
			throws ItemStreamException {
		createNewExcelFile();
	}

	private void createNewExcelFile() throws ItemStreamException {
		try {
			File outputFile = new File(output);

			currentItemCount = 0;

			if (outputFile.exists()) {
				outputFile.delete();
			}

			outputFile.createNewFile();

			workbook = new XSSFWorkbook(new FileInputStream(input));

			outputStream = new FileOutputStream(outputFile);
		} catch (IOException e) {
			throw new ItemStreamException("I/O when opening Excel template", e);
		}
	}

	@Override
	public void close() throws ItemStreamException {
		closeExcelFile();
	}

	private void closeExcelFile() throws ItemStreamException {
		try {
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (IOException e) {
			throw new ItemStreamException("I/O when closing Excel output file",
					e);
		}
	}

	@Override
	public void write(List<? extends List<ExcelRow>> items) {
		Sheet sheet = getFirstSheet();
		int rowIndex = FIRST_ROW_INDEX;

		for (List<ExcelRow> item : items) {
			for (ExcelRow excelRow : item) {

				rowIndex = FIRST_ROW_INDEX + currentItemCount;
				Row templateRow = sheet.getRow(rowIndex);
				writeRow(templateRow, excelRow);

				currentItemCount++;
			}
		}

		try {
			workbook.write(outputStream);
		} catch (IOException e) {
			throw new ItemStreamException("I/O when writing Excel output file",
					e);
		}
	}

	@Override
	public void update(ExecutionContext executionContext)
			throws ItemStreamException {
		super.update(executionContext);
		Assert.notNull(executionContext, "ExecutionContext must not be null");
		executionContext.putInt(getExecutionContextKey(READ_COUNT),
				currentItemCount);

	}

	private Sheet getFirstSheet() {
		Sheet sheet = workbook.getSheetAt(0);
		return sheet;
	}

	private void writeRow(Row row, ExcelRow excelRow) {
		SimpleDateFormat hh = new SimpleDateFormat("HH");
		SimpleDateFormat mm = new SimpleDateFormat("mm");
		String departureStation = excelRow.getDepartureStation()
				.getEnglishName().toUpperCase(Locale.UK);
		String arrivalStation = excelRow.getArrivalStation().getEnglishName()
				.toUpperCase(Locale.UK);

		departureStation = StringUtils.stripAccents(departureStation);
		arrivalStation = StringUtils.stripAccents(arrivalStation);

		row.getCell(2).setCellValue(excelRow.getDate());
		row.getCell(12).setCellValue(departureStation);
		row.getCell(18).setCellValue(arrivalStation);
		if (excelRow.getLinkStation() != null) {
			row.getCell(24).setCellValue(
					excelRow.getLinkStation().getEnglishName());
		}
		row.getCell(30).setCellValue(
				hh.format(excelRow.getExpectedDepartureTime()));
		row.getCell(32).setCellValue(
				mm.format(excelRow.getExpectedDepartureTime()));
		row.getCell(33).setCellValue(
				hh.format(excelRow.getExpectedArrivalTime()));
		row.getCell(35).setCellValue(
				mm.format(excelRow.getExpectedArrivalTime()));
		row.getCell(36).setCellValue(
				excelRow.getExpectedTrain1().getEnglishName());
		if (excelRow.getExpectedTrain2() != null) {
			row.getCell(39).setCellValue(
					excelRow.getExpectedTrain2().getEnglishName());
		}
		row.getCell(42).setCellValue(
				hh.format(excelRow.getEffectiveDepartureTime()));
		row.getCell(44).setCellValue(
				mm.format(excelRow.getEffectiveDepartureTime()));
		row.getCell(45).setCellValue(
				hh.format(excelRow.getEffectiveArrivalTime()));
		row.getCell(47).setCellValue(
				mm.format(excelRow.getEffectiveArrivalTime()));
		row.getCell(48).setCellValue(
				excelRow.getEffectiveTrain1().getEnglishName());
		if (excelRow.getExpectedTrain2() != null) {
			row.getCell(51).setCellValue(
					excelRow.getEffectiveTrain2().getEnglishName());
		}

		XSSFFormulaEvaluator evalutator = new XSSFFormulaEvaluator(workbook);
		evalutator.evaluateFormulaCell(row.getCell(56));
		evalutator.evaluateFormulaCell(row.getCell(55));
		evalutator.evaluateFormulaCell(row.getCell(54));
		evalutator.evaluateFormulaCell(row.getCell(53));
	}

	public void setInput(String input) {
		this.input = input;
	}

	public void setOutput(String output) {
		this.output = output;
	}

}
