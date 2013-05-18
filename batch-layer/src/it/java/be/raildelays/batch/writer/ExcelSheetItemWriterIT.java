package be.raildelays.batch.writer;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.xls.ExcelRow;
import static be.raildelays.domain.xls.ExcelRow.ExcelRowBuilder;

;

@ContextConfiguration(locations = { "ExcelSheetItemWriterIT.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		StepScopeTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class ExcelSheetItemWriterIT {

	@Autowired
	private ExcelSheetItemWriter writer;

	@Before
	public void setUp() throws FileNotFoundException {
		writer.open(MetaDataInstanceFactory.createStepExecution()
				.getExecutionContext());
	}

	@Test
	public void testTemplate() throws Exception {
		List<ExcelRow> items = new ArrayList<>();
		DateFormat formater = new SimpleDateFormat("HH:mm");
		ExcelRow row = new ExcelRowBuilder(new Date(), Sens.DEPARTURE) //
				.departureStation(new Station("Liège-Guillemins")) //
				.arrivalStation(new Station("Bruxelles-central")) //
				.expectedDepartureTime(formater.parse("14:00")) //
				.expectedArrivalTime(formater.parse("15:00")) //
				.expectedTrain1(new Train("466")) //
				.effectiveDepartureTime(formater.parse("14:05")) //
				.effectiveArrivalTime(formater.parse("15:15")) //
				.effectiveTrain1(new Train("466")) //
				.build();

		items.add(row);

		writer.write(items);
		writer.update(MetaDataInstanceFactory.createStepExecution()
				.getExecutionContext());

	}

	@After
	public void tearDown() {
		writer.close();
	}
}
