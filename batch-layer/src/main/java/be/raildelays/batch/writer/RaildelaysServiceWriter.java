package be.raildelays.batch.writer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.service.RaildelaysService;

public class RaildelaysServiceWriter implements ItemWriter<RouteLogDTO> {

	@Autowired
	RaildelaysService raildelaysService;

	private static Logger logger = LoggerFactory.getLogger(RaildelaysServiceWriter.class);

	@Override
	public void write(List<? extends RouteLogDTO> items) throws Exception {

		logger.debug("Entering into batch worker...");

		for (RouteLogDTO routeLog : items) {
			raildelaysService.saveRouteLog(routeLog);
		}
	}
	
	public void setRaildelaysService(RaildelaysService raildelaysService) {
		this.raildelaysService = raildelaysService;
	}

}
