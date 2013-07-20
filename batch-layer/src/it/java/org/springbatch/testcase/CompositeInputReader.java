package org.springbatch.testcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.CompositeItemStream;

public class CompositeInputReader extends CompositeItemStream implements
		ItemStreamReader<CompositeData> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CompositeInputReader.class);

	private InputReader<DataFromSource1> reader1;

	private InputReader<DataFromSource2> reader2;
	
	private ExecutionContext stepExecutionContext;

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecutionContext = stepExecution.getExecutionContext();
	}

	@Override
	public CompositeData read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		CompositeData result = null;		
		DataFromSource1 data1 = null;
		DataFromSource2 data2 = null;
		OutputData previouslyProcessed = (OutputData) stepExecutionContext.get("processed");
				
		if (previouslyProcessed == null) {
			data1 = reader1.read();
			data2 = reader2.read();
		} else {
			
			switch (previouslyProcessed.getEvent()) {
			case ADD:
				data1 = reader1.read();
				data2 = reader2.readPrevious();				
				break;
			case DELETE:			
				data1 = reader1.readPrevious();
				data2 = reader2.read();	
				break;
			case MODIFY:
				data1 = reader1.read();
				data2 = reader2.read();				
				break;

			default:
				throw new IllegalArgumentException("Comparison should always be ADD, DELETE or MODIFY");
			}
			
		}
		
		

		if (data1 != null || data2 != null) {
			result = new CompositeData(data1, data2);

			LOGGER.info("read: {}", result.toString());
		}

		return result;
	}

	public void setReader1(InputReader<DataFromSource1> reader1) {
		this.reader1 = reader1;
	}

	public void setReader2(InputReader<DataFromSource2> reader2) {
		this.reader2 = reader2;
	}

}
