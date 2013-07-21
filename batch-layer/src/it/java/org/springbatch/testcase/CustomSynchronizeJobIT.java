package org.springbatch.testcase;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
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

	@Resource(name = "compositeDataProcessor")
	private ItemProcessor<CompositeData, OutputData> processor;

	@Resource(name = "compositeInputReader")
	private ItemReader<CompositeData> reader;

	@Resource(name = "outputWriter")
	private ItemWriter<OutputData> writer;

	@Test
	public void testSychronize() throws Exception {

		TaskExecutorRepeatTemplate executor = new TaskExecutorRepeatTemplate();

		// RepeatTemplate template = new RepeatTemplate();
		//
		// template.setCompletionPolicy(new SimpleCompletionPolicy(10));
		//
		// template.iterate(new RepeatCallback() {
		//
		// public RepeatStatus doInIteration(RepeatContext context) {
		// List<OutputData> items = new ArrayList<>();
		//
		// for(int i = 0; i < 10; i++){
		// Object item = itemReader.read()
		// Object processedItem = itemProcessor.process(item);
		// items.add(processedItem);
		// }
		// itemWriter.write(items);
		// return RepeatStatus.CONTINUABLE;
		// }
		//
		// });
		
//		reader = new CompositeInputReader();
//		writer = new OutputWriter();
//		processor = new CompositeDataProcessor();

		executor.iterate(new RepeatCallback() {

			public RepeatStatus doInIteration(RepeatContext context)
					throws Exception {
				List<OutputData> items = new ArrayList<>();
				RepeatStatus result = RepeatStatus.CONTINUABLE;

				while (items.size() < 10) {
					CompositeData item = reader.read();

					if (item == null) {
						result = RepeatStatus.FINISHED;
						break;
					} else {
						OutputData processedItem = processor.process(item);

						if (processedItem != null) {
							items.add(processedItem);
						}
					}
				}

				writer.write(items);

				return result;
			}

		});
	}
}
