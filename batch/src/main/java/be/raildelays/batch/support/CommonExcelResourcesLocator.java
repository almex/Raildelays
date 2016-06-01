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
import java.util.stream.Stream;

/**
 * Compare source and destination folder content and return a list of common resources.
 *
 * @author Almex
 * @since 1.2
 */
public class CommonExcelResourcesLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonExcelResourcesLocator.class);

    public static Resource[] getResources(Resource source, Resource destination) throws IOException {
        List<Resource> resourceList = new ArrayList<>();
        Path sourcePath = source.getFile().toPath();
        Path destinationPath = destination.getFile().toPath();

        try (Stream<Path> stream1 = Files.list(sourcePath)) {
            stream1.filter(path1 -> {
                boolean result = false;

                try (Stream<Path> stream2 = Files.list(destinationPath)) {
                    result = stream2
                            .filter(path2 -> path2.getFileName() != null)
                            .anyMatch(path2 -> path2.getFileName().equals(path1.getFileName()));
                } catch (IOException e) {
                    LOGGER.error("Cannot list this directory", e);
                }

                return result;
            }).forEach(path -> resourceList.add(new FileSystemResource(path.toFile())));
        }

        return resourceList.toArray(new Resource[resourceList.size()]);
    }
}
