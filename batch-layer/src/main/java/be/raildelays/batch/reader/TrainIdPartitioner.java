package be.raildelays.batch.reader;

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
 * This {@link org.springframework.batch.core.partition.support.Partitioner} is used to create one partition per train.
 * Meaning that we will have dynamically one step per train. And depending on the
 * {@link org.springframework.core.task.TaskExecutor} those steps can be executed concurrently.
 *
 * @author Almex
 * @since 1.0
 */
public class TrainIdPartitioner implements Partitioner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainIdPartitioner.class);

    private ItemStreamReader<String> trainListReader;

    @Override
    public Map<String, ExecutionContext> partition(final int gridSize) {
        final Map<String, ExecutionContext> partitions = new HashMap<>();

        try {
            trainListReader.open(new ExecutionContext());
            RepeatTemplate template = new RepeatTemplate();
            template.iterate(new RepeatCallback() {

                @Override
                public RepeatStatus doInIteration(RepeatContext context) throws UnexpectedInputException, ParseException {
                    RepeatStatus result = RepeatStatus.CONTINUABLE;

                    try {
                        String trainId = trainListReader.read();

                        if (trainId != null) {
                            ExecutionContext executionContext = new ExecutionContext();
                            int partitionId = partitions.size();

                            executionContext.putInt("trainId", Integer.parseInt(trainId));
                            partitions.put("partition" + partitionId, executionContext);
                        } else {
                            result = RepeatStatus.FINISHED;
                        }
                    } catch (Exception e) {
                        throw new UnexpectedInputException("Error during reading list of train", e);
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

    public void setTrainListReader(ItemStreamReader<String> trainListReader) {
        this.trainListReader = trainListReader;
    }
}
