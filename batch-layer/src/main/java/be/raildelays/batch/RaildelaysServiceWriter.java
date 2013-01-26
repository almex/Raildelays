package be.raildelays.batch;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.RailtimeTrain;
import be.raildelays.service.RaildelaysService;

public class RaildelaysServiceWriter implements ItemWriter<List<LineStop>> {

	@Autowired
	RaildelaysService raildelaysService;

	private Date date;
	
	private String trainId;

	private static Logger logger = LoggerFactory.getLogger(RaildelaysServiceWriter.class);

	@Override
	public void write(List<? extends List<LineStop>> items) throws Exception {

		logger.debug("Entering into batch worker...");

		for (List<LineStop> lineStops : items) {
			for (LineStop lineStop : lineStops) {
				raildelaysService.saveTimetable(date, new RailtimeTrain(trainId, trainId), lineStop);
			}
		}
	}
	
	public void setRaildelaysService(RaildelaysService raildelaysService) {
		this.raildelaysService = raildelaysService;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setTrainId(String trainId) {
		this.trainId = trainId;
	}

}
