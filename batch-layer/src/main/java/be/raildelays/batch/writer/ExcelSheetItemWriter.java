package be.raildelays.batch.writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

import be.raildelays.domain.xls.ExcelRow;

public class ExcelSheetItemWriter implements ItemStreamWriter<ExcelRow> {

	private InputStream input;

	private OutputStream output;

	private Workbook workbook;

	@Override
	public void open(ExecutionContext executionContext)
			throws ItemStreamException {
		try {
			workbook = new XSSFWorkbook(input);
		} catch (IOException e) {
			throw new ItemStreamException("I/O when opening Excel template", e);
		}
	}

	@Override
	public void update(ExecutionContext executionContext)
			throws ItemStreamException {
		try {
			workbook.write(output);
		} catch (IOException e) {
			throw new ItemStreamException("I/O when writing Excel output file",
					e);
		}
	}

	@Override
	public void close() throws ItemStreamException {
		try {
			output.close();
		} catch (IOException e) {
			throw new ItemStreamException("I/O when closing Excel output file",
					e);
		}
	}

	@Override
	public void write(List<? extends ExcelRow> items) throws Exception {
		Sheet sheet = workbook.getSheetAt(0);
		SimpleDateFormat hh = new SimpleDateFormat("HH");
		SimpleDateFormat mm = new SimpleDateFormat("mm");

		Iterator<? extends ExcelRow> it = items.iterator();
		for (int rownum = 21; rownum < 61; rownum++) {
			Row row = sheet.getRow(rownum);

			if (it.hasNext()) {
				ExcelRow excelRow = it.next();
				String departureStation = excelRow.getDepartureStation()
						.getEnglishName().toUpperCase(Locale.UK);
				String arrivalStation = excelRow.getArrivalStation()
						.getEnglishName().toUpperCase(Locale.UK);
				

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
						hh.format(excelRow.getEffectiveDepartureHour()));
				row.getCell(44).setCellValue(
						mm.format(excelRow.getEffectiveDepartureHour()));
				row.getCell(45).setCellValue(
						hh.format(excelRow.getEffectiveArrivalHour()));
				row.getCell(47).setCellValue(
						mm.format(excelRow.getEffectiveArrivalHour()));
				row.getCell(48).setCellValue(
						excelRow.getEffectiveTrain1().getEnglishName());
				if (excelRow.getExpectedTrain2() != null) {
					row.getCell(51).setCellValue(
							excelRow.getEffectiveTrain2().getEnglishName());
				}
			} else {
				break;
			}

		}
	}

	public void setInput(InputStream input) {
		this.input = input;
	}

	public void setOutput(OutputStream output) {
		this.output = output;
	}

}
