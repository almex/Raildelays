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

package be.raildelays.batch.job;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.core.io.Resource;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 * Read a property file to setup all {@link org.springframework.batch.core.JobParameters}.
 *
 * @author Almex
 * @since 1.2
 */
public class PropertiesFileJobParametersExtractor implements JobParametersExtractor {

    private Resource resource;

    @Override
    public JobParameters getJobParameters(Job job, StepExecution stepExecution) {
        Properties properties = new Properties();

        try (Reader reader = new FileReader(resource.getFile())) {
            properties.load(reader);
        } catch (IOException e) {
            if (stepExecution != null) {
                stepExecution.setExitStatus(ExitStatus.FAILED.addExitDescription(e));
            }
        }

        return new DefaultJobParametersConverter().getJobParameters(properties);
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
