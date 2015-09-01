package org.springframework.batch.item.support;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.test.ItemStreamItemReaderDelegator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Almex
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CompositeItemStreamReaderTest {

    private CompositeItemStreamReader<String> reader;

    @Before
    public void setUp() throws Exception {
        List<ItemStreamReader<String>> delegates = new ArrayList<>();

        reader = new CompositeItemStreamReader<>();
        reader.setDelegates(delegates);

        delegates.add(new ItemStreamItemReaderDelegator<>(new ListItemReader<>(Arrays.asList("a", "e", "i"))));
        delegates.add(new ItemStreamItemReaderDelegator<>(new ListItemReader<>(Arrays.asList("b", "f", "j"))));
        delegates.add(new ItemStreamItemReaderDelegator<>(new ListItemReader<>(Arrays.asList("c", "g", "k"))));
        delegates.add(new ItemStreamItemReaderDelegator<>(new ListItemReader<>(Arrays.asList("d", "h", "l"))));
    }

    /**
     * We expect that all alphabet letters are read and sorted in the natural order.
     */
    @Test
    public void testReadWithSorting() throws Exception {
        List<String> expected = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l");

        reader.setSortItems(true);
        reader.setComparator(String::compareTo);

        Assert.assertEquals(expected, readAll());
    }

    /**
     * We expect that all alphabet letters are read in the order they appear during the reading.
     */
    @Test
    public void testReadWithoutSorting() throws Exception {
        List<String> expected = Arrays.asList("a", "e", "i", "b", "f", "j", "c", "g", "k", "d", "h", "l");

        reader.setSortItems(false);

        Assert.assertEquals(expected, readAll());
    }

    /**
     * We expect with that null delegates we get an empty list.
     */
    @Test
    public void testReadNullDelegates() throws Exception {
        reader.setDelegates(null);
        reader.setSortItems(false);

        Assert.assertTrue(readAll().isEmpty());
    }

    /**
     * We expect that with empty list of delegates we get an empty list.
     */
    @Test
    public void testReadEmptyDelegates() throws Exception {
        reader.setDelegates(Collections.emptyList());
        reader.setSortItems(false);

        Assert.assertTrue(readAll().isEmpty());
    }

    private List<String> readAll() throws Exception {
        List<String> result = new ArrayList<>();
        ExecutionContext context = new ExecutionContext();

        reader.open(context);

        for (String item = reader.read(); item != null; item = reader.read()) {
            result.add(item);
        }

        reader.update(context);

        return result;
    }
}
