package be.raildelays.javafx.controller.batch;

import be.raildelays.javafx.service.BatchScheduledService;
import javafx.scene.control.Tab;
import org.springframework.batch.core.step.job.JobParametersExtractor;

/**
 * @author Almex
 * @since 1.2
 */
public interface BatchController {

    void setService(BatchScheduledService service);
    void destroy();
    void setPropertiesExtractor(JobParametersExtractor propertiesExtractor);

}
