package be.raildelays.batch.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Compare source and destination folder content and return a list of common resources.
 *
 * @author Almex
 * @since 1.2
 */
public class ToDeleteExcelResourcesLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToDeleteExcelResourcesLocator.class);

    public static Resource[] getResources(Resource source, Resource destination) throws IOException {
        List<Resource> resourceList = new ArrayList<>();
        Path sourcePath = source.getFile().toPath();
        Path destinationPath = destination.getFile().toPath();

        if (Files.notExists(destinationPath)) {
            Files.createDirectories(destinationPath);
        }

        Files.list(sourcePath)
                .filter(path1 -> {
                    boolean result = false;

                    try {
                        result = Files.list(destinationPath)
                                .filter(path2 -> path2.getFileName() != null)
                                .anyMatch(path2 -> path2.getFileName().equals(path1.getFileName()));
                    } catch (IOException e) {
                        LOGGER.error("Cannot list this directory", e);
                    }

                    return result;
                }).forEach(path -> resourceList.add(new FileSystemResource(path.toFile())));

        return resourceList.toArray(new Resource[resourceList.size()]);
    }
}
