package be.raildelays.batch.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.InitializingBean;

import be.raildelays.domain.xls.ExcelRow;

public class ExcelSheetItemWriter implements ItemStreamWriter<List<ExcelRow>>, InitializingBean {

	private String input;

	private String output;
	
	private OutputStream outputStream;

	private XSSFWorkbook workbook;

	@Override
	public void afterPropertiesSet() throws Exception {
		Validate.notNull(input, "You must provide an input before using this bean");
		Validate.notNull(output, "You must provide an output before using this bean");
	}

	@Override
	public void open(ExecutionContext executionContext)
			throws ItemStreamException {
		try {			
			File outputFile = new File(output);
			
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
	public void update(ExecutionContext executionContext)
			throws ItemStreamException {
	}

	@Override
	public void close() throws ItemStreamException {
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
	public void write(List<? extends List<ExcelRow>> items) throws Exception {
		Sheet sheet = workbook.getSheetAt(0);
		SimpleDateFormat hh = new SimpleDateFormat("HH");
		SimpleDateFormat mm = new SimpleDateFormat("mm");

		Iterator<? extends List<ExcelRow>> itListIterator = items.iterator();
		for (int rownum = 21; rownum < 61 ; ) {
			if(itListIterator.hasNext()) {
				Iterator<ExcelRow> it = itListIterator.next().iterator();
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
					
					XSSFFormulaEvaluator evalutator = new XSSFFormulaEvaluator(workbook);
					evalutator.evaluateFormulaCell(row.getCell(56));
					evalutator.evaluateFormulaCell(row.getCell(55));
					evalutator.evaluateFormulaCell(row.getCell(54));
					evalutator.evaluateFormulaCell(row.getCell(53));
				} else {
					break;
				}
				
				rownum++;
				
				if (rownum >= 61) {
					break;
				}
			}
			
			if (rownum >= 61) {
				break;
			}
		}
		
		
		try {
			workbook.write(outputStream);
		} catch (IOException e) {
			throw new ItemStreamException("I/O when writing Excel output file",
					e);
		}
	}

	public void setInput(String input) {
		this.input = input;
	}

	public void setOutput(String output) {
		this.output = output;
	}

}
