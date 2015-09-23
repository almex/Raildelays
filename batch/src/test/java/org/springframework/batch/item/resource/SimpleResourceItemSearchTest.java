package org.springframework.batch.item.resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.test.SimpleResourceAwareItemStream;
import org.springframework.core.io.ClassPathResource;

import java.util.Comparator;

/**
 * @author Almex
 */
public class SimpleResourceItemSearchTest {

    public static final int EXPECTED_INDEX = 10;
    private SimpleResourceItemSearch<String> itemSearch;

    @Before
    public void setUp() throws Exception {
        itemSearch = new SimpleResourceItemSearch<>();
    }

    /**
     * We expect to find the item and we get the expected index.
     */
    @Test
    public void testIndexOfFound() throws Exception {
        itemSearch.setReader(new AbstractIndexedResourceAccessibleItemStreamReader<String>() {
            @Override
            public String read() throws Exception {
                return "foo";
            }

            @Override
            public int getCurrentIndex() {
                return EXPECTED_INDEX;
            }
        });

        int index = itemSearch.indexOf("foo", new ClassPathResource("./"));

        Assert.assertEquals(EXPECTED_INDEX, index);
    }

    /**
     * We expect to not find the item and we get a EOF index.
     */
    @Test
    public void testIndexOfNotFound() throws Exception {
        itemSearch.setReader(new AbstractIndexedResourceAccessibleItemStreamReader<String>() {

            private boolean notEOF = false;

            @Override
            public String read() throws Exception {
                return notEOF ? "foo" : null;
            }

            @Override
            public int getCurrentIndex() {
                notEOF = false;
                return EXPECTED_INDEX;
            }
        });

        int index = itemSearch.indexOf("bar", new ClassPathResource("./"));

        Assert.assertEquals(ResourceItemSearch.EOF, index);
    }

    /**
     * We expect that if we change the comparator we can change the default comparison.
     */
    @Test
    public void testIndexOfSpecificComparator() throws Exception {
        itemSearch.setComparator(Comparator.comparing(s -> "bar")); // return for everything "bar"
        itemSearch.setReader(new AbstractIndexedResourceAccessibleItemStreamReader<String>() {
            @Override
            public String read() throws Exception {
                return "foo";
            }

            @Override
            public int getCurrentIndex() {
                return EXPECTED_INDEX;
            }
        });

        int index = itemSearch.indexOf("bar", new ClassPathResource("./"));

        Assert.assertEquals(EXPECTED_INDEX, index);
    }

    private abstract static class AbstractIndexedResourceAccessibleItemStreamReader<T>
            extends SimpleResourceAwareItemStream
            implements IndexedResourceAwareItemStreamReader<T> {
    }
}