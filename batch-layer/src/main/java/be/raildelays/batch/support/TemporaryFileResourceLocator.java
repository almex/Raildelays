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
 */.raildelays.batch.support;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.support.ResourceLocator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is responsible to build {@link java.nio.file.Path} of a temporary {@code File}
 * and create it. This file will be destroyed at the shutdown of the JVM.
 * Then it stores, as a <code>String</code>, the resulting absolute {@link java.nio.file.Path} into the
 * {@link org.springframework.batch.item.ExecutionContext} using the {@link #keyName}.
 *
 * @author Almex
 * @since 1.2
 */
public class TemporaryFileResourceLocator implements ResourceLocator, InitializingBean {

    private String keyName;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(keyName, "The 'keyName' property must be provided");
    }

    @Override
    public Resource getResource(ExecutionContext context) throws IOException {
        Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"));
        Path tmpFile = Files.createTempFile(tmpDir, "raildelays-", ".xls.tmp");

        /*
         * To avoid any trouble with 3rd party components, we choose to delete this temporary file
         * on exit of the JVM. Then we have more guaranty that there is no file handle blocking the deletion.
         * In other words, if a 3rd party component have a remaining open stream on this file, the JVM guaranty
         * to close any stream before deleting it otherwise the delete may no be executed by the underlying
         * file system.
         */
        tmpFile.toFile().deleteOnExit();

        context.putString(keyName, tmpFile.toAbsolutePath().toString());

        return new FileSystemResource(tmpFile.toFile());
    }

    @Override
    public void setResource(Resource resource) {
        //NOOP
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
