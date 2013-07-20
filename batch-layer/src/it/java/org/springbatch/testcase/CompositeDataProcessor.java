package org.springbatch.testcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springbatch.testcase.OutputData.Event;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;

public class CompositeDataProcessor implements ItemProcessor<CompositeData, OutputData> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompositeDataProcessor.class);
	
	private ExecutionContext stepExecutionContext;

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecutionContext = stepExecution.getExecutionContext();
	}
	
	@Override
	public OutputData process(CompositeData item) throws Exception {
		OutputData result = null;
		
		switch (item.compare()) {
		case -1:
			result = new OutputData(item.getData1(), Event.ADD);
			LOGGER.info("Process: ADD    {}", item.getData1());
			break;
		case 0:
			result = new OutputData(item.getData1(), Event.MODIFY);
			LOGGER.info("Process: MODIFY {}", item.getData1());
			break;
		case 1:
			result = new OutputData(item.getData2(), Event.DELETE);
			LOGGER.info("Process: DELETE {}", item.getData2());			
			break;

		default:
			throw new IllegalArgumentException("Comparison should always be -1, 0 or 1");
		}
		
		stepExecutionContext.put("processed", result);
		
		return result;
	}


}
