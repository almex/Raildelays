package org.springbatch.testcase;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.batch.repeat.support.TaskExecutorRepeatTemplate;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import be.raildelays.batch.AbstractContextIT;

@ContextConfiguration(locations = { "/jobs/synchronize-job-context.xml" })
public class CustomSynchronizeJobIT extends AbstractContextIT {

	/**
	 * SUT.
	 */
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Resource
	private CompositeDataProcessor processor;
	
	@Resource
	private CompositeInputReader reader;
	
	@Resource
	private OutputWriter writer;
	
	@Test
	public void testSychronize() throws Exception {
		
		TaskExecutorRepeatTemplate executor = new TaskExecutorRepeatTemplate();
		

//		RepeatTemplate template = new RepeatTemplate();		
//
//		template.setCompletionPolicy(new SimpleCompletionPolicy(10));
//		
//		template.iterate(new RepeatCallback() {
//
//		    public RepeatStatus doInIteration(RepeatContext context) {
//		    	List<OutputData> items = new ArrayList<>();
//		    	
//		    	for(int i = 0; i < 10; i++){
//		    	    Object item = itemReader.read()
//		    	    Object processedItem = itemProcessor.process(item);
//		    	    items.add(processedItem);
//		    	}
//		    	itemWriter.write(items);
//		        return RepeatStatus.CONTINUABLE;
//		    }
//
//		});
		

		executor.iterate(new RepeatCallback() {

		    public RepeatStatus doInIteration(RepeatContext context) throws Exception {
		    	List<OutputData> items = new ArrayList<>();
		    	
		    	for(int i = 0; i < 10; i++){
		    		CompositeData item = reader.read();
		    		OutputData processedItem = processor.process(item);
		    	    items.add(processedItem);
		    	}
		    	writer.write(items);
		        return RepeatStatus.CONTINUABLE;
		    }

		});
	}
}
