package be.raildelays.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;

/**
 * If one stop is not deserved (canceled) then we have no expected time. We must 
 * therefore find another way to retrieve line scheduling before persisting a
 * <code>RouteLog</code>. 
 * 
 * @author Almex
 */
public class AggregateExpectedTimeProcessor implements ItemProcessor<RouteLogDTO, RouteLogDTO> {

	@Override
	public RouteLogDTO process(RouteLogDTO item) throws Exception {		
		boolean oneStopIsCanceled = false;
		RouteLogDTO result = item;
		
		for (ServedStopDTO stop : result.getStops()) {
			if (stop.isCanceled()) {
				oneStopIsCanceled = true;
				break;
			}
		}
		
		if (oneStopIsCanceled) {
						
		}
		
		return result;
	}

}
