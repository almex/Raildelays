package be.raildelays.batch.reader;

import be.raildelays.domain.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.support.RepeatTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Almex
 */
public class TrainIdPartitioner implements Partitioner {

    private ItemStreamReader<String> trainListReader;
	
	private TrainIdPartitioner() {
	
	}
	
	public TrainIdPartitioner(ItemStreamReader<String> trainListReader) {
		this.trainListReader = trainListReader;
	}

    static final private Logger LOGGER = LoggerFactory.getLogger(TrainIdPartitioner.class);

    @Override
    public Map<String, ExecutionContext> partition(final int gridSize) {
        final Map<String, ExecutionContext> partitions = new HashMap<>();

        try {
            trainListReader.open(new ExecutionContext());
            RepeatTemplate template = new RepeatTemplate();
            template.iterate(new RepeatCallback() {

                public RepeatStatus doInIteration(RepeatContext context) throws UnexpectedInputException, ParseException, Exception {
                    RepeatStatus result = RepeatStatus.CONTINUABLE;
                    String trainId = trainListReader.read();

                    if (trainId != null) {
                        {   // English
                            ExecutionContext executionContext = new ExecutionContext();
                            int partitionId = partitions.size();
                            executionContext.putInt("trainId", Integer.parseInt(trainId));
//                            executionContext.putString("lang", Language.EN.name());
                            partitions.put("partition" + partitionId, executionContext);
                        }

//                        {   // French
//                            ExecutionContext executionContext = new ExecutionContext();
//                            int partitionId = partitions.size();
//                            executionContext.putInt("trainId", Integer.parseInt(trainId));
//                            executionContext.putString("lang", Language.FR.name());
//                            partitions.put("partition" + partitionId, executionContext);
//                        }
//
//                        {   // Dutch
//                            ExecutionContext executionContext = new ExecutionContext();
//                            int partitionId = partitions.size();
//                            executionContext.putInt("trainId", Integer.parseInt(trainId));
//                            executionContext.putString("lang", Language.NL.name());
//                            partitions.put("partition" + partitionId, executionContext);
//                        }
                    } else {
                        result = RepeatStatus.FINISHED;
                    }
                    return result;
                }

            });
        } catch (Exception e) {
            LOGGER.error("Error during start of retrieveDataFromRailtimeJob we will attempt to restart all failed jobs: {}", e);
        } finally {
            trainListReader.close();
        }

        return partitions;
    }

}
