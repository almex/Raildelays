package org.springbatch.testcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;

public class InputReader<T> extends AbstractItemCountingItemStreamItemReader<T> {

	private T[] dataTable;


	private static final Logger LOGGER = LoggerFactory.getLogger(InputReader.class);
	
	@Override
	protected T doRead() throws Exception {		
		T result =null;
		
		if (getCurrentItemCount() < dataTable.length) {
			result = dataTable[getCurrentItemCount()-1];
			
			LOGGER.info("read: {}", result.toString());
		}
		
		return result;
	}
	
	protected T readPrevious() throws Exception {		
		T result = null;
		
		if (getCurrentItemCount() > 0 && getCurrentItemCount() < dataTable.length) {
			result = dataTable[getCurrentItemCount()-1];
			
			LOGGER.info("readPevious: {}", result.toString());
		}
		
		return result;
	}

	@Override
	protected void doOpen() throws Exception {
		// Nothing to do
	}

	@Override
	protected void doClose() throws Exception {
		// Nothing to do
	}

	public void setDataTable(T[] dataTable) {
		this.dataTable = dataTable;
	}

}
