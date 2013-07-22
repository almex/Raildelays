package be.raildelays.batch.reader;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import be.raildelays.service.RaildelaysService;

/**
 * Search all dates stored in the database starting from the last date.
 * 
 * @author Almex
 */
public class DatabaseDatesItemReader extends AbstractItemCountingItemStreamItemReader <Date> implements InitializingBean  {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DatabaseDatesItemReader.class);

	@Resource
	private RaildelaysService service;
	
	private Date lastDate;
	
	private List<Date> dates;
	
	/**
	 * Default constructor.
	 */
	public DatabaseDatesItemReader() {
		super();
		setName(ClassUtils.getShortName(DatabaseDatesItemReader.class));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Validate all job parameters
		Assert.notNull(lastDate, "You must provide the lastDate parameter to this Reader.");
	}

	@Override
	protected void doOpen() throws Exception {		
		LOGGER.debug("Opening the stream with for dates until {}", lastDate);
		
		dates = service.searchAllDates(lastDate);
		
		LOGGER.debug("Retrieved {} dates", dates.size());
	}

	@Override
	protected void doClose() throws Exception {
		LOGGER.debug("Closing the stream...");
	}

	@Override
	protected Date doRead() throws Exception {
		Date result = null;
		
		if (getCurrentItemCount() <= dates.size()) {
			result = dates.get(getCurrentItemCount()-1);
			
			LOGGER.debug("Reading one more date={}", result);
		}
		
		return result;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

}
