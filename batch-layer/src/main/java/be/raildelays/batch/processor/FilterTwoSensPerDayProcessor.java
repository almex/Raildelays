package be.raildelays.batch.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import be.raildelays.domain.Sens;
import be.raildelays.domain.xls.ExcelRow;

public class FilterTwoSensPerDayProcessor implements
		ItemProcessor<List<ExcelRow>, List<ExcelRow>>, InitializingBean {
        
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FilterTwoSensPerDayProcessor.class);

	private String stationA;

	private String stationB;

	@Override
	public void afterPropertiesSet() throws Exception {
		Validate.notNull(stationA, "Station A name is mandatory");
		Validate.notNull(stationB, "Station B name is mandatory");
		
		LOGGER.info("Processing for stationA={} and stationB={}...", stationA,
				stationB);
	}

	@Override
	public List<ExcelRow> process(final List<ExcelRow> items) throws Exception {
		List<ExcelRow> result = null;
			
		ExcelRow fromAtoB = extractMaxDelay(items, Sens.DEPARTURE);
		ExcelRow fromBtoA = extractMaxDelay(items, Sens.ARRIVAL);

		LOGGER.debug("From A to B : {}", fromBtoA);
		LOGGER.debug("From B to A : {}", fromAtoB);

		if (fromAtoB != null || fromBtoA != null) {
			result = new ArrayList<>();
			
			if (fromAtoB != null) {
				result.add(fromAtoB);
			} 
			
			if (fromBtoA != null) {
				result.add(fromBtoA);
			}
		}


		return result;
	}

	private ExcelRow extractMaxDelay(List<ExcelRow> items, Sens sens) {
		ExcelRow result = null;
		long maxDelay = -1;

		for (ExcelRow excelRow : items) {
			if (excelRow.getSens().equals(sens)
					&& excelRow.getDelay() > maxDelay) {
				maxDelay = excelRow.getDelay();
				result = excelRow;
			}
		}		

		LOGGER.trace("maxDelay={}", maxDelay);

		return result;
	}

	public void setStationA(String stationA) {
		this.stationA = stationA;
	}

	public void setStationB(String stationB) {
		this.stationB = stationB;
	}

}
