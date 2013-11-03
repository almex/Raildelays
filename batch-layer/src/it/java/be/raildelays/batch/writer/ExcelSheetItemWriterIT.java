package be.raildelays.batch.writer;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.xls.ExcelRow;
import be.raildelays.domain.xls.ExcelRow.Builder;

;

@RunWith(BlockJUnit4ClassRunner.class)
public class ExcelSheetItemWriterIT {

	private ExcelSheetItemWriter writer;

	@Before
	public void setUp() throws Exception {
		writer = new ExcelSheetItemWriter();
		
		writer.setInput(new ClassPathResource("template.xlsx").getFile().getAbsolutePath());
		writer.setOutput(new FileSystemResource(new File("output.xlsx")).getPath());
		
		writer.afterPropertiesSet();
		writer.open(MetaDataInstanceFactory.createStepExecution()
				.getExecutionContext());
	}

	@Test
	public void testTemplate() throws Exception {
		List<ExcelRow> excelRows = new ArrayList<>();
		List<List<ExcelRow>> items = new ArrayList<>();
		DateFormat formater = new SimpleDateFormat("HH:mm");
		ExcelRow row = new Builder(new Date(), Sens.DEPARTURE) //
				.departureStation(new Station("Liège-Guillemins")) //
				.arrivalStation(new Station("Bruxelles-central")) //
				.expectedDepartureTime(formater.parse("14:00")) //
				.expectedArrivalTime(formater.parse("15:00")) //
				.expectedTrain1(new Train("466")) //
				.effectiveDepartureTime(formater.parse("14:05")) //
				.effectiveArrivalTime(formater.parse("15:15")) //
				.effectiveTrain1(new Train("466")) //
				.build();

		excelRows.add(row);
		excelRows.add(row);
		items.add(excelRows);

		writer.write(items);

	}

	@After
	public void tearDown() {
		writer.close();
	}
}
