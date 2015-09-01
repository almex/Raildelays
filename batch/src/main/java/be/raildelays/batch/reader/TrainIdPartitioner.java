/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package be.raildelays.batch.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * This {@link org.springframework.batch.core.partition.support.Partitioner} is used to create one partition per train.
 * Meaning that we will have dynamically one step per train. And depending on the
 * {@code TaskExecutor} those steps can be executed concurrently.
 * <p>
 *     The {@link ExecutionContext} of each train contains the key {@code trainId} with a value of {@link Integer} type.
 *     The name of the partition is 'partitionX' where 'X' is the zero-based index in order of creation.
 * </p>
 *
 * @author Almex
 * @since 1.0
 * @see org.springframework.core.task.TaskExecutor
 */
public class TrainIdPartitioner implements Partitioner, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainIdPartitioner.class);

    private ItemStreamReader<String> trainListReader;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(trainListReader, "The property 'trainListReader' must be provided");
    }

    @Override
    public Map<String, ExecutionContext> partition(final int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();

        trainListReader.open(new ExecutionContext());

        try {
            new RepeatTemplate().iterate(context -> {
                RepeatStatus result = RepeatStatus.CONTINUABLE;

                try {
                    String trainId = trainListReader.read();

                    if (trainId != null) {
                        ExecutionContext executionContext = new ExecutionContext();
                        int partitionId = partitions.size();

                        executionContext.putInt("trainId", Integer.parseInt(trainId));
                        partitions.put("partition" + partitionId, executionContext);

                        LOGGER.debug("Partition created with id={} and trainId={}", partitionId, trainId);
                    } else {
                        result = RepeatStatus.FINISHED;
                    }
                } catch (Exception e) {
                    throw new UnexpectedInputException("Error during reading list of train", e);
                }

                return result;
            });
        } finally {
            trainListReader.close();
        }

        return partitions;
    }

    public void setTrainListReader(ItemStreamReader<String> trainListReader) {
        this.trainListReader = trainListReader;
    }

}
