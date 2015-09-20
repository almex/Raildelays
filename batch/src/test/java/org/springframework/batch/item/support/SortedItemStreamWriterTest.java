package org.springframework.batch.item.support;

import be.raildelays.batch.AbstractFileTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.Indexed;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Almex
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class SortedItemStreamWriterTest extends AbstractFileTest {

    public static final String FILE_DESTINATION_PATH = "." + File.separator + "text.txt";

    private SortedItemStreamWriter<Indexed> sortedItemStreamWriter;

    private List<Indexed> items = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        FlatFileItemWriter<Indexed> writer = new FlatFileItemWriter<>();
        FlatFileItemReader<Indexed> reader = new FlatFileItemReader<>();
        ExecutionContext executionContext = MetaDataInstanceFactory.createStepExecution().getExecutionContext();

        cleanUp();
        copyFile();

        writer.setName("test");
        writer.setResource(new FileSystemResource(CURRENT_PATH));
        writer.setLineAggregator(new PassThroughLineAggregator<>());
        writer.afterPropertiesSet();

        reader.setName("test");
        reader.setResource(new FileSystemResource(CURRENT_PATH));
        reader.setLineMapper((line, lineNumber) -> new Indexed(line));
        reader.afterPropertiesSet();

        sortedItemStreamWriter = new SortedItemStreamWriter<>();
        sortedItemStreamWriter.setResource(new FileSystemResource(FILE_DESTINATION_PATH));
        sortedItemStreamWriter.setComparator(Comparator.<Indexed>naturalOrder());
        sortedItemStreamWriter.setReader(reader);
        sortedItemStreamWriter.setWriter(writer);
        sortedItemStreamWriter.afterPropertiesSet();
        sortedItemStreamWriter.open(executionContext);

        items = Arrays.asList(new Indexed("d"), new Indexed("g"), new Indexed("f"), new Indexed("a"));
    }

    private static void assertSequence(String expected) throws IOException {
        StringBuilder actual = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(FILE_DESTINATION_PATH))) {
            stream.forEach(actual::append);
        }

        Assert.assertEquals(expected, actual.toString());
    }

    public void copyFile() throws IOException {
        Path source = new ClassPathResource("text.txt").getFile().toPath();
        Path destination = Paths.get(FILE_DESTINATION_PATH);
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    public void assertFile() {
        Path destination = Paths.get(FILE_DESTINATION_PATH);
        Path tempFile = Paths.get(FILE_DESTINATION_PATH + ".tmp");
        Path backupFile = Paths.get(FILE_DESTINATION_PATH + ".bak");

        Path newPAth = Paths.get(".");
        System.out.println("path=" + newPAth.toFile().getAbsolutePath());

        Assert.assertTrue(destination.toFile().exists());
        Assert.assertTrue(destination.toFile().isFile());
        Assert.assertFalse(tempFile.toFile().exists());
        Assert.assertFalse(backupFile.toFile().exists());
    }

    @After
    public void tearDown() throws Exception {
        sortedItemStreamWriter.close();
        cleanUp();
    }

    /**
     * We expect to write all letters in the right order.
     */
    @Test
    public void testWrite() throws Exception {
        sortedItemStreamWriter.write(items);

        assertFile();
        assertSequence("abcdefgh");
    }

    /**
     * We expect to write all letters in the right order but with some of them were replaced.
     * index    : 0 1 2 3
     * text.txt : c b h e
     * this     : d g f a
     */
    @Test
    public void testWriteIndexed() throws Exception {
        items.get(1).setIndex(1L); // 'g' replace 'b'

        sortedItemStreamWriter.write(items);

        assertFile();
        assertSequence("acdefgh");
    }

    /**
     * We expect to write all letters in the right order and using temporary file.
     */
    @Test
    public void testWriteWithTemporaryFile() throws Exception {
        sortedItemStreamWriter.setUseTemporaryFile(true);
        sortedItemStreamWriter.write(items);

        assertFile();
        assertSequence("abcdefgh");
    }
}