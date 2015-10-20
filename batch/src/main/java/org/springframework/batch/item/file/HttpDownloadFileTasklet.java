package org.springframework.batch.item.file;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Download a file into a destination folder via an  HTTP GET.
 *
 * @author Almex
 * @since 2.0
 */
public class HttpDownloadFileTasklet implements Tasklet {

    private Resource inputFile;

    private Resource destinationFolder;

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        try (InputStream inputStream = inputFile.getInputStream()) {
            Path destinationPath = destinationFolder.getFile().toPath();
            Path outputFile = destinationPath.resolve(getFileName());

            if (!Files.exists(destinationPath)) {
                Files.createDirectories(destinationPath);
            }

            Files.copy(inputStream, outputFile, StandardCopyOption.REPLACE_EXISTING);
            contribution.incrementWriteCount(1);
        }

        return RepeatStatus.FINISHED;
    }

    private String getFileName() throws IOException {
        String file = inputFile.getURL().getFile();
        String result = "";

        if (file != null) {
            int index = file.lastIndexOf("/") + 1;

            if (index > 0 && index < file.length()) {
                result = file.substring(index);
            }
        }

        return result;
    }

    public void setInputFile(Resource inputFile) {
        this.inputFile = inputFile;
    }

    public void setDestinationFolder(Resource destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

}

