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

/**
 * Created by xbmc on 16-04-15.
 */
public class DeleteFileTaskletTest extends AbstractFileTest {

    /*
     * S.U.T.
     */
    private DeleteFileTasklet tasklet;

    @Before
    public void setUp() throws Exception {
        tasklet = new DeleteFileTasklet();
        tasklet.setResources(new Resource[]{new FileSystemResource(CURRENT_PATH + EXCEL_FILE_NAME)});
        tasklet.afterPropertiesSet();
        cleanUp();
        copyFile();
    }

    @Test
    public void testExecute() throws Exception {
        Assert.assertEquals(1, getExcelFiles().length);

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        StepContribution stepContribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        RepeatStatus repeatStatus = tasklet.execute(stepContribution, chunkContext);

        Assert.assertEquals(RepeatStatus.FINISHED, repeatStatus);
        Assert.assertEquals(0, getExcelFiles().length);
        Assert.assertEquals(1, stepContribution.getWriteCount());
    }

    @After
    public void tearDown() throws Exception {
        cleanUp();
    }
}