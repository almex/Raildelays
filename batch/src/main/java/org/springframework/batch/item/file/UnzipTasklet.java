package org.springframework.batch.item.file;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;

import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Unzip a file into a destination folder
 *
 * @author Almex
 * @since 2.0
 */
public class UnzipTasklet implements Tasklet {

    private Resource inputFile;

    private Resource destinationFolder;

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(inputFile.getInputStream()))) {
            Path destinationPath = destinationFolder.getFile().toPath();

            if (!Files.exists(destinationPath)) {
                Files.createDirectories(destinationPath);
            }

            for (ZipEntry entry = zis.getNextEntry() ; entry != null ; entry = zis.getNextEntry()) {
                Path outputFile = destinationPath.resolve(entry.getName());

                Files.copy(zis, outputFile, StandardCopyOption.REPLACE_EXISTING);
                contribution.incrementWriteCount(1);
            }
        }

        return RepeatStatus.FINISHED;
    }

    public void setInputFile(Resource inputFile) {
        this.inputFile = inputFile;
    }

    public void setDestinationFolder(Resource destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

}

