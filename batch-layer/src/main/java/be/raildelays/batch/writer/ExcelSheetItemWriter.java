package be.raildelays.batch.writer;

import java.util.List;

import jxl.Workbook;
import jxl.write.biff.File;

import org.springframework.batch.item.ItemWriter;

public class ExcelSheetItemWriter implements ItemWriter<String> {

	@Override
	public void write(List<? extends String> items) throws Exception {
		//Workbook workbook = Workbook.createWorkbook(new File("myfile.xls"));
	}

}
