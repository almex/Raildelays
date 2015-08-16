package be.raildelays.batch.job;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;

public class PropertiesFileJobParametersExtractorTest {

    private PropertiesFileJobParametersExtractor extractor;
    private ClassPathResource resource;

    @Before
    public void setUp() throws Exception {
        extractor = new PropertiesFileJobParametersExtractor();
        resource = new ClassPathResource("application.properties");
        extractor.setResource(resource);
    }

    @Test
    public void testGetJobParameters() throws Exception {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        JobParameters jobParameters = extractor.getJobParameters(new SimpleJob(), stepExecution);

        Assert.assertNotNull(jobParameters);
        Assert.assertEquals(Files.readAllLines(resource.getFile().toPath()).size(), jobParameters.getParameters().size());
    }
}