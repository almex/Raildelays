package be.raildelays.batch.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Validator;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import be.raildelays.domain.Sens;
import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.Step;
import be.raildelays.domain.xls.ExcelRow;

public class RouteLogMapperProcessor implements
		ItemProcessor<List<Direction>, RouteLogDTO>, InitializingBean {
        
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ItemProcessor.class);

	private Date date;

	@Override
	public void afterPropertiesSet() throws Exception {
		Validate.notNull(date, "Date is mandatory");
		
		LOGGER.info("[Rt] Processing for date={}...", date);
	}

	@Override
	public RouteLogDTO process(final List<Direction> items) throws Exception {		
		RouteLogDTO result = null;
		
		LOGGER.info("[Rt] Processing {} Direction...", items.size());
		
		if (items.size() >= 2) {
			Direction arrivalDirection = items.get(0);
			Direction departureDirection = items.get(1);
			
			result = new RouteLogDTO(arrivalDirection.getTrain().getIdRailtime(), date);
			
			for (Step arrivalStep : arrivalDirection.getSteps()) {
				int index = arrivalDirection.getSteps().indexOf(arrivalStep);
				Step departureStep = departureDirection.getSteps().get(index);
							
				ServedStopDTO stop = new ServedStopDTO(arrivalStep.getStation().getName(),
						departureStep.getTimestamp(), departureStep.getDelay(),
						arrivalStep.getTimestamp(), arrivalStep.getDelay(), arrivalStep.isCanceled() || departureStep.isCanceled());
				
				
				result.addStop(stop);
			}
		}
		
		return result;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
