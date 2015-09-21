/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Move a file from a {@code source} to a {@code destination}. If the {@code destination} denote a directory then we 
 * don't change the file name, otherwise it may be changed by the caller.
 * If the {@code destination} is {@code null} then it's equivalent to delete the {@code source}.
 *
 * @author Almex
 * @since 1.2
 */
public class MoveFileTasklet implements Tasklet, InitializingBean {

    private Resource source;
    private Resource destination;
    private static final Logger LOGGER = LoggerFactory.getLogger(MoveFileTasklet.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(source, "The 'source' property must be provided");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Path sourcePath = source.getFile().toPath();

        // Moving to a null destination is equivalent to delete the file
        if (destination != null) {
            Path destinationPath = destination.getFile().toPath();
            Path directoryPath;

            if (Files.isDirectory(destinationPath)) {
                directoryPath = destinationPath;
                destinationPath = directoryPath.resolve(sourcePath.getFileName());
            } else {
                directoryPath = destinationPath.getParent();
            }

            /**
             * We must create the destination directory if it does not exists
             */
            Files.createDirectories(directoryPath);

            /**
             * If the destination file exists but is writable then it will be overwrite.
             */
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

            LOGGER.debug("Moved file to {}", destinationPath);
        }

        if (Files.deleteIfExists(sourcePath)) {
            LOGGER.info("Deleted file {}", sourcePath);
        }
        
        return RepeatStatus.FINISHED;
    }

    public void setSource(Resource source) {
        this.source = source;
    }

    public void setDestination(Resource destination) {
        this.destination = destination;
    }
}
