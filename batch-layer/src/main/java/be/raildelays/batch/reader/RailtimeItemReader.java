package be.raildelays.batch.reader;

import java.io.Reader;
import java.util.Date;

import javax.annotation.Resource;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import be.raildelays.domain.Language;
import be.raildelays.domain.Sens;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.httpclient.RequestStreamer;
import be.raildelays.parser.StreamParser;
import be.raildelays.parser.impl.RailtimeStreamParser;

public class RailtimeItemReader implements ItemReader<Direction> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RailtimeItemReader.class);

	@Resource
	private RequestStreamer streamer;

	@Resource
	private Mapper mapper;

	private String trainId;

	private Date date;

	private Sens sens;
	
	private StepExecution stepExecution;
	
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	public Direction read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		Direction result = null;
		
		if (trainId != null && date != null && sens != null) {
			// -- Create a request to target Railtime
			Reader englishStream = streamer.getDelays(trainId, date,
					Language.ENGLISH.getRailtimeParameter(),
					sens.getRailtimeParameter());
	
			// -- Parse the content
			StreamParser parser = new RailtimeStreamParser(englishStream);
	
			waitRandomly();
	
			result = parser.parseDelay(trainId, date);
			
			reset();
		}
		
		return result;
	}
	
	/**
	 * Reset the reader for the next iteration
	 */
	private void reset() {
		trainId = null;
		date = null;
		sens = null;		
	}

	/**
	 * Wait a certain period of time before processing. It's more respectful for
	 * grabber to do so.
	 * 
	 * Wait between 1 and 5 seconds.
	 * 
	 * @throws InterruptedException
	 */
	private void waitRandomly() throws InterruptedException {
		long waitTime = 1000 + Math.round(5000L * Math.random());

		LOGGER.debug("Waiting " + (waitTime / 1000) + " seconds...");

		Thread.sleep(waitTime);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Sens getSens() {
		return sens;
	}

	public void setSens(Sens sens) {
		this.sens = sens;
	}

	public void setTrainId(String trainId) {
		this.trainId = trainId;
	}

}
