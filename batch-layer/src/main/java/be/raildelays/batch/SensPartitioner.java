package be.raildelays.batch;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import be.raildelays.domain.Sens;


public class SensPartitioner implements Partitioner {

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> result = new HashMap<>();
		
		for (Sens sens : Sens.values()) {			
			result.put(sens.getRailtimeParameter(), new ExecutionContext());
		}
		
		return result;
	}

}
