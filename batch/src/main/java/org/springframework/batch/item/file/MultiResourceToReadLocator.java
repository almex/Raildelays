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

package org.springframework.batch.item.file;

import org.springframework.batch.item.ItemStreamException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Almex
 * @since 1.2
 */
public class MultiResourceToReadLocator<T> extends CountingItemResourceLocator<T> {

    private String filter = "*";
    private Resource[] resources;
    private Resource directory;
    private int index = 0;

    @Override
    public void onOpen(ResourceContext context) throws ItemStreamException {
        super.onOpen(context);

        try {
            List<Resource> result = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory.getFile().toPath(), filter)) {
                stream.forEach(path -> result.add(new FileSystemResource(path.toFile())));
            }

            resources = result.toArray(new Resource[result.size()]);
            index = 0;

            if (resources.length > 0) {
                context.setResource(resources[index++]);
            }
        } catch (IOException e) {
            throw new ItemStreamException("I/O error when reading directory", e);
        }
    }

    @Override
    public void onRead(T item, ResourceContext context) throws Exception {
        super.onRead(item, context);

        if (item == null && index < resources.length - 1) {
            context.changeResource(resources[index++]);
        }
    }

    public void setDirectory(Resource directory) {
        this.directory = directory;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
