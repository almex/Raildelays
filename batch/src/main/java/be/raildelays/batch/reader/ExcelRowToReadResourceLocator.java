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

package be.raildelays.batch.reader;

import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceContext;
import org.springframework.batch.item.file.SimpleResourceLocator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Almex
 * @since 1.2
 */
public class ExcelRowToReadResourceLocator extends SimpleResourceLocator<ExcelRow> {

    private Resource[] resources;
    private Resource directory;
    private int index = 0;

    @Override
    public void onOpen(ResourceContext context) throws ItemStreamException {
        try {
            List<Resource> result = new ArrayList<>();

            Files.list(directory.getFile().toPath())
                    .forEach(path -> result.add(new FileSystemResource(path.toFile())));

            resources = result.toArray(new Resource[result.size()]);
        } catch (IOException e) {
            throw new ItemStreamException("I/O error when reading directory", e);
        }
    }

    @Override
    public ExcelRow onRead(ExcelRow item, ResourceContext context) throws Exception {

        if (item == null || !context.containsResource()) {
            context.changeResource(resources[index++]);
        }

        return super.onRead(item, context);
    }

    public void setDirectory(Resource directory) {
        this.directory = directory;
    }

    public void setResources(Resource[] resources) {
        this.resources = resources;
    }
}
