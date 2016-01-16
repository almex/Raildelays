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

package be.raildelays.batch;

import be.raildelays.domain.Language;
import com.excilys.ebi.spring.dbunit.config.DBOperation;
import com.excilys.ebi.spring.dbunit.test.DataSet;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;

@ContextConfiguration(locations = {"/jobs/steps/generate-excel-files-job-context.xml"})
@DataSet(value = "classpath:GenerateExcelFilesIT.xml",
        tearDownOperation = DBOperation.DELETE_ALL,
        dataSourceSpringName = "dataSource")
public class GenerateExcelFilesJobIT extends AbstractContextIT {

    @Test
    public void testGrabLineStop() throws Exception {
        BatchStatus batchStatus;
        JobParametersBuilder builder = new JobParametersBuilder(getJobLauncherTestUtils().getUniqueJobParameters());

        builder.addParameter("input.file.path", new JobParameter("train-list.properties"));
        builder.addParameter("date", new JobParameter(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000")));
        builder.addParameter("language", new JobParameter(Language.EN.name()));
        builder.addParameter("station.departure", new JobParameter("Liège-Guillemins"));
        builder.addParameter("station.arrival", new JobParameter("Bruxelles-Central"));
        builder.addParameter("excel.output.path", new JobParameter("./target"));
        builder.addParameter("excel.file.name", new JobParameter("sncb_"));
        builder.addParameter("excel.file.extension", new JobParameter("xls"));
        builder.addParameter("excel.archive.path", new JobParameter("./target"));
        builder.addParameter("text.output.path", new JobParameter("./target/output.txt"));
        builder.addParameter(
                "excel.template.path",
                new JobParameter(new ClassPathResource("template.xls").getFile().getAbsolutePath())
        );

        batchStatus = getJobLauncherTestUtils().launchJob(builder.toJobParameters()).getStatus();

        Assert.assertFalse(batchStatus.isUnsuccessful());
    }

    /**
     * Just to test that I can run the same test more than once.
     */
    @Test
    public void testGrabLineStop2() throws Exception {
        testGrabLineStop();
    }
}
