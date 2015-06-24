package be.raildelays.javafx.controller.batch;

import be.raildelays.javafx.service.BatchScheduledService;
import javafx.scene.control.Tab;

/**
 * @author Almex
 * @since 1.2
 */
public interface BatchController {

    void setService(BatchScheduledService service);
    void destroy();

}
