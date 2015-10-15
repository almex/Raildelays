package org.springframework.batch.item.file;

import be.raildelays.batch.AbstractFileTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import static org.junit.Assert.*;

/**
 * @author Almex
 */
public class UnzipTaskletTest {

    private UnzipTasklet tasklet;

    @Before
    public void setUp() throws Exception {
        tasklet = new UnzipTasklet();
        tasklet.setInputFile(new ClassPathResource("nmbs-latest.zip"));
        tasklet.setDestinationFolder(new FileSystemResource("unzip"));
    }

    /**
     * We expect to unzip 'nmbs-latest.zip' and to have 7 files in the destination folder.
     */
    @Test
    public void testExecute() throws Exception {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        StepContribution stepContribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        RepeatStatus repeatStatus = tasklet.execute(stepContribution, chunkContext);

        assertEquals(RepeatStatus.FINISHED, repeatStatus);
        assertEquals(7, stepContribution.getWriteCount());
    }

    @After
    public void tearDown() {

    }
}