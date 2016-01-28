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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.batch.item.support.ItemStreamItemReaderDelegator;
import org.springframework.batch.item.support.ListItemReader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Almex
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TrainLineIdPartitionerTest {

    public static final List<String> TRAIN_IDS = Arrays.asList("414", "415", "416");
    private TrainIdPartitioner partitioner;

    @Before
    public void setUp() throws Exception {
        partitioner = new TrainIdPartitioner();
        partitioner.setTrainListReader(
                new ItemStreamItemReaderDelegator<>(
                        new ListItemReader<>(
                                TRAIN_IDS
                        )
                )
        );
        partitioner.afterPropertiesSet();
    }

    /**
     * We expect to have a 'trainId' key in the ExecutionContext of each partition and that each partition name
     * follow the regex pattern 'partition[0-9]'.
     */
    @Test
    public void testPartition() throws Exception {
        Map<String, ExecutionContext> partitions = partitioner.partition(10);

        Assert.assertEquals(3, partitions.size());
        Assert.assertTrue(partitions
                        .entrySet()
                        .stream()
                        .allMatch(entry -> entry.getKey().matches("partition[0-9]") &&
                                        TRAIN_IDS
                                                .stream()
                                                .anyMatch(trainId -> String
                                                                .valueOf(entry.getValue().getInt("trainId"))
                                                                .equals(trainId)
                                                )

                        )
        );
    }

    /**
     * We expect to get UnexpectedInputException when any Exception is thrown during a read.
     */
    @Test(expected = UnexpectedInputException.class)
    public void testPartitionWithException() throws Exception {
        partitioner.setTrainListReader(
                new ItemStreamItemReaderDelegator<>(
                        new AbstractItemStreamItemReader<String>() {
                            @Override
                            public String read() throws Exception {
                                throw new Exception();
                            }
                        }
                )
        );
        partitioner.partition(10);
    }
}