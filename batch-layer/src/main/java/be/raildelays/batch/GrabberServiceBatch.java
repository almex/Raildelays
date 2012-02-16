package be.raildelays.batch;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import be.raildelays.service.RaildelaysGrabberService;

public class GrabberServiceBatch implements ItemWriter<String> {

	@Autowired
	RaildelaysGrabberService grabberService;

	private static Logger logger = Logger.getLogger(GrabberServiceBatch.class);

	@Override
	public void write(List<? extends String> items) throws Exception {

		logger.debug("Entering into batch worker...");
		
		@SuppressWarnings("unchecked")
		Iterator<Calendar> dateRange = DateUtils.iterator(new Date(),
				DateUtils.RANGE_WEEK_RELATIVE);

		try {
			
			while (dateRange.hasNext()) {
				Date date = dateRange.next().getTime();
				logger.debug("Grabbing for date:"+date);
				
				for (String idTrain : items) {
					waitRandomly();
					logger.debug("Grabbing for train: "+idTrain);
					grabberService.grabTrainLine(idTrain, date);
				}
			}
			
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.error("Thread as been interupted", e);
		}
	}
	
	/**
	 * Wait a certain period of time before processing.
	 * It's more respectful for grabber to do so. 
	 * 
	 * Wait between 1 and 5 seconds.
	 * 
	 * @throws InterruptedException 
	 */
	private void waitRandomly() throws InterruptedException {
		long waitTime = 1000 + Math.round(5000L * Math.random());
		logger.debug("Waiting "+(waitTime/1000)+" seconds...");
		Thread.sleep(waitTime);
	}

}
