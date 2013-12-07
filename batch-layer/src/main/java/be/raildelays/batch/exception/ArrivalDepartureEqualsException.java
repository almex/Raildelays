package be.raildelays.batch.exception;

/**
 * Throw when buikding a {@link be.raildelays.batch.bean.BatchExcelRow} from two
 * {@link be.raildelays.domain.entities.LineStop} and that departure equals arrival.
 *
 * @author Almex
 */
public class ArrivalDepartureEqualsException extends Exception {

    public ArrivalDepartureEqualsException() {
        super();
    }

    public ArrivalDepartureEqualsException(String message) {
        super(message);
    }

    public ArrivalDepartureEqualsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArrivalDepartureEqualsException(Throwable cause) {
        super(cause);
    }
}
