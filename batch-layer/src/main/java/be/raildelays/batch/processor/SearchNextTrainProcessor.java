package be.raildelays.batch.processor;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import be.raildelays.domain.xls.ExcelRow;
import be.raildelays.service.RaildelaysService;

;

public class SearchNextTrainProcessor implements
		ItemProcessor<List<ExcelRow>, List<ExcelRow>>, InitializingBean {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SearchNextTrainProcessor.class);

	private String stationA;

	private String stationB;
	
	@Resource
	private RaildelaysService service;

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
		
		//TODO On any canceled ExcelRow search for next train
		//service.searchNextTrain(fromA, toB, date);
		
		return result;
	}
	
	public void setStationA(String stationA) {
		this.stationA = stationA;
	}

	public void setStationB(String stationB) {
		this.stationB = stationB;
	}

}
