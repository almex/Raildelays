package org.springbatch.testcase;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.batch.item.ItemWriter;

public class OutputWriter implements ItemWriter<OutputData> {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutputWriter.class);
	
	@Override
	public void write(List<? extends OutputData> items) throws Exception {
		
		LOGGER.info("items.size():={}", items.size());
		
		for (OutputData data : items) {
			
			switch (data.getEvent()) {
			case ADD:				
				LOGGER.info("{} (ADD)", data);				
				break;
			case DELETE:				
				LOGGER.debug("{} (DELETE)", data);				
				break;
			case MODIFY:				
				LOGGER.info("{} (MODIFY)", data);				
				break;

			default:
				throw new IllegalArgumentException("Comparison should always be ADD, DELETE or MODIFY");	
			}
		}
	}

}
