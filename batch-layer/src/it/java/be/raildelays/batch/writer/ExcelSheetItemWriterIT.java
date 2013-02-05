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

import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.xls.ExcelRow;

@ContextConfiguration (locations = {"ExcelSheetItemWriterIT.xml"})
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class, 
    StepScopeTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class ExcelSheetItemWriterIT {

	@Autowired
	private ExcelSheetItemWriter writer;
	
	@Before
	public void setUp() throws FileNotFoundException {
		writer.open(MetaDataInstanceFactory.createStepExecution().getExecutionContext());
	}
	
	@Test
	public void testTemplate() throws Exception {
		List<ExcelRow> items = new ArrayList<>();
		ExcelRow row = new ExcelRow();
		DateFormat formater = new SimpleDateFormat("HH:mm");
		
		row.setDate(new Date());
		row.setDepartureStation(new Station("Liège-Guillemins"));
		row.setArrivalStation(new Station("Bruxelles-central"));
		row.setExpectedDepartureHour(formater.parse("14:00"));
		row.setExpectedArrivalHour(formater.parse("15:00"));
		row.setExpectedTrain1(new Train("466"));
		row.setEffectiveDepartureHour(formater.parse("14:05"));
		row.setEffectiveArrivalHour(formater.parse("15:15"));
		row.setEffectiveTrain1(new Train("466"));
		
		items.add(row);
		
		writer.write(items);
		writer.update(MetaDataInstanceFactory.createStepExecution().getExecutionContext());
		
	}
	
	@After
	public void tearDown() {
		writer.close();
	}
}
