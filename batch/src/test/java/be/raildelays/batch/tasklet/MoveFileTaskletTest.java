package be.raildelays.batch.tasklet;

import be.raildelays.batch.AbstractFileTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;

import static org.junit.Assert.*;

/**
 * @author Almex
 */
public class MoveFileTaskletTest extends AbstractFileTest {

    private MoveFileTasklet tasklet;

    @Before
    public void setUp() throws Exception {
        tasklet = new MoveFileTasklet();
        tasklet.setSource(new FileSystemResource(CURRENT_PATH + EXCEL_FILE_NAME));
        tasklet.setDestination(
                new FileSystemResource(CURRENT_PATH + File.separator + "copy" + File.separator + EXCEL_FILE_NAME)
        );
        tasklet.afterPropertiesSet();
        cleanUp();
        copyFile();
    }

    /**
     * We expect that a move delete the source file and increment the write count.
     */
    @Test
    public void testMove() throws Exception {
        Assert.assertEquals(1, getExcelFiles().length);

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        StepContribution stepContribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        RepeatStatus repeatStatus = tasklet.execute(stepContribution, chunkContext);

        Assert.assertEquals(RepeatStatus.FINISHED, repeatStatus);
        Assert.assertEquals(0, getExcelFiles().length);
        Assert.assertEquals(1, stepContribution.getWriteCount());
    }

    /**
     * We expect that a move delete the source file but has the destination is null the write count should be 0.
     */
    @Test
    public void testMoveToNullDestination() throws Exception {
        Assert.assertEquals(1, getExcelFiles().length);

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        StepContribution stepContribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        tasklet.setDestination(null);

        RepeatStatus repeatStatus = tasklet.execute(stepContribution, chunkContext);

        Assert.assertEquals(RepeatStatus.FINISHED, repeatStatus);
        Assert.assertEquals(0, getExcelFiles().length);
        Assert.assertEquals(0, stepContribution.getWriteCount());
    }

    @After
    public void tearDown() throws Exception {
        cleanUp();
    }
}
