package org.springframework.batch.item.file;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * @author Almex
 */
public class HttpDownloadFileTaskletTest {

    private HttpDownloadFileTasklet tasklet;

    private static final String DIRECTORY = "./download";

    @Before
    public void setUp() throws Exception {
        tasklet = new HttpDownloadFileTasklet();
        tasklet.setInputFile(new UrlResource("http://gtfs.irail.be/nmbs/nmbs-latest.zip"));
        tasklet.setDestinationFolder(new FileSystemResource(DIRECTORY));
        cleanUp();
    }

    /**
     * We expect to download 'nmbs-latest.zip' into the destination folder.
     */
    @Test
    public void testExecute() throws Exception {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        StepContribution stepContribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        RepeatStatus repeatStatus = tasklet.execute(stepContribution, chunkContext);

        assertEquals(RepeatStatus.FINISHED, repeatStatus);
        assertEquals(1, stepContribution.getWriteCount());
        assertEquals(1, getFiles().count());
    }

    @After
    public void tearDown() throws IOException {
        cleanUp();
    }

    private void cleanUp() throws IOException {
        getFiles().forEach((path) -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Stream<Path> getFiles() throws IOException {
        Stream<Path> result = Stream.empty();

        if (Files.exists(Paths.get(DIRECTORY))) {
            result = Files.list(Paths.get(DIRECTORY));
        }

        return result;
    }
}
