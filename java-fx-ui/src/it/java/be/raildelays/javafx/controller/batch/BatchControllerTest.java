package be.raildelays.javafx.controller.batch;

/**
 * Make the abstract class testable.
 *
 * @author Almex
 */
public class BatchControllerTest extends AbstractBatchController {
    @Override
    public void doStart() {
        service.start();
    }
}
