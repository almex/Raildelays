package be.raildelays.batch.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesFileJobParametersExtractor.class);
    private Resource resource;

    @Override
    public JobParameters getJobParameters(Job job, StepExecution stepExecution) {
        Properties properties = new Properties();

        try (Reader reader = new FileReader(resource.getFile())) {
            properties.load(reader);
        } catch (IOException e) {
            LOGGER.error("Exception occurred when retrieving data from properties file", e);
        }

        return new DefaultJobParametersConverter().getJobParameters(properties);
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
