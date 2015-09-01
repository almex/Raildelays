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
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@ContextConfiguration(locations = {"/jobs/steps/generate-excel-files-job-context.xml"})
@DataSet(value = "classpath:GenerateExcelFilesJobIT.xml", tearDownOperation = DBOperation.DELETE_ALL)
public class GenerateExcelFilesJobIT extends AbstractContextIT {

    /**
     * SUT.
     */
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void testGrabLineStop() throws Exception {
        BatchStatus batchStatus;
        Map<String, JobParameter> parameters = new HashMap<>();

        parameters.put("input.file.path", new JobParameter("train-list.properties"));
        parameters.put("date", new JobParameter(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000")));
        parameters.put("language", new JobParameter(Language.FR.name()));
        parameters.put("station.departure", new JobParameter("Liège-Guillemins"));
        parameters.put("station.arrival", new JobParameter("Brussels (Bruxelles)-Central"));
        parameters.put("excel.output.path", new JobParameter("./"));
        parameters.put("excel.file.name", new JobParameter("sncb_"));
        parameters.put("excel.file.extension", new JobParameter("xls"));
        parameters.put("excel.archive.path", new JobParameter("./"));
        parameters.put("text.output.path", new JobParameter("./output.txt"));
        parameters.put("excel.template.path", new JobParameter(new ClassPathResource("template.xls").getFile().getAbsolutePath()));

        batchStatus = jobLauncherTestUtils.launchJob(new JobParameters(parameters)).getStatus();

        Assert.assertFalse(batchStatus.isUnsuccessful());
    }
}
