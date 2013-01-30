package be.raildelays.batch.writer;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.batch.item.ItemWriter;

import be.raildelays.domain.entities.LineStop;

public class ExcelSheetItemWriter implements ItemWriter<LineStop> {

	@Override
	public void write(List<? extends LineStop> items) throws Exception {
		Workbook workbook = new HSSFWorkbook();
		FileOutputStream out = new FileOutputStream("workbook.xls");
	}

}
