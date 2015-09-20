package org.springframework.batch.item.file;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;

import java.util.Arrays;

/**
 * @author Almex
 */
public class AbstractItemCountingItemStreamItemWriterTest {

    private AbstractItemCountingItemStreamItemWriter<Indexed> writer;

    @Before
    public void setUp() throws Exception {
        writer = new AbstractItemCountingItemStreamItemWriter<Indexed>() {

            private StringBuilder builder = new StringBuilder();

            @Override
            protected boolean doWrite(Indexed item) throws Exception {
                builder.append(item);
                return true;
            }

            @Override
            protected void doOpen() throws ItemStreamException {
                // NoOp
            }

            @Override
            protected void doClose() throws ItemStreamException {
                // NoOp
            }

            public String toString() {
                return builder.toString();
            }
        };
        writer.setMaxItemCount(5);
        writer.setUseItemIndex(true);
        writer.setCurrentItemIndex(5);
        writer.setSaveState(true);
        writer.setName("foo");
    }

    @After
    public void tearDown() {
        writer.close();
    }

    /**
     * We expect to write until the limit of the maxItemCount (in this case 5 items).
     */
    @Test
    public void testWrite() throws Exception {
        ExecutionContext context = new ExecutionContext();

        writer.open(context);
        writer.write(Arrays.asList(
                new Indexed("a"), new Indexed("b"), new Indexed("c"),
                new Indexed("d"), new Indexed("e"), new Indexed("f")
        ));
        writer.update(context);

        Assert.assertEquals("abcde", writer.toString());
    }

    /**
     * We expect to test a restart when we save state.
     */
    @Test
    public void testSaveState() throws Exception {
        ExecutionContext context = new ExecutionContext();

        writer.setSaveState(true);

        writer.open(context);
        writer.write(Arrays.asList(
                new Indexed("a"), new Indexed("b"), new Indexed("c")
        ));
        writer.update(context);
        writer.close();

        writer.setCurrentItemIndex(0);
        writer.setMaxItemCount(10);

        writer.open(context);
        writer.write(Arrays.asList(
                new Indexed("d"), new Indexed("e"), new Indexed("f")
        ));
        writer.update(context);
        writer.close();

        Assert.assertEquals("abcde", writer.toString());
    }

    /**
     * We expect to test a restart when we do not save state.
     */
    @Test
    public void testNotSaveState() throws Exception {
        ExecutionContext context = new ExecutionContext();

        writer.setSaveState(false);

        writer.open(context);
        writer.write(Arrays.asList(
                new Indexed("a"), new Indexed("b"), new Indexed("c")
        ));
        writer.update(context);
        writer.close();

        writer.setCurrentItemIndex(0);
        writer.setMaxItemCount(10);

        writer.open(context);
        writer.write(Arrays.asList(
                new Indexed("d"), new Indexed("e"), new Indexed("f")
        ));
        writer.update(context);
        writer.close();

        Assert.assertEquals("abcdef", writer.toString());
    }

}