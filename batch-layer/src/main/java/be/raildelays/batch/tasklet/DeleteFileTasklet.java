package be.raildelays.batch.tasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Delete all files contained in the array of resources.
 * Try first to delete via {@link File#delete()} and then fallback to {@link File#deleteOnExit()} if it did not succeed.
 *
 * @author Almex
 * @since 1.2
 */
public class DeleteFileTasklet implements Tasklet, InitializingBean {

    private Resource[] resources;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteFileTasklet.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(resources, "The 'resources' property must be provided");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<Resource> files = resources != null ? Arrays.asList(resources) : Collections.EMPTY_LIST;

        /*
         * We must delete this on JVM shutdown in order to allow close() of ItemStream coming afterwards.
         */
        files.stream().flatMap(resource -> {
            Stream<File> result = Stream.<File>empty();

            LOGGER.info("Deleting file {}...", resource.getFilename());

            try {
                result = Stream.of(resource.getFile());
            } catch (IOException e) {
                LOGGER.error("Cannot retrieve the File from this Resource", e);
            }

            return result;
        }).filter(file -> {
            contribution.incrementWriteCount(1);
            return !file.delete();
        }).forEach(File::deleteOnExit);

        return RepeatStatus.FINISHED;
    }

    public void setResources(Resource[] resources) {
        this.resources = resources;
    }
}

