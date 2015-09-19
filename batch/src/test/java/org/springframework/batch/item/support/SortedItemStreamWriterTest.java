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
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
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
import java.util.List;

/**
 * @author Almex
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class SortedItemStreamWriterTest extends AbstractFileTest {

    public static final String FILE_DESTINATION_PATH = "." + File.separator + "text.txt";

    private SortedItemStreamWriter<String> sortedItemStreamWriter;

    private List<String> items = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        FlatFileItemWriter<String> writer = new FlatFileItemWriter<>();
        FlatFileItemReader<String> reader = new FlatFileItemReader<>();
        ExecutionContext executionContext = MetaDataInstanceFactory.createStepExecution().getExecutionContext();

        cleanUp();
        copyFile();

        writer.setName("test");
        writer.setResource(new FileSystemResource(CURRENT_PATH));
        writer.setLineAggregator(new PassThroughLineAggregator<>());
        writer.afterPropertiesSet();

        reader.setName("test");
        reader.setResource(new FileSystemResource(CURRENT_PATH));
        reader.setLineMapper(new PassThroughLineMapper());
        reader.afterPropertiesSet();

        sortedItemStreamWriter = new SortedItemStreamWriter<>();
        sortedItemStreamWriter.setResource(new FileSystemResource(FILE_DESTINATION_PATH));
        sortedItemStreamWriter.setReader(reader);
        sortedItemStreamWriter.setWriter(writer);
        sortedItemStreamWriter.afterPropertiesSet();
        sortedItemStreamWriter.open(executionContext);

        items = Arrays.asList("z", "d", "c", "g", "h");
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

    @Test
    public void testWrite() throws Exception {
        sortedItemStreamWriter.write(items);

        assertFile();
    }

    @Test
    public void testWriteWithTemporaryFile() throws Exception {
        sortedItemStreamWriter.setUseTemporaryFile(true);
        sortedItemStreamWriter.write(items);

        assertFile();
    }
}